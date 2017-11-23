using Autodesk.Revit.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Autodesk.Revit.UI;
using Autodesk.Revit.ApplicationServices;

namespace MomenTumV2SpaceSyntaxRevit.Service
{
    public class RevitUtils
    {
        public static KeyValuePair<Result, Level> AttemptToGetLevelByScenarioName(Document doc, string levelname)
        {
            FilteredElementCollector levelCollector = new FilteredElementCollector(doc);
            ICollection<Element> levelCollection = levelCollector.OfClass(typeof(Level)).ToElements();
            
            foreach (Element element in levelCollection)
            {
                Level level = element as Level;
                if (level != null && level.Name.Equals(levelname))
                {
                    return new KeyValuePair<Result, Level>(Result.Succeeded, level);
                }
            }

            return new KeyValuePair<Result, Level>(Result.Failed, null);
        }

        public static KeyValuePair<Result, PlanarFace> GetTopFaceFromLevel(Application app, Level level)
        {
            var allFloorsForLevel = GetAllFloorsFromSelectedLevel(level);
            var allFaces = CollectAllFacesFromAllFloors(app, allFloorsForLevel);

            if (allFaces.Count == 0)
            {
                PromtService.ShowErrorToUser("No Surfaces for visualization found!");
                return new KeyValuePair<Result, PlanarFace>(Result.Failed, null);
            }

            // For now we assume: We select the both Faces with the biggest Area 
            // from the floors (which is assumed to be top and bottom face of the same floor element)
            var topAndBottomFaces = FilterTwoFacesWithBiggestArea(allFaces);
            var topFace = FilterTopFace(topAndBottomFaces);

            if (topFace == null)
            {
                PromtService.ShowInformationToUser("The top of the surface could not be determined. Visualization failed.");
                return new KeyValuePair<Result, PlanarFace>(Result.Failed, null);
            }

            return new KeyValuePair<Result, PlanarFace>(Result.Succeeded, topFace); ;
        }

        private static PlanarFace FilterTopFace(IList<Face> topAndBottomFaces)
        {
            PlanarFace topFace = null;
            foreach (var face in topAndBottomFaces)
            {
                PlanarFace planarFace = face as PlanarFace;
                if (planarFace != null && IsHorizontal(planarFace))
                {
                    if (topFace == null || topFace.Origin.Z < planarFace.Origin.Z)
                    {
                        topFace = planarFace;
                    }
                }
            }

            return topFace;
        }

        private const double _eps = 1.0e-9; 
        private static bool IsHorizontal(PlanarFace planarFace)
        {
            XYZ faceNormal = planarFace.FaceNormal;
            // a plane is horizontal when the normal is vertical <=> normal.X == 0 and normal.Y == 0
            double x = faceNormal.X;
            double y = faceNormal.Y;
            
            // Revit sometimes has rounding issues and stores values like 0.000000001 or similar
            return _eps > Math.Abs(x)  && _eps > Math.Abs(y);
        }

        private static IList<Face> FilterTwoFacesWithBiggestArea(IList<Face> allFaces)
        {
            var topAndBottomFace = new List<Face>();

            Face firstFaceWithMaxArea = allFaces.OrderByDescending(face => face.Area).First();
            allFaces.Remove(firstFaceWithMaxArea);
            Face secondFaceWithMaxArea = allFaces.OrderByDescending(face => face.Area).First();

            topAndBottomFace.Add(firstFaceWithMaxArea);
            topAndBottomFace.Add(secondFaceWithMaxArea);

            return topAndBottomFace;
        }
        
        private static IList<Floor> GetAllFloorsFromSelectedLevel(Level selectedLevel)
        {
            var floors = new List<Floor>();
            var floorsOnLevel = new FilteredElementCollector(selectedLevel.Document).OfClass(typeof(Floor)).ToElements();

            foreach (var spatialElement in floorsOnLevel)
            {
                Floor floor = spatialElement as Floor;
                if (floor != null && selectedLevel.Id.Equals(floor.LevelId))
                {
                    floors.Add(floor);
                }
            }

            return floors;
        }

        public static IList<Face> CollectAllFacesFromAllFloors(Application app, IList<Floor> floors)
        {
            var geometryOptions = app.Create.NewGeometryOptions();
            geometryOptions.ComputeReferences = true;
            geometryOptions.DetailLevel = ViewDetailLevel.Fine;

            var facesList = new List<Face>();
            foreach (var floor in floors)
            {
                GeometryElement geometryElement = floor.get_Geometry(geometryOptions);

                foreach (GeometryObject geoObject in geometryElement)
                {
                    Solid geoSolid = geoObject as Solid;
                    if (geoSolid != null)
                    {
                        foreach (Face face in geoSolid.Faces)
                        {
                            facesList.Add(face);
                        }
                    }
                }
            }

            return facesList;
        }
    }
}
