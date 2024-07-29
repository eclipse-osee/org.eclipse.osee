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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorAction extends AbstractAtsAction {

   private final WorldComposite worldComposite;

   public OpenNewAtsWorldEditorAction(WorldComposite worldComposite) {
      super();
      this.worldComposite = worldComposite;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE));
      setToolTipText("Open in ATS World Editor");
   }

   @Override
   public void runWithException() {
      WorldEditor.open(new WorldEditorSimpleProvider("ATS World", worldComposite.getLoadedArtifacts(),
         worldComposite.getCustomizeDataCopy()));
   }

}
