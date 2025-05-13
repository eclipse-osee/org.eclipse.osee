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
package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_HORZ;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.SAVE;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.team.ChangeTypes;
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
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamDemoProblemReport extends AbstractWorkDef {

   public WorkDefTeamDemoProblemReport() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_Problem_Report);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andWorkDefOption(WorkDefOption.NoTargetedVersion);

      bld.andHeader() //
         .andLayout( //

            new CompositeLayoutItem(4, //
               new EnumeratedArtifactWidgetDefinition(true, AtsAttributeTypes.CogPriority,
                  AtsArtifactToken.CogPriorityConfigArt), //
               new SpaceWidgetDefinition(), //
               new PriorityWidgetDefinition(true) //
            ),

            new ChangeTypeWidgetDefinition(true, ChangeTypes.Problem, ChangeTypes.Improvement, ChangeTypes.Support,
               ChangeTypes.Refinement).andRequired() //

         ) //
         .isShowMetricsHeader(false); //

      bld.andState(1, "Open", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyzed, StateToken.Closed, StateToken.Monitor, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //

            new GroupCompositeLayoutItem(1, "Problem",
               new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.HowFound, "XTextDam", FILL_VERT, SAVE) //
            ),

            new GroupCompositeLayoutItem(1, "Build Impact(s)",

               new CompositeLayoutItem(6, //
                  new WidgetDefinition("Ship", AtsAttributeTypes.Ship, "XTextDam", SAVE),
                  new WidgetDefinition("Test Number", AtsAttributeTypes.TestNumber, "XTextDam", SAVE),
                  new WidgetDefinition("Flight Number", AtsAttributeTypes.FlightNumber, "XTextDam", SAVE)), //

               new WidgetDefinition("Test Date", AtsAttributeTypes.TestDate, "XHyperlinkLabelDateDam"),

               new CompositeLayoutItem(4,
                  new WidgetDefinition("Found-In Version", AtsRelationTypes.TeamWorkflowToFoundInVersion_Version,
                     "XFoundInVersionWithPersistWidget").andWidgetHint(WidgetHint.SortAscending),
                  new WidgetDefinition("Introduced-In Version",
                     AtsRelationTypes.TeamWorkflowToIntroducedInVersion_Version,
                     "XIntroducedInVersionWithPersistWidget").andWidgetHint(WidgetHint.SortAscending)), //

               new WidgetDefinition("Open Build Impacts", "XHyperlinkOpenBitTab") //

            ), //

            new GroupCompositeLayoutItem(1, "Analysis",
               new WidgetDefinition("Feature(s) Impacted", AtsAttributeTypes.FeatureImpactReference,
                  "XHyperlinkFeatureDam", FILL_HORZ, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.SystemAnalysis, "XTextDam", FILL_VERT, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.SoftwareAnalysis, "XTextDam", FILL_VERT, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERT, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.ProposedResolutionDate, "XHyperlinkLabelValueSelectionDam") //
            ),

            new SignByAndDateWidgetDefinition("Manager Signoff", AtsAttributeTypes.ManagerSignedOffBy,
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

}
