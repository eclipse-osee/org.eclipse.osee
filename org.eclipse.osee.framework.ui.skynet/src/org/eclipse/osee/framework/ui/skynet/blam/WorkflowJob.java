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
package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Ryan D. Brooks
 */
public class WorkflowJob extends Job {

   /**
    * @param name
    */
   public WorkflowJob(String name) {
      super(name);
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {

      } catch (Exception ex) {
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getMessage(), ex);
      }
      return Status.OK_STATUS;
   }

}
