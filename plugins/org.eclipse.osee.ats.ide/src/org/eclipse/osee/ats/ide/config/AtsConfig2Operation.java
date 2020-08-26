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

package org.eclipse.osee.ats.ide.config;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Imports team/ai information from xtext importer. Imports work definitions from xtext importer. AtsConfg2Data provides
 * performPostConfig method to allow for extra changes to be made before commit.</br>
 * Implement AtsConfig2Data and run this operation.
 *
 * @author Donald G. Dunne
 */
public class AtsConfig2Operation extends AbstractOperation {

   private final AbstractAtsConfig2Data data;

   public AtsConfig2Operation(AbstractAtsConfig2Data data) {
      super("Configure Ats for " + data.getConfigName(), Activator.PLUGIN_ID);
      this.data = data;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      data.validate();
      data.getResultData().log(getName() + "\n");
      monitor.worked(calculateWork(0.20));

      try {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getName());

         // Perform specialized configuration code (eg set extra attributes, create branches, etc)
         data.performPostConfig(changes, data);

         if (data.getResultData().isErrors()) {
            String errorStr = "Errors found, not persisting.  May need to restart your OSEE";
            data.getResultData().log(errorStr);
            AWorkbench.popup(errorStr);
         } else {
            changes.execute();
         }
      } catch (Exception ex) {
         data.getResultData().error("Exception " + ex.getLocalizedMessage());
      }
      XResultDataUI.report(data.getResultData(), getName());
      monitor.worked(calculateWork(0.10));
   }

}
