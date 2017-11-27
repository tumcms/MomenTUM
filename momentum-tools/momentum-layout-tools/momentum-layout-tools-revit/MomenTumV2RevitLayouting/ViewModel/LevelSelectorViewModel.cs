using Autodesk.Revit.DB;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using MomenTumV2RevitLayouting.Service;
using MomenTumV2RevitLayouting.View;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows.Input;

namespace MomenTumV2RevitLayouting.ViewModel
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
                        + "Press 'OK' to continue.";
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

        private static readonly string _textBoxTextSelectAllLevels = "Select All Levels";
        public string TextBoxTextSelectAllLevels
        {
            get { return _textBoxTextSelectAllLevels; }
        }

        private bool _isCheckedAllLevelsSelected = false;
        public bool IsCheckedAllLevelsSelected
        {
            get { return _isCheckedAllLevelsSelected; }
            set
            {
                if (SelectedLevel == null)
                {
                    if (value == true)
                    {
                        IsEnabledOKButton = true;
                        IsEnabledComboBox = false;
                    }
                    else
                    {
                        IsEnabledOKButton = false;
                        IsEnabledComboBox = true;
                    }
                }
                else
                {
                    if (value == true) { IsEnabledComboBox = false; }
                    else { IsEnabledComboBox = true; }
                }

                Set(ref _isCheckedAllLevelsSelected, value);
            }
        }

        private bool _isEnabledComboBox = true;
        public bool IsEnabledComboBox { get { return _isEnabledComboBox; } set { Set(ref _isEnabledComboBox, value); } }

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
                UserInteractionService.SelectedLevels.Add(SelectedLevel);
            }
            if (IsCheckedAllLevelsSelected == true)
            {
                UserInteractionService.SelectedLevels.AddRange(Levels);
            }

            _hostRef.Close();
        }

        private ICommand _clickAddAllLevelsButton;
        public ICommand ClickAddAllLevelsButton
        {
            get
            {
                if (_clickAddAllLevelsButton == null)
                {
                    _clickAddAllLevelsButton = new RelayCommand(OnClickSelectAllLevelsButton);
                }

                return _clickAddAllLevelsButton;
            }
        }

        private void OnClickSelectAllLevelsButton()
        {
            UserInteractionService.SelectedLevels.AddRange(Levels);

            _hostRef.Close();
        }
    }
}
