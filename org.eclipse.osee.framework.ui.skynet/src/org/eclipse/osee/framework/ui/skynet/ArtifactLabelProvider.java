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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;

public class ArtifactLabelProvider extends LabelProvider {
   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();
   private final ArtifactExplorer artifactExplorer;

   public ArtifactLabelProvider(ArtifactExplorer artifactExplorer) {
      super();
      this.artifactExplorer = artifactExplorer;
   }

   public ArtifactLabelProvider() {
      this(null);
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ((Artifact) element).getImage();
      } else if (element instanceof Match && ((Match) element).getElement() instanceof Artifact) {
         return ((Artifact) ((Match) element).getElement()).getImage();
      }
      return plugin.getImage("laser_16_16.gif");
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
         if (artifactExplorer != null) {
            if (artifactExplorer.showArtIds()) {
               name += " (" + artifact.getArtId() + ") ";
            }
            try {
               if (artifactExplorer.showArtType()) {
                  name += " <" + artifact.getArtifactTypeName() + "> ";
               }

               name += artifactExplorer.getSelectedAttributeData(artifact);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, false);
               name += ex.getLocalizedMessage();
            }
         }
         return name;
      } else {
         return element.toString();
      }
   }
}
