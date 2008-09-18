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
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Donald G. Dunne
 * @author Robert A. Fisher
 */
public class NetworkRelationLinkDeletedEvent extends SkynetRelationLinkEventBase {
   private static final long serialVersionUID = -1451567913757261791L;

   public NetworkRelationLinkDeletedEvent(int relTypeId, int gammaId, int branchId, int relId, int artAId, int artATypeId, int artBId, int artBTypeId, NetworkSender networkSender) {
      super(relTypeId, gammaId, branchId, relId, artAId, artATypeId, artBId, artBTypeId, networkSender);
   }
}
