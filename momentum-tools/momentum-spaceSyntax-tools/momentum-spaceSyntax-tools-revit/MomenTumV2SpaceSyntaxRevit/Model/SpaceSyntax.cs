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
        private const double _feetInMeter = 0.3048;

        [XmlElement("CellIndex", Form = XmlSchemaForm.Unqualified)]
        public CellIndex[] CellIndices { get; set; }

        [XmlAttribute("name")]
        public string Name { get; set; }

        [XmlAttribute("id")]
        public int Id { get; set; }

        // When reading in layouts from the Layouting Plugin, there will always be at least
        // two additional rows and columns because the outermost layers are always walls or do 
        // not have a value. 
        [XmlAttribute("domainRows")]
        public int DomainRows { get { return DomainRows; } set { value -= 2; } }

        [XmlAttribute("domainColumns")]
        public int DomainColumns { get { return DomainColumns; } set { value -= 2; } }

        // convert meters back to feet
        [XmlAttribute("maxX")]
        public double MaxX { get { return MaxX; } set { value /= _feetInMeter; } }

        [XmlAttribute("minX")]
        public double MinX { get { return MinX; } set { value /= _feetInMeter; } }

        [XmlAttribute("maxY")]
        public double MaxY { get { return MaxY; } set { value /= _feetInMeter; } }

        [XmlAttribute("minY")]
        public double MinY { get { return MinY; } set { value /= _feetInMeter; } }

        [XmlAttribute("minValue")]
        public double MinValue { get; set; }

        [XmlAttribute("maxValue")]
        public double MaxValue { get; set; }
    }
}
