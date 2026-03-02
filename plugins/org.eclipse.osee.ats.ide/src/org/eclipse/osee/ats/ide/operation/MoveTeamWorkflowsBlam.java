/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class MoveTeamWorkflowsBlam extends AbstractAtsBlam {

   private final static String SOURCE_TEAM_WORKFLOWS = "Source Team Workflow(s) (drop here)";
   private final static String DEST_TEAM_WORKFLOW = "Destination Team Workflow (drop here)";

   public MoveTeamWorkflowsBlam() {
      // do nothing
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               TeamWorkFlowArtifact destTeamArt = (TeamWorkFlowArtifact) variableMap.getArtifact(DEST_TEAM_WORKFLOW);
               List<TeamWorkFlowArtifact> sourceTeamArts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
                  TeamWorkFlowArtifact.class, variableMap.getArtifacts(SOURCE_TEAM_WORKFLOWS));

               if (sourceTeamArts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Source Team Workflow(s).");
                  return;
               }
               if (destTeamArt == null) {
                  AWorkbench.popup("ERROR", "Must drag in Destination Team Workflow.");
                  return;
               }
               MoveTeamWorkflowsOperation operation =
                  new MoveTeamWorkflowsOperation(getName(), destTeamArt, sourceTeamArts);
               Operations.executeAsJob(operation, true);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget(SOURCE_TEAM_WORKFLOWS, WidgetId.XListDropViewerWidget);
      wb.andWidget(DEST_TEAM_WORKFLOW, WidgetId.XListDropViewerWidget);
      return wb.getXWidgetDatas();
   }

   @Override
   public String getDescriptionUsage() {
      return "Takes source Team Workflow(s) and moves them to destination Team Workflow's Action; Emtpy source Action artifacts will be deleted.";
   }

   @Override
   public String getName() {
      return "Move Team Workflows";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_UTIL);
   }

}