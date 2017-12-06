using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting.Model
{
    public class Polygon2D : Geometry2D
    {
        public Vector2D[] Vertices { get; private set; }
        
        public Polygon2D(Vector2D[] vertices, string name) : base(name)
        {
            Vertices = vertices;
        }

        public override Vector2D[] GetVertices()
        {
            return Vertices;
        }
    }
}
