/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.action;

import java.net.URL;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUrlClient;
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
         String urlString = generateLink();
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
         CoreArtifactTypes.MsWordWholeDocument) ? CoreAttributeTypes.WholeWordContent : CoreAttributeTypes.NativeContent;
   }

   public static boolean isApplicable(Artifact artifact) {
      return artifact.isOfType(CoreArtifactTypes.NativeArtifact) || artifact.isOfType(
         CoreArtifactTypes.MsWordWholeDocument);
   }

   public String generateLink() {
      return String.format("%sorcs/branch/%s/artifact/%s/attribute/type/%s",
         new ArtifactUrlClient().getSelectedPermanentLinkUrl(), artifact.getBranch().getIdString(),
         artifact.getIdString(), getAttributeTypeId().getIdString());
   }

}
