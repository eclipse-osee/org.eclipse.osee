/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.logging.Level;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * @author Dominic Guss
 */
public class ArchiveUnArchiveBranchJob extends Job {

   private final BranchId branchId;
   private final BranchEndpoint branchEndpoint;
   private final ArchiveType archiveType;
   private Response response;

   public enum ArchiveType {
      ARCHIVE,
      UNARCHIVE;
   }

   public ArchiveUnArchiveBranchJob(String name, BranchId branchId, ArchiveType archiveType) {
      super(name);
      this.branchId = branchId;
      this.archiveType = archiveType;
      branchEndpoint = ServiceUtil.getOseeClient().getBranchEndpoint();
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         if (archiveType == ArchiveType.ARCHIVE) {
            response = branchEndpoint.archiveBranch(branchId);
         } else {
            response = branchEndpoint.unarchiveBranch(branchId);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.getLocalizedMessage());
         return toReturn;
      }
      if (response.getStatus() == javax.ws.rs.core.Response.Status.OK.getStatusCode()) {
         toReturn = Status.OK_STATUS;
      }
      return toReturn;
   }

   public Response getResponse() {
      return response;
   }
}
