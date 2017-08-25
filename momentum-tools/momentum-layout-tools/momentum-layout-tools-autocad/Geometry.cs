using Autodesk.AutoCAD.ApplicationServices;
using Autodesk.AutoCAD.Colors;
using Autodesk.AutoCAD.DatabaseServices;
using Autodesk.AutoCAD.EditorInput;
using Autodesk.AutoCAD.Geometry;
using Autodesk.AutoCAD.Runtime;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace MomenTumV2CadLayouting
{
    namespace Geometry
    {
        class GeometryFactory
        {
            private static DynamicCounter<string> typeCounter = new DynamicCounter<string>();

            private static int decimalPlaces = 4;
            private static double minX = 0;
            private static double maxX = 0;
            private static double minY = 0;
            private static double maxY = 0;
            private static bool coordsSet = false;

            public static Polygon2D polylineToPolygon2D(Polyline polyline, String definition, double measurementUnitFactor) 
            {
                List<Vector2D> verticesList = new List<Vector2D>();
                Vector2D vectorToAdd;

                for (int i = 0; i < polyline.NumberOfVertices; i++) 
                {
                    vectorToAdd = new Vector2D(Math.Round(polyline.GetPoint2dAt(i).X * measurementUnitFactor, decimalPlaces),
                                                    Math.Round(polyline.GetPoint2dAt(i).Y * measurementUnitFactor, decimalPlaces));
                    verticesList.Add(vectorToAdd);

                    checkForMaxMinCoordinates(vectorToAdd);
                }

                verticesList = removeDoubledVertices(verticesList);
                Vector2D[] verticesAsArray = null;

                if (isPolylineCounterClockwise(polyline)) 
                {
                    verticesAsArray = verticesList.ToArray();
                }
                else 
                {
                    verticesAsArray = switchOrderOfVertices(verticesList.ToArray());
                }

                return new Polygon2D(verticesAsArray, definition + typeCounter.increment(definition));
            }

            public static void segment2DtoPolyline(Segment2D segment, String referringLayer, Document currentDocument)
            {
                Database currentDatabase = currentDocument.Database;
                Editor editor = currentDocument.Editor;

                using (DocumentLock docLock = currentDocument.LockDocument())
                {
                    using (Transaction transaction = currentDocument.TransactionManager.StartTransaction())
                    {
                        LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);
                        BlockTable blockTable = (BlockTable)transaction.GetObject(currentDatabase.BlockTableId, OpenMode.ForRead);
                        BlockTableRecord modelSpace = (BlockTableRecord)transaction.GetObject(blockTable[BlockTableRecord.ModelSpace], OpenMode.ForWrite);
                    
                        Polyline polyline = new Polyline();
                        Point2d startPoint = new Point2d(segment.getVertices()[0].getCoordinates()[0], segment.getVertices()[0].getCoordinates()[1]);
                        Point2d endPoint = new Point2d(segment.getVertices()[1].getCoordinates()[0], segment.getVertices()[1].getCoordinates()[1]);
                        polyline.AddVertexAt(0, startPoint, 0, 0, 0);
                        polyline.AddVertexAt(1, endPoint, 0, 0, 0);
                        polyline.LayerId = layers[referringLayer];
                        modelSpace.AppendEntity(polyline);
                        transaction.AddNewlyCreatedDBObject(polyline, true);
                        transaction.Commit();
                    }
                }
            }
            
            public static List<Segment2D> polylineToSegment2D(Polyline polyline, String definition, double measurementUnitFactor) 
            {
                List<Segment2D> segmentsList = new List<Segment2D>();
                List<Vector2D> verticesList = new List<Vector2D>();
                Vector2D vectorToAdd;

                for (int i = 0; i < polyline.NumberOfVertices; i++) 
                {
                    vectorToAdd = new Vector2D(Math.Round(polyline.GetPoint2dAt(i).X * measurementUnitFactor, decimalPlaces),
                                                    Math.Round(polyline.GetPoint2dAt(i).Y * measurementUnitFactor, decimalPlaces));
                    verticesList.Add(vectorToAdd);

                    checkForMaxMinCoordinates(vectorToAdd);
                }

                verticesList = removeDoubledVertices(verticesList);
                Vector2D[] verticesAsArray = verticesList.ToArray();

                if (verticesAsArray.Length > 1)
                {
                    for (int i = 1; i < verticesAsArray.Length; i++)
                    {
                        switch (definition)
                        {
                            case "Wall":
                                segmentsList.Add(new Segment2D(new Vector2D[] { verticesAsArray[i - 1], verticesAsArray[i] }, definition + typeCounter.get("Wall")));
                                typeCounter.increment("Wall");
                                break;
                            case "GatheringLines":
                                segmentsList.Add(new Segment2D(new Vector2D[] { verticesAsArray[i - 1], verticesAsArray[i] }, null));
                                break;
                            case "Graph":
                                segmentsList.Add(new Segment2D(new Vector2D[] { verticesAsArray[i - 1], verticesAsArray[i] }, null));
                                break;
                            default:
                                // Error Message
                                break;
                        }
                    }

                    if (polyline.Closed && !verticesAsArray[verticesAsArray.Length - 1].isEqualTo(verticesAsArray[0]))
                    {
                        segmentsList.Add(new Segment2D(new Vector2D[] { verticesAsArray[verticesAsArray.Length - 1], verticesAsArray[0] }, definition + typeCounter.get("Wall")));
                    }
                }

                return segmentsList;
            }

            public static void polygon2DtoPolyline(Polygon2D polygon, String referringLayer, Document currentDocument)
            {
                Database currentDatabase = currentDocument.Database;
                Editor editor = currentDocument.Editor;

                using (DocumentLock docLock = currentDocument.LockDocument())
                {
                    using (Transaction transaction = currentDocument.TransactionManager.StartTransaction())
                    {
                        LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);
                        BlockTable blockTable = (BlockTable)transaction.GetObject(currentDatabase.BlockTableId, OpenMode.ForRead);
                        BlockTableRecord modelSpace = (BlockTableRecord)transaction.GetObject(blockTable[BlockTableRecord.ModelSpace], OpenMode.ForWrite);
                        Vector2D[] polygonVertices = polygon.getVertices();
                        Polyline polyline = new Polyline();

                        for (int i = 0; i < polygonVertices.Length; i++)
                        {
                            polyline.AddVertexAt(i, new Point2d(polygonVertices[i].getCoordinates()[0], polygonVertices[i].getCoordinates()[1]), 0, 0, 0);
                        }

                        if (!polygonVertices[0].isEqualTo(polygonVertices[polygonVertices.Length - 1]))
                        {
                            polyline.AddVertexAt(polygonVertices.Length, new Point2d(polygonVertices[0].getCoordinates()[0], polygonVertices[0].getCoordinates()[1]), 0, 0, 0);
                        }

                        polyline.Closed = true;
                        polyline.LayerId = layers[referringLayer];
                        modelSpace.AppendEntity(polyline);
                        transaction.AddNewlyCreatedDBObject(polyline, true);
                        transaction.Commit();
                    }
                }
                
            }

            public static double[] getMaximumCoordinates()
            {
                return new double[] { maxX, maxY };
            }

            public static double[] getMinimumCoordinates()
            {
                return new double[] { minX, minY };
            }

            public static void reset()
            {
                typeCounter.reset();
            }

            #region Private Methods
            private static void checkForMaxMinCoordinates(Vector2D point)
            {
                if (point.getCoordinates()[0] < minX || coordsSet == false)
                {
                    minX = point.getCoordinates()[0];
                    coordsSet = true;
                }
                if (point.getCoordinates()[1] < minY || coordsSet == false)
                {
                    minY = point.getCoordinates()[1];
                    coordsSet = true;
                }
                if (point.getCoordinates()[0] > maxX || coordsSet == false)
                {
                    maxX = point.getCoordinates()[0];
                    coordsSet = true;
                }
                if (point.getCoordinates()[1] > maxY || coordsSet == false)
                {
                    maxY = point.getCoordinates()[1];
                    coordsSet = true;
                }
            }

            private static List<Vector2D> removeDoubledVertices(List<Vector2D> vertices)
            {
                List<Vector2D> modifiedVerticesList = new List<Vector2D>();
                modifiedVerticesList.Add(vertices.ElementAt(0));

                for (int i = 1; i < vertices.Count; i++)
                {
                    if (!vertices.ElementAt(i).isEqualTo(vertices.ElementAt(i - 1)))
                    {
                        modifiedVerticesList.Add(vertices.ElementAt(i));
                    }
                }

                return modifiedVerticesList;
            }

            private static Vector2D[] switchOrderOfVertices(Vector2D[] oldOrder) 
            {
                Vector2D[] newOrder = new Vector2D[oldOrder.Length];
                int j = 0;

                for (int i = oldOrder.Length - 1; i >= 0; i--) {

                    newOrder[j] = oldOrder[i];
                    j++;
                }

                return newOrder;
            }

            private static bool isPolylineCounterClockwise(Polyline pPoly) 
            {
                if (!pPoly.Closed) 
                {
                    return false;
                }

                int winding = 0;
                double turn = 0;
                int nSegs = pPoly.NumberOfVertices;
                Vector3dCollection startTans = new Vector3dCollection();
                Vector3dCollection endTans = new Vector3dCollection();
                double PI2 = Math.Round(Math.PI * 2, 12); //6.28318530718;

                for (int i = 0; i < nSegs; i++)
                {
                    if (pPoly.GetSegmentType(i) == SegmentType.Arc) 
                    {
                        CircularArc2d arc = pPoly.GetArcSegment2dAt(i);
                        Vector2d startTan;
                        Vector2d endTan;
                        Vector2dCollection startDerivs = new Vector2dCollection();

                        startDerivs.Add(arc.EvaluatePoint(arc.StartAngle).GetAsVector());
                        startTan = startDerivs[0];
                        Vector2dCollection endDerivs = new Vector2dCollection();
                        Point2d ap2 = arc.EvaluatePoint(arc.EndAngle);
                        endDerivs.Add(arc.EvaluatePoint(arc.StartAngle).GetAsVector());
                        endTan = endDerivs[0];
                        startTans.Add(new Vector3d(startTan.X, startTan.Y, 0.0));
                        endTans.Add(new Vector3d(endTan.X, endTan.Y, 0.0));
                        double ang = arc.EndAngle - arc.StartAngle;
                        turn += arc.IsClockWise ? -ang : ang;
                    }
                    else if (pPoly.GetSegmentType(i) == SegmentType.Line)
                    {
                        LineSegment2d line = pPoly.GetLineSegment2dAt(i);
                        Vector2d tan2d = line.EndPoint - line.StartPoint;
                        Vector3d tan = new Vector3d(tan2d.X, tan2d.Y, 0.0);
                        startTans.Add(tan);
                        endTans.Add(tan);
                    }
                }

                nSegs = startTans.Count;

                for (int i = 0; i < nSegs; i++) 
                {
                    double angle = endTans[i].GetAngleTo(startTans[(i + 1) % nSegs]);
                    Vector3d norm = endTans[i].CrossProduct(startTans[(i + 1) % nSegs]);
                    angle = norm.Z > 0 ? angle : -angle;
                    turn += angle;
                }

                turn = turn / PI2;
                double lower = Math.Floor(turn);
                double tol = 1e-6;

                if ((turn - lower) < tol) 
                {
                    winding = (int)lower;
                }
                else if (((lower + 1) - turn) < tol)
                {
                    winding = (int)(lower + 1);
                }
                else 
                {
                    winding = 0;
                }

                if (winding <= 0) 
                {
                    return false;
                }
                else 
                {
                    return true;
                }
            }
            #endregion
        }

        class Vector2D
        {
            double x;
            double y;

            public Vector2D(){

            }

            public Vector2D(double x, double y) {

                this.x = x;
                this.y = y;
            }

            public double[] getCoordinates() {
                return new double[] { x, y };
            }

            public void setXCoordinate(double x) {
                this.x = x;
            }

            public void setYCoordinate(double y) {
                this.y = y;
            }

            public bool isEqualTo(Vector2D secondVector)
            {
                if (this.getCoordinates()[0] == secondVector.getCoordinates()[0] && this.getCoordinates()[1] == secondVector.getCoordinates()[1])
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            public bool isEqualTo(Vector2D secondVector, int decimalPlaces)
            {
                if (Math.Round(this.getCoordinates()[0],4) == Math.Round(secondVector.getCoordinates()[0], 4) && Math.Round(this.getCoordinates()[1], 4) == Math.Round(secondVector.getCoordinates()[1], 4))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        class Geometry2D
        {
            String name;

            public Geometry2D(String name) {
                this.name = name;
            }

            public String getName() {
                return this.name;
            }

            public virtual Vector2D[] getVertices() {
                return null;
            }
        }

        class Segment2D : Geometry2D
        {
            Vector2D start;
            Vector2D end;


            public Segment2D(Vector2D[] nodes, String name) : base(name)  {

                this.start = nodes[0];
                this.end = nodes[1];
            }

            public override Vector2D[] getVertices() {
                return new Vector2D[] { start, end };
            }
        }

        class Polygon2D : Geometry2D
        {
            Vector2D[] nodeArray;
            String name;

            public Polygon2D(Vector2D[] nodes, String name) : base(name) {

                this.nodeArray = nodes;
                this.name = name;
            }

            public override Vector2D[] getVertices() {
                return nodeArray;
            }
        }

        class DynamicCounter<T>
        {
            private static Dictionary<T, int> counter = new Dictionary<T, int>();
            public int increment(T type)
            {
                if (!counter.ContainsKey(type))
                    counter[type] = -1;
                return ++counter[type];
            }
            public int get(T type)
            {
                if (!counter.ContainsKey(type))
                    counter[type] = 0;
                return counter[type];
            }
            public void reset()
            {
                counter.Clear();
            }
        }
    }
}
