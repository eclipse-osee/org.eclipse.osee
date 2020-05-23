/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
}
