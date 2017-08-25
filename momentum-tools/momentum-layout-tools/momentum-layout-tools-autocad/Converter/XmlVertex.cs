using MomenTumV2CadLayouting.Geometry;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2CadLayouting.Converter
{
    class XmlVertex
    {
        #region Attributes
        private String name;
        private int id;
        private Vector2D center;
        #endregion

        #region Constructor
        public XmlVertex(String name, int id, Vector2D center)
        {
            this.name = name;
            this.id = id;
            this.center = center;
        }
        #endregion

        #region Public Methods
        public String getName()
        {
            return this.name;
        }

        public int getId()
        {
            return this.id;
        }

        public Vector2D getCenter()
        {
            return this.center;
        }
        #endregion
    }
}
