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
package org.eclipse.osee.framework.skynet.core.dbinit;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class PostDbUserCleanUp implements IDbInitializationTask {
   private static final String UPDATE_BOOTSTRAP_USER_ID = "UPDATE osee_tx_details SET author = ? where author = 0";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.IDbInitializationTask#run(org.eclipse.osee.framework.db.connection.OseeConnection)
    */
   @Override
   public void run() throws OseeCoreException {
      OseeLog.log(Activator.class, Level.INFO, "Post Initialization User Clean-up");

      int authorArtId = 0;
      boolean isUserAuthenticationAllowed = false;
      try {
         // Check that this is a normal initialization
         Artifact root = ArtifactQuery.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
         if (root != null) {
            isUserAuthenticationAllowed = true;
         }
      } catch (ArtifactDoesNotExist ex) {
         // Do Nothing -- failure expected during base initialization
      }

      if (isUserAuthenticationAllowed) {
         // This is a regular initialization - users have been created.

         // Release bootstrap session session
         ClientSessionManager.releaseSession();

         // Acquire session
         User user = UserManager.getUser();
         authorArtId = user.getArtId();
      } else {
         // This is an initialization for base import -- users are not available
         OseeLog.log(Activator.class, Level.INFO,
               "Post Initialization User Clean-up - Base Initialization - unable to set tx author id");
      }

      if (authorArtId > 0) {
         // Set author to current authenticated user art id
         ConnectionHandler.runPreparedUpdate(UPDATE_BOOTSTRAP_USER_ID, authorArtId);
      }
   }
}
