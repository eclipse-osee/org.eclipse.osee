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
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;

/**
 * ATS-specific extension of XWidgetBuilder providing convenience methods for common ATS widgets.
 *
 * @author Donald G. Dunne
 */
public class XWidgetBuilderAts extends XWidgetBuilder {

   // =========================================================================
   // Constructors
   // =========================================================================

   public XWidgetBuilderAts() {
   }

   // =========================================================================
   // Actionable Item widgets
   // =========================================================================

   public XWidgetBuilderAts andAiWidget() {
      andWidget(XWidgetLabels.ACTIONABLE_ITEMS, WidgetIdAts.XXActionableItemWidget);
      return this;
   }

   public XWidgetBuilder andActionableItemWidget(String label) {
      andWidget(label, WidgetIdAts.XXActionableItemWidget);
      return this;
   }

   public XWidgetBuilder andActionableItemWidget() {
      andWidget(WidgetIdAts.XXActionableItemWidget);
      return this;
   }

   // =========================================================================
   // Change Type / Priority widgets
   // =========================================================================

   public XWidgetBuilder andChangeTypeWidget() {
      andWidget(WidgetIdAts.XXChangeTypeWidget);
      return this;
   }

   public XWidgetBuilder andChangeTypeWidget(List<ChangeTypes> changeTypes) {
      andWidget(WidgetIdAts.XXChangeTypeWidget);
      widData.setSelectable(OseeEnum.toStrings(changeTypes));
      return this;
   }

   public XWidgetBuilder andPriorityWidget() {
      andWidget(WidgetIdAts.XXPriorityWidget);
      return this;
   }

   public XWidgetBuilder andPriorityWidget(List<Priorities> priorities) {
      andWidget(WidgetIdAts.XXPriorityWidget);
      widData.setSelectable(OseeEnum.toStrings(priorities));
      return this;
   }

   public XWidgetBuilder andCogPriorityWidget() {
      andWidget(WidgetId.XXStringsEnumArtWidget);
      widData.setLabel("COG Priority");
      return this;
   }

   // =========================================================================
   // Points widgets
   // =========================================================================

   public XWidgetBuilder andPointsEnumWidget() {
      andWidget(WidgetIdAts.XXPointsEnumWidget);
      return this;
   }

   // =========================================================================
   // Team / Program widgets
   // =========================================================================

   public XWidgetBuilder andTeamDefinitionWidget() {
      andWidget(WidgetIdAts.XXTeamDefinitionsWidget);
      and(XOption.VAL_SINGLE_SEL);
      return this;
   }

   public XWidgetBuilder andTeamDefinitionsWidget() {
      andWidget(WidgetIdAts.XXTeamDefinitionsWidget);
      return this;
   }

   public XWidgetBuilder andProgramWidget() {
      andWidget(WidgetIdAts.XXProgramWidget);
      return this;
   }

   // =========================================================================
   // Version widgets
   // =========================================================================

   public XWidgetBuilder andVersionWidget() {
      andWidget(WidgetIdAts.XXVersionSelWidget);
      return this;
   }

   public XWidgetBuilder andTargetedVersionWidget() {
      andWidget(WidgetIdAts.XXTargetedVersionWidget);
      return this;
   }

   // =========================================================================
   // User widgets (Originator, Assignees)
   // =========================================================================

   public XWidgetBuilder andOriginatorWidget() {
      andWidget(WidgetIdAts.XXOriginatorWidget);
      andDefault(OseeApiService.user());
      return this;
   }

   public XWidgetBuilder andAssigneesWidget() {
      andWidget(WidgetIdAts.XXAssigneesWidget);
      return this;
   }

   // =========================================================================
   // Agile widgets
   // =========================================================================

   public XWidgetBuilder andAgileFeatureWidget() {
      andWidget(WidgetIdAts.XXAgileFeatureSelWidget);
      return this;
   }

   public XWidgetBuilder andSprintWidget() {
      andWidget(WidgetIdAts.XXAgileSprintSelWidget);
      return this;
   }

   // =========================================================================
   // Workflow state widgets (Percent, Hours, Block, Hold, Notes)
   // =========================================================================

   public XWidgetBuilder andPercentCompleteWfeWidget() {
      andWidget(WidgetIdAts.XXPercentWfeWidget);
      return this;
   }

   public XWidgetBuilder andRemainingHoursWfeWidget() {
      andWidget(WidgetIdAts.XXRemainingHoursWfeWidget);
      return this;
   }

   public XWidgetBuilder andHoursSpentWfeWidget() {
      andWidget(WidgetIdAts.XXHoursSpentWfeWidget);
      return this;
   }

   public XWidgetBuilder andTotalHoursSpentWidget() {
      andWidget(WidgetIdAts.XXTotalHoursSpentWfeWidget);
      return this;
   }

   public XWidgetBuilder andBlockWidget() {
      andWidget(WidgetIdAts.XXBlockWfeWidget);
      return this;
   }

   public XWidgetBuilder andHoldWidget() {
      andWidget(WidgetIdAts.XXHoldWfeWidget);
      return this;
   }

   public XWidgetBuilder andWorkflowNotesWidget() {
      andWidget(WidgetIdAts.XXWorkflowNotesWfeWidget).startComp(2);
      return this;
   }

   // =========================================================================
   // Attachment widgets
   // =========================================================================

   public XWidgetBuilder andDropAttachmentWidget() {
      andWidget(WidgetIdAts.XXDragDropAttachWfeWidget);
      return this;
   }

}
