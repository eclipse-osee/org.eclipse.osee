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
import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.EnumArtWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.GroupCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.SignDateWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.SpaceWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.xx.ChangeTypeWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.xx.PriorityWidgetDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.data.conditions.EnableIfAttrValueCondition;
import org.eclipse.osee.framework.core.widget.XOption;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */

@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoProblemReport extends AbstractWorkDef {

   public WorkDefTeamDemoProblemReport() {
      super(DemoWorkDefinitions.WorkDefTeamDemoProblemReport);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andWorkDefOption(WorkDefOption.NoTargetedVersion, WorkDefOption.IsProblemReport);

      bld.andCustomHeader() //
         .andLayout( //

            new CompositeLayoutItem(6, //
               new EnumArtWidgetDef(CogPriority, AtsArtifactToken.CogPriorityConfigArt), //
               new SpaceWidgetDef(), //
               new PriorityWidgetDefinition(), //
               new SpaceWidgetDef(), //
               new AttrWidgetDef(CrewImpact) //
            ),

            new ChangeTypeWidgetDefinition(ChangeTypes.Problem, ChangeTypes.Improvement, ChangeTypes.Support,
               ChangeTypes.Refinement).andRequired() //

         ) //
         .showMetricsHeader(false); //

      bld.andState(1, "Open", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyzed, StateToken.Closed, StateToken.Cancelled) //
         .andToWaitStates(StateToken.Monitor) //

         .andLayout( //

            new GroupCompositeLayoutItem(1, "Problem", new AttrWidgetDef(Description), //
               new AttrWidgetDef(HowFound) //
            ),

            new GroupCompositeLayoutItem(1, "Build Impact(s)",

               new CompositeLayoutItem(6, //
                  new AttrWidgetDef(Ship), //
                  new AttrWidgetDef(TestNumber), //
                  new AttrWidgetDef(FlightNumber)//
               ), //

               new CompositeLayoutItem(4, //
                  new AttrWidgetDef(TestDate), //
                  new AttrWidgetDef(CrashOrBlankDisplay) //
               ),

               new CompositeLayoutItem(4, //
                  new WidgetDef(XXFoundInVersionWidget).set(XOption.SORT_ASC), //
                  new WidgetDef(XXIntroducedInVersionWidget).set(XOption.SORT_ASC)//
               ), //

               new WidgetDef("Open Build Impacts", XXHyperlinkOpenBitTabWidget) //

            ), //

            new GroupCompositeLayoutItem(1, "Analysis", new CompositeLayoutItem(4, //
               new WidgetDef(XXWorkflowApplicWidget), //
               new WidgetDef(XXPleFeatureWidget) //
            ), //

               new AttrWidgetDef(SystemAnalysis), //
               new AttrWidgetDef(SoftwareAnalysis), //
               new AttrWidgetDef(ProposedResolution), //
               new AttrWidgetDef(ProposedResolutionDate) //
            ),

            new AttrWidgetDef(CustomerDescriptionLock), //
            new AttrWidgetDef(CustomerDescription).andCondition(new EnableCustomerDescriptionIfNotLock()), //

            new SignDateWidgetDef("Manager Signoff", ManagerSignedOffBy, ManagerSignedOffByDate).set(XOption.REQUIRED)

         ); //

      bld.andState(2, "Analyzed", StateType.Working) //
         .andToStates(StateToken.Closed, StateToken.Cancelled) //
         .andToWaitStates(StateToken.Monitor) //
         .andColor(StateColor.BLACK) //
         .andLayoutFromState(StateToken.Open);

      bld.andState(3, "Monitor", StateType.Working) //
         .andToStates(StateToken.Closed, StateToken.Analyzed, StateToken.Open, StateToken.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      bld.andState(4, "Closed", StateType.Completed);

      bld.andState(5, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }

   public class EnableCustomerDescriptionIfNotLock extends EnableIfAttrValueCondition {

      public EnableCustomerDescriptionIfNotLock() {
         super(CustomerDescriptionLock, "false", "");
      }

   };

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
