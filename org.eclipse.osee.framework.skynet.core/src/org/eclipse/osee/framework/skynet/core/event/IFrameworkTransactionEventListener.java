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
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * Event that represents a collection of ArtifactModifiedEvent and RelationModifiedEvent events that are collected and
 * persisted within a single SkynetTransaction.
 * 
 * @author Donald G. Dunne
 */
public interface IFrameworkTransactionEventListener extends IEventListner {

   /**
    * Notification of all artifact and relation modifications collected as part of a single skynet transaction.
    * 
    * @param source
    * @param transData collection of all changes within transaction
    * @throws OseeCoreException TODO
    */
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException;

}
