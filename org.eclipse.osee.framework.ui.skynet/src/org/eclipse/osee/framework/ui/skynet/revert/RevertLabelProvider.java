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
package org.eclipse.osee.framework.ui.skynet.revert;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;

public class RevertLabelProvider extends LabelProvider { //StyledCellLabelProvider {
   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();
   private Combo artifactSelectionBox = null;
   private List<List<Artifact>> artifacts = null;

   public RevertLabelProvider(Combo artifactSelectionBox, List<List<Artifact>> artifacts) {
      super();
      this.artifactSelectionBox = artifactSelectionBox;
      this.artifacts = artifacts;
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
      return super.getImage(element);
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
         boolean toDelete = false;
         boolean linkToDelete = false;
         boolean rootArtifact = RevertDeletionCheck.isRootArtifact(artifact, artifactSelectionBox, artifacts);
         try {
            linkToDelete = RevertDeletionCheck.relationWillBeReverted(artifact);
            toDelete = ArtifactPersistenceManager.isArtifactNewOnBranch(artifact);
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), Level.SEVERE, ex);
         }
         if (rootArtifact) {
            if (toDelete) {
               name += " --<Will be Deleted>--";
            } else {
               if (artifact.isDeleted()) {
                  name += " --<Will be Undeleted>--";
               } else {
                  name += " --<Will Revert to Begining Value>--";
               }
            }
         }
         if (!rootArtifact) {
            if (linkToDelete && !rootArtifact) {
               name += " --<Will be Orphaned>--";
            } else {
            }
         }
         return name;
      } else {
         return element.toString();
      }
   }
}
