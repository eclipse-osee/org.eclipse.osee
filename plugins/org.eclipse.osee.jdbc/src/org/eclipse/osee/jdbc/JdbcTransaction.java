/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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