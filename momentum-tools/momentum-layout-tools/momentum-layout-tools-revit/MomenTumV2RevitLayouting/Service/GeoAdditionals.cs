using MomenTumV2RevitLayouting.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting.Service
{
    public static class GeoAdditionals
    {
        private static readonly double EPS = 10e-8;

        public static bool VectorsAreLinearDependent(Vector2D firstVector, Vector2D secondVector)
        {
            double det = firstVector.X * secondVector.Y - secondVector.X * firstVector.Y;

            return (Math.Abs(det) < EPS);
        }

        public static bool SegmentsLieOnTopOfEachOther(Segment2D firstSegment, Segment2D secondSegment)
        {
            bool onTop = false;

            Vector2D[] firstSegmentsVertices = firstSegment.GetVertices();
            Vector2D firstSegmentsStart_A = firstSegmentsVertices[0];
            Vector2D firstSegmentsEnd_B = firstSegmentsVertices[1];
            Vector2D firstSegmentsDirection = firstSegmentsEnd_B.Difference(firstSegmentsStart_A);
            double firstsegmentsLength = firstSegmentsDirection.normalize();
            Vector2D secondSegmentsStart_C = secondSegment.GetVertices()[0];
            Vector2D secondSegmentsEnd_D = secondSegment.GetVertices()[1];
            Vector2D secondSegmentsDirection = secondSegmentsEnd_D.Difference(secondSegmentsStart_C);
            double secondSegmentsLength = secondSegmentsDirection.normalize();

            Vector2D vectorAC = secondSegmentsStart_C.Difference(firstSegmentsStart_A);
            Vector2D vectorAD = secondSegmentsEnd_D.Difference(firstSegmentsStart_A);

            if (VectorsAreLinearDependent(vectorAC, vectorAD))
            {
                double cos = firstSegmentsDirection.Dot(secondSegmentsDirection) / (firstsegmentsLength * secondSegmentsLength);

                if (cos < 0)
                {
                    firstSegmentsStart_A = firstSegmentsVertices[1];
                    firstSegmentsEnd_B = firstSegmentsVertices[0];
                }

                vectorAC = secondSegmentsStart_C.Difference(firstSegmentsStart_A);
                Vector2D vectorDB = firstSegmentsEnd_B.Difference(secondSegmentsEnd_D);

                cos = vectorAC.Dot(vectorDB) / (vectorAC.normalize() * vectorDB.normalize());

                if (cos > 0)
                {
                    onTop = true;
                }
            }

            return onTop;
        }

        public static bool Polygon2DContainsAllOtherObjects(Polygon2D containerPolygon, List<Geometry2D> contentObjects)
        {
            bool containsAll = false;

            foreach (Geometry2D currentContentObject in contentObjects)
            {
                Vector2D[] objectsVertices = currentContentObject.GetVertices();

                if (!currentContentObject.Equals(containerPolygon))
                {
                    for (int i = 0; i < objectsVertices.Length; i++)
                    {
                        if (PointIsInsidePolygon(containerPolygon, objectsVertices[i]))
                        {
                            containsAll = true;
                        }
                        else
                        {
                            containsAll = false;
                            break;
                        }
                    }
                }
            }

            return containsAll;
        }

        public static bool Polygon2DContainsObject(Polygon2D containerPolygon, Geometry2D contentObject)
        {
            bool containsAll = false;
            Vector2D[] objectsVertices = contentObject.GetVertices();

            if (!contentObject.Equals(containerPolygon))
            {
                for (int i = 0; i < objectsVertices.Length; i++)
                {
                    if (PointIsInsidePolygon(containerPolygon, objectsVertices[i]))
                    {
                        containsAll = true;
                    }
                    else
                    {
                        containsAll = false;
                        break;
                    }
                }
            }

            return containsAll;
        }

        // Code from http://forums.autodesk.com/t5/net/is-point-inside-polygon-or-block-s-area/td-p/3164508#
        public static bool PointIsInsidePolygon(Polygon2D polygon, Vector2D point)
        {
            Vector2D[] polygonVertices = polygon.GetVertices();
            int n = polygonVertices.Length;
            double angle = 0;
            Vector2D point1 = null;
            Vector2D point2 = null;

            for (int i = 0; i < n; i++)
            {
                point1 = new Vector2D(
                    polygonVertices[i].X - point.X,
                    polygonVertices[i].Y - point.Y
                    );
                point2 = new Vector2D(
                    polygonVertices[(i + 1) % n].X - point.X,
                    polygonVertices[(i + 1) % n].Y - point.Y
                    );

                angle += Angle2D(point1, point2);
            }

            return !(Math.Abs(angle) < Math.PI);
        }

        // Code from http://forums.autodesk.com/t5/net/is-point-inside-polygon-or-block-s-area/td-p/3164508#
        public static double Angle2D(Vector2D point1, Vector2D point2)
        {
            double dtheta, theta1, theta2;

            theta1 = Math.Atan2(point1.Y, point1.X);
            theta2 = Math.Atan2(point2.Y, point2.X);
            dtheta = theta2 - theta1;

            while (dtheta > Math.PI)
            {
                dtheta -= (Math.PI * 2);
            }

            while (dtheta < -Math.PI)
            {
                dtheta += (Math.PI * 2);
            }

            return (dtheta);
        }

        public static bool ConvertedRoomsContainStairArea(List<Polygon2D> roomBoundaryPolygons, Polygon2D stairArea)
        {
            foreach (Polygon2D boundaryPolygon in roomBoundaryPolygons)
            {
                if (Polygon2DContainsObject(boundaryPolygon, stairArea))
                {
                    return true;
                }
                else
                {
                    continue;
                }
            }

            return false;
        }
    }
}
