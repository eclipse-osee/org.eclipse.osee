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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.skynet.HTMLTransferFormatter;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactClipboard {
   private static final String STATUS = "work";
   private Clipboard clipboard;
   private final String viewId;

   public ArtifactClipboard(String viewId) {
      this.clipboard = new Clipboard(null);
      this.viewId = viewId;
   }

   private List<Artifact> getArtifactsWithPermission(AccessPolicy accessService, PermissionEnum permission, List<Artifact> artifacts)  {
      ArrayList<Artifact> toReturn = new ArrayList<>(artifacts);
      Iterator<Artifact> artIterator = toReturn.iterator();

      // Remove Artifact that do not have write permission.
      while (artIterator.hasNext()) {
         Artifact cur = artIterator.next();
         if (!accessService.hasArtifactPermission(java.util.Collections.singleton(cur), permission,
            Level.WARNING).matched()) {
            artIterator.remove();
         }
      }
      return toReturn;
   }

   public void dispose() {
      if (clipboard != null) {
         clipboard.dispose();
      }
   }

   public void setArtifactsToClipboard(AccessPolicy policyHandlerService, List<Artifact> artifactTransferData)  {
      if (artifactTransferData == null) {
         throw new IllegalArgumentException("Artifacts can not be null for artifact copy.");
      }
      if (artifactTransferData.isEmpty()) {
         throw new IllegalArgumentException("Artifacts can not be empty.");
      }
      if (clipboard.isDisposed()) {
         this.clipboard = new Clipboard(null);
      }

      List<Artifact> authFailedList = new ArrayList<>(artifactTransferData);
      List<Artifact> authorizedArtifacts =
         getArtifactsWithPermission(policyHandlerService, PermissionEnum.READ, artifactTransferData);

      authFailedList.removeAll(authorizedArtifacts);

      if (authorizedArtifacts.size() > 0) {
         ArrayList<String> textTransferData = new ArrayList<>();
         for (Artifact cur : authorizedArtifacts) {
            textTransferData.add(cur.getName());
         }
         Artifact[] artifacts = authorizedArtifacts.toArray(new Artifact[authorizedArtifacts.size()]);
         clipboard.setContents(
            new Object[] {
               new ArtifactData(artifacts, STATUS, viewId),
               HTMLTransferFormatter.getHtml(artifacts),
               Collections.toString(textTransferData, null, ", ", null)},
            new Transfer[] {ArtifactTransfer.getInstance(), HTMLTransfer.getInstance(), TextTransfer.getInstance()});
      }
      if (authFailedList.size() > 0) {
         String failed = Collections.toString(", ", authFailedList) + ".";
         MessageDialog.openError(Displays.getActiveShell(), "Copy Error",
            "Access control has restricted this action. The following artifacts were not copied to the clipboard: " + failed);
      }

   }

   public void setTextToClipboard(Collection<String> textTransferData) {
      if (textTransferData == null) {
         throw new IllegalArgumentException("Artifacts can not be null for artifact copy.");
      }
      if (textTransferData.isEmpty()) {
         throw new IllegalArgumentException("Artifacts can not be empty.");
      }
      if (clipboard.isDisposed()) {
         this.clipboard = new Clipboard(null);
      }

      clipboard.setContents(new Object[] {Collections.toString(textTransferData, null, ", ", null)},
         new Transfer[] {TextTransfer.getInstance()});
   }

   public boolean isEmpty() {
      boolean theReturn = true;
      if (clipboard.isDisposed()) {
         theReturn = true;
      } else {
         theReturn = clipboard.getContents(ArtifactTransfer.getInstance()) == null;
      }
      return theReturn;
   }

   public List<Artifact> getCopiedContents() {
      Object object = clipboard.getContents(ArtifactTransfer.getInstance());
      List<Artifact> copiedItems;
      if (object instanceof ArtifactData) {
         ArtifactData data = (ArtifactData) object;
         copiedItems = Arrays.asList(data.getArtifacts());
      } else {
         copiedItems = java.util.Collections.emptyList();
      }
      return copiedItems;
   }

}
