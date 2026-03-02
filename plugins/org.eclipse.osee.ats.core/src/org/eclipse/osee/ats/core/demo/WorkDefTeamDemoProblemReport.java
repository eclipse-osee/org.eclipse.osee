/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.core.demo;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XHyperlinkApplicabilityArtWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XHyperlinkFeatureArtWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XHyperlinkOpenBitTabWidget;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.LABEL_AFTER;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.SAVE;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ChangeTypeWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.EnumeratedArtifactWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.GroupCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.PriorityWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.SignByAndDateWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.SpaceWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.data.conditions.EnableIfAttrValueCondition;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("unused")
public class WorkDefTeamDemoProblemReport extends AbstractWorkDef {

   public WorkDefTeamDemoProblemReport() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_Problem_Report);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andWorkDefOption(WorkDefOption.NoTargetedVersion, WorkDefOption.IsProblemReport);

      bld.andHeader() //
         .andLayout( //

            new CompositeLayoutItem(4, //
               new EnumeratedArtifactWidgetDefinition(true, AtsAttributeTypes.CogPriority,
                  AtsArtifactToken.CogPriorityConfigArt), //
               new SpaceWidgetDefinition(), //
               new PriorityWidgetDefinition() //
            ),

            new ChangeTypeWidgetDefinition(ChangeTypes.Problem, ChangeTypes.Improvement, ChangeTypes.Support,
               ChangeTypes.Refinement).andRequired() //

         ) //
         .isShowMetricsHeader(false); //

      bld.andState(1, "Open", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyzed, StateToken.Closed, StateToken.Monitor, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //

            new GroupCompositeLayoutItem(1, "Problem", new WidgetDefinition(Description, XXTextWidget, FILL_VERT, SAVE), //
               new WidgetDefinition(HowFound, XXTextWidget, FILL_VERT, SAVE) //
            ),

            new GroupCompositeLayoutItem(1, "Build Impact(s)",

               new CompositeLayoutItem(6, //
                  new WidgetDefinition(Ship, XXTextWidget, SAVE), new WidgetDefinition(TestNumber, XXTextWidget, SAVE),
                  new WidgetDefinition(FlightNumber, XXTextWidget, SAVE)), //

               new CompositeLayoutItem(4, //
                  new WidgetDefinition(TestDate, XHyperlinkLabelDateArtWidget),
                  new WidgetDefinition(CrashOrBlankDisplay, XHyperlinkTriStateBooleanArtWidget)),

               new CompositeLayoutItem(4,
                  new WidgetDefinition(WidgetIdAts.XXFoundInVersionWidget).andWidgetHint(WidgetHint.SortAscending),
                  new WidgetDefinition(WidgetIdAts.XXIntroducedInVersionWidget).andWidgetHint(
                     WidgetHint.SortAscending)), //

               new WidgetDefinition("Open Build Impacts", XHyperlinkOpenBitTabWidget) //

            ), //

            new GroupCompositeLayoutItem(1, "Analysis",
               new CompositeLayoutItem(4, new WidgetDefinition("Applicability", XHyperlinkApplicabilityArtWidget, SAVE), //
                  new WidgetDefinition("Feature(s) Impacted", FeatureImpactReference, XHyperlinkFeatureArtWidget, SAVE) //
               ), new WidgetDefinition(SystemAnalysis, XXTextWidget, FILL_VERT, SAVE), //
               new WidgetDefinition(SoftwareAnalysis, XXTextWidget, FILL_VERT, SAVE), //
               new WidgetDefinition(ProposedResolution, XXTextWidget, FILL_VERT, SAVE), //
               new WidgetDefinition(ProposedResolutionDate, XHyperlinkWfdForEnumAttrArtWidget) //
            ),

            new WidgetDefinition(CustomerDescriptionLock, XCheckBoxArtWidget, LABEL_AFTER, SAVE), //
            new WidgetDefinition(CustomerDescription, XXTextWidget, FILL_VERT, SAVE).andCondition(
               new EnableCustomerDescriptionIfNotLock()), //

            new SignByAndDateWidgetDefinition("Manager Signoff", ManagerSignedOffBy,
               AtsAttributeTypes.ManagerSignedOffByDate).andRequired()

         ); //

      bld.andState(2, "Analyzed", StateType.Working) //
         .andToStates(StateToken.Closed, StateToken.Monitor, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayoutFromState(StateToken.Open);

      bld.andState(3, "Monitor", StateType.Working) //
         .andToStates(StateToken.Closed, StateToken.Analyzed, StateToken.Open, StateToken.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(4, "Closed", StateType.Completed) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }

   public class EnableCustomerDescriptionIfNotLock extends EnableIfAttrValueCondition {

      public EnableCustomerDescriptionIfNotLock() {
         super(AtsAttributeTypes.CustomerDescriptionLock, "false", "");
      }

   };
}
