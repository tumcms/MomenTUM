using Autodesk.Revit.UI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MomenTumV2SpaceSyntaxRevit.Service
{
    class PromtService
    {
        public static void DisplayErrorToUser(string errorMessage)
        {
            TaskDialog mainDialog = new TaskDialog("Error Information");
            mainDialog.MainInstruction = "An error occured:";
            mainDialog.MainContent = errorMessage 
                + "\n\nFix the error and re-run the add-in.";
            mainDialog.Show();
        }

        public static void DisplayInformationToUser(string message)
        {
            TaskDialog mainDialog = new TaskDialog("Information");
            mainDialog.MainContent = message;
            mainDialog.Show();
        }
    }
}
