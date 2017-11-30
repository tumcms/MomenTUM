using Autodesk.Revit.DB;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using MomenTumV2SpaceSyntaxRevit.Service;
using MomenTumV2SpaceSyntaxRevit.View;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows.Input;

namespace MomenTumV2SpaceSyntaxRevit.ViewModel
{
    class LevelSelectorViewModel : ViewModelBase
    {
        private LevelSelectorHost _hostRef;
        public LevelSelectorViewModel(LevelSelectorHost hostRef, List<Level> levels)
        {
            _hostRef = hostRef;
            InitializeLevelListBox(levels);
        }

        private void InitializeLevelListBox(List<Level> levels)
        {
            foreach (Level level in levels)
            {
                this.Levels.Add(level);
            }
        }

        private static readonly string _textBoxTextLevelSelect = "Please select a Level from the dropdown. "
                        + "Press 'OK' to start the Space Syntax computation for the selected level.";
        public string TextBoxTextLevelSelect
        {
            get { return _textBoxTextLevelSelect; }
        }

        private static readonly string _buttonContentOk = "OK";
        public string ButtonContentOk
        {
            get { return _buttonContentOk; }
        }

        private static readonly string _buttonContentCancel = "Cancel";
        public string ButtonContentCancel
        {
            get { return _buttonContentCancel; }
        }

        private ObservableCollection<Level> _levels = new ObservableCollection<Level>();
        public ObservableCollection<Level> Levels
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

        private ICommand _clickOKButton;
        public ICommand ClickOKButton
        {
            get
            {
                if (_clickOKButton == null)
                {
                    _clickOKButton = new RelayCommand(OnClickOKButton);
                }

                return _clickOKButton;
            }
        }

        private ICommand _clickCancelButton;
        public ICommand ClickCancelButton
        {
            get
            {
                if (_clickCancelButton == null)
                {
                    _clickCancelButton = new RelayCommand(OnClickCancelButton);
                }

                return _clickCancelButton;
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
