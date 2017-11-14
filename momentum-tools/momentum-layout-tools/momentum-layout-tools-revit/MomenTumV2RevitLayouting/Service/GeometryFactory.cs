using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MomenTumV2RevitLayouting.Model;

namespace MomenTumV2RevitLayouting
{
    class GeometryFactory
    {
        private static int _originCounter = 0;
        private static int _intermediateCounter = 0;
        private static int _destinationCounter = 0;
        private static int _solidCounter = 0;
        private static int _wallCounter = 0;
        private static int _decimalPlaces = 4;
        private static double _minX = double.MaxValue;
        private static double _maxX = double.MinValue;
        private static double _minY = double.MaxValue;
        private static double _maxY = double.MinValue;

        public static void Reset()
        {
            _originCounter = 0;
            _intermediateCounter = 0;
            _destinationCounter = 0;
            _solidCounter = 0;
            _wallCounter = 0;
            _minX = double.MaxValue;
            _maxX = double.MinValue;
            _minY = double.MaxValue;
            _maxY = double.MinValue;
        }

        public static Segment2D GetLineSegment2D(Polygon2D polygon, int index)
        {
            Segment2D lineSegment = null;
            Vector2D[] polygonVertices = polygon.GetVertices();
            Vector2D startPoint;
            Vector2D endPoint;

            if (index <= polygonVertices.Length - 2)
            {
                startPoint = polygonVertices[index];
                endPoint = polygonVertices[index + 1];

                lineSegment = CreateSegment2D(new Vector2D[] { startPoint, endPoint }, polygon.Name);
            }
            else
            {
                lineSegment = null;
            }

            return lineSegment;
        }

        public static Vector2D CreateVector2D(double x, double y, double measurementUnitFactor)
        {
            return new Vector2D(Math.Round(x * measurementUnitFactor, _decimalPlaces), Math.Round(y * measurementUnitFactor, _decimalPlaces));
        }

        public static Polygon2D CreatePolygon2D(Vector2D[] nodes, string name)
        {
            Polygon2D newPolygon = new Polygon2D(nodes, name);

            return newPolygon;
        }
        public static Segment2D CreateSegment2D(Vector2D[] nodes, string name)
        {
            return new Segment2D(nodes, name);
        }

        public static List<Segment2D> DividePolygon2DintoSegment2D(Polygon2D polygon)
        {
            List<Segment2D> segments = new List<Segment2D>();
            int segmentIterator = 0;

            for (int i = 0; i < polygon.GetVertices().Length - 1; i++)
            {
                Vector2D startPoint = polygon.GetVertices().ElementAt(i);
                Vector2D endPoint = polygon.GetVertices().ElementAt(i + 1);

                if (startPoint.IsEqualTo(endPoint))
                {
                    continue;
                }

                segments.Add(GeometryFactory.CreateSegment2D(new Vector2D[] { startPoint, endPoint }, polygon.Name + "_" + segmentIterator.ToString()));
                segmentIterator++;
            }

            return segments;
        }

        public static double[] GetMaximumCoordinates()
        {
            return new double[] { _maxX, _maxY };
        }

        public static double[] GetMinimumCoordinates()
        {
            return new double[] { _minX, _minY };
        }

        public static void SetMaxMinCoordinates(Vector2D point)
        {
            if (point.X < _minX)
            {
                _minX = point.X;
            }
            if (point.Y < _minY)
            {
                _minY = point.Y;
            }
            if (point.X > _maxX)
            {
                _maxX = point.X;
            }
            if (point.Y > _maxY)
            {
                _maxY = point.Y;
            }
        }
    }
}
