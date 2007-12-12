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
package org.eclipse.osee.ats.health;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ActionsHaveOneTeam extends XNavigateItemAction {

   /**
    * @param parent
    */
   public ActionsHaveOneTeam(XNavigateItem parent) {
      super(parent, "Report Actions Have One Team");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData(AtsPlugin.getLogger());
            List<ISearchPrimitive> artifactTypeCriteria = new LinkedList<ISearchPrimitive>();

            // Get Team and Action artifacts
            java.util.Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
            artTypeNames.add(ActionArtifact.ARTIFACT_NAME);

            for (String artType : artTypeNames)
               artifactTypeCriteria.add(new ArtifactTypeSearch(artType, Operator.EQUAL));

            Collection<Artifact> artifacts =
                  ArtifactPersistenceManager.getInstance().getArtifacts(artifactTypeCriteria, false,
                        BranchPersistenceManager.getInstance().getAtsBranch());

            int x = 0;
            for (Artifact art : artifacts) {
               monitor.subTask(String.format("Processing %d/%d...", x++, artifacts.size()));
               try {
                  if (art instanceof ActionArtifact) {
                     if (((ActionArtifact) art).getTeamWorkFlowArtifacts().size() == 0) {
                        rd.logError("Action " + art.getHumanReadableId() + " has no Team Workflows\n");
                     }
                  }
                  if (art instanceof TeamWorkFlowArtifact) {
                     if (((TeamWorkFlowArtifact) art).getParentActionArtifact() == null) {
                        rd.logError("Team " + art.getHumanReadableId() + " has no parent Action\n");
                     }
                  }
               } catch (IllegalStateException ex) {
                  rd.logError("Team " + art.getHumanReadableId() + " has no parent Action\n" + ex.getLocalizedMessage() + "\n");
               }
            }
            rd.report(getName());
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }
}
