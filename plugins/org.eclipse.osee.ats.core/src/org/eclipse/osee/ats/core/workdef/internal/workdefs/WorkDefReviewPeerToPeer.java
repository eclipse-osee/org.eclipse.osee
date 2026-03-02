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

import static org.eclipse.osee.ats.api.review.ReviewRole.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.MeetingAttendeeWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("unused")
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
         .andReviewRole(Reviewer, 1);//

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToStates(StateToken.Review, StateToken.Meeting, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.Role, XUserRoleViewerWidget, RFT), //
            new WidgetDefinition(AtsAttributeTypes.Location, XXTextWidget, FILL_VERT, RFT), //

            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.ReviewBlocks, XHyperlinkWfdForEnumAttrArtWidget, RFT, HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.ReviewFormalType, XHyperlinkWfdForEnumAttrArtWidget, RFT,
                  HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, XDateArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, XHyperlinkWfdForRelatedStateArtWidget, FILL_VERT) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget) //
            ), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.LocChanged, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.LocReviewed, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.PagesChanged, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.PagesReviewed, XIntegerArtWidget, SAVE) //
            ));

      bld.andState(2, "Review", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Meeting, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.Role, XUserRoleViewerWidget, RFT), //
            new WidgetDefinition(AtsAttributeTypes.ReviewDefect, XDefectViewerWidget), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.ReviewBlocks, XHyperlinkWfdForEnumAttrArtWidget, RFT, HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.ReviewFormalType, XHyperlinkWfdForEnumAttrArtWidget, RFT,
                  HORZ_LABEL), //
               new WidgetDefinition(AtsAttributeTypes.NeedBy, XDateArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.RelatedToState, XHyperlinkWfdForRelatedStateArtWidget, FILL_VERT) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget) //
            ), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.LocChanged, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.LocReviewed, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.PagesChanged, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.PagesReviewed, XIntegerArtWidget, SAVE) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(3, "Meeting", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.EstimatedHours, XFloatArtWidget), //
               new WidgetDefinition(AtsAttributeTypes.EstimatedCompletionDate, XDateArtWidget) //
            ), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.MeetingLength, XFloatArtWidget, RFT), //
               new WidgetDefinition(AtsAttributeTypes.MeetingLocation, XXTextWidget, RFT) //
            ), //
            new MeetingAttendeeWidgetDefinition().andRequiredForFormal(), //
            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT), //
            new WidgetDefinition(AtsAttributeTypes.Role, XUserRoleViewerWidget, RFT), //
            new WidgetDefinition(AtsAttributeTypes.ReviewDefect, XDefectViewerWidget), //
            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.LocChanged, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.LocReviewed, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.PagesChanged, XIntegerArtWidget, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.PagesReviewed, XIntegerArtWidget, SAVE) //
            ), //
            new WidgetDefinition(AtsAttributeTypes.Resolution, XXTextWidget, FILL_VERT));

      bld.andState(4, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }

}
