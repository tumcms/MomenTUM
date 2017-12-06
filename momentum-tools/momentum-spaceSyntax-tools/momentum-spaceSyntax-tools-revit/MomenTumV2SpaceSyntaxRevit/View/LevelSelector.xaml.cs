using Autodesk.Revit.DB;
using System.Collections.Generic;
using System.Windows.Controls;
using MomenTumV2SpaceSyntaxRevit.ViewModel;

namespace MomenTumV2SpaceSyntaxRevit.View
{
    /// <summary>
    /// Interaction logic for LevelSelector.xaml
    /// </summary>
    public partial class LevelSelector : UserControl
    {
        private LevelSelectorViewModel _vm;
        public LevelSelector()
        {
            InitializeComponent();
        }

        public void InitializeLevelListBox(LevelSelectorHost _hostRef, List<Level> levels)
        {
            _vm = new LevelSelectorViewModel(_hostRef, levels);
            DataContext = _vm;
        }
    }
}
