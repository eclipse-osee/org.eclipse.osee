/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.internal.AtsClientService;
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
      if (element instanceof IAtsUser) {
         Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact((IAtsUser) element);
         if (art != null) {
            return super.getImage(art);
         }
      }
      return super.getImage(element);
   }

   @Override
   public String getText(Object element) {
      if (element instanceof IAtsUser) {
         Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact((IAtsUser) element);
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
