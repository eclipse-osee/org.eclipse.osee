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
package org.eclipse.osee.framework.core.message;


/**
 * @author Megumi Telles
 */
public class BranchCommitResponse {
   private Integer transactionId;

   public BranchCommitResponse() {
      this.transactionId = null;
   }

   public Integer getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(Integer transactionId) {
      this.transactionId = transactionId;
   }
}
