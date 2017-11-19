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
    public class Point
    {
        // could implement setter here for double precision 4...
        [XmlAttribute("x")]
        public double X { get; set; }

        [XmlAttribute("y")]
        public double Y { get; set; }
    }
}
