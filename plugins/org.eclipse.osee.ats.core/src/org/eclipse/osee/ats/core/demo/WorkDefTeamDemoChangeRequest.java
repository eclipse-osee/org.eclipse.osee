/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.demo;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.framework.core.widget.XOption.REQUIRED;
import static org.eclipse.osee.framework.core.widget.XOption.VALIDATE_DATE;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.GroupCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoChangeRequest extends AbstractWorkDef {

   public WorkDefTeamDemoChangeRequest() {
      super(DemoWorkDefinitions.WorkDefTeamDemoChangeRequest);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andWorkDefOption(WorkDefOption.IsChangeRequest);

      bld.addCompositeHeader();

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(ExternalReference), //

            new WidgetDef(XXCreateEscapeDemoWfButtonWidget), //
            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(NeedBy), //

            new CompositeLayoutItem(4, //
               new AttrWidgetDef(HowToReproduceProblem), //
               new AttrWidgetDef(Workaround) //
            ), //

            new AttrWidgetDef(CrashOrBlankDisplay).set(REQUIRED), new AttrWidgetDef(ImpactToMissionOrCrew), //

            new GroupCompositeLayoutItem(1, "Build Impact(s)", //
               new CompositeLayoutItem(4, //
                  new WidgetDef(XXPleFeatureWidget) //
               ) //
            ), //

            new GroupCompositeLayoutItem(1, "Environment Configuration", new CompositeLayoutItem(4, //
               new WidgetDef(XXFoundInVersionWidget), //
               new WidgetDef(XXIntroducedInVersionWidget) //
            )), //

            new GroupCompositeLayoutItem(1, "Estimates and Funding", //
               new AttrWidgetDef(ProblemFirstObserved).set(VALIDATE_DATE) //
            ) //
         );

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(XXCreateEscapeDemoWfButtonWidget), //

            new CompositeLayoutItem(8, //
               new AttrWidgetDef(RiskAnalysis), //
               new WidgetDef(XXFoundInVersionWidget), //
               new WidgetDef(XXIntroducedInVersionWidget), //
               new AttrWidgetDef(RevisitDate) //
            ), //

            new AttrWidgetDef(Description).set(REQUIRED), //
            new AttrWidgetDef(Workaround), //
            new CompositeLayoutItem(4, //
               new AttrWidgetDef(RootCause), //
               new AttrWidgetDef(ProposedResolution) //
            ), //

            new WidgetDef(XTaskEstDemoWidget) //

         );

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andLayout( //
            new AttrWidgetDef(RevisitDate).set(VALIDATE_DATE), //
            new WidgetDef(XTaskEstDemoWidget), //
            new WidgetDef(XTaskEstSiblingWorldDemoWidget));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDef(XTaskEstDemoWidget), //
            new WidgetDef(XTaskEstSiblingWorldDemoWidget));

      bld.andState(5, "Completed", StateType.Completed) //
         .andLayout( //
            new WidgetDef(XTaskEstSiblingWorldDemoWidget) //
         );

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
