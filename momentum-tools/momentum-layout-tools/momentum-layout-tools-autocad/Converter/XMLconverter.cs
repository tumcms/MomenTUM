
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2CadLayouting
{
    interface XMLconverter
    {
        void convertGivenListsToXML(List<Geometry.Polygon2D> originList, 
            List<Geometry.Polygon2D> intermediateList,
            List<Geometry.Segment2D> gatheringLineList,
            List<Geometry.Polygon2D> destinationList,
            List<Geometry.Polygon2D> avoidanceList,
            List<Geometry.Segment2D> wallList, 
            List<Geometry.Polygon2D> solidList,
            List<Geometry.Segment2D> graphList,
            Dictionary<String, List<Geometry.Polygon2D>> taggedAreaList);

        void convertXMLtoDrawing();
    }
}
