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
package org.eclipse.osee.ote.ui.test.manager.batches.navigate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.svn.CheckoutProjectSetJob;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;

/**
 * @author Roberto E. Escobar
 */
final class ProjectSetupItem extends XNavigateItem implements Runnable {
   private final URI projectSetFile;
   private final String jobName;

   public ProjectSetupItem(XNavigateItem parent, String name, KeyedImage oseeImage, URI projectSetFile) {
      super(parent, name, oseeImage);
      this.jobName = String.format("Project Configuration: [%s]", name);
      this.projectSetFile = projectSetFile;
   }

   @Override
   public void run() {
      try {
         URL url = projectSetFile.toURL();
         Job job = new CheckoutProjectSetJob(jobName, getName(), url);
         job.setUser(true);
         job.schedule();
      } catch (MalformedURLException ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
   }
}
