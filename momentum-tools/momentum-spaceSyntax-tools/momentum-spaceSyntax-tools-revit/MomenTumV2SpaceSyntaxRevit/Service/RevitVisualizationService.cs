using Autodesk.Revit.DB;
using Autodesk.Revit.DB.Analysis;
using MomenTumV2SpaceSyntaxRevit.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2SpaceSyntaxRevit.Service
{
    class RevitVisualizationService
    {
        public static void CreateSpaceSyntaxAnalysisResult(Document doc, SpaceSyntax spaceSyntax, List<Face> topAndBottomFace, Reference faceReference)
        {
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

                double minU = bb.Min.U;
                double minV = bb.Min.V;
                double maxU = bb.Max.U;
                double maxV = bb.Max.V;

                double distanceU = Math.Abs(minU - maxU);
                double distanceV = Math.Abs(minV - maxV);

                double deltaU = distanceU / (double)(spaceSyntax.DomainColumns - 2);
                double deltaV = distanceV / (double)(spaceSyntax.DomainRows - 2);

                maxU -= deltaU;
                maxV -= deltaV;

                int row = 0, column = 0;

                for (double v = minV + deltaV / 2.0; v < maxV; v += deltaV)
                {
                    if (v == minV + deltaV / 2.0) row = spaceSyntax.DomainRows - 2;

                    for (double u = minU + deltaU / 2.0; u < maxU; u += deltaU)
                    {
                        if (u == minU + deltaU / 2.0) column = spaceSyntax.DomainColumns - 2;

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
    }
}
