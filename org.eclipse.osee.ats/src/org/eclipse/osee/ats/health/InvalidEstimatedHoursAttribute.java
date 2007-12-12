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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
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
public class InvalidEstimatedHoursAttribute extends XNavigateItemAction {

   private boolean fix = true;

   /**
    * @param parent
    */
   public InvalidEstimatedHoursAttribute(XNavigateItem parent) {
      super(parent, "Report Invalid Estimated Hours Attribute");
   }

   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      private final String name;

      public Report(String name) {
         super(name);
         this.name = name;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         final XResultData rd = new XResultData(AtsPlugin.getLogger());
         try {
            List<ISearchPrimitive> artifactTypeCriteria = new LinkedList<ISearchPrimitive>();

            // Get Team and Task artifacts
            java.util.Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
            artTypeNames.add(TaskArtifact.ARTIFACT_NAME);
            for (String artType : artTypeNames)
               artifactTypeCriteria.add(new ArtifactTypeSearch(artType, Operator.EQUAL));

            Collection<Artifact> artifacts =
                  ArtifactPersistenceManager.getInstance().getArtifacts(artifactTypeCriteria, false,
                        BranchPersistenceManager.getInstance().getAtsBranch());

            int x = 0;
            for (Artifact art : artifacts) {
               monitor.subTask(String.format("Processing %d/%d...", x++, artifacts.size()));
               StateMachineArtifact sma = (StateMachineArtifact) art;
               String value = sma.getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
               if (value != null && !value.equals("")) {
                  try {
                     new Float(value).doubleValue();
                  } catch (NumberFormatException ex) {
                     rd.logError("HRID " + art.getHumanReadableId() + " has invalid float \"" + value + "\"");
                     if (fix) {
                        sma.setSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), "0");
                        sma.persist(true);
                        rd.logError("Fixed error by changing estimate to 0.");
                     }
                  }
               }
            }
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            rd.logError(ex.getLocalizedMessage());
         }
         rd.report(name);
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
