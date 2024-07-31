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
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsTaskEditorAction extends AbstractAtsAction {

   private final WorldComposite worldComposite;

   public OpenNewAtsTaskEditorAction(WorldComposite worldComposite) {
      super();
      this.worldComposite = worldComposite;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
      setToolTipText("Open New ATS Task Editor");
   }

   @Override
   public void runWithException() {
      TaskEditor.open(new TaskEditorSimpleProvider("Tasks", worldComposite.getLoadedArtifacts(),
         worldComposite.getCustomizeDataCopy()));
   }

}
