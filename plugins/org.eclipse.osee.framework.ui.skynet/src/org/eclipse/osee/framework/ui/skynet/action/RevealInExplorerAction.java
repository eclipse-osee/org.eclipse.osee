/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class RevealInExplorerAction extends Action {
   private final Artifact artifact;

   public RevealInExplorerAction(Artifact artifact) {
      this.artifact = artifact;
      setText("Reveal in Artifact Explorer");
      setToolTipText("Reveal this artifact in the Artifact Explorer");
   }

   @Override
   public void run() {
      ArtifactExplorerUtil.revealArtifact(artifact);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.MAGNIFY);
   }

}
