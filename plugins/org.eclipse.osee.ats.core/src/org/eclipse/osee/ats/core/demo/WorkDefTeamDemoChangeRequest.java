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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCreateEscapeDemoWfButtonWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XHyperlinkFeatureArtWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XTaskEstDemoWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XTaskEstSiblingWorldDemoWidget;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.*;
import static org.eclipse.osee.framework.core.widget.WidgetId.XHyperlinkLabelDateArtWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XHyperlinkTriStateBooleanArtWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XHyperlinkWfdForEnumAttrArtWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XXTextWidget;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
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
@SuppressWarnings("unused")
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
            new WidgetDefinition(AtsAttributeTypes.ExternalReference, XXTextWidget, SAVE), //

            new WidgetDefinition("Create/Open Great Escape Workflow", XCreateEscapeDemoWfButtonWidget), //

            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT, RFT, SAVE), //

            new WidgetDefinition(AtsAttributeTypes.NeedBy, XHyperlinkLabelDateArtWidget), //

            new CompositeLayoutItem(4,
               new WidgetDefinition(AtsAttributeTypes.HowToReproduceProblem, XXTextWidget, FILL_VERT),
               new WidgetDefinition(AtsAttributeTypes.Workaround, XXTextWidget, FILL_VERT) //
            ), //

            new WidgetDefinition(AtsAttributeTypes.CrashOrBlankDisplay, XHyperlinkTriStateBooleanArtWidget,
               WidgetOption.HORZ_LABEL, RFT, LABEL_AFTER, SAVE),

            new WidgetDefinition(AtsAttributeTypes.ImpactToMissionOrCrew, XXTextWidget, FILL_VERT, SAVE), //

            new GroupCompositeLayoutItem(1, "Build Impact(s)",
               new CompositeLayoutItem(4, new WidgetDefinition("Feature(s) Impacted",
                  AtsAttributeTypes.FeatureImpactReference, XHyperlinkFeatureArtWidget, FILL_HORZ) //
               ) //
            ), //

            new GroupCompositeLayoutItem(1, "Environment Configuration", new CompositeLayoutItem(4, //
               new WidgetDefinition(WidgetIdAts.XXFoundInVersionWidget), //
               new WidgetDefinition(WidgetIdAts.XXIntroducedInVersionWidget) //
            )), //

            new GroupCompositeLayoutItem(1, "Estimates and Funding", //
               new WidgetDefinition(AtsAttributeTypes.ProblemFirstObserved, XHyperlinkWfdForEnumAttrArtWidget,
                  VALIDATE_DATE) //
            ) //
         );

      bld.andState(2, "Analyze", StateType.Working) //
         .andToStates(StateToken.Authorize, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Create/Open Great Escape Workflow", XCreateEscapeDemoWfButtonWidget), //

            new CompositeLayoutItem(8, //
               new WidgetDefinition(AtsAttributeTypes.RiskAnalysis, XHyperlinkWfdForEnumAttrArtWidget), //
               new WidgetDefinition(WidgetIdAts.XXFoundInVersionWidget), //
               new WidgetDefinition(WidgetIdAts.XXIntroducedInVersionWidget), //
               new WidgetDefinition(AtsAttributeTypes.RevisitDate, XHyperlinkWfdForEnumAttrArtWidget) //
            ), //

            new WidgetDefinition(AtsAttributeTypes.Description, XXTextWidget, FILL_VERT, RFT, SAVE), //
            new WidgetDefinition(AtsAttributeTypes.Workaround, XXTextWidget, FILL_VERT, SAVE), //
            new CompositeLayoutItem(4, //
               new WidgetDefinition(AtsAttributeTypes.RootCause, XXTextWidget, FILL_VERT, SAVE), //
               new WidgetDefinition(AtsAttributeTypes.ProposedResolution, XXTextWidget, FILL_VERT, SAVE) //
            ), //

            new WidgetDefinition("Task Estimating Manager", XTaskEstDemoWidget) //

         );

      bld.andState(3, "Authorize", StateType.Working) //
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition(AtsAttributeTypes.RevisitDate, XHyperlinkWfdForEnumAttrArtWidget, VALIDATE_DATE), //
            new WidgetDefinition("Task Estimating Manager", XTaskEstDemoWidget), //
            new WidgetDefinition("Sibling Workflows", XTaskEstSiblingWorldDemoWidget));

      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //
            new WidgetDefinition("Task Estimating Manager", XTaskEstDemoWidget), //
            new WidgetDefinition("Sibling Workflows", XTaskEstSiblingWorldDemoWidget));

      bld.andState(5, "Completed", StateType.Completed) //
         .andColor(StateColor.DARK_GREEN) //
         .andLayout( //
            new WidgetDefinition("Sibling Workflows", XTaskEstSiblingWorldDemoWidget) //
         );

      bld.andState(6, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.DARK_GREEN);

      return bld.getWorkDefinition();
   }
}
