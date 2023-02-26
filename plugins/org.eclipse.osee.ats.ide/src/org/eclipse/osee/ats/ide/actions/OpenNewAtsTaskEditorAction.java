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
import org.eclipse.osee.ats.ide.workflow.task.ITaskEditorProvider;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
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
      if (provider != null) {
         provider.setCustomizeData(openNewAtsTaskEditorHandler.getCustomizeDataCopy());
         provider.setTableLoadOptions(TableLoadOption.NoUI);
         TaskEditor.open(provider);
      }
   }

}
