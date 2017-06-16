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
package org.eclipse.osee.ats.config.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigResultsEditorNavigateItem extends XNavigateItemAction {

   public AtsConfigResultsEditorNavigateItem(XNavigateItem parent) {
      super(parent, "ATS Config Viewer", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      final List<IAtsConfigObject> objs = new ArrayList<>();
      objs.add(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()));
      objs.add(ActionableItems.getTopActionableItem(AtsClientService.get()));
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
