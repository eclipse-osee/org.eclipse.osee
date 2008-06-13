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

package org.eclipse.osee.framework.ui.skynet;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.tagging.TagManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class TagBranchesJob extends Job {
   private Collection<Branch> branches;

   public TagBranchesJob(Collection<Branch> branches) {
      super("Tag Brances");
      this.branches = branches;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      monitor.beginTask("Tag Brances", branches.size());
      for (Branch branch : branches) {
         try {
            Collection<Artifact> arts = ArtifactQuery.getArtifactsFromBranch(branch, false);
            monitor.subTask("Tagging " + arts.size() + " artifacts from " + branch.getBranchName());
            for (Artifact artifact : arts) {
               try {
                  TagManager.autoTag(true, artifact);
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, false);
               }

               if (monitor.isCanceled()) {
                  monitor.done();
                  return Status.CANCEL_STATUS;
               }
            }
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
         monitor.worked(1);
      }
      monitor.done();

      return Status.OK_STATUS;
   }

}
