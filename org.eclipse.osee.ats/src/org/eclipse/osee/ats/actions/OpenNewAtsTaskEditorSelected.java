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
package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsTaskEditorSelected extends Action {

   private final IOpenNewAtsTaskEditorSelectedHandler openNewAtsTaskEditorSelectedHandler;

   public OpenNewAtsTaskEditorSelected(IOpenNewAtsTaskEditorSelectedHandler openNewAtsTaskEditorSelectedHandler) {
      this.openNewAtsTaskEditorSelectedHandler = openNewAtsTaskEditorSelectedHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK_SELECTED));
      setToolTipText("Open Selected in ATS Task Editor");
   }

   public interface IOpenNewAtsTaskEditorSelectedHandler {
      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;

      public ArrayList<? extends Artifact> getSelectedArtifacts() throws OseeCoreException;

   }

   @Override
   public void run() {
      try {
         if (openNewAtsTaskEditorSelectedHandler.getSelectedArtifacts().size() == 0) {
            AWorkbench.popup("ERROR", "Select items to open");
            return;
         }
         TaskEditor.open(new TaskEditorSimpleProvider("Tasks",
               openNewAtsTaskEditorSelectedHandler.getSelectedArtifacts(),
               openNewAtsTaskEditorSelectedHandler.getCustomizeDataCopy()));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
