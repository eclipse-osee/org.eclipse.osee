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

import java.util.List;
import java.util.Map;

/**
 * @author Branden W. Phillips
 */
public class BlockApplicabilityStageRequest {

   private Map<Long, String> views;
   private boolean commentNonApplicableBlocks;
   private String sourcePath;
   private String stagePath;
   private String customStageDir;
   private List<String> files;

   public BlockApplicabilityStageRequest() {
      // for jax-rs
   }

   public BlockApplicabilityStageRequest(Map<Long, String> views, boolean commentNonApplicableBlocks, String sourcePath, String stagePath, String stageDir) {
      this.views = views;
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
      this.sourcePath = sourcePath;
      this.stagePath = stagePath;
      this.customStageDir = stageDir;
   }

   public Map<Long, String> getViews() {
      return views;
   }

   public void setViews(Map<Long, String> views) {
      this.views = views;
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

   public String getCustomStageDir() {
      return customStageDir;
   }

   public void setCustomStageDir(String stageDir) {
      this.customStageDir = stageDir;
   }

   public List<String> getFiles() {
      return files;
   }

   public void setFiles(List<String> files) {
      this.files = files;
   }
}
