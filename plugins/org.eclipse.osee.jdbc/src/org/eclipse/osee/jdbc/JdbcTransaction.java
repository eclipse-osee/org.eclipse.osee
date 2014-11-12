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

package org.eclipse.osee.jdbc;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class JdbcTransaction {

   /**
    * Provides the transaction's work implementation.
    */
   public abstract void handleTxWork(JdbcConnection connection);

   /**
    * When an exception is detected during transaction processing, the exception is caught and passed to this method.
    * This convenience method is provided so child classes have access to the exception. <br/>
    * <b>Override to handle transaction exception</b>
    */
   public void handleTxException(Exception ex) {
      //
   }

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/>
    * <b>Override to add additional code to finally block</b>
    */
   public void handleTxFinally() {
      //
   }

}