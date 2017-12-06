using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting.Model
{
    public class Segment2D : Geometry2D
    {
        Vector2D start;
        Vector2D end;

        public Segment2D(Vector2D[] nodes, string name) : base(name)
        {
            start = nodes[0];
            end = nodes[1];
        }

        public Vector2D GetCenter()
        {
            Vector2D directionalVector = end.Difference(start);
            Vector2D center = start.Add(directionalVector.Multiply(0.5));

            return center;
        }

        public Vector2D IntersectWith(Segment2D secondSegment)
        {
            Vector2D intersectionPoint = null;
            double x1 = start.X;
            double y1 = start.Y;
            double x2 = end.X;
            double y2 = end.Y;
            double x3 = secondSegment.start.X;
            double y3 = secondSegment.start.Y;
            double x4 = secondSegment.end.X;
            double y4 = secondSegment.end.Y;

            double det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

            if (det != 0)
            {
                double intersection_x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
                                        / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
                double intersection_y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
                                       / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
                intersectionPoint = GeometryFactory.CreateVector2D(intersection_x, intersection_y, 1);
            }

            return intersectionPoint;
        }

        public override Vector2D[] GetVertices()
        {
            return new Vector2D[] { start, end };
        }
    }
}
