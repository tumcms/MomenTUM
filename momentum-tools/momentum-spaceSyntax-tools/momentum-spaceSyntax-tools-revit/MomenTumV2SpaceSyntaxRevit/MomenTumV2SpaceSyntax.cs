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

        KeyValuePair<Result, Level> kvSelectedLevel = UserLevelSelectService.LetUserPickLevelFromDialog(doc);
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

        var faces = new List<Face>();
        faces.Add(topFace);
        
        //Reference faceReference = uiApp.ActiveUIDocument.Selection.PickObject((ObjectType.Face);
        //Reference elementReference = uiApp.ActiveUIDocument.Selection.PickObject(ObjectType.Element);
        
        //TOTEST: _getGeometry(view)?
        //var element = doc.GetElement(faceReference);
        //var list = new List<Floor>(); list.Add(element as Floor); var elementFloorFaces = RevitUtils.CollectAllFacesFromAllFloors(app, list);

        //Face selectedFace = element.GetGeometryObjectFromReference(faceReference) as Face;

        // TODO get stable reference from selectionpicker..........
        //var topAndBottomFace = new List<Face>();
        //topAndBottomFace.Add(selectedFace);

        // A (default) AnalysisDisplayStyle must exist, otherwise Revit does not know how to display/interpret anything
        RevitVisualizationService.CheckForAnalysisDisplayStyle(doc);

        try
        {
            RevitVisualizationService.CreateSpaceSyntaxAnalysisResult(doc, spaceSyntax, faces, null);
        }
        catch (Exception e)
        {
            PromtService.DisplayErrorToUser(e.ToString());
            return Result.Failed;
        }

        return Result.Succeeded;
    }
}
