/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IAttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.SkynetSpellModifyDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactMultiChoiceSelect;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactTypeComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactTypeMultiChoiceSelect;
import org.eclipse.osee.framework.ui.skynet.widgets.XAttributeTypeComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XAttributeTypeMultiChoiceSelect;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidgetDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidgetWithSave;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxThreeState;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxThreeStateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboBooleanDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XDslEditorWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XDslEditorWidgetDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog.Type;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XInteger;
import org.eclipse.osee.framework.ui.skynet.widgets.XIntegerDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewWithSave;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XLong;
import org.eclipse.osee.framework.ui.skynet.widgets.XLongDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersList;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtons;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromMultiChoiceBranch;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromMultiChoiceDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XStackedDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextFlatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextResourceDropDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class FrameworkXWidgetProvider {

   private static final FrameworkXWidgetProvider reference = new FrameworkXWidgetProvider();

   private FrameworkXWidgetProvider() {
      // Hide Constructor to enforce singleton pattern
   }

   public static FrameworkXWidgetProvider getInstance() {
      return reference;
   }

   private String getXWidgetNameBasedOnAttribute(String attributeTypeName) {
      AttributeTypeToken attributeType = AttributeTypeManager.getType(attributeTypeName);
      if (attributeType != null) {
         IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
         List<XWidgetRendererItem> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
         return concreteWidgets.iterator().next().getXWidgetName();
      }
      return null;
   }

   public XWidget createXWidget(XWidgetRendererItem xWidgetLayoutData) {
      String xWidgetName = xWidgetLayoutData.getXWidgetName();
      String name = xWidgetLayoutData.getName();
      XWidget xWidget = null;

      // Set xWidgetName from attribute type if not already set
      if (!Strings.isValid(xWidgetName) && Strings.isValid(xWidgetLayoutData.getStoreName())) {
         xWidgetName = getXWidgetNameBasedOnAttribute(xWidgetLayoutData.getStoreName());
      }

      // Look for widget provider to create widget
      for (IXWidgetProvider widgetProvider : getXWidgetProviders()) {
         xWidget = widgetProvider.createXWidget(xWidgetName, name, xWidgetLayoutData);
         if (xWidget != null) {
            break;
         }
      }

      if (xWidget == null) {
         // Otherwise, use default widget creation
         if (xWidgetName.equals("XText")) {
            xWidget = new XText(name);
            if (Strings.isValid(xWidgetLayoutData.getDefaultValue())) {
               ((XText) xWidget).set(xWidgetLayoutData.getDefaultValue());
            }
         } else if (xWidgetName.equals("XSelectFromMultiChoiceBranch")) {
            XSelectFromMultiChoiceBranch multiBranchSelect = new XSelectFromMultiChoiceBranch(name);
            int maxSelectionRequired = 1;
            if (xWidgetLayoutData.getXOptionHandler().contains(XOption.MULTI_SELECT)) {
               maxSelectionRequired = Integer.MAX_VALUE;
            }
            try {
               List<? extends IOseeBranch> branches =
                  BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING, BranchType.BASELINE);
               Collections.sort(branches);

               multiBranchSelect.setSelectableItems(branches);
               multiBranchSelect.setRequiredSelection(1, maxSelectionRequired);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            multiBranchSelect.setRequiredEntry(true);
            xWidget = multiBranchSelect;
         } else if (xWidgetName.equals("XInteger")) {
            xWidget = new XInteger(name);
         } else if (xWidgetName.equals("XLong")) {
            xWidget = new XLong(name);
         } else if (xWidgetName.equals("XTextDam")) {
            xWidget = new XTextDam(name);
         } else if (xWidgetName.equals("XButton")) {
            xWidget = new XButton(name);
         } else if (xWidgetName.equals("XButtonPush")) {
            xWidget = new XButtonPush(name);
         } else if (xWidgetName.startsWith("XRadioButtons")) {
            String values[] =
               xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
            if (values.length > 0) {
               XRadioButtons radio = new XRadioButtons(name, "");

               if (xWidgetLayoutData.getXOptionHandler().contains(XOption.SORTED)) {
                  Arrays.sort(values);
               }

               String defaultValue = xWidgetLayoutData.getDefaultValue();
               for (String value : values) {
                  XRadioButton button = radio.addButton(value);
                  if (Strings.isValid(defaultValue) && value.equals(defaultValue)) {
                     button.setSelected(true);
                  }
               }

               xWidget = radio;
            } else {
               throw new OseeArgumentException(
                  "Invalid XRadioButtons.  Must be \"XRadioButtons(option1,option2,option3)\"");
            }
         } else if (xWidgetName.equals("XLabelDam")) {
            xWidget = new XLabelDam(name);
         } else if (xWidgetName.equals("XMembersList")) {
            try {
               xWidget = new XMembersList(name);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } else if (xWidgetName.equals("XMembersCombo")) {
            xWidget = new XMembersCombo(name);
         } else if (xWidgetName.equals("XMembersComboAll")) {
            xWidget = new XMembersCombo(name, true);
         } else if (xWidgetName.equals("XDate")) {
            xWidget = new XDate(name);
         } else if (xWidgetName.equals("XFileSelectionDialog")) {
            xWidget = new XFileTextWithSelectionDialog(name);
         } else if (xWidgetName.equals("XDirectorySelectionDialog")) {
            String defaultValue = xWidgetLayoutData.getDefaultValue();
            if (Strings.isValid(defaultValue)) {
               xWidget = new XFileTextWithSelectionDialog(name, Type.Directory, defaultValue);
            } else {
               xWidget = new XFileTextWithSelectionDialog(name, Type.Directory);
            }
         } else if (xWidgetName.equals("XDateDam")) {
            xWidget = new XDateDam(name);
         } else if (xWidgetName.equals("XTextResourceDropDam")) {
            xWidget = new XTextResourceDropDam(name);
         } else if (xWidgetName.equals("XFloat")) {
            xWidget = new XFloat(name);
         } else if (xWidgetName.equals("XFloatDam")) {
            xWidget = new XFloatDam(name);
         } else if (xWidgetName.equals("XIntegerDam")) {
            xWidget = new XIntegerDam(name);
         } else if (xWidgetName.equals("XLongDam")) {
            xWidget = new XLongDam(name);
         } else if (xWidgetName.equals("XFileTextWithSelectionDialog")) {
            xWidget = new XFileTextWithSelectionDialog(name);
         } else if (xWidgetName.equals("XLabel")) {
            String defaultValue = xWidgetLayoutData.getDefaultValue();
            if (Strings.isValid(defaultValue)) {
               xWidget = new XLabel(name, xWidgetLayoutData.getDefaultValue());
            } else {
               xWidget = new XLabel(name);
            }
         } else if (xWidgetName.equals("XCheckBox")) {
            XCheckBox checkBox = new XCheckBox(name);
            checkBox.setLabelAfter(xWidgetLayoutData.getXOptionHandler().contains(XOption.LABEL_AFTER));
            if (Strings.isValid(xWidgetLayoutData.getDefaultValue())) {
               checkBox.set(Boolean.valueOf(xWidgetLayoutData.getDefaultValue()));
            }
            xWidget = checkBox;
         } else if (xWidgetName.equals("XCheckBoxDam")) {
            XCheckBoxDam checkBox = new XCheckBoxDam(name);
            checkBox.setLabelAfter(xWidgetLayoutData.getXOptionHandler().contains(XOption.LABEL_AFTER));
            xWidget = checkBox;
         } else if (xWidgetName.equals("XCheckBoxThreeState")) {
            XCheckBoxThreeState checkBox = new XCheckBoxThreeState(name);
            checkBox.setLabelAfter(xWidgetLayoutData.getXOptionHandler().contains(XOption.LABEL_AFTER));
            xWidget = checkBox;
         } else if (xWidgetName.equals("XCheckBoxThreeStateDam")) {
            XCheckBoxThreeStateDam checkBox = new XCheckBoxThreeStateDam(name);
            checkBox.setLabelAfter(xWidgetLayoutData.getXOptionHandler().contains(XOption.LABEL_AFTER));
            xWidget = checkBox;
         } else if (xWidgetName.startsWith("XComboDam")) {
            if (xWidgetLayoutData.getDynamicXWidgetLayout() != null) {
               String values[] =
                  xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
               if (values.length > 0) {
                  xWidget = new XComboDam(name);
                  XComboDam combo = new XComboDam(name);
                  combo.setDataStrings(values);
                  if (xWidgetLayoutData.getXOptionHandler().contains(XOption.NO_DEFAULT_VALUE)) {
                     combo.setDefaultSelectionAllowed(false);
                  }
                  if (xWidgetLayoutData.getXOptionHandler().contains(XOption.ADD_DEFAULT_VALUE)) {
                     combo.setDefaultSelectionAllowed(true);
                  }
                  xWidget = combo;
               } else {
                  throw new OseeArgumentException("Invalid XComboDam.  Must be \"XComboDam(option1,option2,option3)\"");
               }
            }
         } else if (xWidgetName.startsWith("XSelectFromMultiChoiceDam")) {
            if (xWidgetLayoutData.getDynamicXWidgetLayout() != null) {
               String values[] =
                  xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
               if (values.length > 0) {
                  XSelectFromMultiChoiceDam widget = new XSelectFromMultiChoiceDam(name);
                  widget.setSelectableItems(Arrays.asList(values));
                  xWidget = widget;
               } else {
                  throw new OseeArgumentException(
                     "Invalid XSelectFromMultiChoiceDam.  Must be \"XSelectFromMultiChoiceDam(option1,option2,option3)\"");
               }
            }
         } else if (xWidgetName.startsWith("XStackedDam")) {
            xWidget = new XStackedDam(name);
         } else if (xWidgetName.startsWith("XFlatDam")) {
            xWidget = new XTextFlatDam(name);
         } else if (xWidgetName.startsWith("XComboBooleanDam")) {
            xWidget = new XComboBooleanDam(name);
            XComboBooleanDam combo = new XComboBooleanDam(name);
            combo.setDataStrings(BooleanAttribute.booleanChoices);
            xWidget = combo;
            if (Strings.isValid(xWidgetLayoutData.getDefaultValue())) {
               String value = xWidgetLayoutData.getDefaultValue();
               if ("true".equals(value)) {
                  combo.set("true");
               } else if ("false".equals(value)) {
                  combo.set("false");
               } else {
                  combo.set("");
               }
            }
            if (xWidgetLayoutData.getXOptionHandler().contains(XOption.NO_DEFAULT_VALUE)) {
               combo.setDefaultSelectionAllowed(false);
            }
            if (xWidgetLayoutData.getXOptionHandler().contains(XOption.ADD_DEFAULT_VALUE)) {
               combo.setDefaultSelectionAllowed(true);
            }
         } else if (xWidgetName.startsWith("XComboViewer")) {
            xWidget = new XComboViewer(name, SWT.NONE);
         } else if (xWidgetName.startsWith("XCombo")) {
            String values[] =
               xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
            if (values.length > 0) {
               XCombo combo = new XCombo(name);

               if (xWidgetLayoutData.getXOptionHandler().contains(XOption.SORTED)) {
                  Arrays.sort(values);
               }
               combo.setDataStrings(values);

               if (xWidgetLayoutData.getXOptionHandler().contains(XOption.NO_DEFAULT_VALUE)) {
                  combo.setDefaultSelectionAllowed(false);
               }
               if (xWidgetLayoutData.getXOptionHandler().contains(XOption.ADD_DEFAULT_VALUE)) {
                  combo.setDefaultSelectionAllowed(true);
               }
               xWidget = combo;
               String defaultValue = xWidgetLayoutData.getDefaultValue();
               if (Strings.isValid(defaultValue)) {
                  combo.setDefaultValue(defaultValue);
               }
            } else {
               throw new OseeArgumentException("Invalid XCombo.  Must be \"XCombo(option1,option2,option3)\"");
            }
         } else if (xWidgetName.startsWith("XListDam")) {
            if (xWidgetLayoutData.getDynamicXWidgetLayout() != null) {
               String values[] =
                  xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
               if (values.length > 0) {
                  XListDam list = new XListDam(name);
                  list.add(values);
                  xWidget = list;
               } else {
                  throw new OseeArgumentException("Invalid XList.  Must be \"XList(option1,option2,option3)\"");
               }
            }
         } else if (xWidgetName.equals("XHyperlabelMemberSelDam")) {
            xWidget = new XHyperlabelMemberSelDam(name);
         } else if (xWidgetName.equals("XHyperlabelMemberSelection")) {
            xWidget = new XHyperlabelMemberSelection(name);
         } else if (xWidgetName.startsWith("XListDropViewer")) {
            if ("XListDropViewerWithSave".equals(xWidgetName)) {
               xWidget = new XListDropViewWithSave(name);
            } else {
               xWidget = new XListDropViewer(name);
            }
         } else if (xWidgetName.startsWith("XList")) {
            String values[] =
               xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
            if (values.length > 0) {
               XList list = new XList(name);
               list.add(values);
               xWidget = list;
               String defaultValue = xWidgetLayoutData.getDefaultValue();
               if (Strings.isValid(defaultValue)) {
                  list.setSelected(Arrays.asList(defaultValue.split(",")));
               }
            } else {
               throw new OseeArgumentException("Invalid XList.  Must be \"XList(option1,option2,option3)\"");
            }
         } else if (xWidgetName.startsWith("XArtifactList")) {
            XArtifactList artifactList = new XArtifactList(name);
            artifactList.setMultiSelect(xWidgetLayoutData.getXOptionHandler().contains(XOption.MULTI_SELECT));
            xWidget = artifactList;
         } else if (xWidgetName.equals(XBranchSelectWidgetDam.WIDGET_ID)) {
            xWidget = new XBranchSelectWidgetDam();
         } else if (xWidgetName.startsWith(XBranchSelectWidget.WIDGET_ID)) {
            XBranchSelectWidget widget = null;

            if (xWidgetName.endsWith("WithSave")) {
               widget = new XBranchSelectWidgetWithSave(name);
            } else {
               widget = new XBranchSelectWidget(name);
            }

            widget.setToolTip(xWidgetLayoutData.getToolTip());
            try {
               String branchUuid = xWidgetLayoutData.getDefaultValue();
               if (Strings.isValid(branchUuid)) {
                  try {
                     Long uuid = Long.valueOf(branchUuid);
                     widget.setSelection(BranchManager.getBranchToken(uuid));
                  } catch (Exception ex) {
                     // do nothing
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            xWidget = widget;
         } else if (xWidgetName.equals(XArtifactTypeComboViewer.WIDGET_ID)) {
            XArtifactTypeComboViewer widget = new XArtifactTypeComboViewer();
            xWidget = widget;
         } else if (xWidgetName.equals(XAttributeTypeComboViewer.WIDGET_ID)) {
            XAttributeTypeComboViewer widget = new XAttributeTypeComboViewer();
            xWidget = widget;
         } else if (xWidgetName.equals(XAttributeTypeMultiChoiceSelect.WIDGET_ID)) {
            XAttributeTypeMultiChoiceSelect widget = new XAttributeTypeMultiChoiceSelect();
            xWidget = widget;
         } else if (xWidgetName.equals(XArtifactTypeMultiChoiceSelect.WIDGET_ID)) {
            XArtifactTypeMultiChoiceSelect widget = new XArtifactTypeMultiChoiceSelect();
            String defaultType = xWidgetLayoutData.getDefaultValue();
            if (Strings.isValid(defaultType)) {
               List<ArtifactType> types = new LinkedList<>();
               for (String type : defaultType.split(",")) {
                  try {
                     types.add(ArtifactTypeManager.getType(type));
                  } catch (Exception ex) {
                     // do nothing
                  }
               }
               widget.setSelected(types);
            }
            xWidget = widget;
         } else if (xWidgetName.equals(XArtifactMultiChoiceSelect.WIDGET_ID)) {
            xWidget = new XArtifactMultiChoiceSelect();

         } else if (xWidgetName.equals(XTextFlatDam.WIDGET_ID)) {
            XTextFlatDam widget = new XTextFlatDam();
            xWidget = widget;
         } else if (xWidgetName.equals(XDslEditorWidgetDam.WIDGET_ID)) {
            XDslEditorWidgetDam widget = new XDslEditorWidgetDam(name);
            xWidget = widget;
         } else if (xWidgetName.equals(XDslEditorWidget.WIDGET_ID)) {
            XDslEditorWidget widget = new XDslEditorWidget(name);
            String defaultType = xWidgetLayoutData.getDefaultValue();
            if (Strings.isValid(defaultType)) {
               widget.setExtension(defaultType);
            }
            xWidget = widget;
         } else if (xWidgetName.equals("XHyperlinkLabel")) {
            xWidget = new XHyperlinkLabel(name);
            String defaultValue = xWidgetLayoutData.getDefaultValue();
            if (Strings.isValid(defaultValue)) {
               XHyperlinkLabel widget = (XHyperlinkLabel) xWidget;
               widget.setUrl(xWidgetLayoutData.getDefaultValue());
            }
         } else if (xWidgetName.equals("XRadioButton")) {
            xWidget = new XRadioButton(name);
         } else {
            xWidget = new XLabel("Error: Unhandled XWidget \"" + xWidgetName + "\"");
         }
      }

      if (xWidget instanceof XText) {
         ((XText) xWidget).addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
      }

      if (xWidget != null && xWidgetLayoutData.getXOptionHandler().contains(XOption.NO_LABEL)) {
         xWidget.setDisplayLabel(false);
      }
      Artifact artifact = xWidgetLayoutData.getArtifact();
      if (artifact != null) {
         if (xWidget instanceof IAttributeWidget) {
            AttributeTypeToken attributeType = AttributeTypeManager.getType(xWidgetLayoutData.getStoreName());
            ((IAttributeWidget) xWidget).setAttributeType(artifact, attributeType);
         } else if (xWidget instanceof IArtifactWidget) {
            ((IArtifactWidget) xWidget).setArtifact(artifact);
         }
      }
      if (xWidget != null) {
         xWidget.setObject(xWidgetLayoutData.getObject());
      }
      return xWidget;
   }

   private static Collection<IXWidgetProvider> getXWidgetProviders() {
      ExtensionDefinedObjects<IXWidgetProvider> contributions = new ExtensionDefinedObjects<>(
         Activator.PLUGIN_ID + ".XWidgetProvider", "XWidgetProvider", "classname", true);
      return contributions.getObjects();
   }
}
