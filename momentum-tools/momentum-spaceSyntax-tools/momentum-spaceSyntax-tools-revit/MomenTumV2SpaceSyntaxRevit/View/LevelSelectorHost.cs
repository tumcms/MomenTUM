using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MomenTumV2SpaceSyntaxRevit.View
{
    public partial class LevelSelectorHost : Form
    {
        public LevelSelectorHost()
        {
            InitializeComponent();
        }

        public void InitializeLevelListBox(List<Autodesk.Revit.DB.Level> levels)
        {
            this.levelSelector1.InitializeLevelListBox(this, levels);
        }
    }
}
