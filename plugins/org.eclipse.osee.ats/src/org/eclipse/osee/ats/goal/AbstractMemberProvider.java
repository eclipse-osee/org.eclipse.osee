/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.goal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.editor.IMemberProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractMemberProvider implements IMemberProvider, IOseeTreeReportProvider {

   @Override
   public void deCacheAndReload(boolean forcePend, IJobChangeListener listener) {

      if (forcePend) {
         deCacheAndReload();
      } else {
         Job job = new Job("Refreshing " + getMembersName()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               deCacheAndReload();
               return Status.OK_STATUS;
            }

         };
         if (listener != null) {
            job.addJobChangeListener(listener);
         }
         job.setSystem(false);
         job.schedule();
      }
   }

   private void deCacheAndReload() {
      ArtifactCache.deCache(getArtifact());
      for (Artifact art : getMembers()) {
         ArtifactCache.deCache(art);
      }
      deCacheArtifact();
      getArtifact().reloadAttributesAndRelations();
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Table Report - %s - %s", getMembersName(), getCollectorName());
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - " + getMembersName();
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}
