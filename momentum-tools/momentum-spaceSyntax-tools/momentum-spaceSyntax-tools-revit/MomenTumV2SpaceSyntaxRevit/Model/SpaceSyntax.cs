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
        private int _domainRows;
        [XmlAttribute("domainRows")]
        public int DomainRows
        {
            set { _domainRows = value - 2; }
            get { return _domainRows; }
        }

        private int _domainColumns;
        [XmlAttribute("domainColumns")]
        public int DomainColumns
        {
            set { _domainColumns = value - 2; }
            get { return _domainColumns; }
        }

        // convert meters back to feet
        private double _maxX;
        [XmlAttribute("maxX")]
        public double MaxX
        {
            set { _maxX = value / _feetInMeter; }
            get { return _maxX; }
        }

        private double _minX;
        [XmlAttribute("minX")]
        public double MinX
        {
            set { _minX = value / _feetInMeter; }
            get { return _minX; }
        }

        private double _maxY;
        [XmlAttribute("maxY")]
        public double MaxY
        {
            set { _maxY = value / _feetInMeter; }
            get { return _maxY; }
        }

        private double _minY;
        [XmlAttribute("minY")]
        public double MinY
        {
            set { _minY = value / _feetInMeter; }
            get { return _minY; }
        }

        [XmlAttribute("minValue")]
        public double MinValue { get; set; }

        [XmlAttribute("maxValue")]
        public double MaxValue { get; set; }

        [XmlAttribute("scenarioName")]
        public string ScenarioName { get; set; }
    }
}
