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
    [XmlType(AnonymousType = true)]
    public class Obstacle
    {
        [XmlAttribute("id")]
        public int Id { get; set; }
        [XmlAttribute("name")]
        public string Name { get; set; }
        [XmlAttribute("type")]
        public string Type { get; set; }

        [XmlElement("point", Form = XmlSchemaForm.Unqualified)]
        public Point[] Points { get; set; }
    }
}
