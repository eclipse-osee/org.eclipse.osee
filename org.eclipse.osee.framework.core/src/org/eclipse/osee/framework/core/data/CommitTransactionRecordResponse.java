/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Megumi Telles
 */
public class CommitTransactionRecordResponse {
   private int transactionNumber;

   //TODO : Might be able to replace this class and use TrasnactionRecord
   public CommitTransactionRecordResponse(int transactionNumber) {
      this.transactionNumber = transactionNumber;
   }

   public CommitTransactionRecordResponse() {

   }

   public int getTransactionNumber() {
      return transactionNumber;
   }

}
