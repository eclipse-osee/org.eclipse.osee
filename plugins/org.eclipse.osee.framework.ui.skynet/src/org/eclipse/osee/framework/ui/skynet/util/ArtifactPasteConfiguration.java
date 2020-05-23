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
