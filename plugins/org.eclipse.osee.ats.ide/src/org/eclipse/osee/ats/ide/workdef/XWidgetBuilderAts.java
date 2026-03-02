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
package org.eclipse.osee.ats.ide.workdef;

import java.util.List;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;

public class XWidgetBuilderAts extends XWidgetBuilder {

   public XWidgetBuilderAts() {
   }

   public XWidgetBuilderAts andXHyperlinkActionableItemActive() {
      newXWidget();
      widData.setName("Actionable Item(s)");
      widData.setWidgetId(WidgetIdAts.XHyperlinkAiSelWidget);
      return this;
   }

   public XWidgetBuilderAts andXHyperlinkActionableItemActive(boolean singleSelect) {
      newXWidget();
      widData.add(XOption.SINGLE_SELECT);
      widData.setName("Actionable Item(s)");
      widData.setWidgetId(WidgetIdAts.XHyperlinkAiSelWidget);
      return this;
   }

   public XWidgetBuilderAts andXActionableItem() {
      newXWidget();
      widData.setWidgetId(WidgetIdAts.XActionableItemWidget);
      return this;
   }

   public XWidgetBuilder andChangeType() {
      newXWidget();
      widData.setName("Change Type");
      widData.setWidgetId(WidgetIdAts.XXChangeTypeWidget);
      return this;
   }

   public XWidgetBuilder andChangeType(List<ChangeTypes> changeTypes) {
      newXWidget();
      widData.setName("Change Type");
      widData.setWidgetId(WidgetIdAts.XXChangeTypeWidget);
      widData.setSelectable(changeTypes);
      return this;
   }

   public XWidgetBuilder andCogPriority() {
      newXWidget();
      widData.setName("COG Priority");
      widData.setWidgetId(WidgetId.XHyperlinkArtEnumeratedArtWidget);
      return this;
   }

   public XWidgetBuilder andPriority() {
      newXWidget();
      widData.setName("Priority");
      widData.setWidgetId(WidgetIdAts.XXPriorityWidget);
      return this;
   }

   public XWidgetBuilder andPriority(List<Priorities> priorities) {
      newXWidget();
      widData.setName("Priority");
      widData.setWidgetId(WidgetIdAts.XXPriorityWidget);
      widData.setSelectable(OseeEnum.toStrings(priorities));
      return this;
   }

   public XWidgetBuilder andTeamDefinitionWidget() {
      andWidget(WidgetIdAts.XTeamDefinitionComboWidget);
      return this;
   }

   public XWidgetBuilder andActionableItemActiveWidget() {
      andWidget(WidgetIdAts.XHyperlinkWfdForActiveAisWidget);
      return this;
   }

   public XWidgetBuilder andTargetedVersionWidget() {
      andWidget(WidgetIdAts.XXTargetedVersionWidget);
      return this;
   }

   public XWidgetBuilder andProgramSelWidget() {
      andWidget(WidgetIdAts.XAtsProgramComboWidget);
      return this;
   }

   public XWidgetBuilder andOriginator() {
      newXWidget();
      widData.setName("Originator");
      widData.setWidgetId(WidgetIdAts.XXOriginatorWidget);
      return this;
   }

   public XWidgetBuilder andAssignees() {
      newXWidget();
      widData.setName("Assignee(s)");
      widData.setWidgetId(WidgetIdAts.XXAssigneesWidget);
      return this;
   }

   public XWidgetBuilder andAgileFeature() {
      newXWidget();
      widData.setName("Feature(s)");
      widData.setWidgetId(WidgetIdAts.XHyperlinkAgileFeatureWidget);
      return this;
   }

   public XWidgetBuilder andSprint() {
      newXWidget();
      widData.setName("Sprint(s)");
      widData.setWidgetId(WidgetIdAts.XHyperlinkSprintWidget);
      return this;
   }

   public XWidgetBuilder andXXLabel(String label, String value) {
      return andXXLabel(Lib.generateId(), label, value);
   }

   public XWidgetBuilder andXXLabel(long id, String label, String value) {
      newXWidget();
      widData.setWidgetId(WidgetId.XXStringsSelWidget);
      widData.setName(label);
      widData.setDefaultValue(value);
      widData.setId(id);
      widData.add(XOption.NOT_EDITABLE);
      return this;
   }

   public XWidgetBuilderAts andOseeImage(OseeImage image) {
      widData.setOseeImage(image);
      return this;
   }

}
