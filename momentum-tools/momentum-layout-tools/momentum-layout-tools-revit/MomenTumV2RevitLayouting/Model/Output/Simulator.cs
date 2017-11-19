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
    [XmlType(AnonymousType = true, Namespace = "")]
    [XmlRoot(ElementName ="simulator", Namespace = "")]
    public class Simulator
    {
        [XmlAttribute("version")]
        public string Version { get; set; }
        [XmlAttribute("name")]
        public string Name { get; set; }
        
        [XmlElement("layouts")]
        public Layout Layout { get; set; }
    }
}
