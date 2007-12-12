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
package org.eclipse.osee.ats.report;

import java.sql.SQLException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class ExtendedStatusReportItem extends XNavigateItemAction {

   /**
    * @param parent
    * @param name
    */
   public ExtendedStatusReportItem(XNavigateItem parent, String name) {
      super(parent, name);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      WorldView worldView = WorldView.getWorldView();
      if (worldView == null) {
         AWorkbench.popup("ERROR",
               "Can't access ATS World\n\nNOTE: This report is driven off data populated in ATS World.");
         return;
      }
      ExtendedStatusReportJob job =
            new ExtendedStatusReportJob("ATS Extended Status Report - " + worldView.getxViewer().getTitle(),
                  worldView.getxViewer().getLoadedArtifacts());
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

}
