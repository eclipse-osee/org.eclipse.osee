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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerLinkNode;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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

   @Override
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) element);
      } else if (element instanceof Match && ((Match) element).getElement() instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) ((Match) element).getElement());
      } else if (element instanceof ArtifactExplorerLinkNode) {
         return ImageManager.getImage(FrameworkImage.RELATION);
      } else if (element instanceof ArtifactTypeToken) {
         return ArtifactImageManager.getImage((ArtifactTypeToken) element);
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   @Override
   public String getText(Object element) {
      if (element instanceof Match) {
         element = ((Match) element).getElement();
      }

      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;

         List<String> extraInfo = new ArrayList<>();
         String name = artifact.getName();
         extraInfo.add(name != null ? name : "");
         if (artifact.isDeleted()) {
            extraInfo.add("<Deleted>");
         }
         if (decorationProvider != null) {

            if (decorationProvider.showArtIds() && decorationProvider.showArtVersion()) {
               extraInfo.add(String.format("[%s rev.%s]", artifact.getId(), artifact.getGammaId()));
            } else if (decorationProvider.showArtIds() && !decorationProvider.showArtVersion()) {
               extraInfo.add(String.format("[id %s]", artifact.getId()));
            } else if (!decorationProvider.showArtIds() && decorationProvider.showArtVersion()) {
               extraInfo.add(String.format("[rev.%s]", artifact.getGammaId()));
            }

            if (decorationProvider.showArtType()) {
               extraInfo.add("<" + artifact.getArtifactTypeName() + ">");
            }

            try {
               if (decorationProvider.showArtBranch()) {
                  extraInfo.add("[" + artifact.getBranchToken().getShortName() + "]");
               }
               String selectedAttributes = decorationProvider.getSelectedAttributeData(artifact);
               if (Strings.isValid(selectedAttributes)) {
                  extraInfo.add(selectedAttributes);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               extraInfo.add(ex.toString());
            }
         }
         return Collections.toString(" ", extraInfo);
      } else if (element instanceof ArtifactExplorerLinkNode) {
         ArtifactExplorerLinkNode smartifactLinkNode = (ArtifactExplorerLinkNode) element;
         RelationTypeToken relationType = smartifactLinkNode.getRelationType();
         String sideName = smartifactLinkNode.isParentIsOnSideA() ? relationType.getSideName(
            RelationSide.SIDE_B) : relationType.getSideName(RelationSide.SIDE_A);
         return String.format("%s - [%s]", relationType.getName(), sideName);
      } else if (element != null) {
         return element.toString();
      } else {
         return "";
      }
   }
}