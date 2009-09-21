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

package org.eclipse.osee.framework.skynet.core.types.branch;

import java.sql.Timestamp;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;

/**
 * @author Roberto E. Escobar
 */
public class OseeTx {

   private final TransactionDetailsType txType;
   private final long txNumber;
   private final Timestamp creationDate;
   private final String creationComment;
   private final int authorId;
   private final int commitArtId;

   public OseeTx(TransactionDetailsType txType, long txNumber, Timestamp creationDate, String creationComment, int authorId, int commitArtId) {
      super();
      this.txType = txType;
      this.txNumber = txNumber;
      this.creationDate = creationDate;
      this.creationComment = creationComment;
      this.authorId = authorId;
      this.commitArtId = commitArtId;
   }

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public long getTxNumber() {
      return txNumber;
   }

   public Timestamp getCreationDate() {
      return creationDate;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public int getAuthorId() {
      return authorId;
   }

   public int getCommitArtId() {
      return commitArtId;
   }
}
