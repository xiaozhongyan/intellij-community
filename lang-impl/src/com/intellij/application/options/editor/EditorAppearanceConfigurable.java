package com.intellij.application.options.editor;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.options.CompositeConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * @author yole
 */
public class EditorAppearanceConfigurable extends CompositeConfigurable<UnnamedConfigurable> implements EditorOptionsProvider {
  public static final ExtensionPointName<UnnamedConfigurable> EP_NAME = ExtensionPointName.create("com.intellij.editorAppearanceConfigurable");
  private JPanel myRootPanel;
  private JCheckBox myCbBlinkCaret;
  private JCheckBox myCbBlockCursor;
  private JCheckBox myCbRightMargin;
  private JCheckBox myCbShowLineNumbers;
  private JCheckBox myCbShowWhitespaces;
  private JTextField myBlinkIntervalField;
  private JPanel myAddonPanel;
  private JCheckBox myCbShowMethodSeparators;

  public EditorAppearanceConfigurable() {
    myCbBlinkCaret.addActionListener(
    new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        myBlinkIntervalField.setEnabled(myCbBlinkCaret.isSelected());
      }
    }
  );
  }



  @Override
  public void reset() {
    EditorSettingsExternalizable editorSettings = EditorSettingsExternalizable.getInstance();

    myCbShowMethodSeparators.setSelected(DaemonCodeAnalyzerSettings.getInstance().SHOW_METHOD_SEPARATORS);
    myCbBlinkCaret.setSelected(editorSettings.isBlinkCaret());
    myBlinkIntervalField.setText(Integer.toString(editorSettings.getBlinkPeriod()));
    myBlinkIntervalField.setEnabled(editorSettings.isBlinkCaret());
    myCbRightMargin.setSelected(editorSettings.isRightMarginShown());
    myCbShowLineNumbers.setSelected(editorSettings.isLineNumbersShown());
    myCbBlockCursor.setSelected(editorSettings.isBlockCursor());
    myCbShowWhitespaces.setSelected(editorSettings.isWhitespacesShown());

    super.reset();
  }

  @Override
  public void apply() throws ConfigurationException {
    EditorSettingsExternalizable editorSettings = EditorSettingsExternalizable.getInstance();
    editorSettings.setBlinkCaret(myCbBlinkCaret.isSelected());
    try {
      editorSettings.setBlinkPeriod(Integer.parseInt(myBlinkIntervalField.getText()));
    }
    catch (NumberFormatException e) {
    }

    editorSettings.setBlockCursor(myCbBlockCursor.isSelected());
    editorSettings.setRightMarginShown(myCbRightMargin.isSelected());
    editorSettings.setLineNumbersShown(myCbShowLineNumbers.isSelected());
    editorSettings.setWhitespacesShown(myCbShowWhitespaces.isSelected());

    DaemonCodeAnalyzerSettings.getInstance().SHOW_METHOD_SEPARATORS = myCbShowMethodSeparators.isSelected();

    super.apply();
  }

  @Override
  public boolean isModified() {
    if (super.isModified()) return true;
    EditorSettingsExternalizable editorSettings = EditorSettingsExternalizable.getInstance();
    boolean isModified = isModified(myCbBlinkCaret, editorSettings.isBlinkCaret());
    isModified |= isModified(myBlinkIntervalField, editorSettings.getBlinkPeriod());

    isModified |= isModified(myCbBlockCursor, editorSettings.isBlockCursor());

    isModified |= isModified(myCbRightMargin, editorSettings.isRightMarginShown());

    isModified |= isModified(myCbShowLineNumbers, editorSettings.isLineNumbersShown());
    isModified |= isModified(myCbShowWhitespaces, editorSettings.isWhitespacesShown());
    isModified |= isModified(myCbShowMethodSeparators, DaemonCodeAnalyzerSettings.getInstance().SHOW_METHOD_SEPARATORS);

    return isModified;
  }

  private static boolean isModified(JToggleButton checkBox, boolean value) {
    return checkBox.isSelected() != value;
  }

  private static boolean isModified(JTextField textField, int value) {
    try {
      int fieldValue = Integer.parseInt(textField.getText().trim());
      return fieldValue != value;
    }
    catch(NumberFormatException e) {
      return false;
    }
  }

  @Nls
  public String getDisplayName() {
    return ApplicationBundle.message("tab.editor.settings.appearance");
  }

  public Icon getIcon() {
    return null;
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    for (UnnamedConfigurable provider : getConfigurables()) {
      myAddonPanel.add(provider.createComponent(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
                                                                            GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0));
    }
    return myRootPanel;
  }

  protected List<UnnamedConfigurable> createConfigurables() {
    return Arrays.asList(Extensions.getExtensions(EP_NAME));
  }
}
