/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERTICALLY;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.HORIZONTAL_LABEL;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class WorkDefReviewPeerToPeer extends AbstractWorkDef {

   public WorkDefReviewPeerToPeer() {
      super(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer);
   }

   public WorkDefReviewPeerToPeer(AtsWorkDefinitionToken workDefToken) {
      super(workDefToken);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andState(1, "Prepare", StateType.Working).isStartState() //
         .andToDefaultState(StateToken.Review) //
         .andToStates(StateToken.Cancelled, StateToken.Review, StateToken.Meeting) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED_FOR_TRANSITION), //
            new WidgetDefinition(AtsAttributeTypes.LegacyPcrId, "XTextDam"), //
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
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Cancelled, StateToken.Completed, StateToken.Meeting, StateToken.Prepare) //
         .andOverrideValidationStates(StateToken.Meeting, StateToken.Prepare) //
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
         .andToDefaultState(StateToken.Completed) //
         .andToStates(StateToken.Cancelled, StateToken.Review, StateToken.Completed, StateToken.Prepare) //
         .andOverrideValidationStates(StateToken.Review, StateToken.Prepare) //
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
         .andToStates(StateToken.Meeting, StateToken.Review) //
         .andOverrideValidationStates(StateToken.Meeting, StateToken.Review) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andToStates(StateToken.Prepare, StateToken.Review, StateToken.Meeting) //
         .andOverrideValidationStates(StateToken.Prepare, StateToken.Review, StateToken.Meeting) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
