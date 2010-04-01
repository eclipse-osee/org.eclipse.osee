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
package org.eclipse.osee.framework.messaging.event.res.event;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class NetworkArtifactDeletedEvent extends SkynetArtifactEventBase {
   private static final long serialVersionUID = 568951803773151575L;

   public NetworkArtifactDeletedEvent(int branchId, String branchGuid, int transactionId, int artId, String artGuid, int artTypeId, String artTypeGuid, String factoryName, NetworkSender networkSender) {
      super(branchId, branchGuid, transactionId, artId, artGuid, artTypeId, artTypeGuid, factoryName, networkSender);
   }

   public NetworkArtifactDeletedEvent(SkynetArtifactEventBase base) {
      super(base);
   }
}
