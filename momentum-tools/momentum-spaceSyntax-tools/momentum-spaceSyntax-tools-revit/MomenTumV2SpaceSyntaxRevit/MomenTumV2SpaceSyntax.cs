using Autodesk.Revit.DB;
using Autodesk.Revit.UI;
using Autodesk.Revit.Attributes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Diagnostics;
using MomenTumV2SpaceSyntaxRevit.Model;
using MomenTumV2SpaceSyntaxRevit.Service;
using Autodesk.Revit.DB.Analysis;
using Autodesk.Revit.ApplicationServices;
using Autodesk.Revit.UI.Selection;

[TransactionAttribute(TransactionMode.Manual)]
[RegenerationAttribute(RegenerationOption.Manual)]
public class MomenTumV2SpaceSyntax : IExternalCommand
{
    public Result Execute(ExternalCommandData commandData, ref string message, ElementSet elements)
    {
        UIApplication uiApp = commandData.Application;
        Document doc = uiApp.ActiveUIDocument.Document;
        Application app = commandData.Application.Application;

        KeyValuePair<Result, SpaceSyntax> kvSpaceSyntax = FileOpenService.PromtUserForSpaceSyntaxXml();
        if (kvSpaceSyntax.Key != Result.Succeeded)
        {
            return kvSpaceSyntax.Key;
        }
        SpaceSyntax spaceSyntax = kvSpaceSyntax.Value;

        KeyValuePair<Result, Level> kvSelectedLevel = new KeyValuePair<Result, Level>(Result.Failed, null);

        if (!string.IsNullOrEmpty(spaceSyntax.ScenarioName))
        {
            kvSelectedLevel = RevitUtils.AttemptToGetLevelBySpaceSyntaxName(doc, spaceSyntax.ScenarioName);
        }

        if (kvSelectedLevel.Key != Result.Succeeded)
        {
            kvSelectedLevel = UserLevelSelectService.LetUserPickLevelFromDialog(doc);
        }

        if (kvSelectedLevel.Key != Result.Succeeded)
        {
            return kvSelectedLevel.Key;
        }
        Level level = kvSelectedLevel.Value;

        KeyValuePair<Result, PlanarFace> kvTopFace = RevitUtils.GetTopFaceFromLevel(app, level);
        if (kvSelectedLevel.Key != Result.Succeeded)
        {
            return kvSelectedLevel.Key;
        }
        PlanarFace topFace = kvTopFace.Value;

        // A (default) AnalysisDisplayStyle must exist, otherwise Revit does not know how to display/interpret anything
        // TODO create default 3D view?
        RevitVisualizationService.CheckForAnalysisDisplayStyle(doc);

        var result = RevitVisualizationService.CreateSpaceSyntaxAnalysisResult(doc, spaceSyntax, topFace);

        return result;
    }
}
