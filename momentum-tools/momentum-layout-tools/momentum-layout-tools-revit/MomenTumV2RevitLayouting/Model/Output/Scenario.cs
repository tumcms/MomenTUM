using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Schema;
using System.Xml.Serialization;

namespace MomenTumV2RevitLayouting.Model.Output
{
    [Serializable()]
    public class Scenario
    {
        [XmlAttribute("id")]
        public int Id { get; set; }
        [XmlAttribute("name")]
        public string Name { get; set; }
        [XmlAttribute("maxX")]
        public double MaxX { get; set; }
        [XmlAttribute("maxY")]
        public double MaxY { get; set; }
        [XmlAttribute("minX")]
        public double MinX { get; set; }
        [XmlAttribute("minY")]
        public double MinY { get; set; }

        [XmlElement("area", Form = XmlSchemaForm.Unqualified)]
        public Area[] areas { get; set; }
        [XmlElement("obstacle", Form = XmlSchemaForm.Unqualified)]
        public Obstacle[] obstacles { get; set; }
    }
}
