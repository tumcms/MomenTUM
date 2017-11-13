using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;

namespace MomenTumV2RevitLayouting.Model.Output
{
    [Serializable()]
    [XmlType(AnonymousType = true)]
    public class Layout
    {
        [XmlElement("scenario")]
        public Scenario[] scenarios { get; set; }
    }
}
