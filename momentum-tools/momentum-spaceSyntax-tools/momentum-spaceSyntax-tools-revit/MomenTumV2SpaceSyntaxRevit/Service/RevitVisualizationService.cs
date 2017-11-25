using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Analysis;
using Autodesk.Revit.UI;
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
        private const string _defaultSpaceSyntaxDisplayStyleName = "SpaceSyntax default style";

        public static void ActivateAnalysisDisplayStyle(Document doc)
        {
            AnalysisDisplayStyle analysisDisplayStyle = GetDefaultAnalysisDisplayStyle(doc);

            SetAnalysisDisplayStyle(doc, analysisDisplayStyle);
        }

        private static AnalysisDisplayStyle GetDefaultAnalysisDisplayStyle(Document doc)
        {
            var defaultDisplayStyle = FilterDefaultSpaceSyntaxAnalysisDisplayStyle(doc);

            AnalysisDisplayStyle analysisDisplayStyle = null;
            if (defaultDisplayStyle.Count() == 0)
            {
                analysisDisplayStyle = CreateDefaultSpaceSyntaxAnalysisDisplayStyle(doc);
            }
            else
            {
                analysisDisplayStyle = defaultDisplayStyle.Cast<AnalysisDisplayStyle>().ElementAt(0);
            }

            return analysisDisplayStyle;
        }

        private static IEnumerable<AnalysisDisplayStyle> FilterDefaultSpaceSyntaxAnalysisDisplayStyle(Document doc)
        {
            FilteredElementCollector analysisDisplayStyleCollector = new FilteredElementCollector(doc);
            ICollection<Element> analysisDisplayStyles = analysisDisplayStyleCollector.OfClass(typeof(AnalysisDisplayStyle)).ToElements();

            var defaultDisplayStyle = from displayStyle in analysisDisplayStyles
                                      where displayStyle.Name == _defaultSpaceSyntaxDisplayStyleName
                                      select displayStyle;

            return defaultDisplayStyle.Cast<AnalysisDisplayStyle>();
        }

        private static AnalysisDisplayStyle CreateDefaultSpaceSyntaxAnalysisDisplayStyle(Document doc)
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
            legendSettings.ShowUnits = false;
            legendSettings.ShowDataDescription = false;

            var analysisDisplayStyle = AnalysisDisplayStyle.CreateAnalysisDisplayStyle(
                doc,
                _defaultSpaceSyntaxDisplayStyleName,
                coloredSurfaceSettings,
                colorSettings,
                legendSettings);

            return analysisDisplayStyle;
        }

        private static void SetAnalysisDisplayStyle(Document doc, AnalysisDisplayStyle analysisDisplayStyle)
        {
            using (var transaction = new Transaction(doc, "Setting Default Analysis Display Style for Space Syntax."))
            {
                transaction.Start();

                doc.ActiveView.AnalysisDisplayStyleId = analysisDisplayStyle.Id;

                transaction.Commit();
            }
        }

        public static Result CreateSpaceSyntaxAnalysisResult(Document doc, SpaceSyntax spaceSyntax, Face face)
        {
            // A (default) AnalysisDisplayStyle must exist, otherwise Revit does not know how to display/interpret anything
            ActivateAnalysisDisplayStyle(doc);

            using (var transaction = new Transaction(doc, "SpaceSyntax Visualization"))
            {
                transaction.Start();
                try
                {
                    var result = CreateSpaceSyntaxAnalysisResult(doc, spaceSyntax, face, face.Reference);
                    transaction.Commit();
                    return result;
                }
                catch (Exception e)
                {
                    PromtService.ShowErrorToUser(e.ToString());
                    transaction.RollBack();
                    return Result.Failed;
                }
            }
        }

        public static Result CreateSpaceSyntaxAnalysisResult(Document doc, SpaceSyntax spaceSyntax, Face face, Reference faceReference)
        {
            SpatialFieldManager sfm = SpatialFieldManager.GetSpatialFieldManager(doc.ActiveView);
            if (sfm == null)
            {
                sfm = SpatialFieldManager.CreateSpatialFieldManager(doc.ActiveView, 1);
            }
            
            var uvPts = new List<UV>();
            var doubleList = new List<double>();
            var valList = new List<ValueAtPoint>();

            // we map u to x and v to y 
            var localOriginInGlobalVector = face.Evaluate(new UV(0.0, 0.0));
            var matrixAInverted = CalculateMatrixForGlobalToLocalCoordinateSystem(face);

            double deltaX = Math.Abs(spaceSyntax.MinX - spaceSyntax.MaxX) / spaceSyntax.DomainColumns;
            double deltaY = Math.Abs(spaceSyntax.MinY - spaceSyntax.MaxY) / spaceSyntax.DomainRows;

            double minX = spaceSyntax.MinX + deltaX / 2.0;
            double minY = spaceSyntax.MinY + deltaY / 2.0;

            for (double y = minY, i = 1.0; y < spaceSyntax.MaxY; y += deltaY, i += 1.0)
            {
                for (double x = minX, j = 1.0; x < spaceSyntax.MaxX; x += deltaX, j += 1.0)
                {
                    var globalPoint = new XYZ(x, y, 0.0); // z-coordinate is irrelevant, since the UV space is parallel
                    var localUV = GlobalToLocalCoordinate(matrixAInverted, localOriginInGlobalVector, globalPoint);

                    if (face.IsInside(localUV))
                    {
                        uvPts.Add(localUV);
                        doubleList.Add(GetValueFromSpaceSyntax(spaceSyntax, (int)j, (int)i));
                        valList.Add(new ValueAtPoint(doubleList));
                        doubleList.Clear();
                    }
                }
            }

            var points = new FieldDomainPointsByUV(uvPts);
            var values = new FieldValues(valList);
            int index = sfm.AddSpatialFieldPrimitive(faceReference);

            var resultSchema = new AnalysisResultSchema(
                // the name value of an AnalysisResultSchema must be unique, hence Date-Seconds
                "Space Syntax from " + DateTime.Now.ToString("dd.MM.yyyy HH:mm:ss"),
                "Space Syntax");

            sfm.UpdateSpatialFieldPrimitive(index, points, values, sfm.RegisterResult(resultSchema));
            
            return Result.Succeeded;
        }

        /// <summary>
        /// This method calculates a rotation matrix for a given face. This matrix can be used to translate points
        /// from the 'global' coordinate system (XYZ-space) of the project into the 'local' coordinate system (UV-space)
        /// of the face (e.g. the bounding box of the face). 
        /// 
        /// Note: The UV space of a face is always parallel to the elements face in the global coordinate system.
        /// </summary>
        /// <param name="face">the face for which the rotation matrix should be computed</param>
        /// <returns>a rotation matrix for the provided face</returns>
        private static double[,] CalculateMatrixForGlobalToLocalCoordinateSystem(Face face)
        {
            // face.Evaluate uses a rotation matrix and a displacement vector to translate points
            XYZ originDisplacementVectorUV = face.Evaluate(new UV(0.0, 0.0));
            XYZ unitVectorUWithDisplacement = face.Evaluate(new UV(1.0, 0.0));
            XYZ unitVectorVWithDisplacement = face.Evaluate(new UV(0.0, 1.0));

            XYZ unitVectorU = unitVectorUWithDisplacement - originDisplacementVectorUV;
            XYZ unitVectorV = unitVectorVWithDisplacement - originDisplacementVectorUV;

            // The rotationmatrix A in this case is composed of unitVectorU and unitVectorV transposed.
            // To get the rotation matrix that translates from global space to local space, we take the inverse of A

            var a11i = unitVectorU.X;
            var a12i = unitVectorU.Y;
            var a21i = unitVectorV.X;
            var a22i = unitVectorV.Y;

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

        private static double GetValueFromSpaceSyntax(SpaceSyntax spaceSyntax, int column, int row)
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
