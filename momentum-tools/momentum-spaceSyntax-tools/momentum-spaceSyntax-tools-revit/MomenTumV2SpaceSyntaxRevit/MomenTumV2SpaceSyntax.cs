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

        KeyValuePair<Result, Level> kvSelectedLevel = RevitUtils.LetUserPickLevelFromDialog(doc);
        if (kvSelectedLevel.Key != Result.Succeeded)
        {
            return kvSelectedLevel.Key;
        }
        Level level = kvSelectedLevel.Value;

        // could retrieve face by selection from user!
        // For now we assume: We select the both Faces with the biggest Area 
        // from the floors (which is assumed to be top and bottom face of the same element)

        //var floors = RevitUtils.GetAllFloorsFromSelectedLevel(level);
        //var allFaces = RevitUtils.CollectAllFacesFromAllFloors(app, floors);
        //var topAndBottomFace = FilterTopAndBottomFaceIntoList(allFaces);

        IList<Reference> refList = new List<Reference>();
        refList = uiApp.ActiveUIDocument.Selection.PickObjects(ObjectType.Face);
        Face selectedFace = doc.GetElement(refList[0]).GetGeometryObjectFromReference(refList[0]) as Face;
        // TODO get stable reference from selectionpicker..........
        var topAndBottomFace = new List<Face>();
        topAndBottomFace.Add(selectedFace);

        // A (default) AnalysisDisplayStyle must exist, otherwise Revit does not know how to display/interpret anything
        RevitUtils.CheckForAnalysisDisplayStyle(doc);

        try
        {
            RevitVisualizationService.CreateSpaceSyntaxAnalysisResult(doc, spaceSyntax, topAndBottomFace);
        }
        catch (Exception e)
        {
            PromtService.DisplayErrorToUser(e.ToString());
            return Result.Failed;
        }

        return Result.Succeeded;
    }

    private List<Face> FilterTopAndBottomFaceIntoList(List<Face> allFaces)
    {
        var topAndBottomFace = new List<Face>();

        Face firstFaceWithMaxArea = allFaces.OrderByDescending(face => face.Area).First();
        allFaces.Remove(firstFaceWithMaxArea);
        Face secondFaceWithMaxArea = allFaces.OrderByDescending(face => face.Area).First();

        topAndBottomFace.Add(firstFaceWithMaxArea);
        topAndBottomFace.Add(secondFaceWithMaxArea);

        return topAndBottomFace;
    }
}
