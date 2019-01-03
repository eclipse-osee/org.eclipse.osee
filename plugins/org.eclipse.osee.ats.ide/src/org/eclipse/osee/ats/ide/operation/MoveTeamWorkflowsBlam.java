/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class MoveTeamWorkflowsBlam extends AbstractBlam {

   private final static String SOURCE_TEAM_WORKFLOWS = "Source Team Workflow(s) (drop here)";
   private final static String DEST_TEAM_WORKFLOW = "Destination Team Workflow (drop here)";
   private final static String TITLE = "Destination Action Title (leave empty to keep same title)";

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
                  new MoveTeamWorkflowsOperation(getName(), destTeamArt, sourceTeamArts, variableMap.getString(TITLE));
               Operations.executeAsJob(operation, true);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + SOURCE_TEAM_WORKFLOWS + "\" />" +
      //
         "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + DEST_TEAM_WORKFLOW + "\" />" +
         //
         "<XWidget xwidgetType=\"XText\" displayName=\"" + TITLE + "\" horizontalLabel=\"true\" />" +
         //
         "</xWidgets>";
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
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

}