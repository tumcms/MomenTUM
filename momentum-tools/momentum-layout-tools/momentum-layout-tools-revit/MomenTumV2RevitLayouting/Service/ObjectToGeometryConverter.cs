using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Architecture;
using Autodesk.Revit.UI;
using MomenTumV2RevitLayouting.Model;
using MomenTumV2RevitLayouting.Service;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting
{
    public class ObjectToGeometryConverter
    {
        private static readonly double MEASUREMENTUNITFACTOR = 1;
        private static readonly double TOLERANCE = MEASUREMENTUNITFACTOR * 0.5;

        public List<Geometry2D> GetWallsOfRoomAsGeometry2D(UIDocument document, Room room)
        {
            List<Geometry2D> allWalls = new List<Geometry2D>();
            SpatialElementBoundaryOptions option = new SpatialElementBoundaryOptions();
            option.SpatialElementBoundaryLocation = SpatialElementBoundaryLocation.Finish;
            IList<IList<BoundarySegment>> boundariesList = room.GetBoundarySegments(option);
            string roomName = room.Name;
            int boundaryIterator = 0;

            foreach (IList<BoundarySegment> currentBoundary in boundariesList)
            {
                List<XYZ> boundaryVertices = GetBoundaryVertices(currentBoundary);

                if (boundaryVertices.ElementAt(0).IsAlmostEqualTo(boundaryVertices.ElementAt(boundaryVertices.Count - 1)))
                {
                    allWalls.Add(RoomBoundaryToPolygon2D(document, boundaryVertices, currentBoundary, roomName + boundaryIterator.ToString(), MEASUREMENTUNITFACTOR));
                }
                else
                {
                    allWalls.AddRange(RoomBoundaryToSegment2D(document, boundaryVertices, roomName + boundaryIterator.ToString(), MEASUREMENTUNITFACTOR));
                }

                boundaryIterator++;
            }

            return allWalls;
        }

        private List<XYZ> GetBoundaryVertices(IList<BoundarySegment> boundary)
        {
            List<XYZ> boundaryVertices = new List<XYZ>();

            foreach (BoundarySegment segment in boundary)
            {
                Curve segmentCurve = segment.GetCurve();
                XYZ segmentsStart = segmentCurve.GetEndPoint(0);
                XYZ segmentsEnd = segmentCurve.GetEndPoint(1);
                boundaryVertices.Add(segmentsStart);
                boundaryVertices.Add(segmentsEnd);
            }

            return boundaryVertices;
        }

        private Polygon2D RoomBoundaryToPolygon2D(UIDocument document, List<XYZ> boundaryVertices, IList<BoundarySegment> currentBoundary, string definition, double measurementUnitFactor)
        {
            List<Vector2D> boundaryVertices2D = new List<Vector2D>();

            foreach (var currentVertex in boundaryVertices)
            {
                Vector2D vectorToAdd = GeometryFactory.CreateVector2D(ConvertFeetToMeters(replaceCommaWithDot(currentVertex.X)), ConvertFeetToMeters(replaceCommaWithDot(currentVertex.Y)), measurementUnitFactor);
                boundaryVertices2D.Add(vectorToAdd);
                GeometryFactory.SetMaxMinCoordinates(vectorToAdd);
            }

            return GeometryFactory.CreatePolygon2D(boundaryVertices2D.ToArray(), definition);
        }

        private List<Segment2D> RoomBoundaryToSegment2D(UIDocument document, List<XYZ> boundaryVertices, string definition, double measurementUnitFactor)
        {
            List<Segment2D> segmentsList = new List<Segment2D>();
            int segmentIterator = 0;

            for (int i = 0; i < boundaryVertices.Count - 1; i++)
            {
                XYZ currentStartVertex = boundaryVertices.ElementAt(i);
                XYZ currentEndVertex = boundaryVertices.ElementAt(i + 1);
                Vector2D newSegmentsStart = GeometryFactory.CreateVector2D(ConvertFeetToMeters(replaceCommaWithDot(currentStartVertex.X)), ConvertFeetToMeters(replaceCommaWithDot(currentStartVertex.Y)), measurementUnitFactor);
                Vector2D newSegmentsEnd = GeometryFactory.CreateVector2D(ConvertFeetToMeters(replaceCommaWithDot(currentEndVertex.X)), ConvertFeetToMeters(replaceCommaWithDot(currentEndVertex.Y)), measurementUnitFactor);

                if (newSegmentsStart.IsEqualTo(newSegmentsEnd))
                {
                    continue;
                }

                segmentsList.Add(GeometryFactory.CreateSegment2D(new Vector2D[] { newSegmentsStart, newSegmentsEnd }, definition + "_" + segmentIterator));
                segmentIterator++;
                GeometryFactory.SetMaxMinCoordinates(newSegmentsStart);
                GeometryFactory.SetMaxMinCoordinates(newSegmentsEnd);
            }

            return segmentsList;
        }

        private static readonly double METERS_IN_FEET = 0.3048;
        private double ConvertFeetToMeters(double feet)
        {
            return feet * METERS_IN_FEET;
        }

        private double replaceCommaWithDot(double oldDouble)
        {
            return Convert.ToDouble(oldDouble, CultureInfo.GetCultureInfoByIetfLanguageTag(MomenTumV2Layouting.LANGUAGE_TAG_US));
        }

        public Polygon2D OuterBoundaryPolygon { get; private set; } = null;
        public List<Segment2D> GetOuterBoundaryWall(List<Geometry2D> allWallsOfRoom)
        {
            foreach (Geometry2D currentWall in allWallsOfRoom)
            {
                if (currentWall.GetType() == typeof(Polygon2D))
                {
                    Polygon2D currentWallAsPolygon2D = currentWall as Polygon2D;

                    if (allWallsOfRoom.Count == 1)
                    {
                        OuterBoundaryPolygon = currentWallAsPolygon2D;
                    }
                    else
                    {
                        if (GeoAdditionals.Polygon2DContainsAllOtherObjects(currentWallAsPolygon2D, allWallsOfRoom))
                        {
                            OuterBoundaryPolygon = currentWallAsPolygon2D;
                        }
                    }
                }

            }

            return GeometryFactory.DividePolygon2DintoSegment2D(OuterBoundaryPolygon);
        }

        public List<Segment2D> GetObstacleWalls(List<Geometry2D> allWallsOfRoom)
        {
            List<Segment2D> obstacleWalls = new List<Segment2D>();

            foreach (Geometry2D currentWall in allWallsOfRoom)
            {
                if (!currentWall.Equals(OuterBoundaryPolygon) && currentWall.GetType() == typeof(Segment2D))
                {
                    obstacleWalls.Add(currentWall as Segment2D);
                }
            }

            return obstacleWalls;
        }

        public List<Polygon2D> GetObstacleSolids(List<Geometry2D> allWallsOfRoom)
        {
            List<Polygon2D> solidObstacles = new List<Polygon2D>();

            foreach (Geometry2D currentWall in allWallsOfRoom)
            {
                if (!currentWall.Equals(OuterBoundaryPolygon) && currentWall.GetType() == typeof(Polygon2D))
                {
                    solidObstacles.Add(currentWall as Polygon2D);
                }
            }

            return solidObstacles;
        }

        private readonly double STAIRS_AREA_SHRINK_FACTOR = 0.9;
        private readonly double DEFAULT_AREA_RADIUS = 0.5;
        public Polygon2D GetStairAreaPolygon(UIDocument currentDocument, Stairs stair, string key)
        {
            ICollection<ElementId> allRunsIds = stair.GetStairsRuns();
            ElementId currentId = allRunsIds.ElementAt(0);

            if (key.Equals(RevitObjectManager.BASE_LEVEL_KEY))
            {
                foreach (ElementId currentBaseId in allRunsIds)
                {
                    StairsRun currentStairsRun = currentDocument.Document.GetElement(currentBaseId) as StairsRun;
                    StairsRun selectedStairsRun = currentDocument.Document.GetElement(currentId) as StairsRun;

                    if (currentStairsRun.BaseElevation < selectedStairsRun.BaseElevation)
                    {
                        currentId = currentBaseId;
                    }
                }
            }
            else if (key.Equals(RevitObjectManager.TOP_LEVEL_KEY))
            {
                foreach (ElementId currentTopId in allRunsIds)
                {
                    StairsRun currentStairsRun = currentDocument.Document.GetElement(currentTopId) as StairsRun;
                    StairsRun selectedStairsRun = currentDocument.Document.GetElement(currentId) as StairsRun;

                    if (currentStairsRun.TopElevation > selectedStairsRun.TopElevation)
                    {
                        currentId = currentTopId;
                    }
                }
            }

            List<Vector2D> areaNodes = new List<Vector2D>();
            StairsRun finalStairsRun = currentDocument.Document.GetElement(currentId) as StairsRun;
            CurveLoop stairPath = finalStairsRun.GetStairsPath();
            Curve firstStairPathCurve;

            if (stairPath.Count() > 1)
            {
                if ((finalStairsRun.StairsRunStyle.Equals(StairsRunStyle.Winder) || finalStairsRun.StairsRunStyle.Equals(StairsRunStyle.Spiral))
                    && key.Equals(RevitObjectManager.TOP_LEVEL_KEY))
                {
                    firstStairPathCurve = stairPath.ElementAt(stairPath.Count() - 1);
                }
                else
                {
                    firstStairPathCurve = stairPath.ElementAt(0);
                }
            }
            else
            {
                firstStairPathCurve = stairPath.ElementAt(0);
            }

            double stairsRunsWidth = ConvertFeetToMeters(finalStairsRun.ActualRunWidth * STAIRS_AREA_SHRINK_FACTOR);

            if (stairsRunsWidth < DEFAULT_AREA_RADIUS)
            {
                stairsRunsWidth = DEFAULT_AREA_RADIUS;
            }

            double pathsLength = ConvertFeetToMeters(firstStairPathCurve.Length * STAIRS_AREA_SHRINK_FACTOR);

            if (pathsLength < DEFAULT_AREA_RADIUS)
            {
                pathsLength = DEFAULT_AREA_RADIUS;
            }

            Vector2D pathsStart = GeometryFactory.CreateVector2D(ConvertFeetToMeters(firstStairPathCurve.GetEndPoint(0).X), ConvertFeetToMeters(firstStairPathCurve.GetEndPoint(0).Y), MEASUREMENTUNITFACTOR);
            Vector2D pathsEnd = GeometryFactory.CreateVector2D(ConvertFeetToMeters(firstStairPathCurve.GetEndPoint(1).X), ConvertFeetToMeters(firstStairPathCurve.GetEndPoint(1).Y), MEASUREMENTUNITFACTOR);
            Vector2D pathDirection = pathsEnd.Difference(pathsStart);
            Vector2D pathDirectionPerpenticular = pathDirection.Rotate((-1) * Math.PI / 2).GetAsNormalized();

            Vector2D firstNode = pathsStart.Add(pathDirectionPerpenticular.Multiply(stairsRunsWidth / 2));
            areaNodes.Add(firstNode);
            Vector2D pointToAdd = firstNode.Add(pathDirection);
            areaNodes.Add(pointToAdd);
            pointToAdd = pointToAdd.Add(pathDirectionPerpenticular.Multiply((-1) * stairsRunsWidth));
            areaNodes.Add(pointToAdd);
            pointToAdd = pointToAdd.Add(pathDirection.Multiply(-1));
            areaNodes.Add(pointToAdd);
            areaNodes.Add(firstNode);

            Polygon2D areaPolygon = GeometryFactory.CreatePolygon2D(areaNodes.ToArray(), finalStairsRun.Name);

            return areaPolygon;
        }

        private readonly string DOUBLE_SWING_CONTENT1 = "2-flg";
        private readonly string DOUBLE_SWING_CONTENT2 = "Double";
        private readonly string DOUBLE_SWING_CONTENT3 = "double";
        private readonly double DEFAULT_SINGLE_DOOR_WIDTH = 0.885;
        private readonly double DEFAULT_DOUBLE_DOOR_WIDTH = 1.885;
        public List<Geometry2D> CreateDoorSegments(UIDocument currentDocument, List<FamilyInstance> doors)
        {
            List<Geometry2D> convertedDoors = new List<Geometry2D>();

            foreach (FamilyInstance currentDoor in doors)
            {
                string doorName = currentDoor.Name;
                Wall pacedWall = currentDoor.Host as Wall;
                Curve pacedWallCurve = (pacedWall.Location as LocationCurve).Curve;
                Vector2D wallStartPoint = GeometryFactory.CreateVector2D(ConvertFeetToMeters(pacedWallCurve.GetEndPoint(0).X), ConvertFeetToMeters(pacedWallCurve.GetEndPoint(0).Y), MEASUREMENTUNITFACTOR);
                Vector2D wallEndPoint = GeometryFactory.CreateVector2D(ConvertFeetToMeters(pacedWallCurve.GetEndPoint(1).X), ConvertFeetToMeters(pacedWallCurve.GetEndPoint(1).Y), MEASUREMENTUNITFACTOR);
                Vector2D wallDirection = wallEndPoint.Difference(wallStartPoint).GetAsNormalized();

                double doorThickness = ConvertFeetToMeters(pacedWall.WallType.Width);
                double doorWidth = ConvertFeetToMeters(currentDoor.Symbol.get_Parameter(BuiltInParameter.DOOR_WIDTH).AsDouble());

                if (doorWidth == 0)
                {
                    string familyName = currentDoor.Symbol.FamilyName;

                    if (familyName.Contains(DOUBLE_SWING_CONTENT1) || familyName.Contains(DOUBLE_SWING_CONTENT2) || familyName.Contains(DOUBLE_SWING_CONTENT3))
                    {
                        doorWidth = DEFAULT_DOUBLE_DOOR_WIDTH;
                    }
                    else
                    {
                        doorWidth = DEFAULT_SINGLE_DOOR_WIDTH;
                    }
                }

                LocationPoint doorLocationPoint = currentDoor.Location as LocationPoint;
                XYZ doorLocation = doorLocationPoint.Point;
                Vector2D doorLocation2D = GeometryFactory.CreateVector2D(ConvertFeetToMeters(doorLocation.X), ConvertFeetToMeters(doorLocation.Y), MEASUREMENTUNITFACTOR);
                Vector2D doorMidSegmentStart = doorLocation2D.Add(wallDirection.Multiply(doorWidth / 2));
                Vector2D doorMidSegmentEnd = doorLocation2D.Add(wallDirection.Negate().Multiply(doorWidth / 2));

                Vector2D doorPacingDirection = wallDirection.Rotate(Math.PI / 2);
                Vector2D firstWallConnectingStart = doorMidSegmentEnd.Add(doorPacingDirection.Multiply(doorThickness / 2));
                Vector2D firstWallConnectingEnd = doorMidSegmentEnd.Add(doorPacingDirection.Negate().Multiply(doorThickness / 2));
                Segment2D firstWallConnectingSegment = GeometryFactory.CreateSegment2D(new Vector2D[] { firstWallConnectingStart, firstWallConnectingEnd }, doorName);
                convertedDoors.Add(firstWallConnectingSegment);
                Vector2D secondWallConnectingStart = doorMidSegmentStart.Add(doorPacingDirection.Multiply(doorThickness / 2));
                Vector2D secondWallConnectingEnd = doorMidSegmentStart.Add(doorPacingDirection.Negate().Multiply(doorThickness / 2));
                Segment2D secondWallConnectingSegment = GeometryFactory.CreateSegment2D(new Vector2D[] { secondWallConnectingStart, secondWallConnectingEnd }, doorName);
                convertedDoors.Add(secondWallConnectingSegment);

                Vector2D leftDoorSegmentStart = doorMidSegmentStart.Add(doorPacingDirection.Multiply(doorThickness / 2));
                Vector2D leftDoorSegmentEnd = doorMidSegmentEnd.Add(doorPacingDirection.Multiply(doorThickness / 2));
                Segment2D leftDoorSegment = GeometryFactory.CreateSegment2D(new Vector2D[] { leftDoorSegmentStart, leftDoorSegmentEnd }, doorName);
                Vector2D rightDoorSegmentStart = doorMidSegmentStart.Add(doorPacingDirection.Negate().Multiply(doorThickness / 2));
                Vector2D rightDoorSegmentEnd = doorMidSegmentEnd.Add(doorPacingDirection.Negate().Multiply(doorThickness / 2));
                Segment2D rightDoorSegment = GeometryFactory.CreateSegment2D(new Vector2D[] { rightDoorSegmentStart, rightDoorSegmentEnd }, doorName);

                if (currentDoor.FromRoom == null || currentDoor.ToRoom == null)
                {
                    List<Vector2D> doorAreaPoints = new List<Vector2D>();

                    if (pacedWall.Flipped)
                    {
                        leftDoorSegment.Name = null;

                        Vector2D pointToAdd = doorMidSegmentEnd.Add(doorPacingDirection.Multiply(DEFAULT_AREA_RADIUS / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentStart.Add(doorPacingDirection.Multiply(DEFAULT_AREA_RADIUS / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentStart.Add(doorPacingDirection.Negate().Multiply(doorThickness / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentEnd.Add(doorPacingDirection.Negate().Multiply(doorThickness / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentEnd.Add(doorPacingDirection.Multiply(DEFAULT_AREA_RADIUS / 2));
                        doorAreaPoints.Add(pointToAdd);
                    }
                    else
                    {
                        rightDoorSegment.Name = null;

                        Vector2D pointToAdd = doorMidSegmentEnd.Add(doorPacingDirection.Multiply(doorThickness / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentStart.Add(doorPacingDirection.Multiply(doorThickness / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentStart.Add(doorPacingDirection.Negate().Multiply(DEFAULT_AREA_RADIUS / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentEnd.Add(doorPacingDirection.Negate().Multiply(DEFAULT_AREA_RADIUS / 2));
                        doorAreaPoints.Add(pointToAdd);
                        pointToAdd = doorMidSegmentEnd.Add(doorPacingDirection.Multiply(doorThickness / 2));
                        doorAreaPoints.Add(pointToAdd);
                    }

                    Polygon2D exitDoor = GeometryFactory.CreatePolygon2D(doorAreaPoints.ToArray(), doorName);
                    convertedDoors.Add(exitDoor);
                }
                else
                {
                    leftDoorSegment.Name = null;
                    rightDoorSegment.Name = null;
                }

                convertedDoors.Add(leftDoorSegment);
                convertedDoors.Add(rightDoorSegment);
            }

            return convertedDoors;
        }

        public List<Segment2D> RemoveDotWalls(List<Segment2D> allWalls)
        {
            List<Segment2D> filteredWalls = new List<Segment2D>();

            foreach (Segment2D currentWall in allWalls)
            {
                Vector2D[] currentWallsVertices = currentWall.GetVertices();
                Vector2D currentWallsStart = currentWallsVertices[0];
                Vector2D currentWallsEnd = currentWallsVertices[1];

                if (!currentWallsStart.IsEqualTo(currentWallsEnd))
                {
                    filteredWalls.Add(currentWall);
                }
            }

            return filteredWalls;
        }

        public List<Segment2D> CreateDoorOpenings(List<Segment2D> allDoorSegments, List<Segment2D> allWalls)
        {
            allWalls = RemoveDotWalls(allWalls);
            List<Segment2D> doorSegmentsToCutOut = new List<Segment2D>();
            List<Segment2D> doorSegmentsToKeep = new List<Segment2D>();

            foreach (Segment2D currentDoorSegment in allDoorSegments)
            {
                if (currentDoorSegment.Name == null)
                {
                    allWalls = CutOutDoor(currentDoorSegment, allWalls);
                }
                else
                {
                    doorSegmentsToKeep.Add(currentDoorSegment);
                }
            }

            allWalls.AddRange(doorSegmentsToKeep);
            return allWalls;
        }

        private List<Segment2D> CutOutDoor(Segment2D currentDoor, List<Segment2D> allWalls)
        {
            var listArray = new List<List<Segment2D>>();
            var newWallList = new List<Segment2D>();
            var wallsToRemove = new List<Segment2D>();
            Vector2D[] doorVertices = currentDoor.GetVertices();
            Vector2D doorVectorStart = doorVertices[0];
            Vector2D doorVectorEnd = doorVertices[1];
            Vector2D doorVector = doorVectorEnd.Difference(doorVectorStart).GetAsNormalized();

            foreach (Segment2D currentWall in allWalls)
            {
                Vector2D[] wallVertices = currentWall.GetVertices();
                Vector2D wallVectorStart = wallVertices[0];
                Vector2D wallVectorEnd = wallVertices[1];
                Vector2D wallVector = wallVectorEnd.Difference(wallVectorStart).GetAsNormalized();

                if (wallVectorStart.IsEqualTo(wallVectorEnd))
                {
                    wallsToRemove.Add(currentWall);
                    continue;
                }
                else if (GeoAdditionals.SegmentsLieOnTopOfEachOther(currentDoor, currentWall))
                {
                    double cos = wallVector.Dot(doorVector) / (wallVector.normalize() * doorVector.normalize());

                    if (cos < 0)
                    {
                        doorVectorStart = doorVertices[1];
                        doorVectorEnd = doorVertices[0];
                    }

                    if (!wallVectorStart.IsEqualTo(doorVectorStart))
                    {
                        Segment2D wallToDoor = GeometryFactory.CreateSegment2D(new Vector2D[] { wallVectorStart, doorVectorStart }, currentWall.Name);
                        newWallList.Add(wallToDoor);
                    }
                    if (!doorVectorEnd.IsEqualTo(wallVectorEnd))
                    {
                        Segment2D wallFromDoor = GeometryFactory.CreateSegment2D(new Vector2D[] { doorVectorEnd, wallVectorEnd }, currentWall.Name);
                        newWallList.Add(wallFromDoor);
                    }

                    wallsToRemove.Add(currentWall);
                    continue;
                }
                else
                {
                    continue;
                }
            }
            foreach (Segment2D wallToRemove in wallsToRemove)
            {
                allWalls.Remove(wallToRemove);
            }

            allWalls.AddRange(newWallList);
            return allWalls;
        }
        public static readonly string WALL_OBJECT_NAME = "Wall";
        public static readonly string SOLID_OBJECT_NAME = "Solid";
        public static readonly string ORIGIN_OBJECT_NAME = "Origin";
        public static readonly string DESTINATION_OBJECT_NAME = "Destination";

        private int _wallCounter = 0;
        private int _solidCounter = 0;
        private int _originCounter = 0;
        private int _destinationCounter = 0;

        public void Reset() {
            _wallCounter = 0;
            _solidCounter = 0;
            _originCounter = 0;
            _destinationCounter = 0;
        }

        public List<Segment2D> SetCorrectWallNames(List<Segment2D> walls)
        {
            foreach (Segment2D currentWall in walls)
            {
                currentWall.Name = WALL_OBJECT_NAME + _wallCounter.ToString();
                _wallCounter++;
            }

            return walls;
        }

        public List<Polygon2D> SetCorrectPolygonNames(List<Polygon2D> polygons, string definition)
        {
            foreach (Polygon2D currentPolygon in polygons)
            {
                if (definition.Equals(SOLID_OBJECT_NAME))
                {
                    currentPolygon.Name = SOLID_OBJECT_NAME + _solidCounter.ToString();
                    _solidCounter++;
                }
                else if (definition.Equals(ORIGIN_OBJECT_NAME))
                {
                    currentPolygon.Name = ORIGIN_OBJECT_NAME + _originCounter.ToString();
                    _originCounter++;
                }
                else if (definition.Equals(DESTINATION_OBJECT_NAME))
                {
                    currentPolygon.Name = DESTINATION_OBJECT_NAME + _destinationCounter;
                    _destinationCounter++;
                }
            }

            return polygons;
        }
    }
}
