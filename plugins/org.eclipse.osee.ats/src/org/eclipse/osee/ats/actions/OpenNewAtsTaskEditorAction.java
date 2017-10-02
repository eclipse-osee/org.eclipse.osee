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
import org.eclipse.osee.ats.task.ITaskEditorProvider;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsTaskEditorAction extends AbstractAtsAction {

   private final IOpenNewAtsTaskEditorHandler openNewAtsTaskEditorHandler;

   public OpenNewAtsTaskEditorAction(IOpenNewAtsTaskEditorHandler openNewAtsTaskEditorHandler) {
      super();
      this.openNewAtsTaskEditorHandler = openNewAtsTaskEditorHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
      setToolTipText("Open New ATS Task Editor");
   }

   public interface IOpenNewAtsTaskEditorHandler {
      ITaskEditorProvider getTaskEditorProviderCopy();

      CustomizeData getCustomizeDataCopy();
   }

   @Override
   public void runWithException() {
      ITaskEditorProvider provider = openNewAtsTaskEditorHandler.getTaskEditorProviderCopy();
      provider.setCustomizeData(openNewAtsTaskEditorHandler.getCustomizeDataCopy());
      provider.setTableLoadOptions(TableLoadOption.NoUI);
      TaskEditor.open(provider);
   }

}
