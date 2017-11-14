using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting.Model
{
    public class Geometry2D
    {
        public string Name { get; set; }

        public Geometry2D(string name)
        {
            this.Name = name;
        }

        public virtual Vector2D[] GetVertices()
        {
            return null;
        }
    }
}
