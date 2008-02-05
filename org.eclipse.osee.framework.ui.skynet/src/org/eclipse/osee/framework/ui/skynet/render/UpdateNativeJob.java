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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.VisitorEvent;

/**
 * @author Ryan D. Brooks
 */
public class UpdateNativeJob extends UpdateJob {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UpdateNativeJob.class);
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final ArtifactPersistenceManager persistenceManager = ArtifactPersistenceManager.getInstance();

   public UpdateNativeJob() {
      super("Update Native Artifact");
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         // expected file name format: guid_branchId_origionalFileName
         String fileName = workingFile.getName();
         String guid = fileName.substring(0, 22);
         Branch branch =
               BranchPersistenceManager.getInstance().getBranch(
                     Integer.parseInt(fileName.substring(23, fileName.indexOf('_', 23))));
         Artifact artifact = persistenceManager.getArtifact(guid, branch);
         if (artifact == null) {
            return new Status(Status.ERROR, Platform.PI_RUNTIME, -1,
                  "Can't retrieve native content for " + fileName + " for " + branch.getBranchName() + " branch.", null);
         } else {
            artifact.setAttribute(NativeArtifact.CONTENT_NAME, new FileInputStream(workingFile));
            artifact.persistAttributes();
            eventManager.kick(new VisitorEvent(artifact, this));
         }
         return Status.OK_STATUS;
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         return new Status(Status.ERROR, Platform.PI_RUNTIME, -1, ex.getMessage(), ex);
      }
   }
}