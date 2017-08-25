using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2CadLayouting.Converter
{
    class XmlEdge
    {
        #region Attributes
        private int idLeft;
        private int idRight;
        #endregion

        #region Constructor
        public XmlEdge(int idLeft, int idRight)
        {
            this.idLeft = idLeft;
            this.idRight = idRight;
        }
        #endregion

        #region Public Methods
        public int getIdLeft()
        {
            return this.idLeft;
        }

        public int getIdRight()
        {
            return this.idRight;
        }
        #endregion
    }
}
