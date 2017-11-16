using Autodesk.Revit.DB;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using MomenTumV2SpaceSyntaxRevit.Service;
using MomenTumV2SpaceSyntaxRevit.View;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace MomenTumV2SpaceSyntaxRevit.ViewModel
{
    class LevelSelectorViewModel : ViewModelBase
    {
        LevelSelectorHost _hostRef;
        public LevelSelectorViewModel(LevelSelectorHost hostRef, List<Level> levels)
        {
            _hostRef = hostRef;
            InitializeLevelListBox(levels);
        }

        private void InitializeLevelListBox(List<Level> levels)
        {
            foreach (Level level in levels)
            {
                this.levels.Add(level);
            }
        }

        private static string _TEXTBOX_TEXT_LEVEL_SELECT = "Please select a Level from the dropdown. "
                        + "Press 'OK' to start the Space Syntax computation for the selected level.";
        public string TEXTBOX_TEXT_LEVEL_SELECT
        {
            get { return _TEXTBOX_TEXT_LEVEL_SELECT; }
        }

        private static string _BUTTON_CONTENT_OK = "OK";
        public string BUTTON_CONTENT_OK
        {
            get { return _BUTTON_CONTENT_OK; }
        }

        private static string _BUTTON_CONTENT_CANCEL = "Cancel";
        public string BUTTON_CONTENT_CANCEL
        {
            get { return _BUTTON_CONTENT_CANCEL; }
        }

        private ObservableCollection<Level> _levels = new ObservableCollection<Level>();
        public ObservableCollection<Level> levels
        {
            get { return _levels; }
        }

        private Level _selectedLevel;
        public Level SelectedLevel
        {
            get { return _selectedLevel; }
            set
            {
                if (value != null) IsEnabledOKButton = true;
                Set(ref _selectedLevel, value);
            }
        }

        private bool _isEnabledOKButton = false;
        public bool IsEnabledOKButton { get { return _isEnabledOKButton; } set { Set(ref _isEnabledOKButton, value); } }

        private ICommand _ClickOKButton;
        public ICommand ClickOKButton
        {
            get
            {
                if (_ClickOKButton == null)
                {
                    _ClickOKButton = new RelayCommand(OnClickOKButton);
                }

                return _ClickOKButton;
            }
        }

        private ICommand _ClickCancelButton;
        public ICommand ClickCancelButton
        {
            get
            {
                if (_ClickCancelButton == null)
                {
                    _ClickCancelButton = new RelayCommand(OnClickCancelButton);
                }

                return _ClickCancelButton;
            }
        }

        private void OnClickCancelButton()
        {
            _hostRef.Close();
        }

        private void OnClickOKButton()
        {
            if (SelectedLevel != null)
            {
                UserLevelSelectService.LevelSelectedByUser = SelectedLevel;
            }

            _hostRef.Close();
        }
    }
}
