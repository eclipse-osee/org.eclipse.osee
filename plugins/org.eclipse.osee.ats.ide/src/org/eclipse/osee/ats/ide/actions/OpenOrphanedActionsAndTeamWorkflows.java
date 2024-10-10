/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenOrphanedActionsAndTeamWorkflows extends Action {

   public OpenOrphanedActionsAndTeamWorkflows() {
      this("Open Orphaned Actions / Team Workflows");
   }

   public OpenOrphanedActionsAndTeamWorkflows(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      if (MessageDialog.openConfirm(Displays.getActiveShell(), getText(), getText())) {

         AbstractOperation operation =
            new org.eclipse.osee.framework.core.operation.AbstractOperation(getText(), Activator.PLUGIN_ID) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  List<ArtifactId> ids =
                     ArtifactQuery.createQueryBuilder(AtsApiService.get().getAtsBranch()).andIsOfType(
                        AtsArtifactTypes.Action).andNotExists(AtsRelationTypes.ActionToWorkflow_Action).getIds();
                  if (ids.isEmpty()) {
                     AWorkbench.popup("No Orphaned Action(s) Found");
                  } else {
                     List<Artifact> artifacts =
                        ArtifactQuery.getArtifactListFrom(ids, AtsApiService.get().getAtsBranch());
                     MassArtifactEditor.editArtifacts("Orphaned Action(s)", artifacts, TableLoadOption.None);
                  }

                  List<ArtifactId> ids2 =
                     ArtifactQuery.createQueryBuilder(AtsApiService.get().getAtsBranch()).andIsOfType(
                        AtsArtifactTypes.TeamWorkflow).andNotExists(
                           AtsRelationTypes.ActionToWorkflow_TeamWorkflow).getIds();
                  if (ids2.isEmpty()) {
                     AWorkbench.popup("No Orphaned Workflow(s) Found");
                  } else {
                     List<Artifact> artifacts =
                        ArtifactQuery.getArtifactListFrom(ids2, AtsApiService.get().getAtsBranch());
                     MassArtifactEditor.editArtifacts("Orphaned Team Workflows", artifacts, TableLoadOption.None);
                  }
               }
            };
         Operations.executeAsJob(operation, true);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.ACTION);
   }

}
