using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Analysis;
using MomenTumV2SpaceSyntaxRevit.Model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2SpaceSyntaxRevit.Service
{
    class RevitVisualizationService
    {
        private static string _defaultSpaceSyntaxDisplayStyleName = "SpaceSyntax default style";

        public static void CreateSpaceSyntaxAnalysisResult(Document doc, SpaceSyntax spaceSyntax, List<Face> topAndBottomFace, Reference faceReference)
        {
            spaceSyntax.DomainColumns -= 2;
            spaceSyntax.DomainRows -= 2;

            var trans = new Transaction(doc, "SpaceSyntax Visualization");
            trans.Start();

            SpatialFieldManager sfm = SpatialFieldManager.GetSpatialFieldManager(doc.ActiveView);
            if (sfm == null)
            {
                sfm = SpatialFieldManager.CreateSpatialFieldManager(doc.ActiveView, 1);
            }

            foreach (Face face in topAndBottomFace)
            {
                var uvPts = new List<UV>();
                var doubleList = new List<double>();
                var valList = new List<ValueAtPoint>();
                
                BoundingBoxUV bb = face.GetBoundingBox();
                var min = face.Evaluate(bb.Min);
                var max = face.Evaluate(bb.Max);
                
                Debug.WriteLine("Bounding Box: Max " + bb.Max + " Min " +  bb.Min);

                double minU = bb.Min.U;
                double minV = bb.Min.V;
                double maxU = bb.Max.U;
                double maxV = bb.Max.V;

                double distanceU = Math.Abs(minU - maxU);
                double distanceV = Math.Abs(minV - maxV);

                double deltaU = distanceU / (double)(spaceSyntax.DomainColumns);
                double deltaV = distanceV / (double)(spaceSyntax.DomainRows);

                maxU -= deltaU;
                maxV -= deltaV;

                int row = 0, column = 0;

                for (double v = minV + deltaV / 2.0; v < maxV; v += deltaV)
                {
                    if (v == minV + deltaV / 2.0) row = spaceSyntax.DomainRows - 2;

                    for (double u = minU + deltaU / 2.0; u < maxU; u += deltaU)
                    {
                        if (u == minU + deltaU / 2.0) column = spaceSyntax.DomainColumns;

                        var uv = new UV(u, v);
                        if (face.IsInside(uv))
                        {
                            uvPts.Add(uv);
                            doubleList.Add(GetValueFromSpaceSyntaxFor(spaceSyntax, column, row));
                            valList.Add(new ValueAtPoint(doubleList));
                            doubleList.Clear();
                        }
                        column--;
                    }
                    row--;
                }

                var points = new FieldDomainPointsByUV(uvPts);
                var values = new FieldValues(valList);

                int index;
                if (face.Reference == null)
                {
                    index = sfm.AddSpatialFieldPrimitive(faceReference);
                }
                else
                {
                    index = sfm.AddSpatialFieldPrimitive(face.Reference);
                }

                var resultSchema = new AnalysisResultSchema(
                    // the name value of an AnalysisResultSchema must be unique (hence Date-Milliseconds), else an exception is thrown
                    "Space Syntax from " + DateTime.Now.ToString("dd.MM.yyyy HH:mm:ss.ffff"),
                    "DepthMap");

                sfm.UpdateSpatialFieldPrimitive(index, points, values, sfm.RegisterResult(resultSchema));
            }

            trans.Commit();
        }

        private static double GetValueFromSpaceSyntaxFor(SpaceSyntax spaceSyntax, int column, int row)
        {
            // we use the hard-coded index 0, because we assume that there is only one floor(or face) that contains all rooms
            foreach (CellIndex cellIndex in spaceSyntax.CellIndices)
            {
                if (cellIndex.X == column && cellIndex.Y == row)
                {
                    return cellIndex.Value;
                }
            }

            return spaceSyntax.MinValue;
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
    }
}
