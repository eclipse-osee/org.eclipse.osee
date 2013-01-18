/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class Transaction {

   private String branchUuid;
   private TransactionDetailsType txType;
   private String comment;
   private Date time;
   private int authorArtId;
   private int commitArtId;

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public void setTxType(TransactionDetailsType txType) {
      this.txType = txType;
   }

   public String getBranchId() {
      return branchUuid;
   }

   public void setBranchId(String branchUuid) {
      this.branchUuid = branchUuid;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public Date getTimeStamp() {
      return time;
   }

   public void setTimeStamp(Date time) {
      this.time = time;
   }

   public int getAuthor() {
      return authorArtId;
   }

   public void setAuthor(int authorArtId) {
      this.authorArtId = authorArtId;
   }

   public int getCommit() {
      return commitArtId;
   }

   public void setCommit(int commitArtId) {
      this.commitArtId = commitArtId;
   }

}
