using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;

namespace MomenTumV2SpaceSyntaxRevit.Model
{
    [Serializable()]
    [XmlType(AnonymousType = true)]
    public class CellIndex
    {
        [XmlAttribute("x")]
        public int X { get; set; }
        [XmlAttribute("y")]
        public int Y { get; set; }
        [XmlAttribute("value")]
        public double Value { get; set; }
    }
}
