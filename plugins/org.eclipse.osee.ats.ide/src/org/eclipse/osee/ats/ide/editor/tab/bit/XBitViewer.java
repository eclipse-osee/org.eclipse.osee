/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.bit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XBitViewer extends TaskXViewer {

   protected final IAtsTeamWorkflow crTeamWf;
   protected final AtsApi atsApi;
   private BuildImpactDatas bids;

   public XBitViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor, IAtsTeamWorkflow teamWf) {
      super(parent, style, xViewerFactory, editor, teamWf);
      atsApi = AtsApiService.get();
      crTeamWf = teamWf;
   }

   @Override
   protected boolean isAddTaskEnabled() {
      return false;
   }

   @Override
   public List<Artifact> getSelectedArtifacts() {
      List<Artifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof JaxTeamWorkflow) {
               JaxTeamWorkflow jTeamWf = (JaxTeamWorkflow) item.getData();
               Long id = jTeamWf.getId();
               Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(id);
               if (artifact != null) {
                  arts.add(artifact);
               }
            } else if (item.getData() instanceof BuildImpactData) {
               BuildImpactData bid = (BuildImpactData) item.getData();
               Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(bid.getBidArt());
               if (artifact != null) {
                  arts.add(artifact);
               }
            }
         }
      }
      return arts;
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      Set<Artifact> smaArts = new HashSet<>();
      for (Artifact art : getSelectedArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            smaArts.add(art);
         }
      }
      return smaArts;
   }

   public void loadTable() {
      try {
         if (getContentProvider() != null) {
            setInput(bids);
            refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void handleDoubleClick() {
      List<Artifact> artifacts = getSelectedArtifacts();
      if (!artifacts.isEmpty()) {
         ArtifactDoubleClick.open(artifacts.iterator().next());
      }
   }

   public BuildImpactDatas getBids() {
      return bids;
   }

   public void setBids(BuildImpactDatas bids) {
      this.bids = bids;
   }

   @Override
   protected boolean showTaskMenu() {
      return false;
   }

   @Override
   public boolean isRemoveItemsMenuOptionEnabled() {
      return false;
   }

}
