using Autodesk.Revit.DB;
using MomenTumV2RevitLayouting.ViewModel;
using System.Collections.Generic;
using System.Windows.Controls;

namespace MomenTumV2RevitLayouting.View
{
    /// <summary>
    /// Interaction logic for LevelSelector.xaml
    /// </summary>
    public partial class LevelSelector : System.Windows.Controls.UserControl
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
