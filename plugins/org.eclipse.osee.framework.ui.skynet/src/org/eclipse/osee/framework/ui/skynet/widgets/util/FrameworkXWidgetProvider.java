/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XOptionHandler;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeType2Widget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeTypeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.EnumeratedArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.SkynetSpellModifyDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactMultiChoiceSelect;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactSelectWidgetWithSave;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxesExample;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboBooleanDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboEnumDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XComputedCharacteristicWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog.Type;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelectionDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkArtifactRefIdEntryWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelDateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelEnumeratedArt;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelEnumeratedArtDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelectionDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueStringSel;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkTriStateBoolean;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkTriStateBooleanDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnumAttr;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnumAttrDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForUser;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForUserAll;
import org.eclipse.osee.framework.ui.skynet.widgets.XInteger;
import org.eclipse.osee.framework.ui.skynet.widgets.XIntegerDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewWithSave;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XListRelationWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLong;
import org.eclipse.osee.framework.ui.skynet.widgets.XLongDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XMapEntry;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtons;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriStateDam;
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
   private static Map<String, Class<? extends XWidget>> nameToClass = null;
   private static List<IXWidgetProvider> providers;
   public final static String OPTIONS_FROM_ATTRIBUTE_VALIDITY = "OPTIONS_FROM_ATTRIBUTE_VALIDITY";

   private static Map<String, Class<? extends XWidget>> getNameToClass() {
      register(XArtifactSelectWidget.class);
      register(XArtifactSelectWidgetWithSave.class);
      register(XHyperlabelMemberSelectionDam.class);
      register(XHyperlinkArtifactRefIdEntryWidget.class);
      register(XHyperlinkLabelDate.class);
      register(XHyperlinkLabelDateDam.class);
      register(XHyperlinkLabelEnumeratedArt.class);
      register(XHyperlinkLabelEnumeratedArtDam.class);
      register(XHyperlinkLabelValueSelectionDam.class);
      register(XHyperlinkLabelValueStringSel.class);
      register(XHyperlinkTriStateBoolean.class);
      register(XHyperlinkTriStateBooleanDam.class);
      register(XHyperlinkWfdForEnumAttr.class);
      register(XHyperlinkWfdForEnumAttrDam.class);
      register(XHyperlinkWfdForUser.class);
      register(XHyperlinkWfdForUserAll.class);
      return nameToClass;
   }

   public static void register(Class<? extends XWidget> clazz) {
      if (nameToClass == null) {
         nameToClass = new HashMap<String, Class<? extends XWidget>>();
      }
      nameToClass.put(clazz.getSimpleName(), clazz);
   }

   private FrameworkXWidgetProvider() {
      // Hide Constructor to enforce singleton pattern
   }

   public static FrameworkXWidgetProvider getInstance() {
      return reference;
   }

   private String getXWidgetNameBasedOnAttribute(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      if (attributeType != AttributeTypeToken.SENTINEL) {
         IAttributeXWidgetProvider xWidgetProvider =
            AttributeXWidgetManager.getAttributeXWidgetProvider(artType, attributeType);
         List<XWidgetData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(artType, attributeType);
         return concreteWidgets.iterator().next().getXWidgetName();
      }
      return null;
   }

   public XWidget createXWidget(XWidgetData widData, SwtXWidgetRenderer swtXWidgetRenderer) {
      String xWidgetName = widData.getXWidgetName();
      String widgetLabel = widData.getName();
      XWidget xWidget = null;
      try {

         OrcsTokenService tokenService = ServiceUtil.getTokenService();
         // Set xWidgetName from attribute type if not already set
         Artifact artifact = (widData.getArtifact() instanceof Artifact) ? (Artifact) widData.getArtifact() : null;
         if (!Strings.isValid(xWidgetName) && artifact != null) {
            AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
            ArtifactTypeToken artType = ArtifactTypeToken.SENTINEL;
            if (!Strings.isValid(xWidgetName) && widData.getStoreId() > 0) {
               tokenService.getArtifactType(widData.getArtifactType().getId());
            }
            if (attributeType == AttributeTypeToken.SENTINEL && !Strings.isValid(xWidgetName) && Strings.isValid(
               widData.getStoreName())) {
               attributeType = tokenService.getAttributeType(widData.getStoreName());
            }
            xWidgetName = getXWidgetNameBasedOnAttribute(artType, attributeType);
         }
         if (xWidgetName != null) {
            xWidget = getXWidget(widData, xWidgetName, widgetLabel, artifact, swtXWidgetRenderer);
         }
         if (xWidget != null) {
            if (widData.getArtifactType().isValid()) {
               xWidget.setArtifactType(widData.getArtifactType());
            }
            if (widData.getAttributeType().isValid()) {
               xWidget.setAttributeType(widData.getAttributeType());
            }
            if (widData.getAttributeType2().isValid()) {
               xWidget.setAttributeType2(widData.getAttributeType2());
            }
            if (widData.getEnumeratedArt().isValid()) {
               xWidget.setEnumeratedArt(widData.getEnumeratedArt());
            }
            xWidget.setOseeImage(widData.getOseeImage());
            xWidget.setTeamId(widData.getTeamId());
            if (artifact != null) {
               AttributeTypeToken attributeType = getAttributeTypeOrSentinel(widData, xWidget, tokenService);
               if (attributeType != AttributeTypeToken.SENTINEL && xWidget instanceof AttributeWidget) {
                  ((AttributeWidget) xWidget).setAttributeType(artifact, attributeType);
               }
               if (xWidget instanceof ArtifactWidget) {
                  ((ArtifactWidget) xWidget).setArtifact(artifact);
               }
            }
            if (xWidget instanceof AttributeTypeWidget) {
               AttributeTypeToken attributeType = getAttributeTypeOrSentinel(widData, xWidget, tokenService);
               ((AttributeTypeWidget) xWidget).setAttributeType(attributeType);
            }
            if (xWidget instanceof EnumeratedArtifactWidget) {
               EnumeratedArtifactWidget widget = (EnumeratedArtifactWidget) xWidget;
               if (widData.getEnumeratedArt() != null) {
                  widget.setEnumeratedArt(widData.getEnumeratedArt());
               }
            }
            xWidget.setObject(widData.getObject());
         }
      } catch (Exception ex) {
         String msg = String.format("Error creating widget for [%s][%s] exception: [%s] (see error log for details)",
            xWidgetName, widgetLabel, ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, msg, ex);
         xWidget = new XLabel(msg);
      }
      return xWidget;
   }

   private AttributeTypeToken getAttributeTypeOrSentinel(XWidgetData widData, XWidget xWidget,
      OrcsTokenService tokenService) {
      AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
      if (xWidget instanceof AttributeWidget && widData.getStoreId() > 0) {
         attributeType = tokenService.getAttributeType(widData.getStoreId());
      }
      if (attributeType == AttributeTypeToken.SENTINEL && Strings.isValid(widData.getStoreName())) {
         attributeType = tokenService.getAttributeType(widData.getStoreName());
      }
      return attributeType;
   }

   public static XWidget getXWidget(XWidgetData widData, String xWidgetName, String widgetLabel, Artifact artifact,
      SwtXWidgetRenderer swtXWidgetRenderer) {
      XWidget xWidget = null;
      // Look for widget provider to create widget
      Collection<IXWidgetProvider> providers = getXWidgetProviders();
      for (IXWidgetProvider widgetProvider : providers) {
         xWidget = widgetProvider.createXWidget(xWidgetName, widgetLabel, widData);
         if (xWidget != null) {
            break;
         }
      }

      @SuppressWarnings("unchecked")
      Class<XWidget> clazz = (Class<XWidget>) getNameToClass().get(xWidgetName);
      if (clazz != null) {
         try {
            xWidget = clazz.getDeclaredConstructor().newInstance();
         } catch (Exception ex) {
            OseeLog.log(FrameworkXWidgetProvider.class, Level.SEVERE, ex.toString(), ex);
         }
      }

      // Otherwise, use default widget creation
      XOptionHandler options = widData.getXOptionHandler();
      if (xWidget == null) {

         if (xWidgetName.equals("XText")) {
            xWidget = new XText(widgetLabel);
            if (Strings.isValid(widData.getDefaultValue())) {
               ((XText) xWidget).set(widData.getDefaultValue());
            }
         } else if (xWidgetName.equals("XSelectFromMultiChoiceBranch")) {
            XSelectFromMultiChoiceBranch multiBranchSelect = new XSelectFromMultiChoiceBranch(widgetLabel);
            try {
               List<? extends BranchToken> branches =
                  BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING, BranchType.BASELINE);
               Collections.sort(branches);

               multiBranchSelect.setSelectableItems(branches);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            multiBranchSelect.setRequiredEntry(true);
            xWidget = multiBranchSelect;
         } else if (xWidgetName.equals("XInteger")) {
            xWidget = new XInteger(widgetLabel);
         } else if (xWidgetName.equals("XLong")) {
            xWidget = new XLong(widgetLabel);
         } else if (xWidgetName.equals("XTextDam")) {
            xWidget = new XTextDam(widgetLabel);
         } else if (xWidgetName.equals("XButton")) {
            xWidget = new XButton(widgetLabel);
         } else if (xWidgetName.equals("XButtonPush")) {
            xWidget = new XButtonPush(widgetLabel);
         } else if (xWidgetName.equals(XRadioButtonsBooleanTriStateDam.WIDGET_ID)) {
            xWidget = new XRadioButtonsBooleanTriStateDam(widgetLabel);
         } else if (xWidgetName.equals(XRadioButtonsBooleanTriState.WIDGET_ID)) {
            xWidget = new XRadioButtonsBooleanTriState(widgetLabel);
         } else if (xWidgetName.startsWith("XRadioButtons")) {
            XRadioButtons radio = new XRadioButtons(widgetLabel);
            xWidget = radio;

            List<String> values = getWidgetOptions(widData);
            if (!values.isEmpty()) {

               if (options.contains(XOption.SORTED)) {
                  values.sort(Comparator.naturalOrder());
               }

               String defaultValue = widData.getDefaultValue();
               for (String value : values) {
                  XRadioButton button = radio.addButton(value);
                  if (Strings.isValid(defaultValue) && value.equals(defaultValue)) {
                     button.setSelected(true);
                  }
               }
            }
         } else if (xWidgetName.equals("XLabelDam")) {
            xWidget = new XLabelDam(widgetLabel);
         } else if (xWidgetName.equals("XMembersCombo")) {
            xWidget = new XMembersCombo(widgetLabel);
         } else if (xWidgetName.equals("XMembersComboAll")) {
            xWidget = new XMembersCombo(widgetLabel, true);
         } else if (xWidgetName.equals("XDate")) {
            xWidget = new XDate(widgetLabel);
            xWidget.setDefaultValueObj(widData.getDefaultValueObj());
         } else if (xWidgetName.equals("XMapEntry")) {
            xWidget = new XMapEntry();
            xWidget.setDefaultValueObj(widData.getDefaultValueObj());
         } else if (xWidgetName.equals("XFileSelectionDialog")) {
            xWidget = new XFileTextWithSelectionDialog(widgetLabel);
         } else if (xWidgetName.equals("XDirectorySelectionDialog")) {
            String defaultValue = widData.getDefaultValue();
            if (Strings.isValid(defaultValue)) {
               xWidget = new XFileTextWithSelectionDialog(widgetLabel, Type.Directory, defaultValue);
            } else {
               xWidget = new XFileTextWithSelectionDialog(widgetLabel, Type.Directory);
            }
         } else if (xWidgetName.equals("XDateDam")) {
            xWidget = new XDateDam(widgetLabel);
         } else if (xWidgetName.equals("XTextResourceDropDam")) {
            xWidget = new XTextResourceDropDam(widgetLabel);
         } else if (xWidgetName.equals("XFloat")) {
            xWidget = new XFloat(widgetLabel);
         } else if (xWidgetName.equals("XFloatDam")) {
            xWidget = new XFloatDam(widgetLabel);
         } else if (xWidgetName.equals("XIntegerDam")) {
            xWidget = new XIntegerDam(widgetLabel);
         } else if (xWidgetName.equals("XLongDam")) {
            xWidget = new XLongDam(widgetLabel);
         } else if (xWidgetName.equals("XFileTextWithSelectionDialog")) {
            xWidget = new XFileTextWithSelectionDialog(widgetLabel);
         } else if (xWidgetName.equals("XLabel")) {
            String defaultValue = widData.getDefaultValue();
            if (Strings.isValid(defaultValue)) {
               xWidget = new XLabel(widgetLabel, widData.getDefaultValue());
            } else {
               xWidget = new XLabel(widgetLabel);
            }
         } else if (xWidgetName.equals(XCheckBox.WIDGET_ID)) {
            XCheckBox checkBox = new XCheckBox(widgetLabel);
            checkBox.setLabelAfter(options.contains(XOption.LABEL_AFTER));
            if (Strings.isValid(widData.getDefaultValue())) {
               checkBox.set(Boolean.valueOf(widData.getDefaultValue()));
            }
            xWidget = checkBox;
         } else if (xWidgetName.equals(XCheckBoxDam.WIDGET_ID)) {
            XCheckBoxDam checkBox = new XCheckBoxDam(widgetLabel);
            checkBox.setLabelAfter(options.contains(XOption.LABEL_AFTER));
            xWidget = checkBox;
         } else if (xWidgetName.equals(XCheckBoxThreeState.WIDGET_ID)) {
            XCheckBoxThreeState checkBox = new XCheckBoxThreeState(widgetLabel);
            checkBox.setLabelAfter(options.contains(XOption.LABEL_AFTER));
            xWidget = checkBox;
         } else if (xWidgetName.equals(XCheckBoxThreeStateDam.WIDGET_ID)) {
            XCheckBoxThreeStateDam checkBox = new XCheckBoxThreeStateDam(widgetLabel);
            checkBox.setLabelAfter(options.contains(XOption.LABEL_AFTER));
            xWidget = checkBox;
         } else if (xWidgetName.equals(XComboEnumDam.WIDGET_ID)) {
            XComboEnumDam combo = new XComboEnumDam(widgetLabel);
            xWidget = combo;
         } else if (xWidgetName.startsWith("XComboDam")) {
            if (swtXWidgetRenderer != null) {
               XComboDam combo = new XComboDam(widgetLabel);
               xWidget = combo;
               List<String> values = getWidgetOptions(widData);
               if (!values.isEmpty()) {
                  xWidget = new XComboDam(widgetLabel);
                  combo.setDataStrings(values);
                  if (options.contains(XOption.NO_DEFAULT_VALUE)) {
                     combo.setDefaultSelectionAllowed(false);
                  }
                  if (options.contains(XOption.ADD_DEFAULT_VALUE)) {
                     combo.setDefaultSelectionAllowed(true);
                  }
               }
            }
         } else if (xWidgetName.startsWith("XSelectFromMultiChoiceDam")) {
            if (swtXWidgetRenderer != null) {
               XSelectFromMultiChoiceDam widget = new XSelectFromMultiChoiceDam(widgetLabel);
               xWidget = widget;
               List<String> values = getWidgetOptions(widData);
               if (!values.isEmpty()) {
                  widget.setSelectableItems(values);
               }
            }
         } else if (xWidgetName.startsWith("XStackedDam")) {
            xWidget = new XStackedDam(widgetLabel);
         } else if (xWidgetName.startsWith("XFlatDam")) {
            xWidget = new XTextFlatDam(widgetLabel);
         } else if (xWidgetName.startsWith("XComboBooleanDam")) {
            xWidget = new XComboBooleanDam(widgetLabel);
            XComboBooleanDam combo = new XComboBooleanDam(widgetLabel);
            combo.setDataStrings(BooleanAttribute.booleanChoices);
            xWidget = combo;
            if (Strings.isValid(widData.getDefaultValue())) {
               String value = widData.getDefaultValue();
               if ("true".equals(value)) {
                  combo.set("true");
               } else if ("false".equals(value)) {
                  combo.set("false");
               } else {
                  combo.set("");
               }
            }
            if (options.contains(XOption.NO_DEFAULT_VALUE)) {
               combo.setDefaultSelectionAllowed(false);
            }
            if (options.contains(XOption.ADD_DEFAULT_VALUE)) {
               combo.setDefaultSelectionAllowed(true);
            }
         } else if (xWidgetName.startsWith("XComboViewer")) {
            xWidget = new XComboViewer(widgetLabel, SWT.NONE);
         } else if (xWidgetName.startsWith("XCombo")) {
            XCombo combo = new XCombo(widgetLabel);
            xWidget = combo;
            List<String> values = getWidgetOptions(widData);
            if (!values.isEmpty()) {

               if (options.contains(XOption.SORTED)) {
                  values.sort(Comparator.naturalOrder());
               }
               combo.setDataStrings(values);

               if (options.contains(XOption.NO_DEFAULT_VALUE)) {
                  combo.setDefaultSelectionAllowed(false);
               }
               if (options.contains(XOption.ADD_DEFAULT_VALUE)) {
                  combo.setDefaultSelectionAllowed(true);
               }
               String defaultValue = widData.getDefaultValue();
               if (Strings.isValid(defaultValue)) {
                  combo.setDefaultValue(defaultValue);
               }
            }
         } else if (xWidgetName.startsWith("XListDam")) {
            XListDam list = new XListDam(widgetLabel);
            xWidget = list;
            List<String> values = getWidgetOptions(widData);
            if (!values.isEmpty()) {
               list.add(values);
            }
         } else if (xWidgetName.equals("XHyperlabelMemberSelectionDam")) {
            xWidget = new XHyperlabelMemberSelectionDam(widgetLabel);
         } else if (xWidgetName.equals("XHyperlabelMemberSelection")) {
            xWidget = new XHyperlabelMemberSelection(widgetLabel);
         } else if (xWidgetName.startsWith("XListDropViewer")) {
            if ("XListDropViewerWithSave".equals(xWidgetName)) {
               xWidget = new XListDropViewWithSave(widgetLabel);
            } else {
               xWidget = new XListDropViewer(widgetLabel);
            }
         } else if (xWidgetName.equals(XListRelationWidget.WIDGET_ID)) {
            return new XListRelationWidget(artifact, widgetLabel, widData.getRelationTypeSide());
         } else if (xWidgetName.equals(XComputedCharacteristicWidget.WIDGET_ID)) {
            if (widgetLabel.equals("IsInTest")) {
               return new XComputedCharacteristicWidget(ComputedCharacteristicToken.SENTINEL);
            }
            return new XComputedCharacteristicWidget(widData.getComputedCharacteristic());
         } else if (xWidgetName.equals("XListDropViewWithSave")) {
            XListDropViewWithSave xList = new XListDropViewWithSave(widgetLabel);
            xWidget = xList;
         } else if (xWidgetName.startsWith("XList")) {
            XList list = new XList(widgetLabel);
            xWidget = list;
            List<String> values = getWidgetOptions(widData);
            if (!values.isEmpty()) {
               list.add(values);
               String defaultValue = widData.getDefaultValue();
               if (Strings.isValid(defaultValue)) {
                  list.setSelected(Arrays.asList(defaultValue.split(",")));
               }
            }
         } else if (xWidgetName.startsWith("XArtifactList")) {
            XArtifactList artifactList = new XArtifactList(widgetLabel);
            artifactList.setMultiSelect(options.contains(XOption.MULTI_SELECT));
            xWidget = artifactList;
         } else if (xWidgetName.equals(XBranchSelectWidgetDam.WIDGET_ID)) {
            xWidget = new XBranchSelectWidgetDam();
         } else if (xWidgetName.startsWith(XBranchSelectWidget.WIDGET_ID)) {
            XBranchSelectWidget widget = null;

            if (xWidgetName.endsWith("WithSave")) {
               widget = new XBranchSelectWidgetWithSave(widgetLabel);
            } else {
               widget = new XBranchSelectWidget(widgetLabel);
            }

            widget.setToolTip(widData.getToolTip());
            try {
               String branchUuid = widData.getDefaultValue();
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
            XArtifactTypeMultiChoiceSelect widget = new XArtifactTypeMultiChoiceSelect(widgetLabel);
            String defaultType = widData.getDefaultValue();
            if (Strings.isValid(defaultType)) {
               List<ArtifactTypeToken> types = new LinkedList<>();
               for (String type : defaultType.split(",")) {
                  try {
                     types.add(ServiceUtil.getTokenService().getArtifactType(type));
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
         } else if (xWidgetName.equals("XHyperlinkLabel")) {
            xWidget = new XHyperlinkLabel(widgetLabel);
            String defaultValue = widData.getDefaultValue();
            if (Strings.isValid(defaultValue)) {
               XHyperlinkLabel widget = (XHyperlinkLabel) xWidget;
               widget.setUrl(widData.getDefaultValue());
            }
         } else if (xWidgetName.equals(XRadioButton.WIDGET_ID)) {
            xWidget = new XRadioButton(widgetLabel);
         } else if (xWidgetName.equals(XCheckBoxesExample.WIDGET_ID)) {
            xWidget = new XCheckBoxesExample();
         } else if (xWidgetName.equals(XLabelValue.WIDGET_ID)) {
            String defaultValue = widData.getDefaultValue();
            xWidget = new XLabelValue(widgetLabel, defaultValue);
         } else if (xWidgetName.equals(XHyperlinkLabelValueSelectionDam.WIDGET_ID)) {
            xWidget = new XHyperlinkLabelValueSelectionDam(widgetLabel);
         } else if (xWidgetName.equals(XHyperlinkLabelEnumeratedArtDam.WIDGET_ID)) {
            xWidget = new XHyperlinkLabelEnumeratedArtDam(widgetLabel);
         } else {
            xWidget = new XLabel("Error: Unhandled XWidget \"" + xWidgetName + "\"");
         }
      }

      if (xWidget instanceof XText) {
         ((XText) xWidget).addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
      }

      if (xWidget != null && options.contains(XOption.NO_LABEL)) {
         xWidget.setDisplayLabel(false);
      }
      if (xWidget != null) {
         xWidget.setOseeImage(widData.getOseeImage());
      }
      if (widData.getAttributeType().isValid() && xWidget instanceof AttributeTypeWidget) {
         ((AttributeTypeWidget) xWidget).setAttributeType(widData.getAttributeType());
      }
      if (widData.getAttributeType2().isValid() && xWidget instanceof AttributeType2Widget) {
         ((AttributeType2Widget) xWidget).setAttributeType2(widData.getAttributeType2());
      }

      return xWidget;
   }

   private static List<String> getWidgetOptions(XWidgetData widData) {
      if (widData.getXWidgetName().contains(OPTIONS_FROM_ATTRIBUTE_VALIDITY)) {
         List<String> options = new ArrayList<String>();
         OrcsTokenService tokenService = ServiceUtil.getTokenService();
         AttributeTypeGeneric<?> attributeType = AttributeTypeGeneric.SENTINEL;
         try {
            String storeName = widData.getStoreName();
            Long storeId = widData.getStoreId();
            if (storeId > 0) {
               attributeType = tokenService.getAttributeType(storeId);
            } else if (Strings.isValid(storeName)) {
               attributeType = tokenService.getAttributeType(storeName);
            } else if (Strings.isValid(widData.getName())) {
               attributeType = tokenService.getAttributeType(widData.getName());
            } else {
               throw new OseeArgumentException(
                  "Attribute Type can not be determined from storeName [%s] or Name [%s] and is needed for OPTIONS_FROM_ATTRIBUTE_VALIDITY for widget [%s]",
                  widData.getStoreName(), widData.getName(), widData);
            }
            if (attributeType.isEnumerated()) {
               options = attributeType.toEnum().getEnumStrValues();
            }
         } catch (Exception ex) {
            throw new OseeArgumentException(
               "Exception determining Attribute Type from storeName [%s] or Name [%s] and widget [%s]: %s",
               widData.getStoreName(), widData.getName(), widData, ex.getLocalizedMessage());
         }
         options.sort(Comparator.naturalOrder());
         return options;
      }

      List<String> values = new ArrayList<>();
      Matcher m = Pattern.compile("\\((.*?)\\)$").matcher(widData.getXWidgetName());
      if (m.find()) {
         String data = m.group(1);
         for (String val : data.split(",")) {
            if (Strings.isValid(val)) {
               values.add(val);
            }
         }
      }

      return values;
   }

   private static Collection<IXWidgetProvider> getXWidgetProviders() {
      if (providers == null) {
         ExtensionDefinedObjects<IXWidgetProvider> contributions = new ExtensionDefinedObjects<>(
            Activator.PLUGIN_ID + ".XWidgetProvider", "XWidgetProvider", "classname", true);
         providers = contributions.getObjects();
      }
      return providers;
   }
}
