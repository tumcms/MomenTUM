using Autodesk.Revit.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MomenTumV2SpaceSyntaxRevit.View;
using Autodesk.Revit.UI;
using Autodesk.Revit.DB.Analysis;
using Autodesk.Revit.DB.Architecture;
using Autodesk.Revit.ApplicationServices;

namespace MomenTumV2SpaceSyntaxRevit.Service
{
    public class RevitUtils
    {
        public static Level LevelSelectedByUser { private get; set; }
        private static string _defaultSpaceSyntaxDisplayStyleName = "SpaceSyntax default style";

        public static KeyValuePair<Result, Level> LetUserPickLevelFromDialog(Document doc)
        {
            FilteredElementCollector levelCollector = new FilteredElementCollector(doc);
            ICollection<Element> levelCollection = levelCollector.OfClass(typeof(Level)).ToElements();

            var levels = new List<Level>();
            foreach (Element element in levelCollection)
            {
                Level level = element as Level;
                if (level != null)
                {
                    levels.Add(level);
                }
            }

            if (levels.Count == 0)
            {
                PromtService.DisplayErrorToUser("The project does not contain any levels.");
                return new KeyValuePair<Result, Level>(Result.Failed, null);
            }

            return OpenLevelSelector(levels);
        }

        private static KeyValuePair<Result, Level> OpenLevelSelector(List<Level> levels)
        {
            var levelSelectorDialog = new LevelSelectorHost();
            levelSelectorDialog.InitializeLevelListBox(levels);

            levelSelectorDialog.ShowDialog();

            if (LevelSelectedByUser == null)
            {
                PromtService.DisplayInformationToUser("Operation cancelled by User.");
                return new KeyValuePair<Result, Level>(Result.Cancelled, null);
            }

            return new KeyValuePair<Result, Level>(Result.Succeeded, LevelSelectedByUser);
        }

        public static KeyValuePair<Result, Floor> GetFloorFromLevel()
        {
            return new KeyValuePair<Result, Floor>();
        }

        public static void CheckForAnalysisDisplayStyle(Document doc)
        {
            FilteredElementCollector analysisDisplayStyleCollector = new FilteredElementCollector(doc);
            ICollection<Element> analysisDisplayStyles = analysisDisplayStyleCollector.OfClass(typeof(AnalysisDisplayStyle)).ToElements();
            var defaultDisplayStyle = from element in analysisDisplayStyles
                                      where element.Name == _defaultSpaceSyntaxDisplayStyleName
                                      select element;

            if (defaultDisplayStyle.Count() == 0)
            {
                CreateDefaultSpaceSyntaxAnalysisDisplayStyle(doc);
            }
        }

        private static void CreateDefaultSpaceSyntaxAnalysisDisplayStyle(Document doc)
        {

            AnalysisDisplayColoredSurfaceSettings coloredSurfaceSettings =
                new AnalysisDisplayColoredSurfaceSettings();
            coloredSurfaceSettings.ShowGridLines = false;
            coloredSurfaceSettings.ShowContourLines = false;

            AnalysisDisplayColorSettings colorSettings = new AnalysisDisplayColorSettings();

            colorSettings.ColorSettingsType = AnalysisDisplayStyleColorSettingsType.GradientColor;
            colorSettings.MaxColor = new Color(255, 0, 255); // Magenta
            colorSettings.MinColor = new Color(255, 255, 0); // Yellow

            AnalysisDisplayLegendSettings legendSettings = new AnalysisDisplayLegendSettings();
            legendSettings.ShowLegend = true;
            legendSettings.ShowUnits = true;
            legendSettings.ShowDataDescription = false;

            var transaction = new Transaction(doc, "Default Analysis Display Style Creation for Space Syntax.");
            transaction.Start();

            var analysisDisplayStyle = AnalysisDisplayStyle.CreateAnalysisDisplayStyle(
                doc,
                _defaultSpaceSyntaxDisplayStyleName,
                coloredSurfaceSettings,
                colorSettings,
                legendSettings);

            doc.ActiveView.AnalysisDisplayStyleId = analysisDisplayStyle.Id;
            transaction.Commit();
        }

        public static List<Floor> GetAllFloorsFromSelectedLevel(Level selectedLevel)
        {
            var floors = new List<Floor>();
            var floorsOnLevel = new FilteredElementCollector(selectedLevel.Document).OfClass(typeof(Floor)).ToElements();

            foreach (var spatialElement in floorsOnLevel)
            {
                Floor floor1 = spatialElement as Floor;
                if (floor1 != null)
                {
                    floors.Add(floor1);
                }
            }

            return floors;
        }

        public static List<Face> CollectAllFacesFromAllFloors(Application app, List<Floor> floors)
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
