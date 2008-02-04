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
 * @author Robert A. Fisher
 */
public class NetworkRelationLinkDeletedEvent extends SkynetRelationLinkEventBase {
   private static final long serialVersionUID = -1451567913757261791L;

   /**
    * @param branchId
    * @param transactionId
    * @param relId
    * @param artAId
    * @param artATypeId
    * @param artBId
    * @param artBTypeId
    * @param author TODO
    */
   public NetworkRelationLinkDeletedEvent(int gammaId, int branchId, int transactionId, int relId, int artAId, int artATypeId, int artBId, int artBTypeId, String aFactoryName, String bFactoryName, int author) {
      super(gammaId, branchId, transactionId, relId, artAId, artATypeId, artBId, artBTypeId, aFactoryName,
            bFactoryName, author);
   }
}
