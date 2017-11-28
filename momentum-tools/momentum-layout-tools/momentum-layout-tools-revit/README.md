## General Information
This plugin is used to export geometrical information from a Revit project to a xml. The generated xml contains layouts (scenarios/levels) which can be used for Momentum simulations.

## User Guide
There is no pre-compiled Layouting Add-Ins for Revit available. If you want to use the Plugin, you have to compile the Add-In yourself. 
The some steps have to be taken:

1 Install Autodesk Revit, ILMerge and a C# IDE (i.e. Visual Studio)

2 Clone the Momentum repository or the project

3 Open the solution in your IDE, go to project references and add 'RevitAPI.dll' and 'RevitAPIUI' from your local Revit installation
(Hint: dlls are usually located in 'Program Files/Autodesk/Revit 20XX/')

4 Open the post-build events in the project and adapt the paths to ILMerge and the Revit dll folders (usually you just need to replace the Revit version).

5 Check for build errors in the post-bulid and fix them according to the message provided by MS Build.

6 Start Revit and confirm that you trust the authors of the plugin and check if the External Command 'MomentumV2RevitLayouting' under the 'Add-Ins' Tab in Revit shows up.


## Add-In Workflow
Executing the plugin promts the user to select one level or all levels of a project. The selected level(s) will be exported as xml. 

Note: Walls and doors are exported as obstacles as type 'Wall'. Stairs can will either be shown as areas of either type 'Origin' or 'Destination'. 

More detailed information on the workflow can be found in a pdf within a subfolder of this project.

## Helpful Links

[My First Plugin Training by Autodesk](http://usa.autodesk.com/adsk/servlet/index?siteID=123112&id=16459234) is the first and best start into developing a Revit Add-In. 
[Revit SDK](http://usa.autodesk.com/adsk/servlet/index?siteID=123112&id=2484975) provides code examples for a wide range of Revit functionality.

[Official Revit API Documentation](http://www.revitapidocs.com/) sometimes provides example code for classes and methods.

[Jeremy Tammik](https://github.com/jeremytammik) is well experienced with the Revit API and has an useful [Blog](http://thebuildingcoder.typepad.com/) where he publishes discussions on Add-Ins. In his [Github Repository](https://github.com/jeremytammik) many helpful projects are published. A useful tool is [RevitLookup](https://github.com/jeremytammik/RevitLookup). It allows to dynamically look into Revit objects at runtime and can help understanding the RevitAPI.

[Revit API Forum](https://forums.autodesk.com/t5/revit-api-forum/bd-p/160)

[Stackoverflow Revit-API Tag](https://stackoverflow.com/tags/revit-api/info) 