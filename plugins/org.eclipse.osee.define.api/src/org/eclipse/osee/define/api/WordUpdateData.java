/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.define.api;

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * @author David W. Miller
 */
public class WordUpdateData implements ToMessage {
   private List<Long> artifacts;
   private BranchId branch;
   private String comment;
   private boolean multiEdit;
   private boolean threeWayMerge;
   private UserId userArtId;
   private byte[] wordData;

   public List<Long> getArtifacts() {
      return artifacts;
   }

   public BranchId getBranch() {
      return branch;
   }

   public String getComment() {
      return comment;
   }

   public UserId getUserArtId() {
      return userArtId;
   }

   public byte[] getWordData() {
      return wordData;
   }

   public boolean isMultiEdit() {
      return multiEdit;
   }

   public boolean isThreeWayMerge() {
      return threeWayMerge;
   }

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.artifacts ) && !this.artifacts.isEmpty()
         && Objects.nonNull( this.branch    )
         && Objects.nonNull( this.comment   )
         && Objects.nonNull( this.userArtId )
         && Objects.nonNull( this.wordData  )
         ;
      //@formatter:on
   }

   public void setArtifacts(List<Long> artifacts) {
      this.artifacts = artifacts;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setMultiEdit(boolean multiEdit) {
      this.multiEdit = multiEdit;
   }

   public void setThreeWayMerge(boolean threeWayMerge) {
      this.threeWayMerge = threeWayMerge;
   }

   public void setUserArtId(UserId userArtId) {
      this.userArtId = UserId.valueOf(userArtId.getId());
   }

   public void setWordData(byte[] wordData) {
      this.wordData = wordData;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "WordUpdateData" )
         .indentInc()
         .segment( "branch",           this.branch        )
         .segment( "comment",          this.comment       )
         .segment( "multiEdit",        this.multiEdit     )
         .segment( "threeWayMerge",    this.threeWayMerge )
         .segment( "userArtId",        this.userArtId     )
         .segment( "artifacts",        this.artifacts     )
         .segment( "wordData.length",  this.wordData,     ( a ) -> a.length )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}
