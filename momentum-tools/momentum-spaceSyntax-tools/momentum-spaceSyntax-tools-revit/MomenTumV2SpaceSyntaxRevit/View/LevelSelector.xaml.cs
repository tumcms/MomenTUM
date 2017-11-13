using Autodesk.Revit.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
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
