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
package org.eclipse.osee.framework.skynet.core.utility;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 */
public class RemoteArtifactEventFactory {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RemoteArtifactEventFactory.class);

   public static NetworkArtifactModifiedEvent makeEvent(Artifact artifact, int transactionNumber) throws OseeCoreException, SQLException {
      NetworkArtifactModifiedEvent networkArtifactModifiedEvent = null;

      if (artifact == null || transactionNumber < 0) {
         throw new IllegalStateException("Artifact or transactionNumber can not be null.");
      }

      try {
         networkArtifactModifiedEvent =
               new NetworkArtifactModifiedEvent(artifact.getBranch().getBranchId(), transactionNumber,
                     artifact.getArtId(), artifact.getArtTypeId(), artifact.getFactory().getClass().getCanonicalName(),
                     artifact.getDirtySkynetAttributeChanges(),
                     SkynetAuthentication.getUser().isInDb() ? SkynetAuthentication.getUser().getArtId() : -1);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }

      return networkArtifactModifiedEvent;
   }
}
