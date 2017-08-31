// (C) Copyright 2015 by berndtornede, pk
//
using Autodesk.AutoCAD.ApplicationServices;
using Autodesk.AutoCAD.DatabaseServices;
using Autodesk.AutoCAD.EditorInput;
using Autodesk.AutoCAD.Geometry;
using Autodesk.AutoCAD.Windows;
using MomenTumV2CadLayouting.Converter;
using MomenTumV2CadLayouting.Geometry;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Xml;

namespace MomenTumV2CadLayouting
{
    class XMLconverter_MomenTumV2 : XMLconverter
    {
        private static int decimalPlaces = 4;
        private static String version_Value = "MomenTumV2.0.0";
        private static String simulationName_Value = "AutCADLayout";
        private static String graphType_Value = "Raw";

        private static int scenarioID = 0;
        private static int graphID = 0;
        private static int areaID = 0;
        private static int taggedAreaID = 0;
        private static int obstacleID = 0;

        private static String simulator_Node = "simulator";
        private static String version_Attribute = "version";
        private static String simulationName_Attribute = "simulationName";
        private static String layouts_Node = "layouts";
        private static String scenario_Node = "scenario";
        private static String scenarioName_Value = "pedSim";
        private static String maxX_Attribute = "maxX";
        private static String maxY_Attribute = "maxY";
        private static String minX_Attribute = "minX";
        private static String minY_Attribute = "minY";
        private static String graph_Node = "graph";
        private static String vertex_Node = "vertex";
        private static String center_Node = "center";
        private static String edge_Node = "edge";
        private static String idLeft_Attribute = "idLeft";
        private static String idRight_Attribute = "idRight";
        private static String area_Node = "area";
        private static String originArea_Value = "Origin";
        private static String intermediateArea_Value = "Intermediate";
        private static String gatheringLine_Node = "gatheringLine";
        private static String destinationArea_Value = "Destination";
        private static String avoidanceArea_Value = "Avoidance";
        private static String taggedArea_Node = "taggedArea";
        private static String obstacle_Node = "obstacle";
        private static String wall_Value = "Wall";
        private static String solid_Value = "Solid";
        private static String id_Attribute = "id";
        private static String name_Attribute = "name";
        private static String type_Attribute = "type";
        private static String point_Node = "point";
        private static String x_Attribute = "x";
        private static String y_Attribute = "y";
        private static XmlFactory xmlFactory = new XmlFactory();


        public void convertGivenListsToXML(
            List<Geometry.Polygon2D> originList, 
            List<Geometry.Polygon2D> intermediateList,
            List<Geometry.Segment2D> gatheringLineList,
            List<Geometry.Polygon2D> destinationList,
            List<Geometry.Polygon2D> avoidanceList,
            List<Geometry.Segment2D> wallList, 
            List<Geometry.Polygon2D> solidList,
            List<Geometry.Segment2D> graphList,
            Dictionary<String, List<Polygon2D>> taggedAreaList) 
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Editor editor = currentDocument.Editor;
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            settings.IndentChars = "  ";
            XmlWriter writer = null;

            HostApplicationServices hs = HostApplicationServices.Current;
            String drawingFilePath = "";

            try
            {
               drawingFilePath = hs.FindFile(currentDocument.Name, currentDocument.Database, FindFileHint.Default);
            }
            catch (Exception)
            {
                drawingFilePath = currentDocument.Database.Filename;
            }
            
            String targetPath = drawingFilePath.Remove(drawingFilePath.Count() - 4) + "_pedSimScenario.xml";
            SaveFileDialog fileDialog = new SaveFileDialog("Choose target folder:", targetPath, "xml", "XMLfileToLink", SaveFileDialog.SaveFileDialogFlags.DoNotTransferRemoteFiles);
            System.Windows.Forms.DialogResult result = fileDialog.ShowDialog();

            if (result != System.Windows.Forms.DialogResult.OK)
            {
                editor.WriteMessage("\nWrong file format.");
                return;
            }

            targetPath = fileDialog.Filename;
            simulationName_Value = this.getFileName(targetPath);

            writer = XmlWriter.Create(@targetPath, settings);
            
            writer.WriteStartDocument();
            writer.WriteStartElement(simulator_Node);
            writer.WriteAttributeString(version_Attribute, version_Value);
            writer.WriteAttributeString(simulationName_Attribute, simulationName_Value);
            writer.WriteStartElement(layouts_Node);
            writer.WriteStartElement(scenario_Node);
            writer.WriteAttributeString(id_Attribute, scenarioID.ToString());
            writer.WriteAttributeString(name_Attribute, scenarioName_Value);

            double[] maxCoordinates = GeometryFactory.getMaximumCoordinates();
            double[] minCoordinates = GeometryFactory.getMinimumCoordinates();
            writer.WriteAttributeString(maxX_Attribute, Math.Round(maxCoordinates[0] + 1.0, decimalPlaces).ToString());
            writer.WriteAttributeString(maxY_Attribute, Math.Round(maxCoordinates[1] + 1.0, decimalPlaces).ToString());
            writer.WriteAttributeString(minX_Attribute, Math.Round(minCoordinates[0] - 1.0, decimalPlaces).ToString());
            writer.WriteAttributeString(minY_Attribute, Math.Round(minCoordinates[1] - 1.0, decimalPlaces).ToString());

            graphID = this.generateGraph(writer, graphList, graphType_Value, graphID);

            areaID = this.generatePolygon(writer, originList, area_Node, originArea_Value, areaID);
            areaID = this.generateIntermediates(writer, intermediateList, gatheringLineList, areaID);
            areaID = this.generatePolygon(writer, destinationList, area_Node, destinationArea_Value, areaID);
            areaID = this.generatePolygon(writer, avoidanceList, area_Node, avoidanceArea_Value, areaID);

            foreach(KeyValuePair<string, List<Polygon2D>> currentTaggedAreaList in taggedAreaList)
            {
                taggedAreaID = this.generatePolygon(writer, currentTaggedAreaList.Value, taggedArea_Node, currentTaggedAreaList.Key, taggedAreaID);
            }

            obstacleID = this.generateSegment(writer, wallList, obstacle_Node, wall_Value, obstacleID);
            obstacleID = this.generatePolygon(writer, solidList, obstacle_Node, solid_Value, obstacleID);

            writer.WriteEndElement();
            writer.WriteEndDocument();
            writer.Flush();
            writer.Close();

            editor.WriteMessage("\nXMLconversion successfull:" + "\n" + targetPath);
        }

        private int generateGraph(XmlWriter writer, List<Geometry.Segment2D> graphList, String graphType, int id)
        {
            if(graphList.Count == 0)
            {
                return -1;
            }

            writer.WriteStartElement(graph_Node);

            if (id != -1)
            {
                writer.WriteAttributeString(id_Attribute, id.ToString());
                id++;
            }

            writer.WriteAttributeString(name_Attribute, id.ToString());
            writer.WriteAttributeString(type_Attribute, graphType);

            List<Converter.XmlVertex> xmlVertices = this.createXmlVertices(graphList);

            foreach (XmlVertex currentVertex in xmlVertices)
            {
                String currentName = currentVertex.getName();
                String currentId = currentVertex.getId().ToString();
                Vector2D currentCenter = currentVertex.getCenter();
                double[] centerCoordinates = currentCenter.getCoordinates();
                String current_x = Math.Round(centerCoordinates[0], decimalPlaces).ToString();
                String current_y = Math.Round(centerCoordinates[1], decimalPlaces).ToString();

                writer.WriteStartElement(vertex_Node);
                writer.WriteAttributeString(name_Attribute, currentName);
                writer.WriteAttributeString(id_Attribute, currentId);
                writer.WriteStartElement(center_Node);
                writer.WriteAttributeString(x_Attribute, current_x);
                writer.WriteAttributeString(y_Attribute, current_y);
                writer.WriteEndElement();
                writer.WriteEndElement();
            }

            List<Converter.XmlEdge> xmlEdges = this.createXmlEdges(graphList, xmlVertices);

            foreach (XmlEdge currentEdge in xmlEdges)
            {
                String leftId = currentEdge.getIdLeft().ToString();
                String rightId = currentEdge.getIdRight().ToString();

                writer.WriteStartElement(edge_Node);
                writer.WriteAttributeString(idLeft_Attribute, leftId);
                writer.WriteAttributeString(idRight_Attribute, rightId);
                writer.WriteEndElement();
            }

            writer.WriteEndElement();

            return id;
        }
                 
        private int generateIntermediates(XmlWriter writer, List<Geometry.Polygon2D> intermediates,  List<Geometry.Segment2D> gatheringLines, int id) 
        {
            foreach (Polygon2D polygon in intermediates)
            {
                writer.WriteStartElement(area_Node);

                if (id != -1)
                {
                    writer.WriteAttributeString(id_Attribute, id.ToString());
                    id++;
                }

                writer.WriteAttributeString(name_Attribute, polygon.getName());
                writer.WriteAttributeString(type_Attribute, intermediateArea_Value);

                Vector2D[] polygonVertices = polygon.getVertices();

                for (int i = 0; i < polygonVertices.Length; i++)
                {
                    double[] coordinates = polygonVertices[i].getCoordinates();
                    writer.WriteStartElement(point_Node);
                    writer.WriteAttributeString(x_Attribute, coordinates[0].ToString());
                    writer.WriteAttributeString(y_Attribute, coordinates[1].ToString());
                    writer.WriteEndElement();
                }

                int isMatchAt = -1;

                foreach (Vector2D point in polygon.getVertices())
                {
                    for(int iter = 0; iter < gatheringLines.Count; iter++)
                    {
                         foreach (Vector2D gatheringCorner in gatheringLines[iter].getVertices())
                         {
                             if (gatheringCorner.isEqualTo(point))
                             {
                                 isMatchAt = iter;
                             }
                         }

                         if (isMatchAt > -1)
                             break;
                    }

                    if (isMatchAt > -1)
                        break;
                }

                if (isMatchAt > -1)
                {
                    this.generateSegment(writer, gatheringLines.GetRange(isMatchAt,1), gatheringLine_Node, null, -1);
                }

                writer.WriteEndElement();
            }

            return id;
        }

        private int generatePolygon(XmlWriter writer, List<Geometry.Polygon2D> polygons, String nodeName, String typeName, int id)
        {
            foreach (Polygon2D polygon in polygons)
            {
                writer.WriteStartElement(nodeName);

                if (id != -1)
                {
                    writer.WriteAttributeString(id_Attribute, id.ToString());
                    id++;
                }

                if (polygon.getName() != null)
                {
                    writer.WriteAttributeString(name_Attribute, polygon.getName());
                }

                if (typeName != null)
                {
                    writer.WriteAttributeString(type_Attribute, typeName);
                }

                Vector2D[] polygonVertices = polygon.getVertices();

                for (int i = 0; i < polygonVertices.Length; i++)
                {
                    double[] coordinates = polygonVertices[i].getCoordinates();
                    writer.WriteStartElement(point_Node);
                    writer.WriteAttributeString(x_Attribute, coordinates[0].ToString());
                    writer.WriteAttributeString(y_Attribute, coordinates[1].ToString());
                    writer.WriteEndElement();
                }

                writer.WriteEndElement();
            }

            return id;
        }

        private int generateSegment(XmlWriter writer, List<Geometry.Segment2D> segments, String nodeName, String typeName, int id)
        {
            foreach (Segment2D segment in segments)
            {
                writer.WriteStartElement(nodeName);

                if (id != -1)
                {
                    writer.WriteAttributeString(id_Attribute, id.ToString());
                    id++;
                }

                if (segment.getName() != null)
                {
                    writer.WriteAttributeString(name_Attribute, segment.getName());
                }

                if (typeName != null)
                {
                    writer.WriteAttributeString(type_Attribute, typeName);
                }

                Vector2D[] polygonVertices = segment.getVertices();

                for (int i = 0; i < polygonVertices.Length; i++)
                {
                    double[] coordinates = polygonVertices[i].getCoordinates();
                    writer.WriteStartElement(point_Node);
                    writer.WriteAttributeString(x_Attribute, coordinates[0].ToString());
                    writer.WriteAttributeString(y_Attribute, coordinates[1].ToString());
                    writer.WriteEndElement();
                }

                writer.WriteEndElement();
            }

            return id;
        }

        public void convertXMLtoDrawing() 
        {
            Document currentDocument = Autodesk.AutoCAD.ApplicationServices.Application.DocumentManager.MdiActiveDocument;
            Database currentDatabase = currentDocument.Database;
            Editor editor = currentDocument.Editor;

            OpenFileDialog fileDialog = new OpenFileDialog("Select .xml-file for import", null, "xml", "XMLfileToLink", OpenFileDialog.OpenFileDialogFlags.DoNotTransferRemoteFiles);
            System.Windows.Forms.DialogResult result = fileDialog.ShowDialog();

            if (result != System.Windows.Forms.DialogResult.OK)
            {
                editor.WriteMessage("\nWrong file format.");
                return;
            }
            
            XmlReaderSettings settings = new XmlReaderSettings();
            settings.IgnoreWhitespace = true;
            settings.IgnoreComments = true;
            settings.IgnoreProcessingInstructions = true;
            XmlReader reader = XmlReader.Create(fileDialog.Filename, settings);

            String objectType = "";
            String objectName = "";
            List<Vector2D> vectorList = null;
            Vector2D[] vectorArray;
            double x_Value = 0;
            double y_Value = 0;
            Polygon2D newPolygon;
            Segment2D newSegment;
            double maxX_Value = 0;
            double maxY_Value = 0;
            double minX_Value = 0;
            double minY_Value = 0;

            bool objectUnderConstruction = false;

            while (reader.Read())
            {
                switch (reader.NodeType)
                {
                    case XmlNodeType.XmlDeclaration:
                        break;

                    case XmlNodeType.Element:

                        if (reader.Name.Equals(scenario_Node)) 
                        {
                            if (reader.HasAttributes)
                            {
                                while (reader.MoveToNextAttribute())
                                {
                                    if (reader.Name.Equals(id_Attribute)) 
                                    {
                                        editor.WriteMessage("\n" + reader.Name + "=" + reader.Value);
                                    }
                                    else if (reader.Name.Equals(name_Attribute)) 
                                    {
                                        editor.WriteMessage("\n" + reader.Name + "=" + reader.Value);
                                    }
                                    else if (reader.Name.Equals(maxX_Attribute)) 
                                    {
                                        maxX_Value = reader.ReadContentAsDouble();
                                    }
                                    else if (reader.Name.Equals(maxY_Attribute)) 
                                    {
                                        maxY_Value = reader.ReadContentAsDouble();
                                    }
                                    else if (reader.Name.Equals(minX_Attribute)) 
                                    {
                                        minX_Value = reader.ReadContentAsDouble();
                                    }
                                    else if (reader.Name.Equals(minY_Attribute)) 
                                    {
                                        minY_Value = reader.ReadContentAsDouble();

                                        ViewTableRecord oldView = editor.GetCurrentView();
                                        ViewTableRecord newView = (ViewTableRecord)oldView.Clone();
                                        Point2d minPoint = new Point2d(minX_Value, minY_Value);
                                        Point2d maxPoint = new Point2d(maxX_Value, maxY_Value);

                                        newView.CenterPoint = minPoint + ((maxPoint - minPoint) / 2.0);
                                        newView.Height = maxPoint.Y - minPoint.Y;
                                        newView.Width = maxPoint.X - minPoint.X;
                                        editor.SetCurrentView(newView);
                                    }
                                }
                            }
                        }
                        else if (reader.Name.Equals(area_Node) || reader.Name.Equals(obstacle_Node))
                        {
                            objectUnderConstruction = true;
                            vectorList = new List<Vector2D>();

                            if (reader.HasAttributes)
                            {
                                while (reader.MoveToNextAttribute())
                                {
                                    if (reader.Name.Equals(name_Attribute))
                                    {
                                        objectName = reader.Value;
                                    }
                                    else if (reader.Name.Equals(type_Attribute))
                                    {
                                        objectType = reader.Value;
                                    }
                                }
                            }
                        }
                        else if (reader.Name.Equals(point_Node))
                        {
                            if (objectUnderConstruction)
                            {
                                if (reader.HasAttributes)
                                {
                                    while (reader.MoveToNextAttribute())
                                    {
                                        if (reader.Name.Equals(x_Attribute))
                                        {
                                            x_Value = reader.ReadContentAsDouble();
                                        }
                                        else if (reader.Name.Equals(y_Attribute))
                                        {
                                            y_Value = reader.ReadContentAsDouble();
                                        }
                                    }
                                }

                                vectorList.Add(new Vector2D(x_Value, y_Value));
                            }
                        }
                        break;

                    case XmlNodeType.EndElement:
                        
                        if (reader.Name.Equals(area_Node))
                        {
                            vectorArray = vectorList.ToArray();
                            newPolygon = new Polygon2D(vectorArray, objectName);
                            GeometryFactory.polygon2DtoPolyline(newPolygon, objectType, currentDocument);
                            objectUnderConstruction = false;
                        }
                        else if (reader.Name.Equals(obstacle_Node))
                        {
                            vectorArray = vectorList.ToArray();

                            if (objectType.Equals(wall_Value) && vectorArray[0].isEqualTo(vectorArray[vectorArray.Length - 1]))
                            {
                                newPolygon = new Polygon2D(vectorArray, objectName);
                                GeometryFactory.polygon2DtoPolyline(newPolygon, objectType, currentDocument);
                            }
                            else if (objectType.Equals(wall_Value))
                            {
                                newSegment = new Segment2D(vectorArray, objectName);
                                GeometryFactory.segment2DtoPolyline(newSegment, objectType, currentDocument);
                            }
                            else if (objectType.Equals(solid_Value))
                            {
                                newPolygon = new Polygon2D(vectorArray, objectName);
                                GeometryFactory.polygon2DtoPolyline(newPolygon, objectType, currentDocument);
                            }

                            objectUnderConstruction = false;
                        }
                        break;

                    default:
                        break;
                }
            }

            reader.Close();
        }

        public void reset()
        {
            areaID = 0;
            taggedAreaID = 0;
            obstacleID = 0;
            scenarioID = 0;
            graphID = 0;
            xmlFactory.reset();
            //fileIterator = 0;
        }

        #region Private Methods
        private String getFileName(String filePath)
        {
            String fileName = "";
            bool suffixLeftBehind = false;
            
            for (int i = (filePath.Length - 5); i >= 0; i--)
            {
                if (!suffixLeftBehind)
                {
                    String currentChar = filePath.ElementAt(i).ToString();

                    if (currentChar.Equals("\\"))
                    {
                        break;
                    }
                    else
                    {
                        fileName = currentChar + fileName;
                    }
                }
            }

            return fileName;
        }

        private List<Converter.XmlVertex> createXmlVertices(List<Geometry.Segment2D> graphSegments)
        {
            List<Converter.XmlVertex> xmlVertices = new List<Converter.XmlVertex>();

            foreach (Segment2D currentSegment in graphSegments)
            {
                Vector2D segmentsStart = currentSegment.getVertices()[0];
                Vector2D segmentsEnd = currentSegment.getVertices()[1];

                if (this.getEqualXmlVertex(xmlVertices, segmentsStart) == null)
                {
                    xmlVertices.Add(xmlFactory.createXmlVertex(null, segmentsStart));
                }
                if (this.getEqualXmlVertex(xmlVertices, segmentsEnd) == null)
                {
                    xmlVertices.Add(xmlFactory.createXmlVertex(null, segmentsEnd));
                }
            }

            return xmlVertices;
        }

        private List<Converter.XmlEdge> createXmlEdges(List<Geometry.Segment2D> graphSegments, List<Converter.XmlVertex> xmlVertices)
        {
            List<Converter.XmlEdge> xmlEdges = new List<XmlEdge>();

            foreach (Segment2D currentSegment in graphSegments)
            {
                Vector2D segmentsStart = currentSegment.getVertices()[0];
                Vector2D segmentsEnd = currentSegment.getVertices()[1];
                XmlVertex edgesLeftVertex = this.getEqualXmlVertex(xmlVertices, segmentsStart);
                XmlVertex edgesRightVertex = this.getEqualXmlVertex(xmlVertices, segmentsEnd);
                xmlEdges.Add(xmlFactory.createXmlEdge(edgesLeftVertex.getId(), edgesRightVertex.getId()));
            }

            return xmlEdges;
        }

        private Converter.XmlVertex getEqualXmlVertex(List<Converter.XmlVertex> vertices, Vector2D segmentPoint) 
        {
            XmlVertex equalVertex = null;

            foreach (XmlVertex currentVertex in vertices)
            {
                if (currentVertex.getCenter().isEqualTo(segmentPoint, decimalPlaces))
                {
                    equalVertex = currentVertex;
                    break;
                }
                else
                {
                    continue;
                }
            }

            return equalVertex;
        }
        #endregion
    }
}

