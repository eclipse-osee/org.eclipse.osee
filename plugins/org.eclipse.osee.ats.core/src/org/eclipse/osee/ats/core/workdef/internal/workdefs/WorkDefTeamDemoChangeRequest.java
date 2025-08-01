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
package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_HORZ;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.LABEL_AFTER;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.RFT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.SAVE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.VALIDATE_DATE;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.GroupCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;

/**
 * @author Donald G. Dunne
 */
public class WorkDefTeamDemoChangeRequest extends AbstractWorkDef {

   public WorkDefTeamDemoChangeRequest() {
      super(DemoWorkDefinitions.WorkDef_Team_Demo_Change_Request);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andWorkDefOption(WorkDefOption.IsChangeRequest);

      addCompositeHeader(bld);

      bld.andState(1, "Endorse", StateType.Working).isStartState() //
         .andToStates(StateToken.Analyze, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.ExternalReference, "XTextDam", SAVE), //

            new WidgetDefinition("Create/Open Great Escape Workflow", "XCreateEscapeDemoWfXButton"), //

            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, RFT, SAVE), //

            new WidgetDefinition(AtsAttributeTypes.NeedBy, "XHyperlinkLabelDateDam"), //

            new CompositeLayoutItem(4,
               new WidgetDefinition(AtsAttributeTypes.HowToReproduceProblem, "XTextDam", FILL_VERT),
               new WidgetDefinition(AtsAttributeTypes.Workaround, "XTextDam", FILL_VERT) //
            ), //

            new WidgetDefinition(AtsAttributeTypes.CrashOrBlankDisplay, "XHyperlinkTriStateBooleanDam",
               WidgetOption.HORZ_LABEL, RFT, LABEL_AFTER, SAVE),

            new WidgetDefinition(AtsAttributeTypes.ImpactToMissionOrCrew, "XTextDam", FILL_VERT, SAVE), //

            new GroupCompositeLayoutItem(1, "Build Impact(s)",
               new CompositeLayoutItem(4, new WidgetDefinition("Feature(s) Impacted",
                  AtsAttributeTypes.FeatureImpactReference, "XHyperlinkFeatureDam", FILL_HORZ) //
               ) //
            ), //

            new GroupCompositeLayoutItem(1, "Environment Configuration", new CompositeLayoutItem(4, //
               new WidgetDefinition("Found-In Version", "XFoundInVersionWithPersistWidget"), //
               new WidgetDefinition("Introduced-In Version", "XIntroducedInVersionWithPersistWidget") //
            )), //

            new GroupCompositeLayoutItem(1, "Estimates and Funding", //
               new WidgetDefinition(AtsAttributeTypes.ProblemFirstObserved, "XHyperlinkLabelValueSelectionDam",
                  VALIDATE_DATE) //
            ) //
         );

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Create/Open Great Escape Workflow", "XCreateEscapeDemoWfXButton"), //

            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.RiskAnalysis, "XHyperlinkLabelValueSelectionDam"), //
               new WidgetDefinition("Found-In Version", "XFoundInVersionWithPersistWidget"), //
               new WidgetDefinition("Introduced-In Version", "XIntroducedInVersionWithPersistWidget"), //
               new WidgetDefinition(AtsAttributeTypes.RevisitDate, "XHyperlinkLabelValueSelectionDam") //
            ), //

            new WidgetDefinition(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, RFT, SAVE), //
            new WidgetDefinition(AtsAttributeTypes.Workaround, "XTextDam", FILL_VERT, SAVE), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.RootCause, "XTextDam", FILL_VERT, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERT, SAVE) //
            ), //

            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget") //

         );

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.RevisitDate, "XHyperlinkLabelValueSelectionDam", VALIDATE_DATE), //
            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget"), //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget"));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Task Estimating Manager", "XTaskEstDemoWidget"), //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget"));

      bld.andState(5, "Completed", StateType.Completed) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Sibling Workflows", "XTaskEstSiblingWorldDemoWidget") //
         );

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
