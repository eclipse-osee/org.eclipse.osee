/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * @author Morgan E. Cook
 */
public class WordTemplateContentData implements ToMessage {

   private ArtifactId artId;
   private boolean artIsChanged;
   private BranchId branch;
   private String footer;
   private boolean isEdit;

   /**
    * This member is optional and maybe <code>null</code>.
    */

   private String linkType;

   /**
    * This member is optional and maybe <code>null</code>.
    */

   private String oseeLink;
   private String permanentLinkUrl;
   private PresentationType presentationType;

   /**
    * This member is optional and maybe <code>null</code>.
    */

   private String sessionId;

   private TransactionToken txId;
   private ArtifactId viewId;

   public ArtifactId getArtId() {
      return artId;
   }

   public boolean getArtIsChanged() {
      return artIsChanged;
   }

   public BranchId getBranch() {
      return branch;
   }

   public String getFooter() {
      return footer;
   }

   public boolean getIsEdit() {
      return isEdit;
   }

   public String getLinkType() {
      return linkType;
   }

   public String getOseeLink() {
      return oseeLink;
   }

   public String getPermanentLinkUrl() {
      return permanentLinkUrl;
   }

   public PresentationType getPresentationType() {
      return presentationType;
   }

   public String getSessionId() {
      return sessionId;
   }

   public TransactionId getTxId() {
      return txId;
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   /**
    * Validates that required class members have been set.
    *
    * @return <code>true</code>, when the class members have been populated; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.artId            )
         && Objects.nonNull( this.branch           )
         && Objects.nonNull( this.footer           )
         && Objects.nonNull( this.permanentLinkUrl )
         && Objects.nonNull( this.presentationType )
         && Objects.nonNull( this.txId             )
         && Objects.nonNull( this.viewId           )
         ;
      //@formatter:on
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public void setArtIsChanged(boolean artIsChanged) {
      this.artIsChanged = artIsChanged;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public void setFooter(String footer) {
      this.footer = footer;
   }

   public void setIsEdit(boolean isEdit) {
      this.isEdit = isEdit;
   }

   public void setLinkType(String linkType) {
      this.linkType = linkType;
   }

   public void setOseeLink(String oseeLink) {
      this.oseeLink = oseeLink;
   }

   public void setPermanentLinkUrl(String permanentLinkUrl) {
      this.permanentLinkUrl = permanentLinkUrl;
   }

   public void setPresentationType(PresentationType presentationType) {
      this.presentationType = presentationType;
   }

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   public void setTxId(TransactionToken txId) {
      this.txId = txId;
   }

   public void setViewId(ArtifactId viewId) {
      this.viewId = viewId;
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
         .title( "WordTemplateContentData" )
         .indentInc()
         .segment( "artId",            this.artId            )
         .segment( "artIsChanged",     this.artIsChanged     )
         .segment( "branch",           this.branch           )
         .segment( "footer",           this.footer           )
         .segment( "isEdit",           this.isEdit           )
         .segment( "linkType",         this.linkType         )
         .segment( "oseeLink",         this.oseeLink         )
         .segment( "permanentLinkUrl", this.permanentLinkUrl )
         .segment( "presentationType", this.presentationType )
         .segment( "sessionId",        this.sessionId        )
         .segment( "txId",             this.txId             )
         .segment( "viewId",           this.viewId           )
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
