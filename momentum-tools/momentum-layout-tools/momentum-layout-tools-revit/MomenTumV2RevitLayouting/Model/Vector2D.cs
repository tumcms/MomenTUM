using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting
{
    public class Vector2D
    {
        public double X { get; private set; }
        public double Y { get; private set; }

        public Vector2D(double x, double y)
        {
            X = x;
            Y = y;
        }

        public Vector2D Add(Vector2D secondVector)
        {
            return new Vector2D(X + secondVector.X, Y + secondVector.Y);
        }

        public Vector2D Difference(Vector2D secondVector)
        {
            return new Vector2D(X - secondVector.X, Y - secondVector.Y);
        }

        public Vector2D Multiply(double scalar)
        {
            return new Vector2D(X * scalar, Y * scalar);
        }

        public double Dot(Vector2D secondVector)
        {
            return X * secondVector.X + Y * secondVector.Y;
        }

        public double CrossProduct(Vector2D secondVector)
        {
            return (X * secondVector.Y) - (secondVector.X * Y);
        }

        public bool IsEqualTo(Vector2D secondVector)
        {
            return (X == secondVector.X && Y == secondVector.Y);
        }

        public Vector2D GetAsNormalized()
        {
            double norm = normalize();
            Vector2D normalizedVector = Multiply(1 / norm);
            return normalizedVector;
        }

        public double normalize()
        {
            return Math.Sqrt(X * X + Y * Y);
        }

        public Vector2D Negate()
        {
            return new Vector2D(X * (-1), Y * (-1));
        }

        public Vector2D Rotate(double angle)
        {
            double newX = X * Math.Cos(angle) - Y * Math.Sin(angle);
            double newY = X * Math.Sin(angle) + Y * Math.Cos(angle);
            return new Vector2D(newX, newY);
        }
    }
}
