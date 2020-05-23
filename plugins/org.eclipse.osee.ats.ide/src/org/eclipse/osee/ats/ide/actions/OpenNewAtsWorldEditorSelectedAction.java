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

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorSelectedAction extends AbstractAtsAction {

   private final IOpenNewAtsWorldEditorSelectedHandler openNewAtsWorldEditorSelectedHandler;

   public OpenNewAtsWorldEditorSelectedAction(IOpenNewAtsWorldEditorSelectedHandler openNewAtsWorldEditorSelectedHandler) {
      super();
      this.openNewAtsWorldEditorSelectedHandler = openNewAtsWorldEditorSelectedHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE_SELECT));
      setToolTipText("Open Selected in ATS World Editor");
   }

   public interface IOpenNewAtsWorldEditorSelectedHandler {
      CustomizeData getCustomizeDataCopy();

      List<Artifact> getSelectedArtifacts();
   }

   @Override
   public void runWithException() {
      if (openNewAtsWorldEditorSelectedHandler.getSelectedArtifacts().isEmpty()) {
         AWorkbench.popup("ERROR", "Select items to open");
         return;
      }
      WorldEditor.open(
         new WorldEditorSimpleProvider("ATS World", openNewAtsWorldEditorSelectedHandler.getSelectedArtifacts(),
            openNewAtsWorldEditorSelectedHandler.getCustomizeDataCopy()));
   }

}
