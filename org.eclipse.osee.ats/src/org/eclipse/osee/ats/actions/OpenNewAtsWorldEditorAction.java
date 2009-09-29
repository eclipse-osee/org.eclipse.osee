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
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorAction extends Action {

   private final IOpenNewAtsWorldEditorHandler openNewAtsWorldEditorHandler;

   public OpenNewAtsWorldEditorAction(IOpenNewAtsWorldEditorHandler openNewAtsWorldEditorHandler) {
      this.openNewAtsWorldEditorHandler = openNewAtsWorldEditorHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE));
      setToolTipText("Open in ATS World Editor");
   }

   public interface IOpenNewAtsWorldEditorHandler {
      public IWorldEditorProvider getWorldEditorProviderCopy() throws OseeCoreException;

      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;
   }

   @Override
   public void run() {
      try {
         IWorldEditorProvider provider = openNewAtsWorldEditorHandler.getWorldEditorProviderCopy();
         provider.setCustomizeData(openNewAtsWorldEditorHandler.getCustomizeDataCopy());
         provider.setTableLoadOptions(TableLoadOption.NoUI);
         WorldEditor.open(provider);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
