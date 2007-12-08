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

package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.sql.SQLException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxJobTemplate;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Used to perform a specific java action
 * 
 * @author Donald G. Dunne
 */
public class XNavigateItemSkynetTxJobItem extends XNavigateItem {

   private AbstractSkynetTxJobTemplate txJob;
   private boolean promptFirst = false;

   public XNavigateItemSkynetTxJobItem(XNavigateItem parent, AbstractSkynetTxJobTemplate txJob) {
      this(parent, txJob, null, true);
   }

   public XNavigateItemSkynetTxJobItem(XNavigateItem parent, AbstractSkynetTxJobTemplate txJob, Image image, boolean promptFirst) {
      super(parent, txJob != null ? txJob.getName() : "", image);
      this.txJob = txJob;
      this.promptFirst = promptFirst;
   }

   public void run() throws SQLException {
      if (txJob != null) {
         if (promptFirst) {
            Displays.ensureInDisplayThread(new Runnable() {
               /*
                * (non-Javadoc)
                * 
                * @see java.lang.Runnable#run()
                */
               public void run() {
                  if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
                     txJob.run(true, Job.LONG);
                  }
               }
            }, true);
         } else {
            txJob.setUser(true);
            txJob.setPriority(Job.LONG);
            txJob.schedule();
         }
      }
   }

   public boolean isPromptFirst() {
      return promptFirst;
   }

   public void setPromptFirst(boolean promptFirst) {
      this.promptFirst = promptFirst;
   }

   /**
    * @param txJob the txJob to set
    */
   public void setTxJob(AbstractSkynetTxJobTemplate txJob) {
      this.txJob = txJob;
   }

}
