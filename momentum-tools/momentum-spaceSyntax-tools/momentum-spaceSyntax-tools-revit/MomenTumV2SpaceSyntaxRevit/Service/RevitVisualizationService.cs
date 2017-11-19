using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Analysis;
using Autodesk.Revit.UI;
using MathNet.Numerics.LinearAlgebra;
using MathNet.Numerics.LinearAlgebra.Double;
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

        public static void CheckForAnalysisDisplayStyle(Document doc)
        {
            FilteredElementCollector analysisDisplayStyleCollector = new FilteredElementCollector(doc);
            ICollection<Element> analysisDisplayStyles = analysisDisplayStyleCollector.OfClass(typeof(AnalysisDisplayStyle)).ToElements();
            var defaultDisplayStyle = from displaystyle in analysisDisplayStyles
                                      where displaystyle.Name == _defaultSpaceSyntaxDisplayStyleName
                                      select displaystyle;

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

        public static Result CreateSpaceSyntaxAnalysisResult(Document doc, SpaceSyntax spaceSyntax, Face face)
        {
            return CreateSpaceSyntaxAnalysisResult(doc, spaceSyntax, face, face.Reference);
        }

        public static Result CreateSpaceSyntaxAnalysisResult(Document doc, SpaceSyntax spaceSyntax, Face face, Reference faceReference)
        {
            var transaction = new Transaction(doc, "SpaceSyntax Visualization");
            transaction.Start();

            try
            {
                SpatialFieldManager sfm = SpatialFieldManager.GetSpatialFieldManager(doc.ActiveView);
                if (sfm == null)
                {
                    sfm = SpatialFieldManager.CreateSpatialFieldManager(doc.ActiveView, 1);
                }

                // mapping u to x and v to y 
                double deltaX = Math.Abs(spaceSyntax.MinX - spaceSyntax.MaxX) / spaceSyntax.DomainColumns;
                double deltaY = Math.Abs(spaceSyntax.MinY - spaceSyntax.MaxY) / spaceSyntax.DomainRows;

                double minX = spaceSyntax.MinX + deltaX / 2.0;
                double minY = spaceSyntax.MinY + deltaY / 2.0;

                var localOriginInGlobalVector = face.Evaluate(new UV(0.0, 0.0));
                var matrixAInverted = CalculateMatrixForGlobalToLocalCoordinateSystem(face, localOriginInGlobalVector);

                var uvPts = new List<UV>();
                var doubleList = new List<double>();
                var valList = new List<ValueAtPoint>();
                
                for (double y = minY, i = 1.0; y < spaceSyntax.MaxY; y += deltaY, i += 1.0)
                {
                    for (double x = minX, j = 1.0; x < spaceSyntax.MaxX; x += deltaX, j += 1.0)
                    {
                        var globalPoint = new XYZ(x, y, 0.0);
                        var localUV = GlobalToLocalCoordinate(matrixAInverted, localOriginInGlobalVector, globalPoint);

                        if (face.IsInside(localUV))
                        {
                            uvPts.Add(localUV);
                            doubleList.Add(GetValueFromSpaceSyntaxFor(spaceSyntax, (int)j, (int)i));
                            valList.Add(new ValueAtPoint(doubleList));
                            doubleList.Clear();
                        }
                    }
                }

                var points = new FieldDomainPointsByUV(uvPts);
                var values = new FieldValues(valList);
                int index = sfm.AddSpatialFieldPrimitive(faceReference);

                var resultSchema = new AnalysisResultSchema(
                    // the name value of an AnalysisResultSchema must be unique (hence Date-Milliseconds), else an exception is thrown
                    "Space Syntax from " + DateTime.Now.ToString("dd.MM.yyyy HH:mm:ss.ffff"),
                    "DepthMap");

                sfm.UpdateSpatialFieldPrimitive(index, points, values, sfm.RegisterResult(resultSchema));

                transaction.Commit();
                return Result.Succeeded;
            }
            catch (Exception e)
            {
                PromtService.DisplayErrorToUser(e.ToString());
                transaction.RollBack();
                return Result.Failed;
            }
        }

        private static double[,] CalculateMatrixForGlobalToLocalCoordinateSystem(Face face, XYZ vector_t)
        {
            // coordinate origin = koordinatenursprung || unit vector = einheitsvektor
            var hilfs_vector_e_u = face.Evaluate(new UV(1.0, 0.0));
            var hilfs_vector_e_v = face.Evaluate(new UV(0.0, 1.0));

            var g_vector_e_u_lokal = hilfs_vector_e_u - vector_t;
            var g_vector_e_v_lokal = hilfs_vector_e_v - vector_t;

            // folglich sind die beiden letzten vectoren eu und ev die Drehmatrix A
            /*
            var g_x = g_vector_e_u_lokal.X * face.GetBoundingBox().Min.U + g_vector_e_v_lokal.X * face.GetBoundingBox().Min.V + vector_t.X;
            var g_y = g_vector_e_u_lokal.Y * face.GetBoundingBox().Min.U + g_vector_e_v_lokal.Y * face.GetBoundingBox().Min.V + vector_t.Y;

            var xyz = new XYZ(g_x, g_y, face.Evaluate(face.GetBoundingBox().Min).Z);
            var check_x_y = face.Evaluate(face.GetBoundingBox().Min);
            */
            // A^-1 (A invertiert) => a12 vertauscht mit a21 ->
            var a11i = g_vector_e_u_lokal.X;
            var a12i = g_vector_e_u_lokal.Y;
            var a21i = g_vector_e_v_lokal.X;
            var a22i = g_vector_e_v_lokal.Y;

            // jetzt test ob ein globaler punkt richtig ins lokale übersetzt wurde! eval(face.GetBoundingBox().min) nach uv
            /*
            var g_punkt_g_t = face.Evaluate(bb.Min) - vector_t;
            var l_punkt_g = new UV(
                a11i * g_punkt_g_t.X + a12i * g_punkt_g_t.Y,
                a21i * g_punkt_g_t.X + a22i * g_punkt_g_t.Y
                );
            */
            // l_punkt_g muss gleich bb.min sein!

            return new double[2, 2] {
                { a11i, a12i },
                { a21i, a22i }};
        }

        private static UV GlobalToLocalCoordinate(double[,] matrix, XYZ localOriginInGlobalCoordinateSystemVector, XYZ globalPoint)
        {
            var xyz = globalPoint - localOriginInGlobalCoordinateSystemVector;

            return new UV(
                matrix[0, 0] * xyz.X + matrix[0, 1] * xyz.Y,
                matrix[1, 0] * xyz.X + matrix[1, 1] * xyz.Y);
        }

        private static double GetValueFromSpaceSyntaxFor(SpaceSyntax spaceSyntax, int column, int row)
        {
            foreach (CellIndex cellIndex in spaceSyntax.CellIndices)
            {
                if (cellIndex.X == column && cellIndex.Y == row)
                {
                    return cellIndex.Value;
                }
            }

            return spaceSyntax.MinValue;
        }
    }
}
