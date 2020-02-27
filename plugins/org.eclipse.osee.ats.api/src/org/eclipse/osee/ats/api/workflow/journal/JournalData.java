/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.journal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class JournalData {

   private String currentMsg;
   private String addMsg;
   private AtsUser user;
   private XResultData results = new XResultData();
   private TransactionId transaction = TransactionId.SENTINEL;
   private List<AtsUser> subscribed = new ArrayList<>();

   public JournalData() {
      // for jax-rs
   }

   public AtsUser getUser() {
      return user;
   }

   public void setUser(AtsUser user) {
      this.user = user;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public TransactionId getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionId transaction) {
      this.transaction = transaction;
   }

   public String getCurrentMsg() {
      return currentMsg;
   }

   public void setCurrentMsg(String currentMsg) {
      this.currentMsg = currentMsg;
   }

   public String getAddMsg() {
      return addMsg;
   }

   public void setAddMsg(String addMsg) {
      this.addMsg = addMsg;
   }

   public List<AtsUser> getSubscribed() {
      return subscribed;
   }

   public void setSubscribed(List<AtsUser> subscribed) {
      this.subscribed = subscribed;
   }

}
