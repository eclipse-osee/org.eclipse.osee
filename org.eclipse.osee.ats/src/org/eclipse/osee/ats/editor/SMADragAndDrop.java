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
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class SMADragAndDrop extends SkynetDragAndDrop {

   private final StateMachineArtifact sma;

   public SMADragAndDrop(Control control, StateMachineArtifact sma, String viewId) {
      super(control, viewId);
      this.sma = sma;
   }

   @Override
   public Artifact[] getArtifacts() {
      return new Artifact[] {sma};
   }

   @Override
   public void artifactTransferDragSetData(DragSourceEvent event) {
      super.artifactTransferDragSetData(event);
   }

}
