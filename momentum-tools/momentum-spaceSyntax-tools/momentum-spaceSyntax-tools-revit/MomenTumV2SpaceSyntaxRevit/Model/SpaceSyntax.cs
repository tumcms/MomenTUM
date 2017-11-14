using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;
using System.Xml.Schema;

namespace MomenTumV2SpaceSyntaxRevit.Model
{
    [Serializable()]
    [XmlType(AnonymousType = true)]
    [XmlRoot(Namespace = "", IsNullable = false)]
    public class SpaceSyntax
    {
        [XmlElement("CellIndex", Form = XmlSchemaForm.Unqualified)]
        public CellIndex[] CellIndices { get; set; }

        [XmlAttribute("name")]
        public string Name { get; set; }

        [XmlAttribute("id")]
        public int Id { get; set; }

        [XmlAttribute("domainRows")]
        public int DomainRows { get; set; }

        [XmlAttribute("domainColumns")]
        public int DomainColumns { get; set; }

        [XmlAttribute("maxX")]
        public double MaxX { get; set; }

        [XmlAttribute("minX")]
        public double MinX { get; set; }

        [XmlAttribute("maxY")]
        public double MaxY { get; set; }

        [XmlAttribute("minY")]
        public double MinY { get; set; }

        [XmlAttribute("minValue")]
        public double MinValue { get; set; }

        [XmlAttribute("maxValue")]
        public double MaxValue { get; set; }
    }
}
