/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.workflow.pr;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.internal.Activator;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.bit.WfeBitTab;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Contributed through OSGI-INF
 *
 * @author Donald G. Dunne
 */
public class DemoProblemReportWorkItemHook implements IAtsWorkItemHookIde {

   private Boolean handleDebug = null;

   public String getName() {
      return DemoProblemReportWorkItemHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Add hooks and listeners to Demo Problem Report";
   }

   @Override
   public WfeBitTab createBitTab(WorkflowEditor editor, IAtsTeamWorkflow teamWf) {
      try {
         if (teamWf.getArtifactType().equals(AtsArtifactTypes.DemoProblemReportTeamWorkflow)) {
            return new WfeBitTabDemo(editor, teamWf);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art,
      boolean isEditable) {
      if (art.isOfType(
         AtsArtifactTypes.DemoProblemReportTeamWorkflow) && ((IAtsTeamWorkflow) art).getWorkDefinition().getName().equals(
            DemoWorkDefinitions.WorkDef_Team_Demo_Problem_Report.getName())) {
         if (xWidget.getLabel().equals(AtsAttributeTypes.Description.getUnqualifiedName())) {
            XTextDam descptionWidget = (XTextDam) xWidget;
            if (isHandleDebug()) {
               descptionWidget.getLabelWidget().addMouseListener(new MouseAdapter() {

                  @Override
                  public void mouseUp(MouseEvent e) {
                     if (e.button == 3) {
                        setDebugData(art);
                     }
                  }

               });
            }
         }
      }
   }

   private void setDebugData(Artifact art) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set PR Values");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.SystemAnalysis, "Do that");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.Workaround, "This is the workaround");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.ProposedResolution, "Do this to fix");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.HowFound, "IntegrationProcessorAndDisplays Testing");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.ImpactToMissionOrCrew, "No Impact");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.Priority, "3");
      changes.setSoleAttributeValue(art, AtsAttributeTypes.ManagerSignedOffBy, DemoUsers.Joe_Smith.getId());
      changes.setSoleAttributeValue(art, AtsAttributeTypes.ManagerSignedOffByDate, new Date());
      changes.executeIfNeeded();
   }

   private boolean isHandleDebug() {
      if (handleDebug == null) {
         handleDebug = AtsApiService.get().getUserService().isAtsAdmin();
         if (!handleDebug) {
            handleDebug = !AtsApiService.get().getStoreService().isProductionDb();
         }
      }
      return handleDebug;
   }

   @Override
   public boolean createSiblingWorkflowEnabled(IAtsWorkItem workItem) {
      return !workItem.isOfType(AtsArtifactTypes.DemoProblemReportTeamWorkflow);
   }

}
