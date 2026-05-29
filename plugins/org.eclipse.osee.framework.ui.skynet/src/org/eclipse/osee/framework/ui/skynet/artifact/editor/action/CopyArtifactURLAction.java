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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * Copy link to open native and whole word document or artifact to the clipboard.
 *
 * @author Donald G. Dunne
 */
public final class CopyArtifactURLAction extends Action {

   private final Artifact artifact;

   public CopyArtifactURLAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(CoreImage.COPYTOCLIPBOARD));
      setToolTipText("Copy Artifact URL link or word/native content link to clipboard.");
   }

   @Override
   public void run() {
      Clipboard clipboard = null;
      try {
         clipboard = new Clipboard(null);
         String urlString = generateLink();
         if (Strings.isValid(urlString)) {
            clipboard.setContents(new Object[] {urlString}, new Transfer[] {TextTransfer.getInstance()});
         }
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
      String webBaseUrl = OseeInfo.getValue(OseeClient.OSEE_APPLICATION_SERVER_WEB);
      String urlString = "";
      boolean openArtifact = true;
      boolean hasNativeOrWholWordAttr = artifact.hasAttributeType(getAttributeTypeId());
      if (hasNativeOrWholWordAttr) {
         MessageDialog dialog =
            new MessageDialog(Displays.getActiveShell(), "Select link type", null, "Select link type",
               MessageDialog.QUESTION, 3, new String[] {"Content Download Link", "Artifact Link", "Cancel"});

         int buttonNum = dialog.open();
         if (buttonNum == 2) {
            return "";
         }
         if (buttonNum == 0) {
            openArtifact = false;
         }
      }
      if (openArtifact) {
         urlString = OpenArtifactInBrowserAction.getArtifactUrl(artifact);
      } else {
         urlString = String.format("%sorcs/branch/%s/artifact/%s/attribute/type/%s", webBaseUrl,
            artifact.getBranch().getIdString(), artifact.getIdString(), getAttributeTypeId().getIdString());
      }
      return urlString;
   }

}
