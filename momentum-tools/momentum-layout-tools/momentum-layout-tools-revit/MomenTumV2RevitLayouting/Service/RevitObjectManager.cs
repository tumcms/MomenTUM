using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Architecture;
using Autodesk.Revit.UI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2RevitLayouting
{
    public class RevitObjectManager
    {
        public static readonly string BASE_LEVEL_KEY = "base";
        public static readonly string TOP_LEVEL_KEY = "top";
        public RevitObjectManager() { }

        public List<Level> GetAllLevels(Document doc)
        {
            var levelList = new List<Level>();
            var allLevels = new FilteredElementCollector(doc).OfClass(typeof(Level)).ToElements();

            foreach (Level level in allLevels)
            {
                levelList.Add(doc.GetElement(level.Id) as Level);
            }

            return levelList;
        }

        public List<Room> GetAllRoomsFromLevel(Level level)
        {
            var roomList = new List<Room>();
            var allSpatialElements = new FilteredElementCollector(level.Document).OfClass(typeof(SpatialElement)).ToElements();

            foreach (Element element in allSpatialElements)
            {
                Room room = element as Room;
                if (room != null && room.Level.Id.Equals(level.Id))
                {
                    roomList.Add(room);
                }
            }

            return roomList;
        }

        public bool RoomsOnCurrentLevelPlaced(List<Room> roomsOnLevel)
        {
            foreach (Room currentRoom in roomsOnLevel)
                if (currentRoom.Location != null)
                    return true;
            
            return false;
        }

        public List<FamilyInstance> GetAllDoorsFromLevel(Level level)
        {
            var doorList = new List<FamilyInstance>();
            var allFamilyInstances = new FilteredElementCollector(level.Document).OfCategory(BuiltInCategory.OST_Doors).OfClass(typeof(FamilyInstance));

            foreach (FamilyInstance currentDoor in allFamilyInstances)
            {
                if (!(currentDoor.FromRoom == null && currentDoor.ToRoom == null) && currentDoor.LevelId.Equals(level.Id))
                {
                    doorList.Add(currentDoor);
                }
            }

            return doorList;
        }

        public List<Stairs> GetAllStairs(Document currentDocument)
        {
            List<Stairs> allStairs = new List<Stairs>();
            FilteredElementCollector stairsRunCollector = new FilteredElementCollector(currentDocument).OfCategory(BuiltInCategory.OST_Stairs).OfClass(typeof(Stairs));

            foreach (Stairs currentStairsRun in stairsRunCollector)
            {
                allStairs.Add(currentStairsRun);
            }

            return allStairs;
        }


        /** param name="level" the level for which the stairs should be retrieved for.
            The list always consists of two elements.
            The first list contains the list of stairs, that start on this level.
            The second list contains the list of stairs, that end on this level.
         
            returns a List of all stairs that start or end at the current level.
        */
        public List<KeyValuePair<string, List<Stairs>>> GetAllStairsFromLevel(UIDocument currentDocument, List<Stairs> allStairs, Level currentLevel)
        {
            var filteredStairs = new List<KeyValuePair<string, List<Stairs>>>();
            var baseStairsOnLevel = new List<Stairs>();
            var topStairsOnLevel = new List<Stairs>();
            FilterNumericRuleEvaluator evaluator = new FilterNumericEquals();

            BuiltInParameter baseLevelParameter = BuiltInParameter.STAIRS_BASE_LEVEL_PARAM;
            ParameterValueProvider baseLevelProvider = new ParameterValueProvider(new ElementId(baseLevelParameter));
            FilterRule baseLevelRule = new FilterElementIdRule(baseLevelProvider, evaluator, currentLevel.Id);
            ElementParameterFilter baseLevelFilter = new ElementParameterFilter(baseLevelRule);

            FilteredElementCollector baseStairsCollector = new FilteredElementCollector(currentDocument.Document).WhereElementIsNotElementType().OfCategory(BuiltInCategory.OST_Stairs);
            ICollection<ElementId> baseStairIds = baseStairsCollector.WherePasses(baseLevelFilter).ToElementIds();

            foreach (ElementId currentBaseStairsId in baseStairIds)
            {
                if (Stairs.IsByComponent(currentDocument.Document, currentBaseStairsId))
                {
                    Stairs currentBaseStairs = currentDocument.Document.GetElement(currentBaseStairsId) as Stairs;

                    if (!StairsListContainsElement(baseStairsOnLevel, currentBaseStairs) && !StairsListContainsElement(topStairsOnLevel, currentBaseStairs))
                    {
                        baseStairsOnLevel.Add(currentBaseStairs);
                    }
                }
            }

            BuiltInParameter topLevelParameter = BuiltInParameter.STAIRS_TOP_LEVEL_PARAM;
            ParameterValueProvider topLevelProvider = new ParameterValueProvider(new ElementId(topLevelParameter));
            FilterRule topLevelRule = new FilterElementIdRule(topLevelProvider, evaluator, currentLevel.Id);
            ElementParameterFilter topLevelFilter = new ElementParameterFilter(topLevelRule);

            FilteredElementCollector topStairsCollector = new FilteredElementCollector(currentDocument.Document).WhereElementIsNotElementType().OfCategory(BuiltInCategory.OST_Stairs);
            ICollection<ElementId> topStairsIds = topStairsCollector.WherePasses(topLevelFilter).ToElementIds();

            foreach (ElementId currentTopStairsId in topStairsIds)
            {
                if (Stairs.IsByComponent(currentDocument.Document, currentTopStairsId))
                {
                    Stairs currentTopStairs = currentDocument.Document.GetElement(currentTopStairsId) as Stairs;

                    if (!StairsListContainsElement(baseStairsOnLevel, currentTopStairs) && !StairsListContainsElement(topStairsOnLevel, currentTopStairs))
                    {
                        topStairsOnLevel.Add(currentTopStairs);
                    }
                }
            }

            filteredStairs.Add(new KeyValuePair<string, List<Stairs>>(BASE_LEVEL_KEY, baseStairsOnLevel));
            filteredStairs.Add(new KeyValuePair<string, List<Stairs>>(TOP_LEVEL_KEY, topStairsOnLevel));
            return filteredStairs;
        }

        private bool StairsListContainsElement(List<Stairs> allStairs, Stairs stairs)
        {
            bool contains = false;

            foreach (Stairs currentStairs in allStairs)
            {
                if (currentStairs.Id.Equals(stairs.Id))
                {
                    contains = true;
                    break;
                }
                else
                {
                    continue;
                }
            }

            return contains;
        }
    }
}
