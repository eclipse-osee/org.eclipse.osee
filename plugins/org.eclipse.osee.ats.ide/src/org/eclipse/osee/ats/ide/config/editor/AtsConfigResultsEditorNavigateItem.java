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

package org.eclipse.osee.ats.ide.config.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigResultsEditorNavigateItem extends XNavigateItemAction {

   public AtsConfigResultsEditorNavigateItem() {
      super("ATS Config Viewer", AtsImage.REPORT, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      final List<IAtsConfigObject> objs = new ArrayList<>();
      objs.add(AtsApiService.get().getTeamDefinitionService().getTopTeamDefinition());
      objs.add(AtsApiService.get().getActionableItemService().getTopActionableItem(AtsApiService.get()));
      Job job = new Job("ATS Config Viewer") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            AtsConfigResultsEditor editor = new AtsConfigResultsEditor("ATS Config Viewer", Activator.PLUGIN_ID, objs);
            return Operations.executeWork(editor);
         }
      };
      job.setPriority(Job.SHORT);
      job.schedule();
   }

}
