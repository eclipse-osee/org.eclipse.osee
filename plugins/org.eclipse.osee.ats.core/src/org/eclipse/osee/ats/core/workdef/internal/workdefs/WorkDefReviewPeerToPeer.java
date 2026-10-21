/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.review.ReviewRole.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XUserRoleViewerWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXDefectViewerWidget;
import static org.eclipse.osee.framework.core.widget.XOption.HORZ_LABEL;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefReviewPeerToPeer extends AbstractWorkDef {

   private WorkDefBuilder bld;

   public WorkDefReviewPeerToPeer() {
      super(AtsWorkDefinitionTokens.WorkDefReviewPeerToPeer);
   }

   public WorkDefReviewPeerToPeer(AtsWorkDefinitionToken workDefToken) {
      super(workDefToken);
   }

   public WorkDefBuilder getWorkDefBuilder() {
      return bld;
   }

   @Override
   public WorkDefinition build() {
      bld = new WorkDefBuilder(workDefToken, AtsArtifactTypes.PeerToPeerReview);

      bld.andReviewRole(Author, 1)//
         .andReviewRole(Moderator)//
         .andReviewRole(ModeratorReviewer)//
         .andReviewRole(Quality)//
         .andReviewRole(Reviewer, 1);//

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToStates(StateToken.Review, StateToken.Meeting, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(Role, XUserRoleViewerWidget, REQUIRED), //
            new AttrWidgetDef(Location).set(REQUIRED), //

            new CompositeLayoutItem(8, //
               new AttrWidgetDef(ReviewBlocks).set(REQUIRED, HORZ_LABEL), //
               new AttrWidgetDef(ReviewFormalType).set(REQUIRED, HORZ_LABEL), //
               new AttrWidgetDef(NeedBy), //
               new AttrWidgetDef(RelatedToState) //
            ), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(EstimatedHours), //
               new AttrWidgetDef(EstimatedCompletionDate) //
            ), //
            new CompositeLayoutItem(8, //
               new AttrWidgetDef(LocChanged), //
               new AttrWidgetDef(LocReviewed), //
               new AttrWidgetDef(PagesChanged), //
               new AttrWidgetDef(PagesReviewed) //
            ));

      bld.andState(2, "Review", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Meeting, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(Role, XUserRoleViewerWidget, REQUIRED), //
            new AttrWidgetDef(ReviewDefect, XXDefectViewerWidget), //
            new CompositeLayoutItem(8, //
               new AttrWidgetDef(ReviewBlocks).set(REQUIRED, HORZ_LABEL), //
               new AttrWidgetDef(ReviewFormalType).set(REQUIRED, HORZ_LABEL), //
               new AttrWidgetDef(NeedBy), //
               new AttrWidgetDef(RelatedToState) //
            ), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(EstimatedHours), //
               new AttrWidgetDef(EstimatedCompletionDate) //
            ), //
            new CompositeLayoutItem(8, //
               new AttrWidgetDef(LocChanged), //
               new AttrWidgetDef(LocReviewed), //
               new AttrWidgetDef(PagesChanged), //
               new AttrWidgetDef(PagesReviewed) //
            ), //
            new AttrWidgetDef(Resolution));

      bld.andState(3, "Meeting", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(EstimatedHours), //
               new AttrWidgetDef(EstimatedCompletionDate) //
            ), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(MeetingLength).set(REQUIRED), //
               new AttrWidgetDef(MeetingLocation).set(REQUIRED) //
            ), //
            new WidgetDef(WidgetIdAts.XXMeetingAttendeesWfeWidget), //
            new AttrWidgetDef(Description), //
            new AttrWidgetDef(Role, XUserRoleViewerWidget, REQUIRED), //
            new AttrWidgetDef(ReviewDefect, XXDefectViewerWidget), //
            new CompositeLayoutItem(8, //
               new AttrWidgetDef(LocChanged), //
               new AttrWidgetDef(LocReviewed), //
               new AttrWidgetDef(PagesChanged), //
               new AttrWidgetDef(PagesReviewed) //
            ), //
            new AttrWidgetDef(Resolution));

      bld.andState(4, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview);

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }

}
