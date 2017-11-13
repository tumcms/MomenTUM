## General Information
In Space Syntax ... TODO...

## User Guide
There are no pre-compiled Add-Ins for Revit available, since this plugin is still under development.
In order to compile the Add-In some steps have to be taken:

1 Install Revit, ILMerge and a C# IDE (preferably Visual Studio)

2 Clone the repository or the project

3 Open the solution in your IDE, go to project properties and add a 'Reference Path' to the 

4 Open the solution in your IDE and build the project

5 Start Revit and confirm that you trust the authors of the plugin

## Add-In Workflow and Limits
TODO cuz tbd
must have origins
must have all rooms on one floor or surface
must have a 3D view

## Developer Guide




## Helpful Links

[My First Plugin Training by Autodesk](http://usa.autodesk.com/adsk/servlet/index?siteID=123112&id=16459234)

[Jeremy Tammik](https://github.com/jeremytammik) is well experienced with the Revit API and has an extremly useful [Blog](http://thebuildingcoder.typepad.com/) where he publishes discussions on addins. In his [Github Repository](https://github.com/jeremytammik) many helpful projects are published. A useful tool is [RevitLookup](https://github.com/jeremytammik/RevitLookup). It allows to dynamically look into Revit objects at runtime and can help understanding the RevitAPI.

[Revit API Forum](https://forums.autodesk.com/t5/revit-api-forum/bd-p/160)

[Stackoverflow Revit-API Tag](https://stackoverflow.com/tags/revit-api/info) 




## TODO
Documentation Notes:

* Revit Project must have a 3D View, since Analytical Results can only be shown in 3D View! View -> Camera! Creates a 3D View (If you only chose 3D view from the menu, the 3D view is not the same!)
* The Revit Project must have a default Analytical Display Style! Select 3D View from Browser and change or create a style for 'Default Analysis Display Style' in Properties-Window.
* The results of such an analyis can be disabled by pressing 'Edit...' in properties window -> 'Analyis Display Settings'. 
* Beschreiben wie das dll-mergen geht
* click auf build zieht sich die fehlenden nuget-packages von alleine, sofern die Option im Package Manager 'Allow NuGet to download missing packages during build' angehakt ist.
* Reference Path setzten in Rechts-klick Project properties zu revitapi.dll-mergen
	=> wichtig: ist der reference path gesetzt sollte man entgegen dem revit "myfirstplugin" die CopyLocal property auf true setzen, sonst findet der dll merger die dll nicht.

Workflow:
* create layouts by using layouting plugin
* add spaceSyntax tag + writer tag for space syntax
* run momentum with the resulting configuration
* read the resulting file into spaceSyntax revit plugin
* done