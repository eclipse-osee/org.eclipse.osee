/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class TransactionResult {

   private TransactionToken tx = TransactionToken.SENTINEL;
   private XResultData results = new XResultData();
   private List<GammaId> failedGammas = new ArrayList<>();

   public TransactionResult() {
      // for jax-rs
   }

   public TransactionToken getTx() {
      return tx;
   }

   public void setTx(TransactionToken tx) {
      this.tx = tx;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public boolean isFailed() {
      return results.isErrors() || tx.isInvalid();
   }

   public boolean isSuccess() {
      return !isFailed();
   }

   public List<GammaId> getFailedGammas() {
      return this.failedGammas;
   }

   public void setFailedGammas(List<GammaId> failedGammas) {
      this.failedGammas = failedGammas;
   }

   @Override
   public String toString() {
      return String.format("Transaction Result transId [%s] results [%s]", tx.toString(), results.toString());
   }
}
