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

import java.sql.SQLException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;

public class ArtifactLabelProvider extends LabelProvider {
   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();
   private final ArtifactExplorer artifactExplorer;
   private boolean showBranch;

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
         if (artifact.isDeleted()) throw new IllegalArgumentException("Can not display a deleted artifact");

         String name = artifact.getDescriptiveName();
         if (name == null) name = "";
         if (artifactExplorer != null) {
            if (artifactExplorer.showArtIds()) {
               name += " (" + artifact.getArtId() + ") ";
            }
            if (artifactExplorer.showArtType()) {
               name += " <" + artifact.getArtifactTypeName() + "> ";
            }
            try {
               name += artifactExplorer.getSelectedAttributeData(artifact);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, false);
               name += ex.getLocalizedMessage();
            }
         }
         if (showBranch) {
            name += " <" + artifact.getBranch().getBranchShortName() + "> ";
         }
         try {
            int deletionTransactionId = artifact.getDeletionTransactionId();
            if (deletionTransactionId != -1) {
               name += " <deleted in tx " + deletionTransactionId + "> ";
            }
         } catch (SQLException ex) {
            OSEELog.logException(getClass(), ex, false);
         }

         return name;
      } else {
         return element.toString();
      }
   }

   public void showBranch(boolean showBranch) {
      this.showBranch = showBranch;
   }
}
