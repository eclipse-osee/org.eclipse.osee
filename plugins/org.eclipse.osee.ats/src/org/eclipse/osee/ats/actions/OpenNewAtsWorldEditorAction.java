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

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldEditor;
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
      provider.setCustomizeData(openNewAtsWorldEditorHandler.getCustomizeDataCopy());
      provider.setTableLoadOptions(TableLoadOption.NoUI);
      WorldEditor.open(provider);
   }

}
