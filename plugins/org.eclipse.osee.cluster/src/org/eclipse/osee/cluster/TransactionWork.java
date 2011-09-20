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
package org.eclipse.osee.cluster;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionWork<T> {

   /**
    * Gets the name of this transaction work section
    * 
    * @return transaction work name
    */
   String getName();

   /**
    * Work section for this transaction
    * 
    * @return T work result
    * @throws Exception during work section
    */
   T doWork() throws Exception;

   /**
    * Handles errors from doWork section
    */
   void handleException(Throwable throwable);

   /**
    * Transaction work finally section. Will always be executed even if an error is encountered.
    */
   void handleTxFinally();
}