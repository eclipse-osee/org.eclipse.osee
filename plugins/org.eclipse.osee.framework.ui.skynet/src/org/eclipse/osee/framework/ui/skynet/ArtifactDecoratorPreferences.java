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

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDecoratorPreferences implements IArtifactDecoratorPreferences {

   private boolean isShowArtIdsAllowed;
   private boolean isShowArtTypeAllowed;
   private boolean isShowBranchAllowed;
   private boolean isShowArtVersionAllowed;
   private boolean isShowRelations;

   @Override
   public String getSelectedAttributeData(Artifact artifact) {
      return "";
   }

   public void setShowArtIds(boolean isShowArtIdsAllowed) {
      this.isShowArtIdsAllowed = isShowArtIdsAllowed;
   }

   public void setShowArtType(boolean isShowArtTypeAllowed) {
      this.isShowArtTypeAllowed = isShowArtTypeAllowed;
   }

   public void setShowArtBranch(boolean isShowBranchAllowed) {
      this.isShowBranchAllowed = isShowBranchAllowed;
   }

   public void setShowArtVersion(boolean isShowArtVersionAllowed) {
      this.isShowArtVersionAllowed = isShowArtVersionAllowed;
   }

   @Override
   public boolean showArtIds() {
      return isShowArtIdsAllowed;
   }

   @Override
   public boolean showArtType() {
      return isShowArtTypeAllowed;
   }

   @Override
   public boolean showArtBranch() {
      return isShowBranchAllowed;
   }

   @Override
   public boolean showArtVersion() {
      return isShowArtVersionAllowed;
   }

   @Override
   public boolean showRelations() {
      return isShowRelations;
   }

   public void setShowRelations(boolean isShowRelations) {
      this.isShowRelations = isShowRelations;
   }

}
