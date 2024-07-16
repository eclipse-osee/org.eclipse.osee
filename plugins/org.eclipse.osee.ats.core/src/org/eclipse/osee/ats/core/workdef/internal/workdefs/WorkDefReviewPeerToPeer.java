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

import static org.eclipse.osee.ats.api.review.ReviewRole.Author;
import static org.eclipse.osee.ats.api.review.ReviewRole.Moderator;
import static org.eclipse.osee.ats.api.review.ReviewRole.ModeratorReviewer;
import static org.eclipse.osee.ats.api.review.ReviewRole.Quality;
import static org.eclipse.osee.ats.api.review.ReviewRole.Reviewer;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.HORIZONTAL_LABEL;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.ReviewRoleType;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefReviewPeerToPeer extends AbstractWorkDef {

   private WorkDefBuilder bld;

   public WorkDefReviewPeerToPeer() {
      super(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer);
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
         .andReviewRole(Reviewer);//
      bld.andReviewRoleTypeMinimum(ReviewRoleType.Reviewer, 1);

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToStates(StateToken.Review, StateToken.Meeting, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.Role, "XUserRoleViewer", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Location, "XTextDam", FILL_VERTICALLY, REQUIRED_FOR_TRANSITION), //

            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.ReviewBlocks, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, HORIZONTAL_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.ReviewFormalType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, HORIZONTAL_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam"), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, "XStateCombo", FILL_VERTICALLY) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam") //
            ), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.LocChanged, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.LocReviewed, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.PagesChanged, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.PagesReviewed, "XIntegerDam") //
            ));

      bld.andState(2, "Review", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Meeting, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.Role, "XUserRoleViewer", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.ReviewDefect, "XDefectViewer"), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.ReviewBlocks, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, HORIZONTAL_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.ReviewFormalType, "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)",
                  REQUIRED_FOR_TRANSITION, HORIZONTAL_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam"), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, "XStateCombo", FILL_VERTICALLY) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam") //
            ), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.LocChanged, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.LocReviewed, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.PagesChanged, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.PagesReviewed, "XIntegerDam") //
            ), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(3, "Meeting", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, "XFloatDam"), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam") //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.MeetingLength, "XFloatDam", REQUIRED_FOR_TRANSITION), //
               new WidgetDefinition(AtsAttributeTypes.MeetingLocation, "XTextDam", REQUIRED_FOR_TRANSITION) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.MeetingAttendee, "XHyperlabelMemberSelDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERTICALLY), //
            new WidgetDefinition(AtsAttributeTypes.Role, "XUserRoleViewer", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.ReviewDefect, "XDefectViewer"), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.LocChanged, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.LocReviewed, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.PagesChanged, "XIntegerDam"), //
               new WidgetDefinition(AtsAttributeTypes.PagesReviewed, "XIntegerDam") //
            ), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));

      bld.andState(4, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }

}
