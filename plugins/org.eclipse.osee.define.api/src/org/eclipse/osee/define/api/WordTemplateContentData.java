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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.PresentationType;

/**
 * @author Morgan E. Cook
 */
public class WordTemplateContentData {

   private ArtifactId artId;
   private BranchId branch;
   private String footer;
   private boolean isEdit;
   private String linkType;
   private TransactionToken txId;
   private String sessionId;
   private String oseeLink;
   private ArtifactId viewId;
   private PresentationType presentationType;
   private String permanentLinkUrl;
   private boolean artIsChanged;

   public ArtifactId getArtId() {
      return artId;
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public String getFooter() {
      return footer;
   }

   public void setFooter(String footer) {
      this.footer = footer;
   }

   public boolean getIsEdit() {
      return isEdit;
   }

   public void setIsEdit(boolean isEdit) {
      this.isEdit = isEdit;
   }

   public String getLinkType() {
      return linkType;
   }

   public void setLinkType(String linkType) {
      this.linkType = linkType;
   }

   public TransactionId getTxId() {
      return txId;
   }

   public void setTxId(TransactionToken txId) {
      this.txId = txId;
   }

   public String getSessionId() {
      return sessionId;
   }

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   public String getOseeLink() {
      return oseeLink;
   }

   public void setOseeLink(String oseeLink) {
      this.oseeLink = oseeLink;
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   public void setViewId(ArtifactId viewId) {
      this.viewId = viewId;
   }

   public PresentationType getPresentationType() {
      return presentationType;
   }

   public void setPresentationType(PresentationType presentationType) {
      this.presentationType = presentationType;
   }

   public String getPermanentLinkUrl() {
      return permanentLinkUrl;
   }

   public void setPermanentLinkUrl(String permanentLinkUrl) {
      this.permanentLinkUrl = permanentLinkUrl;
   }

   public boolean getArtIsChanged() {
      return artIsChanged;
   }

   public void setArtIsChanged(boolean artIsChanged) {
      this.artIsChanged = artIsChanged;
   }
}
