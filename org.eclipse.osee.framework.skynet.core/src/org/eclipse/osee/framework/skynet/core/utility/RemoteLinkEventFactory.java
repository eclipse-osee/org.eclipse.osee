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
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class RemoteLinkEventFactory {

   public static NetworkNewRelationLinkEvent makeEvent(RelationLink link, int transactionNumber) throws SQLException {

      if (link == null || transactionNumber < 0) {
         throw new IllegalStateException("Link or transactionNumber can not be null.");
      }

      Artifact aArtifact = link.getArtifactA();
      Artifact bArtifact = link.getArtifactB();

      return new NetworkNewRelationLinkEvent(
            link.getPersistenceMemo().getGammaId(),
            link.getBranch().getBranchId(),
            transactionNumber,
            link.getPersistenceMemo().getLinkId(),
            aArtifact.getArtId(),
            aArtifact.getArtTypeId(),
            bArtifact.getArtId(),
            bArtifact.getArtTypeId(),
            link.getRationale(),
            link.getAOrder(),
            link.getBOrder(),
            link.getRelationType().getRelationTypeId(),
            aArtifact.getFactory().getClass().getCanonicalName(),
            bArtifact.getFactory().getClass().getCanonicalName(),
            aArtifact.getGuid(),
            bArtifact.getGuid(),
            aArtifact.getHumanReadableId(),
            bArtifact.getHumanReadableId(),
            link.getRelationType().getTypeName(),
            SkynetAuthentication.getInstance().getAuthenticatedUser().isInDb() ? SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId() : -1);

   }
}
