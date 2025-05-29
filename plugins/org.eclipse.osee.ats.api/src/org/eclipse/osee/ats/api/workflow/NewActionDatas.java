/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.api.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class NewActionDatas {

   private List<NewActionData> newActionDatas = new ArrayList<>();
   private boolean emailPocs = true;
   private XResultData rd = new XResultData();
   private TransactionToken transaction = TransactionToken.SENTINEL;
   private String asUserArtId;
   private String opName;
   private IAtsChangeSet changes;
   private boolean persist;

   public NewActionDatas() {
      // for jax-rs
   }

   public NewActionDatas(String opName, AtsUser user) {
      this.opName = opName;
      this.asUserArtId = user.getIdString();
   }

   public List<NewActionData> getNewActionDatas() {
      return newActionDatas;
   }

   public void setNewActionDatas(List<NewActionData> newActionDatas) {
      this.newActionDatas = newActionDatas;
   }

   public boolean isEmailPocs() {
      return emailPocs;
   }

   public void setEmailPocs(boolean emailPocs) {
      this.emailPocs = emailPocs;
   }

   public XResultData getRd() {
      for (NewActionData data : newActionDatas) {
         rd.merge(data.getRd());
      }
      return rd;
   }

   @JsonIgnore
   public void add(NewActionData data) {
      newActionDatas.add(data);
   }

   @JsonIgnore
   public List<IAtsTeamWorkflow> getAtsTeamWfs() {
      List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
      for (NewActionData data : newActionDatas) {
         teamWfs.addAll(data.getActResult().getAtsTeamWfs());
      }
      return teamWfs;
   }

   @Override
   public String toString() {
      return "NewActionDatas [datas=" + newActionDatas + ", email=" + emailPocs + "]";
   }

   public TransactionToken getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionToken transaction) {
      this.transaction = transaction;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public String getAsUserArtId() {
      return asUserArtId;
   }

   /**
    * Set what user initiated the operation. By default, this is the createdByUser. This will be used as the commit
    * author.
    */
   public void setAsUserArtId(String asUserArtId) {
      this.asUserArtId = asUserArtId;
   }

   public String getOpName() {
      return opName;
   }

   public void setOpName(String opName) {
      this.opName = opName;
   }

   /**
    * Only to be used on server where action creation and other changes need to happen together. IAtsChangeSet will not
    * serialize and work from client to server
    */
   @JsonIgnore
   public IAtsChangeSet getChanges() {
      return changes;
   }

   public void setChanges(IAtsChangeSet changes) {
      this.changes = changes;
   }

   public boolean isPersist() {
      return persist;
   }

   /**
    * Execute change set before return
    */
   public void setPersist(boolean persist) {
      this.persist = persist;
   }

}
