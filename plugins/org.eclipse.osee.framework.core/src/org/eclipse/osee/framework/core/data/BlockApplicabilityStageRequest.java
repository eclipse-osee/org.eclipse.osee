/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.core.data;

/**
 * @author Branden W. Phillips
 */
public class BlockApplicabilityStageRequest {

   private ArtifactId view;
   private boolean commentNonApplicableBlocks;
   private String sourcePath;
   private String stagePath;

   public BlockApplicabilityStageRequest() {
      // for jax-rs
   }

   public BlockApplicabilityStageRequest(ArtifactId view, boolean commentNonApplicableBlocks, String sourcePath, String stagePath) {
      this.view = view;
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
      this.sourcePath = sourcePath;
      this.stagePath = stagePath;
   }

   public ArtifactId getView() {
      return view;
   }

   public void setView(ArtifactId view) {
      this.view = view;
   }

   public boolean isCommentNonApplicableBlocks() {
      return commentNonApplicableBlocks;
   }

   public void setCommentNonApplicableBlocks(boolean commentNonApplicableBlocks) {
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
   }

   public String getSourcePath() {
      return sourcePath;
   }

   public void setSourcePath(String sourcePath) {
      this.sourcePath = sourcePath;
   }

   public String getStagePath() {
      return stagePath;
   }

   public void setStagePath(String stagePath) {
      this.stagePath = stagePath;
   }
}