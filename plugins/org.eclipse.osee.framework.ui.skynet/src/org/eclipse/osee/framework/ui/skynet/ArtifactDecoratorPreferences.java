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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDecoratorPreferences implements IArtifactDecoratorPreferences {

   private boolean isShowArtIdsAllowed;
   private boolean isShowArtTypeAllowed;
   private boolean isShowBranchAllowed;
   private boolean isShowArtVersionAllowed;

   public ArtifactDecoratorPreferences() {

   }

   @Override
   public String getSelectedAttributeData(Artifact artifact) throws OseeCoreException {
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

}
