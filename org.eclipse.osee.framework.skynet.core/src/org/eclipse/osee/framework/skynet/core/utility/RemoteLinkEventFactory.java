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
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class RemoteLinkEventFactory {

   public static NetworkNewRelationLinkEvent makeEvent(RelationLink link, int transactionNumber) throws SQLException {

      if (link == null || transactionNumber < 0) {
         throw new IllegalStateException("Link or transactionNumber can not be null.");
      }

      return new NetworkNewRelationLinkEvent(
            link.getGammaId(),
            link.getBranch().getBranchId(),
            transactionNumber,
            link.getRelationId(),
            link.getAArtifactId(),
            link.getBArtifactId(),
            link.getRationale(),
            link.getAOrder(),
            link.getBOrder(),
            link.getRelationType().getRelationTypeId(),
            link.getRelationType().getTypeName(),
            SkynetAuthentication.getUser().isInDb() ? SkynetAuthentication.getUser().getArtId() : -1);

   }
}
