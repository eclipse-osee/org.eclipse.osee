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
package org.eclipse.osee.ote.define.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ote.define.internal.Activator;
import org.eclipse.osee.ote.define.operations.RemoteResourceRequestOperation;

/**
 * @author Roberto E. Escobar
 */
public class RemoteResourceRequestJob extends Job {
   private static final String JOB_NAME = "Download Resource";
   private final String urlRequest;
   private final String targetFileName;
   private IFile downloaded;

   public RemoteResourceRequestJob(String urlRequest, String targetFileName) {
      super(JOB_NAME);
      this.urlRequest = urlRequest;
      this.targetFileName = targetFileName;
      setUser(false);
      setPriority(Job.LONG);
   }

   public IFile getDownloadedFile() {
      return downloaded;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus status = Status.CANCEL_STATUS;
      try {
         RemoteResourceRequestOperation remoteRequest =
            new RemoteResourceRequestOperation("TEMP", urlRequest, targetFileName);
         remoteRequest.execute(monitor);
         downloaded = remoteRequest.getResults();
         status = Status.OK_STATUS;
      } catch (Exception ex) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
            String.format("Error downloading resource [%s]", targetFileName), ex);
      }
      return status;
   }

}
