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
using Autodesk.Revit.DB;
using MomenTumV2RevitLayouting.View;

namespace MomenTumV2RevitLayouting.Service
{
    class UserInteractionService
    {
        public static List<Level> SelectedLevels { get; set; } = new List<Level>();

        public static List<Level> LetUserPickLevels(Document doc, RevitObjectManager rom)
        {
            var levelSelector = new LevelSelectorHost();
            levelSelector.InitializeLevelListBox(rom.GetAllLevels(doc));
            // clear list from previous selection in case of exporting multiple times
            SelectedLevels.Clear(); 
            levelSelector.ShowDialog();
            
            // Levels were added by the dialog
            return SelectedLevels;
        }

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
