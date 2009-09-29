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

import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.task.ITaskEditorProvider;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsTaskEditorAction extends Action {

   private final IOpenNewAtsTaskEditorHandler openNewAtsTaskEditorHandler;

   public OpenNewAtsTaskEditorAction(IOpenNewAtsTaskEditorHandler openNewAtsTaskEditorHandler) {
      this.openNewAtsTaskEditorHandler = openNewAtsTaskEditorHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
      setToolTipText("Open New ATS Task Editor");
   }

   public interface IOpenNewAtsTaskEditorHandler {
      public ITaskEditorProvider getTaskEditorProviderCopy() throws OseeCoreException;

      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;
   }

   @Override
   public void run() {
      try {
         ITaskEditorProvider provider = openNewAtsTaskEditorHandler.getTaskEditorProviderCopy();
         provider.setCustomizeData(openNewAtsTaskEditorHandler.getCustomizeDataCopy());
         provider.setTableLoadOptions(TableLoadOption.NoUI);
         TaskEditor.open(provider);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
