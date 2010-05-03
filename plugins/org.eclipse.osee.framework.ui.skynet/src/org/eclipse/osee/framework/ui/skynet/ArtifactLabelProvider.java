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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;

public class ArtifactLabelProvider extends LabelProvider { //StyledCellLabelProvider {

   private final IArtifactDecoratorPreferences decorationProvider;

   public ArtifactLabelProvider(IArtifactDecoratorPreferences decorationProvider) {
      super();
      this.decorationProvider = decorationProvider;
   }

   public ArtifactLabelProvider() {
      this(null);
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   @Override
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) element);
      } else if (element instanceof Match && ((Match) element).getElement() instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) ((Match) element).getElement());
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   /*
    * @see ILabelProvider#getText(Object)
    */
   @Override
   public String getText(Object element) {
      if (element instanceof Match) {
         element = ((Match) element).getElement();
      }

      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;

         List<String> extraInfo = new ArrayList<String>();
         String name = artifact.getName();
         extraInfo.add(name != null ? name : "");
         if (artifact.isDeleted()) {
            extraInfo.add("<Deleted>");
         }
         if (decorationProvider != null) {

            if (decorationProvider.showArtIds() && decorationProvider.showArtVersion()) {
               extraInfo.add(String.format("[%s rev.%s]", artifact.getArtId(), artifact.getGammaId()));
            } else if (decorationProvider.showArtIds() && !decorationProvider.showArtVersion()) {
               extraInfo.add(String.format("[id %s]", artifact.getArtId()));
            } else if (!decorationProvider.showArtIds() && decorationProvider.showArtVersion()) {
               extraInfo.add(String.format("[rev.%s]", artifact.getGammaId()));
            }

            if (decorationProvider.showArtType()) {
               extraInfo.add("<" + artifact.getArtifactTypeName() + ">");
            }

            if (decorationProvider.showArtBranch()) {
               extraInfo.add("[" + artifact.getBranch().getShortName() + "]");
            }

            try {
               String selectedAttributes = decorationProvider.getSelectedAttributeData(artifact);
               if (Strings.isValid(selectedAttributes)) {
                  extraInfo.add(selectedAttributes);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               extraInfo.add(ex.toString());
            }
         }
         return Collections.toString(extraInfo, " ");
      } else {
         return element.toString();
      }
   }
}
