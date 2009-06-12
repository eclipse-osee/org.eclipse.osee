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

import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;

public class ArtifactLabelProvider extends LabelProvider { //StyledCellLabelProvider {
//   /* (non-Javadoc)
//    * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
//    */
//   @Override
//   public void update(ViewerCell cell) {
//      Object element = cell.getElement();
//      if (element instanceof Match) {
//         element = ((Match) element).getElement();
//      }
//
//      if (element instanceof Artifact) {
//         Artifact artifact = (Artifact) element;
//
//         String name = artifact.getDescriptiveName();
//         if (artifact.isDeleted()) {
//            name += " <Deleted>";
//         }
//         if (artifactExplorer != null) {
//            if (artifactExplorer.showArtVersion()) {
//               name += " -" + artifact.getGammaId() + "- ";
//            }
//
//            if (artifactExplorer.showArtIds()) {
//               name += " (" + artifact.getArtId() + ") ";
//            }
//            try {
//               if (artifactExplorer.showArtType()) {
//                  name += " <" + artifact.getArtifactTypeName() + "> ";
//               }
//
//               name += artifactExplorer.getSelectedAttributeData(artifact);
//            } catch (Exception ex) {
//               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
//               name += ex.getLocalizedMessage();
//            }
//         }
//         cell.setText(name);
//      } else {
//         cell.setText(element.toString());
//      }
//   }

   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();
   private final ArtifactDecorator artifactDecorator;

   public ArtifactLabelProvider(ArtifactDecorator artifactDecorator) {
      super();
      this.artifactDecorator = artifactDecorator;
   }

   public ArtifactLabelProvider() {
      this(null);
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ImageManager.getImage((Artifact) element);
      } else if (element instanceof Match && ((Match) element).getElement() instanceof Artifact) {
         return ImageManager.getImage((Artifact) ((Match) element).getElement());
      }
      return ImageManager.getImage(FrameworkImage.MISSING);
   }

   /*
    * @see ILabelProvider#getText(Object)
    */
   public String getText(Object element) {
      if (element instanceof Match) {
         element = ((Match) element).getElement();
      }

      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;

         String name = artifact.getDescriptiveName();
         if (artifact.isDeleted()) {
            name += " <Deleted>";
         }
         if (artifactDecorator != null) {
            if (artifactDecorator.showArtVersion()) {
               name += " -" + artifact.getGammaId() + "- ";
            }

            if (artifactDecorator.showArtIds()) {
               name += " (" + artifact.getArtId() + ") ";
            }
            try {
               if (artifactDecorator.showArtType()) {
                  name += " <" + artifact.getArtifactTypeName() + "> ";
               }

               name += artifactDecorator.getSelectedAttributeData(artifact);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               name += ex.getLocalizedMessage();
            }
         }
         return name;
      } else {
         return element.toString();
      }
   }
}
