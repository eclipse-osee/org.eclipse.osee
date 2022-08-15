/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.config.wizard;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.config.AtsConfigOperation;
import org.eclipse.osee.ats.ide.config.AtsConfigOperation.Display;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigWizard extends Wizard implements INewWizard {

   private AtsConfigWizardPage1 page1;

   @Override
   public void addPages() {
      page1 = new AtsConfigWizardPage1();
      addPage(page1);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      // do nothing
   }

   @Override
   public boolean performFinish() {
      try {
         String teamDefName = page1.getTeamDefName();
         Collection<String> aias = page1.getActionableItems();
         Collection<String> versionNames = page1.getVersions();
         String workDefName = page1.getWorkDefinitionName();

         AtsConfigOperation.Display display = new OpenAtsConfigEditors();
         AtsConfigOperation operation = new AtsConfigOperation(workDefName, teamDefName, versionNames, aias);
         Operations.executeWork(operation);

         display.openAtsConfigurationEditors(operation.getTeamDefinition(), operation.getActionableItems(),
            operation.getWorkDefinition());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
      return true;
   }

   private static final class OpenAtsConfigEditors implements Display {

      @Override
      public void openAtsConfigurationEditors(final IAtsTeamDefinition teamDef, final Collection<IAtsActionableItem> aias, final WorkDefinition workDefinition) {
         Job job = new UIJob("Open Ats Configuration Editors") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  AtsEditors.openATSAction(AtsApiService.get().getQueryService().getArtifact(teamDef),
                     AtsOpenOption.OpenAll);
                  for (IAtsActionableItem aia : aias) {
                     AtsEditors.openATSAction(AtsApiService.get().getQueryService().getArtifact(aia),
                        AtsOpenOption.OpenAll);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job, true);
      }
   }
}
