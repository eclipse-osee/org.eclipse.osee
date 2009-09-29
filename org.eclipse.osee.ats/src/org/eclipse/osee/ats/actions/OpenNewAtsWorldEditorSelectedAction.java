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
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorSelectedAction extends Action {

   private final IOpenNewAtsWorldEditorSelectedHandler openNewAtsWorldEditorSelectedHandler;

   public OpenNewAtsWorldEditorSelectedAction(IOpenNewAtsWorldEditorSelectedHandler openNewAtsWorldEditorSelectedHandler) {
      this.openNewAtsWorldEditorSelectedHandler = openNewAtsWorldEditorSelectedHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE_SELECT));
      setToolTipText("Open Selected in ATS World Editor");
   }

   public interface IOpenNewAtsWorldEditorSelectedHandler {
      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;

      public ArrayList<Artifact> getSelectedArtifacts() throws OseeCoreException;

   }

   @Override
   public void run() {
      try {
         if (openNewAtsWorldEditorSelectedHandler.getSelectedArtifacts().size() == 0) {
            AWorkbench.popup("ERROR", "Select items to open");
            return;
         }
         WorldEditor.open(new WorldEditorSimpleProvider("ATS World",
               openNewAtsWorldEditorSelectedHandler.getSelectedArtifacts(),
               openNewAtsWorldEditorSelectedHandler.getCustomizeDataCopy(), (TableLoadOption[]) null));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
