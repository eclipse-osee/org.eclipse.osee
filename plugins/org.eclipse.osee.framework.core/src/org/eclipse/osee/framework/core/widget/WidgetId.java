/*********************************************************************
 * Copyright (c) 2026
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
package org.eclipse.osee.framework.core.widget;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * WidgetId tokens for use by code to reference a widget. This file is auto-generated from XWidgetReview, so file
 * shouldn't be manually edited and start/end comments need to remain.
 *
 * @author Donald G. Dunne
 */
public class WidgetId extends OseeEnum {

   private static final Long ENUM_ID = 1288239239L;
   public static long count = 0;
   public static Map<String, WidgetId> nameToWidgetId = new HashMap<>(1000);
   public static WidgetId SENTINEL = new WidgetId(-1, "Sentinel");

   // @formatter:off
   // START
   public static final WidgetId XArtEdAttrViewerWidget = new WidgetId("XArtEdAttrViewerWidget");
   public static final WidgetId XArtifactListWidget = new WidgetId("XArtifactListWidget");
   public static final WidgetId XArtifactSelectPersistWidget = new WidgetId("XArtifactSelectPersistWidget");
   public static final WidgetId XArtifactSelectWidget = new WidgetId("XArtifactSelectWidget");
   public static final WidgetId XArtifactTypeComboViewerWidget = new WidgetId("XArtifactTypeComboViewerWidget");
   public static final WidgetId XArtifactTypeSelectionWidget = new WidgetId("XArtifactTypeSelectionWidget");
   public static final WidgetId XAttributeTypeComboViewerWidget = new WidgetId("XAttributeTypeComboViewerWidget");
   public static final WidgetId XAttributeTypeSelectionWidget = new WidgetId("XAttributeTypeSelectionWidget");
   public static final WidgetId XBarGraphTableWidget = new WidgetId("XBarGraphTableWidget");
   public static final WidgetId XBranchSelectArtWidget = new WidgetId("XBranchSelectArtWidget");
   public static final WidgetId XBranchSelectWidget = new WidgetId("XBranchSelectWidget");
   public static final WidgetId XBranchSelectionWidget = new WidgetId("XBranchSelectionWidget");
   public static final WidgetId XBranchWidget = new WidgetId("XBranchWidget");
   public static final WidgetId XButtonPushWidget = new WidgetId("XButtonPushWidget");
   public static final WidgetId XButtonViaActionWidget = new WidgetId("XButtonViaActionWidget");
   public static final WidgetId XButtonWidget = new WidgetId("XButtonWidget");
   public static final WidgetId XCheckBoxArtWidget = new WidgetId("XCheckBoxArtWidget");
   public static final WidgetId XCheckBoxWidget = new WidgetId("XCheckBoxWidget");
   public static final WidgetId XComboArtWidget = new WidgetId("XComboArtWidget");
   public static final WidgetId XComboBooleanArtWidget = new WidgetId("XComboBooleanArtWidget");
   public static final WidgetId XComboEnumArtWidget = new WidgetId("XComboEnumArtWidget");
   public static final WidgetId XComboViewerWidget = new WidgetId("XComboViewerWidget");
   public static final WidgetId XComboWidget = new WidgetId("XComboWidget");
   public static final WidgetId XComboWithTextWidget = new WidgetId("XComboWithTextWidget");
   public static final WidgetId XComputedCharacteristicArtWidget = new WidgetId("XComputedCharacteristicArtWidget");
   public static final WidgetId XDateArtWidget = new WidgetId("XDateArtWidget");
   public static final WidgetId XDateWidget = new WidgetId("XDateWidget");
   public static final WidgetId XErrorUnhandledWidget = new WidgetId("XErrorUnhandledWidget");
   public static final WidgetId XFilteredTreeWidget = new WidgetId("XFilteredTreeWidget");
   public static final WidgetId XFloatArtWidget = new WidgetId("XFloatArtWidget");
   public static final WidgetId XFloatTextWidget = new WidgetId("XFloatTextWidget");
   public static final WidgetId XHistoryWidget = new WidgetId("XHistoryWidget");
   public static final WidgetId XHyperLinkMemberSelArtWidget = new WidgetId("XHyperLinkMemberSelArtWidget");
   public static final WidgetId XHyperlinkArtEnumeratedArtWidget = new WidgetId("XHyperlinkArtEnumeratedArtWidget");
   public static final WidgetId XHyperlinkArtEnumeratedWidget = new WidgetId("XHyperlinkArtEnumeratedWidget");
   public static final WidgetId XHyperlinkArtifactRefIdEntryWidget = new WidgetId("XHyperlinkArtifactRefIdEntryWidget");
   public static final WidgetId XHyperlinkLabelDateArtWidget = new WidgetId("XHyperlinkLabelDateArtWidget");
   public static final WidgetId XHyperlinkLabelDateWidget = new WidgetId("XHyperlinkLabelDateWidget");
   public static final WidgetId XHyperlinkLabelValueSelectionArtWidget = new WidgetId("XHyperlinkLabelValueSelectionArtWidget");
   public static final WidgetId XHyperlinkLabelValueStringSelWidget = new WidgetId("XHyperlinkLabelValueStringSelWidget");
   public static final WidgetId XHyperlinkLabelWidget = new WidgetId("XHyperlinkLabelWidget");
   public static final WidgetId XHyperlinkMemberSelWidget = new WidgetId("XHyperlinkMemberSelWidget");
   public static final WidgetId XHyperlinkTriStateBooleanArtWidget = new WidgetId("XHyperlinkTriStateBooleanArtWidget");
   public static final WidgetId XHyperlinkTriStateBooleanWidget = new WidgetId("XHyperlinkTriStateBooleanWidget");
   public static final WidgetId XHyperlinkWfdBranchAndViewSelWidget = new WidgetId("XHyperlinkWfdBranchAndViewSelWidget");
   public static final WidgetId XHyperlinkWfdBranchSelWidget = new WidgetId("XHyperlinkWfdBranchSelWidget");
   public static final WidgetId XHyperlinkWfdBranchViewSelWidget = new WidgetId("XHyperlinkWfdBranchViewSelWidget");
   public static final WidgetId XHyperlinkWfdForEnumAttrArtWidget = new WidgetId("XHyperlinkWfdForEnumAttrArtWidget");
   public static final WidgetId XHyperlinkWfdForEnumAttrWidget = new WidgetId("XHyperlinkWfdForEnumAttrWidget");
   public static final WidgetId XHyperlinkWfdForEnumWidget = new WidgetId("XHyperlinkWfdForEnumWidget");
   public static final WidgetId XHyperlinkWfdForObjectWidget = new WidgetId("XHyperlinkWfdForObjectWidget");
   public static final WidgetId XHyperlinkWfdForUserAllWidget = new WidgetId("XHyperlinkWfdForUserAllWidget");
   public static final WidgetId XHyperlinkWfdForUserWidget = new WidgetId("XHyperlinkWfdForUserWidget");
   public static final WidgetId XIntegerArtWidget = new WidgetId("XIntegerArtWidget");
   public static final WidgetId XIntegerWidget = new WidgetId("XIntegerWidget");
   public static final WidgetId XLabelArtWidget = new WidgetId("XLabelArtWidget");
   public static final WidgetId XLabelValueWidget = new WidgetId("XLabelValueWidget");
   public static final WidgetId XLabelWidget = new WidgetId("XLabelWidget");
   public static final WidgetId XListArtWidget = new WidgetId("XListArtWidget");
   public static final WidgetId XListDropViewPersistArtWidget = new WidgetId("XListDropViewPersistArtWidget");
   public static final WidgetId XListDropViewerWidget = new WidgetId("XListDropViewerWidget");
   public static final WidgetId XListRelationArtWidget = new WidgetId("XListRelationArtWidget");
   public static final WidgetId XListViewerWidget = new WidgetId("XListViewerWidget");
   public static final WidgetId XListWidget = new WidgetId("XListWidget");
   public static final WidgetId XLongArtWidget = new WidgetId("XLongArtWidget");
   public static final WidgetId XLongWidget = new WidgetId("XLongWidget");
   public static final WidgetId XMapEntryWidget = new WidgetId("XMapEntryWidget");
   public static final WidgetId XMembersComboWidget = new WidgetId("XMembersComboWidget");
   public static final WidgetId XMergeWidget = new WidgetId("XMergeWidget");
   public static final WidgetId XPercentArtWidget = new WidgetId("XPercentArtWidget");
   public static final WidgetId XPercentWidget = new WidgetId("XPercentWidget");
   public static final WidgetId XRadioButtonWidget = new WidgetId("XRadioButtonWidget");
   public static final WidgetId XRadioButtonsBooleanArtWidget = new WidgetId("XRadioButtonsBooleanArtWidget");
   public static final WidgetId XRadioButtonsBooleanTriStateArtWidget = new WidgetId("XRadioButtonsBooleanTriStateArtWidget");
   public static final WidgetId XRadioButtonsBooleanTriStateWidget = new WidgetId("XRadioButtonsBooleanTriStateWidget");
   public static final WidgetId XRadioButtonsWidget = new WidgetId("XRadioButtonsWidget");
   public static final WidgetId XStackedArtWidget = new WidgetId("XStackedArtWidget");
   public static final WidgetId XTextFlatArtWidget = new WidgetId("XTextFlatArtWidget");
   public static final WidgetId XTextResourceDropArtWidget = new WidgetId("XTextResourceDropArtWidget");
   public static final WidgetId XTextWidget = new WidgetId("XTextWidget");
   public static final WidgetId XTextWithDirSelDialogWidget = new WidgetId("XTextWithDirSelDialogWidget");
   public static final WidgetId XTextWithFileSelDialogWidget = new WidgetId("XTextWithFileSelDialogWidget");
   public static final WidgetId XXStringsSelWidget = new WidgetId("XXStringsSelWidget");
   public static final WidgetId XXTextWidget = new WidgetId("XXTextWidget");
   public static final WidgetId XXUserTokenWidget = new WidgetId("XXUserTokenWidget");
   //END
   // @formatter:on

   public WidgetId(String name) {
      this(++count, name);
      nameToWidgetId.put(getName(), this);
   }

   public WidgetId(long id, String name) {
      super(ENUM_ID, id, name);
      nameToWidgetId.put(getName(), this);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return SENTINEL;
   }

   public static WidgetId getByName(String widgetName) {
      WidgetId wName = nameToWidgetId.get(widgetName);
      if (wName == null) {
         return WidgetId.SENTINEL;
      }
      return wName;
   }

   @Override
   public String toString() {
      return "WidgetId [id=" + id + ", name=" + getName() + "]";
   }

}
