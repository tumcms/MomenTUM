## General Information
This plugin is used to export geometrical information from revit project into xml format. The generated xml contains layouts and scenarios which can be used for Momentum simulations.

## User Guide
There is no pre-compiled Layouting Add-Ins for Revit available. If you want to use the Plugin, you have to compile the Add-In yourself. 
The some steps have to be taken:

1 Install Autodesk Revit, ILMerge and a C# IDE (i.e. Visual Studio)

2 Clone the Momentum repository or the project

3 Open the solution in your IDE, go to project references and add 'RevitAPI.dll' and 'RevitAPIUI' from your local Revit installation
(Hint: dlls are usually located in 'Program Files/Autodesk/Revit 20XX/')

4 Open the post-build events in the project and adapt the paths to ILMerge and the Revit dll folders (usually you just need to replace the revit version).

5 Check for build errors in the post-bulid and fix them according to the message provided by MS Build.

6 Start Revit and confirm that you trust the authors of the plugin and check if the External Command 'MomentumV2RevitLayouting' under the 'Add-Ins' Tab in Revit shows up.


## Add-In Workflow
Executing the plugin promts the user to select a level from a list. Also all levels can be exported. Walls and doors are exported as obstacles as type 'Wall'. Stairs can will either be shown as areas of either type 'Origin' or 'Destination'. 

More detailed information on the workflow can be found in a pdf within a subfolder of this project.