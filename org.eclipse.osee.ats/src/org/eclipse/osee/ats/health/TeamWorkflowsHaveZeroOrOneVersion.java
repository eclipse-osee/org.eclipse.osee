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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowsHaveZeroOrOneVersion extends XNavigateItemAction {

   boolean fixIt = false;

   /**
    * @param parent
    */
   public TeamWorkflowsHaveZeroOrOneVersion(XNavigateItem parent) {
      super(parent, "Report Team Workflows have > 1 version.");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new ReportCompletedWorkflowsJob(getName()), true);
   }

   private final class ReportCompletedWorkflowsJob extends Job {

      public ReportCompletedWorkflowsJob(String jobName) {
         super(jobName);
      }

      public IStatus run(IProgressMonitor monitor) {
         IStatus toReturn = Status.CANCEL_STATUS;
         try {
            SearchWorkFlowsTx searchWorkFlowsTx =
                  new SearchWorkFlowsTx(BranchPersistenceManager.getInstance().getAtsBranch(), getName(), monitor);
            searchWorkFlowsTx.execute();
            toReturn = Status.OK_STATUS;
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            toReturn = new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
         } finally {
            monitor.done();
         }
         return toReturn;
      }
   }

   private final class SearchWorkFlowsTx extends AbstractSkynetTxTemplate {
      private XResultData rd;
      private String jobName;
      private IProgressMonitor monitor;

      public SearchWorkFlowsTx(Branch branch, String jobName, IProgressMonitor monitor) {
         super(branch);
         this.rd = new XResultData(AtsPlugin.getLogger());
         this.jobName = jobName;
         this.monitor = monitor;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         monitor.subTask("Searching Team Workflows...");

         // Get Team artifacts
         List<ISearchPrimitive> artifactTypeCriteria = new LinkedList<ISearchPrimitive>();
         java.util.Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
         for (String artType : artTypeNames)
            artifactTypeCriteria.add(new ArtifactTypeSearch(artType, Operator.EQUAL));

         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(artifactTypeCriteria, false, getTxBranch());

         int x = 0;

         for (Artifact art : arts) {
            monitor.subTask(String.format("Processing %d/%d...", x++, arts.size()));
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            if (teamArt.getArtifacts(RelationSide.TeamWorkflowTargetedForVersion_Version).size() > 1) {
               rd.logError("Team workflow " + teamArt.getHumanReadableId() + " has " + teamArt.getArtifacts(
                     RelationSide.TeamWorkflowTargetedForVersion_Version).size() + " versions");
            }
         }
         rd.report(jobName);
      }
   }
}
