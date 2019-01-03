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

package org.eclipse.osee.ats.ide.config;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(getName());

         // Import Work Definition sheets
         data.getResultData().log("Importing Work Definitions");
         Artifact folder = getWorkDefinitionFolder();
         Set<String> stateNames = new HashSet<>();
         AtsWorkDefinitionSheetProviders.importWorkDefinitionSheets(data.getResultData(), changes, folder,
            data.getWorkDefSheets(), stateNames);

         monitor.worked(calculateWork(0.30));
         data.getResultData().log("Importing Work Definitions...Complete");

         // Import AI and Team sheets
         data.getResultData().log("Importing AIs and Teams");
         for (WorkDefinitionSheet sheet : data.getTeamsAiSheets()) {
            AtsWorkDefinitionSheetProviders.importAIsAndTeamsToDb(sheet, changes);
         }
         monitor.worked(calculateWork(0.40));

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

   private Artifact getWorkDefinitionFolder() {
      Artifact result = null;
      result = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Work Definitions",
         AtsClientService.get().getAtsBranch());
      if (result == null) {
         result = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Action Tracking System",
            AtsClientService.get().getAtsBranch());
      }
      return result;
   }

}
