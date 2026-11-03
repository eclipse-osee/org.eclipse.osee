/*********************************************************************
 * Copyright (c) 2026 Boeing
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
   public static final WidgetId COMPOSITE = new WidgetId(8888, "Group Composite");
   public static final WidgetId GROUP_NAMED_COMPOSITE = new WidgetId(9999, "Group Composite");

   // @formatter:off
   // START
   public static final WidgetId XBranchViewerWidget = new WidgetId("XBranchViewerWidget");
   public static final WidgetId XHistoryWidget = new WidgetId("XHistoryWidget");
   public static final WidgetId XMergeWidget = new WidgetId("XMergeWidget");
   public static final WidgetId XXArtEdAttrViewerWidget = new WidgetId("XXArtEdAttrViewerWidget");
   public static final WidgetId XXArtifactListWidget = new WidgetId("XXArtifactListWidget");
   public static final WidgetId XXArtifactRefWidget = new WidgetId("XXArtifactRefWidget");
   public static final WidgetId XXArtifactSelWidget = new WidgetId("XXArtifactSelWidget");
   public static final WidgetId XXArtifactTypeWidget = new WidgetId("XXArtifactTypeWidget");
   public static final WidgetId XXAttributeTypeWidget = new WidgetId("XXAttributeTypeWidget");
   public static final WidgetId XXBooleanWidget = new WidgetId("XXBooleanWidget");
   public static final WidgetId XXBranchAndViewSelWidget = new WidgetId("XXBranchAndViewSelWidget");
   public static final WidgetId XXBranchSelWidget = new WidgetId("XXBranchSelWidget");
   public static final WidgetId XXBranchViewWidget = new WidgetId("XXBranchViewWidget");
   public static final WidgetId XXButtonHyperWidget = new WidgetId("XXButtonHyperWidget");
   public static final WidgetId XXButtonPushWidget = new WidgetId("XXButtonPushWidget");
   public static final WidgetId XXButtonViaActionWidget = new WidgetId("XXButtonViaActionWidget");
   public static final WidgetId XXCheckBoxWidget = new WidgetId("XXCheckBoxWidget");
   public static final WidgetId XXCopyIdHyperlinkWidget = new WidgetId("XXCopyIdHyperlinkWidget");
   public static final WidgetId XXChildSelWidget = new WidgetId("XXChildSelWidget");
   public static final WidgetId XXComputedCharacteristicWidget = new WidgetId("XXComputedCharacteristicWidget");
   public static final WidgetId XXDateSelWidget = new WidgetId("XXDateSelWidget");
   public static final WidgetId XXDirSelWidget = new WidgetId("XXDirSelWidget");
   public static final WidgetId XXDoubleSTextWidget = new WidgetId("XXDoubleSTextWidget");
   public static final WidgetId XXFileSelWidget = new WidgetId("XXFileSelWidget");
   public static final WidgetId XXFlatAttrsWidget = new WidgetId("XXFlatAttrsWidget");
   public static final WidgetId XXIntegerSTextWidget = new WidgetId("XXIntegerSTextWidget");
   public static final WidgetId XXLabelValueWidget = new WidgetId("XXLabelValueWidget");
   public static final WidgetId XXLabelWidget = new WidgetId("XXLabelWidget");
   public static final WidgetId XXListArtifactDropWidget = new WidgetId("XXListArtifactDropWidget");
   public static final WidgetId XXListRelationWidget = new WidgetId("XXListRelationWidget");
   public static final WidgetId XXListWidget = new WidgetId("XXListWidget");
   public static final WidgetId XXLongSTextWidget = new WidgetId("XXLongSTextWidget");
   public static final WidgetId XXMapEntryWidget = new WidgetId("XXMapEntryWidget");
   public static final WidgetId XXPercentSTextWidget = new WidgetId("XXPercentSTextWidget");
   public static final WidgetId XXStackedWidget = new WidgetId("XXStackedWidget");
   public static final WidgetId XXStringSTextHEditWidget = new WidgetId("XXStringSTextHEditWidget");
   public static final WidgetId XXStringsEnumArtWidget = new WidgetId("XXStringsEnumArtWidget");
   public static final WidgetId XXStringsWidget = new WidgetId("XXStringsWidget");
   public static final WidgetId XXTextInternalWidget = new WidgetId("XXTextInternalWidget");
   public static final WidgetId XXTextWidget = new WidgetId("XXTextWidget");
   public static final WidgetId XXUserTokenWidget = new WidgetId("XXUserTokenWidget");
   public static final WidgetId XXWorkDefReferenceWidget = new WidgetId("XXWorkDefReferenceWidget");
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
