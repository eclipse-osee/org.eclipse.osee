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
package org.eclipse.osee.framework.ui.skynet.dbinit;

import java.sql.Connection;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.TagArtifactsJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class TagCommonBranchArtifacts extends DbInitializationTask {
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   public void run(Connection connection) throws Exception {
      TagArtifactsJob job =
            new TagArtifactsJob(ArtifactQuery.getArtifactsFromBranch(BranchPersistenceManager.getCommonBranch(), false));
      Jobs.startJob(job);
      try {
         job.join();
      } catch (InterruptedException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }
}