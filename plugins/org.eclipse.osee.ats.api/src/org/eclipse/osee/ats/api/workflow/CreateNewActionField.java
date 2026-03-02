/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.api.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;

/**
 * @author Ryan T. Baldwin
 */
public class CreateNewActionField {

   public static final CreateNewActionField Originator =
      new CreateNewActionField("Originator", WidgetType.SELECT, WidgetIdAts.XXOriginatorWidget);
   public static final CreateNewActionField Assignees =
      new CreateNewActionField("Assignees", WidgetType.MULTISELECT, WidgetIdAts.XHyperlinkAssigneesWidget);
   public static final CreateNewActionField TargetedVersion =
      new CreateNewActionField("Targeted Version", WidgetType.SELECT, WidgetIdAts.XXTargetedVersionWidget);
   public static final CreateNewActionField Points = new CreateNewActionField("Points", WidgetType.SELECT,
      WidgetId.XHyperlinkWfdForEnumAttrWidget, AtsAttributeTypes.Points);
   public static final CreateNewActionField UnplannedWork = new CreateNewActionField("Unplanned Work",
      WidgetType.BOOLEAN, WidgetId.XCheckBoxWidget, AtsAttributeTypes.UnplannedWork);
   public static final CreateNewActionField WorkPackage =
      new CreateNewActionField("Work Package", WidgetType.TEXT, WidgetId.XTextWidget, AtsAttributeTypes.WorkPackage);
   public static final CreateNewActionField Sprint =
      new CreateNewActionField("Sprint", WidgetType.SELECT, WidgetIdAts.XHyperlinkSprintWidget);
   public static final CreateNewActionField FeatureGroup =
      new CreateNewActionField("Feature Group", WidgetType.MULTISELECT, WidgetIdAts.XHyperlinkAgileFeatureWidget);

   private final String name;
   private final String widgetName;
   private final WidgetType widgetType;
   private final AttributeTypeToken attribute;
   private final WidgetId widgetId;

   public CreateNewActionField(String name, WidgetType widgetType, WidgetId widgetId) {
      this(name, widgetType, widgetId, null);
   }

   public CreateNewActionField(String name, WidgetType widgetType, WidgetId widgetId, AttributeTypeToken attribute) {
      this.name = name;
      this.widgetId = widgetId;
      this.widgetName = widgetId.getName();
      this.widgetType = widgetType;
      this.attribute = attribute;
   }

   public String getName() {
      return name;
   }

   @JsonIgnore
   public String getWidgetName() {
      return widgetName;
   }

   public WidgetType getWidgetType() {
      return widgetType;
   }

   public AttributeTypeToken getAttribute() {
      return attribute;
   }

   public WidgetId getWidgetId() {
      return widgetId;
   }

}
