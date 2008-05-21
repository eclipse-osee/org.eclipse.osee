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

import java.util.Collection;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.HTMLTransferFormatter;
import org.eclipse.osee.framework.ui.swt.NonBlankValidator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactClipboard {
   private static final String STATUS = "work";
   private Clipboard clipboard;
   private String viewId;

   public ArtifactClipboard(String viewId) {
      this.clipboard = new Clipboard(null);
      this.viewId = viewId;
   }

   public void setArtifactsToClipboard(Collection<Artifact> artifactTransferData, Collection<String> textTransferData) {
      if (artifactTransferData == null) {
         throw new IllegalArgumentException("Artifacts can not be null for artifact copy.");
      }
      if (artifactTransferData.isEmpty()) {
         throw new IllegalArgumentException("Artifacts can not be empty.");
      }

      Artifact[] artifacts = artifactTransferData.toArray(new Artifact[artifactTransferData.size()]);

      clipboard.setContents(new Object[] {new ArtifactData(artifacts, STATUS, viewId),
            HTMLTransferFormatter.getHtml(artifacts), Collections.toString(textTransferData, null, ", ", null)},
            new Transfer[] {ArtifactTransfer.getInstance(), HTMLTransfer.getInstance(), TextTransfer.getInstance()});
   }

   public void setTextToClipboard(Collection<String> textTransferData) {
      if (textTransferData == null) {
         throw new IllegalArgumentException("Artifacts can not be null for artifact copy.");
      }
      if (textTransferData.isEmpty()) {
         throw new IllegalArgumentException("Artifacts can not be empty.");
      }

      clipboard.setContents(new Object[] {Collections.toString(textTransferData, null, ", ", null)},
            new Transfer[] {TextTransfer.getInstance()});
   }

   private static IInputValidator inputValidator = new NonBlankValidator("The new name must not be blank");

   /**
    * This method must be called from the display thread
    * 
    * @throws Exception
    */
   public void pasteArtifactsFromClipboard(Artifact parent) throws Exception {
      if (parent == null) throw new IllegalArgumentException("Parent can not be null.");

      Object object = clipboard.getContents(ArtifactTransfer.getInstance());

      if (object instanceof ArtifactData) {
         Artifact[] clipboardArtifacts = ((ArtifactData) object).getArtifacts();

         if (clipboardArtifacts.length == 1) {
            Artifact clipboardArtifact = clipboardArtifacts[0];
            if (clipboardArtifact instanceof User) {
               return;
            }

            InputDialog dialog =
                  new InputDialog(Display.getCurrent().getActiveShell(), "Name Artifact", "Enter artifact name",
                        clipboardArtifacts[0].getDescriptiveName(), inputValidator);

            if (dialog.open() == Window.CANCEL) {
               return;
            } else {
               Artifact newArtifact = null;
               newArtifact = clipboardArtifact.duplicate(parent.getBranch());
               newArtifact.setDescriptiveName(dialog.getValue());
               parent.addChild(newArtifact);
            }
         } else {
            for (Artifact clipboardArtifact : clipboardArtifacts) {
               // We do not support duplicating user artifacts.
               if (clipboardArtifact instanceof User) {
                  continue;
               }

               Artifact newArtifact = null;
               newArtifact = clipboardArtifact.duplicate(parent.getBranch());
               parent.addChild(newArtifact);
            }
         }

         parent.persistAttributesAndRelations();
      }
   }
}
