using Autodesk.Revit.Attributes;
using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Architecture;
using Autodesk.Revit.UI;
using System.Collections.Generic;
using System.Threading;
using System.Globalization;
using MomenTumV2RevitLayouting;
using MomenTumV2RevitLayouting.Model;
using MomenTumV2RevitLayouting.Service;

[TransactionAttribute(TransactionMode.Manual)]
[RegenerationAttribute(RegenerationOption.Manual)]
public class MomenTumV2Layouting : IExternalCommand
{
    public static readonly string LANGUAGE_TAG_US = "en-US";

    public Result Execute(ExternalCommandData commandData, ref string message, ElementSet elements)
    {
        Thread.CurrentThread.CurrentUICulture = CultureInfo.GetCultureInfo(LANGUAGE_TAG_US);
        Thread.CurrentThread.CurrentCulture = CultureInfo.GetCultureInfo(LANGUAGE_TAG_US);

        UIApplication uiApp = commandData.Application;
        UIDocument uiDoc = commandData.Application.ActiveUIDocument;
        Document doc = commandData.Application.ActiveUIDocument.Document;

        var rom = new RevitObjectManager();
        var ogc = new ObjectToGeometryConverter();

        List<Level> allLevels = UserInteractionService.LetUserPickLevels(doc, rom);

        if (allLevels.Count == 0)
        {

            TaskDialog.Show("Plugin abort", "User did not select any levels, Plugin aborts.");
            return Result.Cancelled;
        }

        List<Scenario> allScenarios = new List<Scenario>();
        List<Stairs> allStairs = rom.GetAllStairs(doc);

        foreach (Level level in allLevels)
        {
            GeometryFactory.Reset();
            ogc.Reset();

            List<Room> rooms = rom.GetAllRoomsFromLevel(level);

            if (!rom.RoomsOnCurrentLevelPlaced(rooms))
            {
                continue;
            }

            List<FamilyInstance> doors = rom.GetAllDoorsFromLevel(level);
            List<KeyValuePair<string, List<Stairs>>> stairs = rom.GetAllStairsFromLevel(uiDoc, allStairs, level);

            var originList = new List<Polygon2D>();
            var intermediateList = new List<Polygon2D>(); // unused... probably for stairs that pass through a level
            var destinationList = new List<Polygon2D>();
            var gatheringLineList = new List<Segment2D>();
            var wallList = new List<Segment2D>();
            var solidList = new List<Polygon2D>();
            var roomBoundaryPolygons = new List<Polygon2D>();

            // assemble list of all walls and the most outer boundary wall
            foreach (Room currentRoom in rooms)
            {
                if (currentRoom.Location != null)
                {
                    List<Geometry2D> allWallsOfRoom = ogc.GetWallsOfRoomAsGeometry2D(uiDoc, currentRoom);
                    wallList.AddRange(ogc.GetOuterBoundaryWall(allWallsOfRoom));
                    roomBoundaryPolygons.Add(ogc.OuterBoundaryPolygon);// also adds nulls... and duplicates?!
                    wallList.AddRange(ogc.GetObstacleWalls(allWallsOfRoom));
                    solidList.AddRange(ogc.GetObstacleSolids(allWallsOfRoom));
                }
            }

            foreach (var currentPair in stairs)
            {
                List<Polygon2D> allStairAreas = new List<Polygon2D>();

                if (currentPair.Key.Equals(RevitObjectManager.BASE_LEVEL_KEY))
                {
                    foreach (Stairs currentStairs in currentPair.Value)
                    {
                        Polygon2D stairArea = ogc.GetStairAreaPolygon(uiDoc, currentStairs, RevitObjectManager.BASE_LEVEL_KEY);

                        if (GeoAdditionals.ConvertedRoomsContainStairArea(roomBoundaryPolygons, stairArea))
                        {
                            allStairAreas.Add(stairArea);
                        }
                    }
                }
                else if (currentPair.Key.Equals(RevitObjectManager.TOP_LEVEL_KEY))
                {
                    foreach (Stairs currentStairs in currentPair.Value)
                    {
                        Polygon2D stairArea = ogc.GetStairAreaPolygon(uiDoc, currentStairs, RevitObjectManager.TOP_LEVEL_KEY);

                        if (GeoAdditionals.ConvertedRoomsContainStairArea(roomBoundaryPolygons, stairArea))
                        {
                            allStairAreas.Add(stairArea);
                        }
                    }
                }

                originList.AddRange(allStairAreas);
                destinationList.AddRange(allStairAreas);
            }

            List<Geometry2D> convertedDoors = ogc.CreateDoorSegments(uiDoc, doors);
            List<Segment2D> doorSegments = new List<Segment2D>();

            foreach (Geometry2D currentDoor in convertedDoors)
            {
                if (currentDoor.GetType() == typeof(Segment2D))
                {
                    doorSegments.Add((Segment2D)currentDoor);
                }
                else if (currentDoor.GetType() == typeof(Polygon2D))
                {
                    originList.Add((Polygon2D)currentDoor);
                    destinationList.Add((Polygon2D)currentDoor);
                }
            }

            wallList = ogc.RemoveDotWalls(wallList);
            wallList = ogc.CreateDoorOpenings(doorSegments, wallList);

            wallList = ogc.SetCorrectWallNames(wallList);
            solidList = ogc.SetCorrectPolygonNames(solidList, ObjectToGeometryConverter.SOLID_OBJECT_NAME);
            originList = ogc.SetCorrectPolygonNames(originList, ObjectToGeometryConverter.ORIGIN_OBJECT_NAME);
            destinationList = ogc.SetCorrectPolygonNames(destinationList, ObjectToGeometryConverter.DESTINATION_OBJECT_NAME);

            double[] minCoords = GeometryFactory.GetMinimumCoordinates();
            double[] maxCoords = GeometryFactory.GetMaximumCoordinates();
            Scenario currentScenario = new Scenario(level.Name, minCoords, maxCoords, originList, intermediateList, gatheringLineList, destinationList, wallList, solidList);
            allScenarios.Add(currentScenario);
        }

        var converter = new OutputModelConverter();
        var simulatorOutput = converter.ToSimulator(allScenarios);

        var saveFileService = new UserInteractionService();
        saveFileService.SaveSimulatorToXml(simulatorOutput);

        return Result.Succeeded;
    }
}

