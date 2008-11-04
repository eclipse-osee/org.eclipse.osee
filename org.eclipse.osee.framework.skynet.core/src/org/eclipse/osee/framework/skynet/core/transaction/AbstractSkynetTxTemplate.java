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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * This abstract class provides a uniform way of executing transactions. It handles exceptions ensuring that
 * transactions are processed in the correct order and roll-backs are performed whenever errors are detected.
 * 
 * @author Roberto E. Escobar
 */
public abstract class AbstractSkynetTxTemplate {
   private final Branch branch;

   /**
    * Transaction Constructor
    * 
    * @param branch The branch this transaction should operate on
    */
   public AbstractSkynetTxTemplate(Branch branch) {
      checkForNull(branch);
      this.branch = branch;
   }

   /**
    * Checks that argument is valid.
    * 
    * @param object
    * @throws IllegalArgumentExpception if argument is null
    */
   private void checkForNull(Object object) {
      if (object == null) {
         throw new IllegalArgumentException("Error argument was null.");
      }
   }

   /**
    * Gets the Branch the transaction is operating on
    * 
    * @return branch Transaction is operating on
    */
   protected Branch getTxBranch() {
      return branch;
   }

   /**
    * This template method calls {@link #handleTxWork} which is provided by child classes. This method handles
    * roll-backs and exception handling to prevent transactions from being left in an incorrect state.
    * 
    * @throws Exception
    */
   public void execute() throws OseeCoreException {

   }

   /**
    * Provides the transaction's work implementation.
    * 
    * @throws Exception
    */
   protected abstract void handleTxWork() throws OseeCoreException;

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/><b>Override to add additional code to finally block</b>
    * 
    * @throws Exception
    */
   protected void handleTxFinally() throws OseeCoreException {
      // override to add additional code to finally
   }
}
