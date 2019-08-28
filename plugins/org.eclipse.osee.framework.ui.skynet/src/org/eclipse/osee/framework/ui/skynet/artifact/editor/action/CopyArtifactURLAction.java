/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.action;

import java.net.URL;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * Copy link to open native and whole word document to the clipboard.
 *
 * @author Donald G. Dunne
 */
public final class CopyArtifactURLAction extends Action {

   private final Artifact artifact;

   public CopyArtifactURLAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.COPYTOCLIPBOARD));
      setToolTipText(
         "Copy Artifact URL link to clipboard. NOTE: This is a link pointing to the latest version of the artifact.");
   }

   @Override
   public void run() {
      Clipboard clipboard = null;
      try {
         String urlString =
            String.format("%sorcs/branch/%s/artifact/%s/attribute/type/%s", ArtifactURL.getSelectedPermanenrLinkUrl(),
               artifact.getBranch().getIdString(), artifact.getIdString(), getAttributeTypeId().getIdString());
         URL url = new URL(urlString);
         clipboard = new Clipboard(null);
         clipboard.setContents(new Object[] {url.toString()}, new Transfer[] {TextTransfer.getInstance()});
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error obtaining url for - guid: [%s] branch:[%s]",
            artifact.getGuid(), artifact.getBranch().getIdString());
      } finally {
         if (clipboard != null && !clipboard.isDisposed()) {
            clipboard.dispose();
            clipboard = null;
         }
      }
   }

   private AttributeTypeId getAttributeTypeId() {
      return artifact.isOfType(
         CoreArtifactTypes.MsWholeWordDocument) ? CoreAttributeTypes.WholeWordContent : CoreAttributeTypes.NativeContent;
   }

   public static boolean isApplicable(Artifact artifact) {
      return artifact.isOfType(CoreArtifactTypes.NativeArtifact) || artifact.isOfType(
         CoreArtifactTypes.MsWholeWordDocument);
   }

}
