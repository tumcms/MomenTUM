using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting.Model
{
    public class Scenario
    {
        public string Name { get; private set; }
        public double[] MinCoordinates { get; private set; }
        public double[] MaxCoordinates { get; private set; }
        public List<Polygon2D> OriginList { get; private set; }
        public List<Polygon2D> IntermediateList { get; private set; }
        public List<Segment2D> GatheringLineList { get; private set; }
        public List<Polygon2D> DestinationList { get; private set; }
        public List<Segment2D> WallList { get; private set; }
        public List<Polygon2D> SolidList { get; private set; }

        public Scenario(
            string name,
            double[] minCoordinates,
            double[] maxCoordinates,
            List<Polygon2D> originList,
            List<Polygon2D> intermediateList,
            List<Segment2D> gatheringLineList,
            List<Polygon2D> destinationList,
            List<Segment2D> wallList,
            List<Polygon2D> solidList
            )
        {
            Name = name;
            MinCoordinates = minCoordinates;
            MaxCoordinates = maxCoordinates;
            OriginList = originList;
            IntermediateList = intermediateList;
            GatheringLineList = gatheringLineList;
            DestinationList = destinationList;
            WallList = wallList;
            SolidList = solidList;
        }
    }
}
