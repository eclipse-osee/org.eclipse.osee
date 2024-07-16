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

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.world.IWorldEditorProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorAction extends AbstractAtsAction {

   private final IOpenNewAtsWorldEditorHandler openNewAtsWorldEditorHandler;

   public OpenNewAtsWorldEditorAction(IOpenNewAtsWorldEditorHandler openNewAtsWorldEditorHandler) {
      super();
      this.openNewAtsWorldEditorHandler = openNewAtsWorldEditorHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE));
      setToolTipText("Open in ATS World Editor");
   }

   public interface IOpenNewAtsWorldEditorHandler {
      IWorldEditorProvider getWorldEditorProviderCopy();

      CustomizeData getCustomizeDataCopy();
   }

   @Override
   public void runWithException() {
      IWorldEditorProvider provider = openNewAtsWorldEditorHandler.getWorldEditorProviderCopy();
      if (provider != null) {
         provider.setCustomizeData(openNewAtsWorldEditorHandler.getCustomizeDataCopy());
         provider.setTableLoadOptions(TableLoadOption.NoUI);
         WorldEditor.open(provider);
      }
   }

}
