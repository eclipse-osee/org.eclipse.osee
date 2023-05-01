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

package org.eclipse.osee.framework.core.publishing;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.LinkType;
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

   private LinkType linkType;

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

   /**
    * This member is required and maybe {@link TransactionToken#SENTINEL}.
    */

   private TransactionToken txId;

   /**
    * This member is required an maybe {@link ArtifactId#SENTINEL}.
    */

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

   public LinkType getLinkType() {
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
    * Predicate to determine if the member {@link #linkType} is non-<code>null</code>.
    *
    * @return <code>true</code>, when the member {@link #linkType} is non-<code>null</code>; otherwise;
    * <code>false</code>.
    */

   public boolean isLinkTypeValid() {
      return Objects.nonNull(this.linkType);
   }

   /**
    * Predicate to determine if the member {@link #txId} is a valid {@link TransactionId}.
    *
    * @return <code>true</code>, when the {@link #txId} is valid; otherwise, <code>false</code>.
    */

   public boolean isTxIdValid() {
      //@formatter:off
      return
            Objects.nonNull( this.txId )
         && this.txId.isValid();
      //@formatter:on
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

   /**
    * Predicate to determine if the member {@link #viewId} is a valid {@link ArtifactId}.
    *
    * @return <code>true</code>, when the {@link #viewId} is valid; otherwise, <code>false</code>.
    */

   public boolean isViewIdValid() {
      //@formatter:off
      return
            Objects.nonNull( this.viewId )
         && this.viewId.isValid();
      //@formatter:on
   }

   /**
    * Sets the {@link ArtifactId}. Used for deserialization.
    *
    * @param artId the artifact identifier. Maybe {@link ArtifactId#SENTINEL} but not <code>null</code>.
    * @throws NullPointerException when the parameter <code>artId</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #artId} has already been set.
    */

   public void setArtId(ArtifactId artId) {
      if (Objects.nonNull(this.artId)) {
         throw new IllegalStateException("WordTemplateContentData::setArtId, member \"artId\" has already been set.");
      }
      this.artId =
         Objects.requireNonNull(artId, "WordTemplateContentData::setArtId, parameter \"artId\" cannot be null.");
   }

   public void setArtIsChanged(boolean artIsChanged) {
      this.artIsChanged = artIsChanged;
   }

   /**
    * Sets the {@link BranchId}. Used for deserialization.
    *
    * @param branch the branch identifier. Maybe {@link BranchId#SENTINEL} but not <code>null</code>.
    * @throws NullPointerException when the parameter <code>branchId</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #branch} has already been set.
    */

   public void setBranch(BranchId branch) {
      if (Objects.nonNull(this.branch)) {
         throw new IllegalStateException("WordTemplateContentData::setBranch, member \"branch\" has already been set.");
      }
      this.branch =
         Objects.requireNonNull(branch, "WordTemplateContentData::setBranch, parameter \"branch\" cannot be null.");
   }

   /**
    * Sets the footer content string. Used for deserialization.
    *
    * @param footer the footer content. Maybe an empty {@link String} but not <code>null</code>.
    * @throws NullPointerException when the parameter <code>footer</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #footer} has already been set.
    */

   public void setFooter(String footer) {
      if (Objects.nonNull(this.footer)) {
         throw new IllegalStateException("WordTemplateContentData::setFooter, member \"footer\" has already been set.");
      }
      this.footer =
         Objects.requireNonNull(footer, "WordTemplateContentData::setFooter, parameter \"footer\" cannot be null.");
   }

   public void setIsEdit(boolean isEdit) {
      this.isEdit = isEdit;
   }

   public void setLinkType(LinkType linkType) {
      this.linkType = linkType;
   }

   public void setOseeLink(String oseeLink) {
      this.oseeLink = oseeLink;
   }

   /**
    * Sets the permanent link URL. Used for deserialization.
    *
    * @param permanentLinkUrl the permanent link URL. Maybe an empty {@link String} but not <code>null</code>.
    * @throws NullPointerException when the parameter <code>permanentLinkUrl</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #permanentLinkUrl} has already been set.
    */

   public void setPermanentLinkUrl(String permanentLinkUrl) {
      if (Objects.nonNull(this.permanentLinkUrl)) {
         throw new IllegalStateException(
            "WordTemplateContentData::setPermanentLinkUrl, member \"permanentLinkUrl\" has already been set.");
      }
      this.permanentLinkUrl = Objects.requireNonNull(permanentLinkUrl,
         "WordTemplateContentData::setPermanentLinkUrl, parameter \"permanentLinkUrl\" cannot be null.");
   }

   /**
    * Sets the presentation type. Used for deserialization.
    *
    * @param presentationType the {@link PresentationType}. This parameter cannot be <code>null</code>.
    * @throws NullPointerException when the parameter <code>presentationType</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #presentationType} has already been set.
    */

   public void setPresentationType(PresentationType presentationType) {
      if (Objects.nonNull(this.presentationType)) {
         throw new IllegalStateException(
            "WordTemplateContentData::setPresentationType, member \"presentationType\" has already been set.");
      }
      this.presentationType = Objects.requireNonNull(presentationType,
         "WordTemplateContentData::setPresentationType, parameter \"presentationType\" cannot be null.");
   }

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   /**
    * Sets the transaction identifier. Used for deserialization.
    *
    * @param txId the transaction identifier. Maybe {@link TransactionToken#SENTINEL} but not <code>null</code>.
    * @throws NullPointerException when the parameter <code>txId</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #txId} has already been set.
    */

   public void setTxId(TransactionToken txId) {
      if (Objects.nonNull(this.txId)) {
         throw new IllegalStateException("WordTemplateContentData::setTxId, member \"txId\" has already been set.");
      }
      this.txId = Objects.requireNonNull(txId, "WordTemplateContentData::setTxId, parameter \"txId\" cannot be null.");
   }

   /**
    * Sets the view identifier. Used for deserialization.
    *
    * @param viewId the {@link ArtifactId} of the view. Maybe {@link ArtifactId#SENTINEL} but not <code>null</code>.
    * @throws NullPointerException when the parameter <code>viewId</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #viewId} has already been set.
    */

   public void setViewId(ArtifactId viewId) {
      if (Objects.nonNull(this.viewId)) {
         throw new IllegalStateException("WordTemplateContentData::setViewId, member \"viewId\" has already been set.");
      }
      this.viewId =
         Objects.requireNonNull(viewId, "WordTemplateContentData::setViewId, parameter \"viewId\" cannot be null.");
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
