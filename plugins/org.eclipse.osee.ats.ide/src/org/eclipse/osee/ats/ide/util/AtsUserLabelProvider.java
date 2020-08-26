/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.util;


import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provide normal labels and icons for AtsUser and User artifact
 *
 * @author Donald G. Dunne
 */
public class AtsUserLabelProvider extends ArtifactLabelProvider {
   private final boolean includeInActive;

   public AtsUserLabelProvider() {
      this(true);
   }

   public AtsUserLabelProvider(boolean includeInActive) {
      this.includeInActive = includeInActive;
   }

   @Override
   public Image getImage(Object element) {
      if (element instanceof AtsUser) {
         Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact((AtsUser) element);
         if (art != null) {
            return super.getImage(art);
         }
      }
      return super.getImage(element);
   }

   @Override
   public String getText(Object element) {
      if (element instanceof AtsUser) {
         Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact((AtsUser) element);
         if (art != null) {
            String name = super.getText(art);
            if (includeInActive && Strings.isValid(
               name) && !art.getSoleAttributeValue(CoreAttributeTypes.Active, true)) {
               return name += " (InActive)";
            }
         }
      }
      return super.getText(element);
   }

}
