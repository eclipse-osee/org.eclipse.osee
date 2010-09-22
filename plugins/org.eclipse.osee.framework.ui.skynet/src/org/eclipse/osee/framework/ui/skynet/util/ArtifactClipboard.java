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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.skynet.HTMLTransferFormatter;
import org.eclipse.osee.framework.ui.skynet.accessProviders.ArtifactAccessProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.artifact.IAccessPolicyHandlerService;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactClipboard {
   private static final String STATUS = "work";
   private final Clipboard clipboard;
   private final String viewId;

   public ArtifactClipboard(String viewId) {
      this.clipboard = new Clipboard(null);
      this.viewId = viewId;
   }

   public void setArtifactsToClipboard(ArtifactAccessProvider artifactAccessProvider, IAccessPolicyHandlerService policyHandlerService, List<Artifact> artifactTransferData, List<String> textTransferData) throws OseeCoreException {
      if (artifactTransferData == null) {
         throw new IllegalArgumentException("Artifacts can not be null for artifact copy.");
      }
      if (artifactTransferData.isEmpty()) {
         throw new IllegalArgumentException("Artifacts can not be empty.");
      }

      List<Artifact> authorizedArtifacts =
         artifactAccessProvider.getArtifactsWithPermission(policyHandlerService, PermissionEnum.WRITE,
            artifactTransferData);

      Artifact[] artifacts = authorizedArtifacts.toArray(new Artifact[authorizedArtifacts.size()]);

      clipboard.setContents(
         new Object[] {
            new ArtifactData(artifacts, STATUS, viewId),
            HTMLTransferFormatter.getHtml(artifacts),
            Collections.toString(textTransferData, null, ", ", null)}, new Transfer[] {
            ArtifactTransfer.getInstance(),
            HTMLTransfer.getInstance(),
            TextTransfer.getInstance()});
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

   public boolean isEmpty() {
      return clipboard.getContents(ArtifactTransfer.getInstance()) == null;
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
