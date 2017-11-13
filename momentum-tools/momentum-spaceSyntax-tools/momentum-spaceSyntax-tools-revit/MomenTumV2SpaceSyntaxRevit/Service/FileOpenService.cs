using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Windows.Forms;
using MomenTumV2SpaceSyntaxRevit.Model;
using System.Xml.Serialization;
using Autodesk.Revit.UI;

namespace MomenTumV2SpaceSyntaxRevit.Service
{
    class FileOpenService
    {
        public static KeyValuePair<Result, SpaceSyntax> PromtUserForSpaceSyntaxXml()
        {
            var openFileDialog = new OpenFileDialog();
            openFileDialog.InitialDirectory = "%USER%";
            openFileDialog.Filter = "xml files (*.xml)|*.xml|All files (*.*)|*.*";
            openFileDialog.FilterIndex = 1;
            openFileDialog.RestoreDirectory = true;

            if (!(openFileDialog.ShowDialog() == DialogResult.OK))
            {
                PromtService.DisplayInformationToUser("SpaceSyntax Addin cancelled by user.");
                return new KeyValuePair<Result, SpaceSyntax>(Result.Cancelled, null);
            }

            if (!openFileDialog.CheckFileExists)
            {
                PromtService.DisplayInformationToUser("File does not exist.");
                return new KeyValuePair<Result, SpaceSyntax>(Result.Failed, null);
            }

            XmlSerializer xmlDeserializer = null;

            try
            {
                xmlDeserializer = new XmlSerializer(typeof(SpaceSyntax));
            } catch(Exception)
            {
                PromtService.DisplayErrorToUser("The provided xml file could not be parsed correctly.");
                return new KeyValuePair<Result, SpaceSyntax>(Result.Failed, null);
            }

            var spaceSyntax = (SpaceSyntax)xmlDeserializer.Deserialize(openFileDialog.OpenFile());

            return new KeyValuePair<Result, SpaceSyntax>(Result.Succeeded, spaceSyntax);
        }
    }
}
