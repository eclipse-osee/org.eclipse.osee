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
package org.eclipse.osee.framework.ui.skynet.util;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactPasteConfiguration {

   private boolean includeChildrenOfCopiedElements;
   private boolean isOrderDuplicationAllowed;

   public ArtifactPasteConfiguration() {
      includeChildrenOfCopiedElements = false;
      isOrderDuplicationAllowed = true;
   }

   public void setIncludeChildrenOfCopiedElements(boolean includeChildrenOfCopiedElements) {
      this.includeChildrenOfCopiedElements = includeChildrenOfCopiedElements;
   }

   public boolean isIncludeChildrenOfCopiedElements() {
      return includeChildrenOfCopiedElements;
   }

   public boolean isKeepRelationOrderSettings() {
      return isOrderDuplicationAllowed;
   }

   public void setKeepRelationOrderSettings(boolean isOrderDuplicationAllowed) {
      this.isOrderDuplicationAllowed = isOrderDuplicationAllowed;
   }

}
