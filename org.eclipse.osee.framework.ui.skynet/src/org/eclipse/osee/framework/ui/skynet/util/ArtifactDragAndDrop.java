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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class ArtifactDragAndDrop extends SkynetDragAndDrop {

   private final Artifact artifact;

   public ArtifactDragAndDrop(Control control, Artifact artifact, String viewId) {
      super(control, viewId);
      this.artifact = artifact;
   }

   @Override
   public Artifact[] getArtifacts() {
      return new Artifact[] {artifact};
   }

   @Override
   public void artifactTransferDragSetData(DragSourceEvent event) {
      super.artifactTransferDragSetData(event);
   }

}
