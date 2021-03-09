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

package org.eclipse.osee.framework.core.model;

import java.util.Date;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jeff C. Phillips
 */
public class TransactionRecord extends BaseId implements TransactionToken, Adaptable {
   public static TransactionRecord SENTINEL = new TransactionRecord(Id.SENTINEL, BranchToken.SENTINEL, null, null,
      UserId.SENTINEL, ArtifactId.SENTINEL, TransactionDetailsType.INVALID, 0L);
   private final TransactionDetailsType txType;
   private final BranchId branch;
   private String comment;
   private Date time;
   private UserId authorArtId;
   private final ArtifactId commitArtId;
   private Long buildId;

   public TransactionRecord(Long id, BranchId branch, String comment, Date time, UserId authorArtId, ArtifactId commitArtId, TransactionDetailsType txType, Long buildId) {
      super(id);
      this.branch = branch;
      this.buildId = buildId;
      this.comment = Strings.intern(comment);
      this.time = time;
      this.authorArtId = authorArtId;
      this.commitArtId = commitArtId;
      this.txType = txType;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public String getComment() {
      return comment;
   }

   public Date getTimeStamp() {
      return time;
   }

   public UserId getAuthor() {
      return authorArtId;
   }

   public ArtifactId getCommitArtifact() {
      return commitArtId;
   }

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setTimeStamp(Date time) {
      this.time = time;
   }

   public void setAuthor(UserId authorArtId) {
      this.authorArtId = authorArtId;
   }

   public Long getBuildId() {
      return buildId;
   }

   public void setBuildId(Long buildId) {
      this.buildId = buildId;
   }
}