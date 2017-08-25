using MomenTumV2CadLayouting.Geometry;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2CadLayouting.Converter
{
    class XmlFactory
    {
        #region Attributes

        private int vertexId = 0;

        #endregion

        #region Constructor
        public XmlFactory()
        {

        }
        #endregion

        #region Public Methods
        public XmlVertex createXmlVertex(String name, Vector2D center)
        {
            if (name == null)
            {
                name = this.vertexId.ToString();
            }

            XmlVertex newVertex = new XmlVertex(name, this.vertexId, center);
            vertexId++;
            return newVertex;
        }

        public XmlEdge createXmlEdge(int leftId, int rightId)
        {
            return new XmlEdge(leftId, rightId);
        }

        public void reset()
        {
            this.vertexId = 0;
        }
        #endregion
    }
}
