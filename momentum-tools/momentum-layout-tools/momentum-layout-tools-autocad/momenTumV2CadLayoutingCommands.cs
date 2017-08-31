// (C) Copyright 2015 by berndtornede, pk
//
using Autodesk.AutoCAD.ApplicationServices;
using Autodesk.AutoCAD.Colors;
using Autodesk.AutoCAD.DatabaseServices;
using Autodesk.AutoCAD.EditorInput;
using Autodesk.AutoCAD.Geometry;
using Autodesk.AutoCAD.LayerManager;
using Autodesk.AutoCAD.Runtime;
using MomenTumV2CadLayouting.Geometry;
using System;
using System.Collections.Generic;
using System.Reflection;
using System.Runtime;
using System.Windows;

// This line is not mandatory, but improves loading performances
[assembly: CommandClass(typeof(MomenTumV2CadLayouting.momenTumV2CadLayoutingCommands))]

namespace MomenTumV2CadLayouting
{

    // This class is instantiated by AutoCAD for each document when
    // a command is called by the user the first time in the context
    // of a given document. In other words, non static data in this class
    // is implicitly per-document!
    public class momenTumV2CadLayoutingCommands
    {
        XMLconverter_MomenTumV2 xmlConverter = new XMLconverter_MomenTumV2();

        private static String originLayerName = "Origin";
        private static short originColorCode = 1;

        private static String intermediateLayerName = "Intermediate";
        private static short intermediateColorCode = 2;

        private static String gatheringLineLayerName = "GatheringLines";
        private static short gatheringLineColorCode = 7;

        private static String graphLayerName = "Graph";
        private static short graphColorCode = 94;

        private static String avoidanceLayerName = "Avoidance";
        private static short avoidanceColorCode = 34;

        private static String destinationLayerName = "Destination";
        private static short destinationColorCode = 3;

        private static String wallLayerName = "Wall";
        private static short wallColorCode = 4;

        private static String solidLayerName = "Solid";
        private static short solidColorCode = 5;

        private static String taggedAreasLayerFilterName = "TaggedAreas";

        private static double measurementUnitFactor = 1;
        private static double pointEqualityTolerance = Tolerance.Global.EqualPoint * measurementUnitFactor;//0.25 * measurementUnitFactor;
        //private static double lowerTolerance = 0.000001 * measurementUnitFactor;

        #region Commands

        [CommandMethod("ped_joinAndCloseLines", CommandFlags.Session)]
        public void joinAndCloseLines()
        {
            activateConvertScenarioPlugin();
            closeOpenedPolygons();
            joinTouchingLines();
        }

        [CommandMethod("ped_allocateObjectsToLayers", CommandFlags.Session)]
        public void allocateObjectsToLayers()
        {
            activateConvertScenarioPlugin();
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            closeOpenedPolygons();
            joinTouchingLines();


            using (DocumentLock docLock = currentDocument.LockDocument())
            {
                TypedValue[] tvs = new TypedValue[] { 
                new TypedValue((int)DxfCode.Operator, "<OR"),
                new TypedValue((int)DxfCode.Start, "LWPOLYLINE"),
                new TypedValue((int)DxfCode.Start, "POLYLINE"),
                new TypedValue((int)DxfCode.Operator, "OR>")};
                SelectionFilter filter = new SelectionFilter(tvs);
                PromptSelectionResult allDrawingObjects = editor.SelectAll(filter);

                if (allDrawingObjects.Status == PromptStatus.OK)
                {
                    using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
                    {
                        ObjectId[] allDrawingObjectsIDs = allDrawingObjects.Value.GetObjectIds();
                        LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);
                        ObjectId referringLayerID = ObjectId.Null;

                        foreach (ObjectId currentDrawingObjectID in allDrawingObjectsIDs)
                        {
                            Polyline currentDrawingObject = (Polyline)transaction.GetObject(currentDrawingObjectID, OpenMode.ForWrite);

                            if (currentDrawingObject.Closed == true
                                && polylineContainsAllOtherDrawings(currentDrawingObject, allDrawingObjectsIDs, transaction))
                            {
                                if (layers.Has(wallLayerName))
                                {
                                    referringLayerID = layers[wallLayerName];
                                    currentDrawingObject.LayerId = referringLayerID;
                                }
                            }
                            else if (currentDrawingObject.Closed == true)
                            {
                                if (layers.Has(solidLayerName))
                                {
                                    referringLayerID = layers[solidLayerName];
                                    currentDrawingObject.LayerId = referringLayerID;
                                }
                            }
                            else if (currentDrawingObject.Closed == false)
                            {
                                if (layers.Has(wallLayerName))
                                {
                                    referringLayerID = layers[wallLayerName];
                                    currentDrawingObject.LayerId = referringLayerID;
                                }
                            }
                            else
                            {
                                editor.WriteMessage("\nSomething bad happened while allocating layers for polylines");
                            }
                        }

                        transaction.Commit();
                    }
                }
                else
                {
                    editor.WriteMessage("\nSomething bad happened while allocating objects to layers. Maybe there are no drawing objects yet.");
                }
            }
            editor.WriteMessage("\nIf the Origin/Intermediate/Destination areas have already been drawn, please allocate them to the specific layer manually.");
        }

        [CommandMethod ("ped_selectForConversion", CommandFlags.Session)]
        public void selectForConversion() 
        {
            activateConvertScenarioPlugin();
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            
            PromptKeywordOptions askForLayerName = new PromptKeywordOptions("");
            askForLayerName.Message = "\nChoose target layer: ";

            askForLayerName.Keywords.Add(originLayerName);
            askForLayerName.Keywords.Add(intermediateLayerName);
            askForLayerName.Keywords.Add(gatheringLineLayerName);
            askForLayerName.Keywords.Add(destinationLayerName);
            askForLayerName.Keywords.Add(avoidanceLayerName);
            askForLayerName.Keywords.Add(wallLayerName);
            askForLayerName.Keywords.Add(solidLayerName);
            askForLayerName.Keywords.Add(graphLayerName);
            
            askForLayerName.AllowNone = false;
            PromptResult layerNameAnswer = currentDocument.Editor.GetKeywords(askForLayerName);

            if (layerNameAnswer.Status != PromptStatus.OK) 
            {
                Autodesk.AutoCAD.ApplicationServices.Application.ShowAlertDialog("Entered keyword: " +  layerNameAnswer.StringResult);
                return;
            }

            String layerName = layerNameAnswer.StringResult;

            using (DocumentLock docLock = currentDocument.LockDocument())
            {
                using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
                {
                    LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);
                    PromptSelectionResult objectsToChange = editor.GetSelection();
                    short colorID = 0;

                    if (!layers.Has(layerName))
                    {

                        if (layerName == originLayerName)
                        {
                            colorID = originColorCode;
                        }
                        else if (layerName == intermediateLayerName)
                        {
                            colorID = intermediateColorCode;
                        }
                        else if (layerName == gatheringLineLayerName)
                        {
                            colorID = gatheringLineColorCode;
                        }
                        else if (layerName == destinationLayerName)
                        {
                            colorID = destinationColorCode;
                        }
                        else if (layerName == avoidanceLayerName)
                        {
                            colorID = avoidanceColorCode;
                        }
                        else if (layerName == wallLayerName)
                        {
                            colorID = wallColorCode;
                        }
                        else if (layerName == solidLayerName)
                        {
                            colorID = solidColorCode;
                        }
                        else if (layerName == graphLayerName)
                        {
                            colorID = graphColorCode;
                        }

                        createLayer(layerName, colorID, currentDocument, transaction);
                    }

                    if (objectsToChange.Status == PromptStatus.OK)
                    {
                        SelectionSet selectedObjects = objectsToChange.Value;

                        foreach (SelectedObject selectedItem in selectedObjects)
                        {
                            if (selectedItem != null)
                            {
                                try
                                {
                                    Entity currentObject = (Entity)transaction.GetObject(selectedItem.ObjectId, OpenMode.ForWrite);

                                    if (currentObject != null)
                                    {
                                        currentObject.Layer = layerName;
                                    }
                                }
                                catch (Autodesk.AutoCAD.Runtime.Exception e)
                                {
                                    editor.WriteMessage("\nSomething bad happened at 'selectObjectsForConversion': " + e.ToString());
                                }
                            }
                        }
                    }

                    transaction.Commit();
                }
            }
        }

        [CommandMethod("ped_hideMomenTumLayers", CommandFlags.Session)]
        public void hideMomenTumLayers()
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            using (DocumentLock docLock = currentDocument.LockDocument())
            {
                using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
                {
                    LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForWrite);

                    foreach (ObjectId currentLayerId in layers)
                    {
                        LayerTableRecord currentLayer = (LayerTableRecord)transaction.GetObject(currentLayerId, OpenMode.ForWrite);
                        String layerName = currentLayer.Name;

                        if (layerName.Equals(originLayerName) || layerName.Equals(destinationLayerName) || layerName.Equals(intermediateLayerName) 
                            || layerName.Equals(gatheringLineLayerName) || layerName.Equals(graphLayerName) || layerName.Equals(avoidanceLayerName)
                            || layerName.Equals(wallLayerName) || layerName.Equals(solidLayerName))
                        {
                            currentLayer.IsOff = true;
                        }
                        else
                        {
                            currentLayer.IsOff = false;
                        }
                    }

                    transaction.Commit();
                }
            }
        }

        [CommandMethod("ped_hideExternalLayers", CommandFlags.Session)]
        public void hideExternalLayers()
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            using (DocumentLock docLock = currentDocument.LockDocument())
            {
                using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
                {
                    LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForWrite);

                    foreach (ObjectId currentLayerId in layers)
                    {
                        LayerTableRecord currentLayer = (LayerTableRecord)transaction.GetObject(currentLayerId, OpenMode.ForWrite);
                        String layerName = currentLayer.Name;

                        if (layerName.Equals(originLayerName) || layerName.Equals(destinationLayerName) || layerName.Equals(intermediateLayerName)
                            || layerName.Equals(gatheringLineLayerName) || layerName.Equals(graphLayerName) || layerName.Equals(avoidanceLayerName)
                            || layerName.Equals(wallLayerName) || layerName.Equals(solidLayerName))
                        {
                            currentLayer.IsOff = false;
                        }
                        else
                        {
                            currentLayer.IsOff = true;
                        }
                    }

                    transaction.Commit();
                }
            }
        }

        [CommandMethod("ped_triangulateAllObstaclePolygons", CommandFlags.Session)]
        public void triangulateAllObstaclePolygons()
        {
            activateConvertScenarioPlugin();
            joinTouchingLines();
            closeOpenedPolygons();

            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            TypedValue[] tvs = new TypedValue[] {
                        new TypedValue((int)DxfCode.Start, "LWPOLYLINE"),
                        new TypedValue((int)DxfCode.Operator, "<AND"),
                        new TypedValue((int)DxfCode.LayerName, "Solid"),
                        new TypedValue((int)DxfCode.Operator, "AND>")
                    };
            SelectionFilter filter = new SelectionFilter(tvs);
            PromptSelectionResult selection = editor.SelectAll(filter);

            if (selection.Status == PromptStatus.OK)
            {
                ObjectId[] allLines = selection.Value.GetObjectIds();

                int oldCounter = allLines.Length;
                int newCounter = int.MaxValue;

                while (oldCounter != newCounter)
                {
                    oldCounter = allLines.Length;
                    using (DocumentLock docLock = currentDocument.LockDocument())
                    {
                        using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
                        {
                            foreach (ObjectId currentLineID in allLines)
                            {
                                Polyline currentLine = (Polyline)transaction.GetObject(currentLineID, OpenMode.ForRead);

                                if (currentLine.NumberOfVertices > 3)
                                {
                                    for (int i = 0; i < currentLine.NumberOfVertices; i++)
                                    {
                                        int overnextIterator = 0;
                                        int nextIterator = 0;

                                        if (i == currentLine.NumberOfVertices - 2)
                                        {
                                            overnextIterator = 1;
                                            nextIterator = i + 1;
                                        }
                                        else if (i == currentLine.NumberOfVertices - 1)
                                        {
                                            overnextIterator = 2;
                                            nextIterator = 1;
                                        }
                                        else
                                        {
                                            overnextIterator = i + 2;
                                            nextIterator = i + 1;
                                        }

                                        Point3d currentPoint = currentLine.GetPoint3dAt(i);
                                        Point3d nextPoint = currentLine.GetPoint3dAt(nextIterator);
                                        Point3d overnextPoint = currentLine.GetPoint3dAt(overnextIterator);
                                        LineSegment3d connectingLine = new LineSegment3d(currentPoint, overnextPoint);
                                        Point3d midPoint = connectingLine.MidPoint;

                                        if (nextPointIsEar(currentPoint, nextPoint, overnextPoint, currentLine))
                                        {
                                            reducePolyline(currentLine, nextPoint, transaction, currentDocument);
                                            break;
                                        }
                                    }
                                }
                            }

                            transaction.Commit();
                        }

                        allLines = editor.SelectAll(filter).Value.GetObjectIds();
                        newCounter = allLines.Length;
                    }
                }
            }
            else
            {
                editor.WriteMessage("\nThere are no solid objects to triangulate. Please execute 'ped_allocateObjectsToLayers' or switch your solid obstacles manually to fitting layers via 'ped_selectObjectsForConversion'.");
            }
        }

        // This Method should be executed after all scenario objects have been created and are ready for conversion.
        [CommandMethod("ped_convertAllExistingObjects", CommandFlags.Session)]
        public void convertAllExistingObjects() 
        {
            activateConvertScenarioPlugin();
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            adjustMeasurement();

            // geometryList is used to collect all drawing objects from those layers used for the pedestrian simulation. This list will be
            // transferred to the XMLconverter to filter the objects by their layer and geometry and write them into the .xml-file.
            List<Polygon2D> originList = new List<Polygon2D>();
            List<Polygon2D> intermediateList = new List<Polygon2D>();
            List<Segment2D> gatheringLineList = new List<Segment2D>();
            List<Segment2D> graphList = new List<Segment2D>();
            List<Polygon2D> avoidanceList = new List<Polygon2D>();
            List<Polygon2D> destinationList = new List<Polygon2D>();
            List<Segment2D> wallList = new List<Segment2D>();
            List<Polygon2D> solidList = new List<Polygon2D>();
            Dictionary<String, List<Polygon2D>> taggedAreaLists = new Dictionary<String, List<Polygon2D>>();

            using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
            {

                LayerTable layerTable = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);
                TypedValue[] filterValue = new TypedValue[1];

                PromptSelectionResult filteredObjects = null;
                ObjectId[] filteredObjectsIDs = null;

                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, originLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    originList = this.polygonDrawingsToGeometry(filteredObjectsIDs, originLayerName, currentDocument, transaction);
                }


                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, intermediateLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    intermediateList = this.polygonDrawingsToGeometry(filteredObjectsIDs, intermediateLayerName, currentDocument, transaction);
                }

                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, gatheringLineLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    gatheringLineList = this.segmentDrawingsToGeometry(filteredObjectsIDs, gatheringLineLayerName, currentDocument, transaction);
                }

                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, destinationLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    destinationList = this.polygonDrawingsToGeometry(filteredObjectsIDs, destinationLayerName, currentDocument, transaction);
                }

                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, avoidanceLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    avoidanceList = this.polygonDrawingsToGeometry(filteredObjectsIDs, avoidanceLayerName, currentDocument, transaction);
                }

                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, wallLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    wallList = this.segmentDrawingsToGeometry(filteredObjectsIDs, wallLayerName, currentDocument, transaction);
                }


                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, solidLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    solidList = this.polygonDrawingsToGeometry(filteredObjectsIDs, solidLayerName, currentDocument, transaction);
                }

                filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, graphLayerName), 0);
                filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                if (filteredObjects.Status == PromptStatus.OK)
                {
                    filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                    graphList = this.segmentDrawingsToGeometry(filteredObjectsIDs, graphLayerName, currentDocument, transaction);
                }

                List<String> taggedAreaLayerNames = getLayersFromLayerFilter(taggedAreasLayerFilterName);
                foreach(String currentLayerName in taggedAreaLayerNames)
                {
                    filterValue.SetValue(new TypedValue((int)DxfCode.LayerName, currentLayerName), 0);
                    filteredObjects = editor.SelectAll(new SelectionFilter(filterValue));

                    if (filteredObjects.Status == PromptStatus.OK)
                    {
                        filteredObjectsIDs = filteredObjects.Value.GetObjectIds();
                        List<Polygon2D> currentLayerPolygonList = this.polygonDrawingsToGeometry(filteredObjectsIDs, currentLayerName, currentDocument, transaction);
                        taggedAreaLists.Add(currentLayerName, currentLayerPolygonList);

                    }
                }

                transaction.Commit();
            }

            xmlConverter.convertGivenListsToXML(originList,
                intermediateList,
                gatheringLineList,
                destinationList,
                avoidanceList,
                wallList,
                solidList,
                graphList,
                taggedAreaLists);

            xmlConverter.reset();
            GeometryFactory.reset();
        }

        [CommandMethod("ped_importXMLtoDrawing", CommandFlags.Session)]
        public void importXMLtoDrawing()
        {
            activateConvertScenarioPlugin();
            xmlConverter.convertXMLtoDrawing();
            Object currentApplication = Autodesk.AutoCAD.ApplicationServices.Application.AcadApplication;
            currentApplication.GetType().InvokeMember("ZoomExtents", BindingFlags.InvokeMethod, null, currentApplication, null);
        }

        // This method creates all layers necessary for the pedestrian simulation scenario if they do not exist yet.
        public static void activateConvertScenarioPlugin()
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;

            using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
            {
                LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);

                if (!layers.Has(originLayerName))
                {
                    createLayer(originLayerName, originColorCode, currentDocument, transaction);
                }
                if (!layers.Has(intermediateLayerName))
                {
                    createLayer(intermediateLayerName, intermediateColorCode, currentDocument, transaction);
                }
                if (!layers.Has(gatheringLineLayerName))
                {
                    createLayer(gatheringLineLayerName, gatheringLineColorCode, currentDocument, transaction);
                }
                if (!layers.Has(destinationLayerName))
                {
                    createLayer(destinationLayerName, destinationColorCode, currentDocument, transaction);
                }
                if (!layers.Has(avoidanceLayerName))
                {
                    createLayer(avoidanceLayerName, avoidanceColorCode, currentDocument, transaction);
                }
                if (!layers.Has(wallLayerName))
                {
                    createLayer(wallLayerName, wallColorCode, currentDocument, transaction);
                }
                if (!layers.Has(solidLayerName))
                {
                    createLayer(solidLayerName, solidColorCode, currentDocument, transaction);
                }
                if (!layers.Has(graphLayerName))
                {
                    createLayer(graphLayerName, graphColorCode, currentDocument, transaction);
                }


                if (!isLayerFilterExistent(taggedAreasLayerFilterName))
                {
                    // hidden feature at the moment, due to the introduction of a different concept
                    //createLayerGroup(taggedAreasLayerFilterName, currentDocument, transaction);
                }
                

                transaction.Commit();
            }

            
        }

        #endregion Commands

        #region PrivateMethods

        private static ObjectIdCollection getAllLineObjectsFromModelspace(Document currentDocument)
        {
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            ObjectIdCollection lineObjectsIDs = new ObjectIdCollection();

            using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
            {
                BlockTable blockTable = (BlockTable)transaction.GetObject(currentDatabase.BlockTableId, OpenMode.ForRead);
                BlockTableRecord modelSpace = (BlockTableRecord)transaction.GetObject(SymbolUtilityServices.GetBlockModelSpaceId(currentDatabase),
                                                                         OpenMode.ForRead);

                foreach (ObjectId currentID in modelSpace)
                {
                    Entity currentEntity = (Entity)transaction.GetObject(currentID, OpenMode.ForRead);

                    if ((currentEntity.GetType() == typeof(Polyline) && ((Polyline)currentEntity).Closed == false)
                        || currentEntity.GetType() == typeof(Line))
                    {
                        lineObjectsIDs.Add(currentID);
                    }
                }

                transaction.Commit();
            }

            return lineObjectsIDs;
        }

        private static void joinTouchingLines()
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            BlockTable blockTable = null;
            BlockTableRecord modelSpace = null;
            ObjectIdCollection allLineObjectsIDs = getAllLineObjectsFromModelspace(currentDocument);
            allLinesToPolylines(currentDocument);

            int oldObjectsCount = allLineObjectsIDs.Count;
            int newObjectsCount = int.MaxValue;

            while (newObjectsCount != oldObjectsCount)
            {
                bool somethingChanged = false;

                allLineObjectsIDs = getAllLineObjectsFromModelspace(currentDocument);

                foreach (ObjectId currentID in allLineObjectsIDs)
                {
                    if (somethingChanged == false)
                    {
                        using (DocumentLock docLock = currentDocument.LockDocument())
                        {
                            using (Transaction transaction = currentDocument.TransactionManager.StartTransaction())
                            {
                                blockTable = (BlockTable)transaction.GetObject(currentDatabase.BlockTableId, OpenMode.ForWrite);
                                modelSpace = (BlockTableRecord)transaction.GetObject(blockTable[BlockTableRecord.ModelSpace], OpenMode.ForWrite);
                                Polyline currentLine = (Polyline)transaction.GetObject(currentID, OpenMode.ForRead);

                                if (currentLine.Closed == false)
                                {
                                    ObjectIdCollection allAttachedLines = getAllAttachedLines(currentLine, allLineObjectsIDs, transaction);

                                    foreach (ObjectId currentAttachedID in allAttachedLines)
                                    {
                                        Polyline currentAttachedLine = (Polyline)transaction.GetObject(currentAttachedID, OpenMode.ForRead);
                                        List<Point3d> verticesList = new List<Point3d>();

                                        if (currentLine.EndPoint.DistanceTo(currentAttachedLine.StartPoint) <= pointEqualityTolerance)
                                        {
                                            for (int i = 0; i < currentLine.NumberOfVertices; i++)
                                            {
                                                verticesList.Add(currentLine.GetPoint3dAt(i));
                                            }
                                            for (int j = 0; j < currentAttachedLine.NumberOfVertices; j++) 
                                            {
                                                verticesList.Add(currentAttachedLine.GetPoint3dAt(j));
                                            }

                                            somethingChanged = true;
                                        }
                                        else if (currentLine.EndPoint.DistanceTo(currentAttachedLine.EndPoint) <= pointEqualityTolerance)
                                        {
                                            for (int i = 0; i < currentLine.NumberOfVertices; i++)
                                            {
                                                verticesList.Add(currentLine.GetPoint3dAt(i));
                                            }
                                            for (int j = (currentAttachedLine.NumberOfVertices - 1); j >= 0; j--)
                                            {
                                                verticesList.Add(currentAttachedLine.GetPoint3dAt(j));
                                            }

                                            somethingChanged = true;
                                        }
                                        else if (currentLine.StartPoint.DistanceTo(currentAttachedLine.EndPoint) <= pointEqualityTolerance)
                                        {
                                            for (int i = 0; i < currentAttachedLine.NumberOfVertices; i++)
                                            {
                                                verticesList.Add(currentAttachedLine.GetPoint3dAt(i));
                                            }
                                            for (int j = 0; j < currentLine.NumberOfVertices; j++)
                                            {
                                                verticesList.Add(currentLine.GetPoint3dAt(j));
                                            }

                                            somethingChanged = true;
                                        }
                                        else if (currentLine.StartPoint.DistanceTo(currentAttachedLine.StartPoint) <= pointEqualityTolerance)
                                        {
                                            for (int i = (currentLine.NumberOfVertices - 1); i >= 0; i--)
                                            {
                                                verticesList.Add(currentLine.GetPoint3dAt(i));
                                            }
                                            for (int j = 0; j < currentAttachedLine.NumberOfVertices; j++) 
                                            {
                                                verticesList.Add(currentAttachedLine.GetPoint3dAt(j));
                                            }

                                            somethingChanged = true;
                                        }

                                        if (somethingChanged) 
                                        {
                                            Polyline newLine = new Polyline();
                                            Point3d[] verticesArray = verticesList.ToArray();

                                            for (int v = 0; v < verticesArray.Length; v++) 
                                            {
                                                newLine.AddVertexAt(v, new Point2d(verticesArray[v].X, verticesArray[v].Y), 0, 0, 0);
                                            }

                                            if (verticesArray[0].IsEqualTo(verticesArray[verticesArray.Length - 1]))
                                            {
                                                newLine.Closed = true;
                                            }

                                            newLine.LayerId = currentLine.LayerId;
                                            modelSpace.AppendEntity(newLine);
                                            transaction.AddNewlyCreatedDBObject(newLine, true);

                                            currentLine.UpgradeOpen();
                                            currentLine.Erase(true);
                                            currentAttachedLine.UpgradeOpen();
                                            currentAttachedLine.Erase(true);
                                        }
                                    }
                                }

                                transaction.Commit();
                            }
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                allLineObjectsIDs = getAllLineObjectsFromModelspace(currentDocument);
                oldObjectsCount = newObjectsCount;
                newObjectsCount = allLineObjectsIDs.Count;
            }
        }

        private static void closeOpenedPolygons()
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            ObjectIdCollection allPolylineIDs = null;

            try
            {
                PromptSelectionResult selectionResult = null;

                TypedValue[] tvs = new TypedValue[] {new TypedValue((int)DxfCode.Start, "LWPOLYLINE")};

                SelectionFilter filter = new SelectionFilter(tvs);
                selectionResult = editor.SelectAll(filter);

                if (selectionResult.Status == PromptStatus.OK)
                {
                    allPolylineIDs = new ObjectIdCollection(selectionResult.Value.GetObjectIds());
                }
                else
                {
                    allPolylineIDs = new ObjectIdCollection();
                }

                foreach (ObjectId polylineID in allPolylineIDs)
                {
                    using (DocumentLock docLock = currentDocument.LockDocument())
                    {
                        using (Transaction transaction = currentDocument.TransactionManager.StartTransaction())
                        {
                            Polyline currentPolyline = (Polyline)transaction.GetObject(polylineID, OpenMode.ForRead);
                            Point3d startVertex = currentPolyline.StartPoint;
                            Point3d endVertex = currentPolyline.EndPoint;

                            if (startVertex.IsEqualTo(endVertex) && currentPolyline.Closed == false)
                            {
                                currentPolyline.UpgradeOpen();
                                currentPolyline.Closed = true;
                            }

                            transaction.Commit();
                        }
                    }
                }
            }
            catch (System.Exception ex)
            {
                editor.WriteMessage("\nSomething bad happened at closing all polylines " + ex.ToString());
            }
        }

        private static void adjustMeasurement()
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            PromptKeywordOptions askForMeasurement = new PromptKeywordOptions("");
            askForMeasurement.Message = "\nAdjust drawing unit: ";
            askForMeasurement.Keywords.Add("Millimeter");
            askForMeasurement.Keywords.Add("Centimeter");
            askForMeasurement.Keywords.Add("Decimeter");
            askForMeasurement.Keywords.Add("WholeMeter");
            askForMeasurement.AllowNone = false;
            PromptResult measurementAnswer = currentDocument.Editor.GetKeywords(askForMeasurement);
            String measurementResult = measurementAnswer.StringResult;

            if (measurementAnswer.Status != PromptStatus.OK)
            {
                Autodesk.AutoCAD.ApplicationServices.Application.ShowAlertDialog("Entered keyword: " + measurementAnswer.StringResult);
                return;
            }
            else
            {
                switch (measurementResult)
                {
                    case "Millimeter":
                        measurementUnitFactor = 0.001;
                        editor.WriteMessage("\nDrawing in millimeters [mm] -> Factor set to " + measurementUnitFactor.ToString());
                        break;

                    case "Centimeter":
                        measurementUnitFactor = 0.01;
                        editor.WriteMessage("\nDrawing in centimeters [cm] -> Factor set to " + measurementUnitFactor.ToString());
                        break;

                    case "Decimeter":
                        measurementUnitFactor = 0.1;
                        editor.WriteMessage("\nDrawing in decimeters [dm] -> Factor set to " + measurementUnitFactor.ToString());
                        break;

                    case "WholeMeter":
                        measurementUnitFactor = 1;
                        editor.WriteMessage("\nDrawing in meters [m] -> Factor set to " + measurementUnitFactor.ToString());
                        break;
                }
            }
        }

        private static void allLinesToPolylines(Document currentDocument) 
        {
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            ObjectIdCollection allLineObjectsIDs = getAllLineObjectsFromModelspace(currentDocument);

            using (DocumentLock docLoc = currentDocument.LockDocument())
            {
                using (Transaction transaction = currentDocument.TransactionManager.StartTransaction())
                {
                    BlockTable blockTable = (BlockTable)transaction.GetObject(currentDatabase.BlockTableId, OpenMode.ForRead);
                    BlockTableRecord modelSpace = (BlockTableRecord)transaction.GetObject(blockTable[BlockTableRecord.ModelSpace], OpenMode.ForWrite);
                    LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForRead);
                    Polyline newLine = null;

                    foreach (ObjectId currentID in allLineObjectsIDs)
                    {
                        Entity oldLine = (Entity)transaction.GetObject(currentID, OpenMode.ForRead);

                        if (oldLine.GetType() == typeof(Line))
                        {
                            newLine = new Polyline();
                            newLine.AddVertexAt(0, new Point2d(((Line)oldLine).StartPoint.X, ((Line)oldLine).StartPoint.Y), 0, 0, 0);
                            newLine.AddVertexAt(1, new Point2d(((Line)oldLine).EndPoint.X, ((Line)oldLine).EndPoint.Y), 0, 0, 0);
                            newLine.LayerId = oldLine.LayerId;
                            modelSpace.AppendEntity(newLine);
                            transaction.AddNewlyCreatedDBObject(newLine, true);

                            oldLine.UpgradeOpen();
                            oldLine.Erase(true);
                        }
                    }

                    transaction.Commit();
                }
            }
        }

        private static ObjectIdCollection getAllAttachedLines(Polyline currentLine, ObjectIdCollection allLines, Transaction transaction) 
        {
            ObjectIdCollection attachedLinesIDs = new ObjectIdCollection();

            foreach (ObjectId currentAttachedtID in allLines) 
            {
                Polyline currentAttachingLine = (Polyline)transaction.GetObject(currentAttachedtID, OpenMode.ForRead);
                    
                if (!currentLine.Id.Equals(currentAttachedtID) && currentAttachingLine.Closed == false 
                    && currentLine.LayerId.Equals(currentAttachingLine.LayerId)
                    && (currentLine.StartPoint.DistanceTo(currentAttachingLine.StartPoint) <= pointEqualityTolerance 
                    || currentLine.StartPoint.DistanceTo(currentAttachingLine.EndPoint) <= pointEqualityTolerance
                    || currentLine.EndPoint.DistanceTo(currentAttachingLine.StartPoint) <= pointEqualityTolerance
                    || currentLine.EndPoint.DistanceTo(currentAttachingLine.EndPoint) <= pointEqualityTolerance)) 
                {
                    attachedLinesIDs.Add(currentAttachedtID);
                }
            }

            return attachedLinesIDs;
        }

        private static void createLayer(String layerName, short colorID, Document currentDocument, Transaction transaction) 
        {
            Database currentDatabase = currentDocument.Database;

            using (DocumentLock docLock = currentDocument.LockDocument())
            {
                LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForWrite);
                LayerTableRecord newLayer = new LayerTableRecord();
                newLayer.Name = layerName;
                newLayer.Color = Color.FromColorIndex(ColorMethod.ByAci, colorID);
                layers.Add(newLayer);
                transaction.AddNewlyCreatedDBObject(newLayer, true);
            }   
        }

        private static void createLayerGroup(String layerGroupName, Document currentDocument, Transaction transaction)
        {
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            try
            {
                using (DocumentLock docLock = currentDocument.LockDocument())
                {
                    LayerFilterTree lft = currentDatabase.LayerFilters;
                    LayerFilterCollection lfc = lft.Root.NestedFilters;

                    LayerGroup lg = new LayerGroup();
                    lg.Name = layerGroupName;

                    lfc.Add(lg);
                    currentDatabase.LayerFilters = lft;
                }


            } catch (Autodesk.AutoCAD.Runtime.Exception ex)
            {
                editor.WriteMessage("\nException: Could not create LayerGroup: " + layerGroupName);
            }
        }

        private static bool isLayerFilterExistent(String layerFilterName)
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            LayerFilterCollection lfc = currentDatabase.LayerFilters.Root.NestedFilters;

            foreach (LayerFilter curLayerFilter in lfc)
            {
                if (curLayerFilter.Name == layerFilterName)
                    return true;
            }
            return false;
        }

        private static List<String> getLayersFromLayerFilter(String layerFilterName)
        {
            List<String> filteredLayerNameList = new List<String>();
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            LayerFilterCollection lfc = currentDatabase.LayerFilters.Root.NestedFilters;
            Editor editor = currentDocument.Editor;
            LayerFilter desiredLayerFilter = null;

            foreach(LayerFilter curLayerFilter in lfc)
            {
                if (curLayerFilter.Name == layerFilterName)
                {
                    desiredLayerFilter = curLayerFilter;
                }
            }
            if(desiredLayerFilter == null)
            {
                return filteredLayerNameList;
            }

            using (DocumentLock docLock = currentDocument.LockDocument())
            {
                using (Transaction transaction = currentDatabase.TransactionManager.StartTransaction())
                {
                    LayerTable layers = (LayerTable)transaction.GetObject(currentDatabase.LayerTableId, OpenMode.ForWrite);
                    foreach (ObjectId currentLayerId in layers)
                    {
                        LayerTableRecord currentLayer = (LayerTableRecord)transaction.GetObject(currentLayerId, OpenMode.ForWrite);

                        if(desiredLayerFilter.Filter(currentLayer))
                        {
                            filteredLayerNameList.Add(currentLayer.Name);
                        }
                    }
                    transaction.Commit();
                }
            }

            return filteredLayerNameList;
            }

        private List<Polygon2D> polygonDrawingsToGeometry(ObjectId[] drawingObjectIDs, String referringLayer, Document currentDocument, Transaction transaction) 
        {
            Editor editor = currentDocument.Editor;
            List<Polygon2D> geometryList = new List<Polygon2D>();

            if (drawingObjectIDs.Length > 0) 
            {
                try 
                {
                    for (int i = 0; i < drawingObjectIDs.Length; i++) 
                    {
                        Polyline currentObject = (Polyline)transaction.GetObject(drawingObjectIDs[i], OpenMode.ForRead);                            
                        String type = currentObject.GetType().ToString();

                        if (currentObject.GetType() == typeof(Polyline) && currentObject.Closed == true)
                        {
                            geometryList.Add(GeometryFactory.polylineToPolygon2D(currentObject, referringLayer, measurementUnitFactor));
                        }
                        else 
                        {
                            editor.WriteMessage("\nPolygon has to be closed to fit the " + referringLayer + " area specification! " + type);
                        }
                    }
                }
                catch (Autodesk.AutoCAD.Runtime.Exception exception) 
                {
                    editor.WriteMessage("\nSomething bad happened in polygonDrawingsToGeometry()" + exception.ToString());
                }
            }

            return geometryList;
        }

        private List<Segment2D> segmentDrawingsToGeometry(ObjectId[] drawingObjectIDs, String referringLayer, Document currentDocument, Transaction transaction)
        {
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;
            List<Segment2D> geometryList = new List<Segment2D>();

            if (drawingObjectIDs.Length > 0) 
            { 
                try 
                {
                    Polyline currentObject = null;

                    for (int i = 0; i < drawingObjectIDs.Length; i++) 
                    {
                        currentObject = (Polyline)transaction.GetObject(drawingObjectIDs[i], OpenMode.ForRead);
                        geometryList.AddRange(GeometryFactory.polylineToSegment2D((Polyline)currentObject, referringLayer, measurementUnitFactor));
                    }
                }
                catch (Autodesk.AutoCAD.Runtime.Exception e) 
                {
                    currentDocument.Editor.WriteMessage("\nSomething bad happened at 'changeDrawingsToGeometry': " + e.ToString());
                }
            }

            return geometryList;
        }

        private static bool nextPointIsEar(Point3d currentPoint, Point3d nextPoint, Point3d overnextPoint, Polyline currentPolyline)
        {
            bool isEar = true;

            LineSegment3d connectingLine = new LineSegment3d(currentPoint, overnextPoint);
            Point3d midPoint = connectingLine.MidPoint;

            if (!pointIsInsidePolygon(currentPolyline, midPoint))
            {
                isEar = false;
            }
            else
            {
                isEar = true;
            }

            return isEar;
        }

        private static void reducePolyline(Polyline oldLine, Point3d vertexToRemove, Transaction transaction, Document currentDocument)
        {
            Polyline newLine = new Polyline();
            int vertexIterator = 0;

            BlockTable blockTable = (BlockTable)transaction.GetObject(currentDocument.Database.BlockTableId, OpenMode.ForRead);
            BlockTableRecord modelSpace = (BlockTableRecord)transaction.GetObject(blockTable[BlockTableRecord.ModelSpace], OpenMode.ForWrite);

            for (int i = 0; i < oldLine.NumberOfVertices; i++)
            {
                if (!vertexToRemove.IsEqualTo(oldLine.GetPoint3dAt(i)))
                {
                    newLine.AddVertexAt(vertexIterator, new Point2d(oldLine.GetPoint3dAt(i).X, oldLine.GetPoint3dAt(i).Y), 0, 0, 0);
                    vertexIterator++;
                }
                else 
                {
                    Polyline triangle = new Polyline();
                    int firstIterator;
                    int secondIterator;
                    int thirdIterator;

                    if (i == 0)
                    {
                        firstIterator = oldLine.NumberOfVertices - 2;
                        secondIterator = i;
                        thirdIterator = i + 1;
                    }
                    else if (i == oldLine.NumberOfVertices - 1)
                    {
                        firstIterator = i - 1;
                        secondIterator = i;
                        thirdIterator = 1;
                    }
                    else 
                    {
                        firstIterator = i - 1;
                        secondIterator = i;
                        thirdIterator = i + 1;
                    }

                    triangle.AddVertexAt(0, new Point2d(oldLine.GetPoint3dAt(firstIterator).X, oldLine.GetPoint3dAt(firstIterator).Y), 0, 0, 0);
                    triangle.AddVertexAt(1, new Point2d(oldLine.GetPoint3dAt(secondIterator).X, oldLine.GetPoint3dAt(secondIterator).Y), 0, 0, 0);
                    triangle.AddVertexAt(2, new Point2d(oldLine.GetPoint3dAt(thirdIterator).X, oldLine.GetPoint3dAt(thirdIterator).Y), 0, 0, 0);
                    triangle.Closed = true;
                    triangle.LayerId = oldLine.LayerId;
                    modelSpace.AppendEntity(triangle);
                    transaction.AddNewlyCreatedDBObject(triangle, true);
                }
            }

            newLine.LayerId = oldLine.LayerId;
            newLine.Closed = true;
            modelSpace.AppendEntity(newLine);
            transaction.AddNewlyCreatedDBObject(newLine, true);
            oldLine.UpgradeOpen();
            oldLine.Erase(true);
        }

        private static bool polylineContainsAllOtherDrawings(Polyline containerPolyline, ObjectId[] contentObjects, Transaction transaction) 
        {
            bool containsAll = false;

            if (containerPolyline.Closed == true) 
            {
                foreach (ObjectId currentContentObjectID in contentObjects) 
                {
                    Polyline currentContentObject = (Polyline)transaction.GetObject(currentContentObjectID, OpenMode.ForRead);

                    if (!currentContentObject.Equals(containerPolyline)) 
                    {
                        for (int i = 0; i < currentContentObject.NumberOfVertices; i++)
                        {
                            if (pointIsInsidePolygon(containerPolyline, currentContentObject.GetPoint3dAt(i))) 
                            {
                                containsAll = true;
                            }
                            else 
                            {
                                containsAll = false;
                                break;
                            }
                        }
                    }
                }
            }
            
            return containsAll;
        }

        // Code from http://forums.autodesk.com/t5/net/is-point-inside-polygon-or-block-s-area/td-p/3164508#
        private static bool pointIsInsidePolygon(Polyline polygon, Point3d point) 
        {
            int n = polygon.NumberOfVertices;
            double angle = 0;
            Vector2D point1 = new Vector2D();
            Vector2D point2 = new Vector2D();

            for (int i = 0; i < n; i++)
            {
                point1.setXCoordinate(polygon.GetPoint3dAt(i).X - point.X);
                point1.setYCoordinate(polygon.GetPoint3dAt(i).Y - point.Y);
                point2.setXCoordinate(polygon.GetPoint3dAt((i + 1) % n).X - point.X);
                point2.setYCoordinate(polygon.GetPoint3dAt((i + 1) % n).Y - point.Y);
                angle += Angle2D(point1.getCoordinates()[0], point1.getCoordinates()[1], point2.getCoordinates()[0], point2.getCoordinates()[1]);
            }

            if (Math.Abs(angle) < Math.PI)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        // Code from http://forums.autodesk.com/t5/net/is-point-inside-polygon-or-block-s-area/td-p/3164508#
        private static double Angle2D(double x1, double y1, double x2, double y2) 
        {
            double dtheta, theta1, theta2;

            theta1 = Math.Atan2(y1, x1);
            theta2 = Math.Atan2(y2, x2);
            dtheta = theta2 - theta1;

            while (dtheta > Math.PI) 
            {
                dtheta -= (Math.PI * 2);
            }

            while (dtheta < -Math.PI) 
            {
                dtheta += (Math.PI * 2);
            }
                
            return (dtheta);
        }
        
        #endregion
    }
}
