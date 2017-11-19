using Autodesk.Revit.UI;
using MomenTumV2RevitLayouting.Model.Output;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using System.Xml.Serialization;

namespace MomenTumV2RevitLayouting.Service
{
    class UserInteractionService
    {
        public void SaveSimulatorToXml(Simulator simulator)
        {
            var serializer = new XmlSerializer(typeof(Simulator));

            var saveFileDialog = new SaveFileDialog();
            saveFileDialog.InitialDirectory = "%USER%";
            saveFileDialog.Title = "Save Layouting...";
            saveFileDialog.AddExtension = true;
            saveFileDialog.DefaultExt = "xml";
            DialogResult result = saveFileDialog.ShowDialog();

            if (result != DialogResult.OK)
            {
                TaskDialog.Show("Error", "User did not specify an output file. Plugin aborts.");
                return;
            }
            
            var targetFilePath = saveFileDialog.FileName;
            var streamWriter = new StreamWriter(targetFilePath, false, Encoding.UTF8);
            serializer.Serialize(streamWriter, simulator);

            TaskDialog.Show("Success", "Xml successfully written to '" + targetFilePath + "'.");
        }
    }
}
