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
package org.eclipse.osee.ats.ide.workflow.cr.sibling;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.world.mini.XMiniWorldWidget;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Table showing siblings and roll-up for Team Workflow
 *
 * @author Donald G. Dunne
 */
public abstract class XSiblingWorldWidget extends XMiniWorldWidget implements ArtifactWidget, IArtifactEventListener {

   protected IAtsTeamWorkflow teamWf;

   public XSiblingWorldWidget(XSiblingXViewerFactory xSiblingXViewerFactory) {
      super("Sibling Workflows", xSiblingXViewerFactory);
      OseeEventManager.addListener(this);
   }

   @Override
   public ToolBar createActionBar(Composite tableComp) {
      final XSiblingWorldWidget fWidget = this;
      tableComp.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            OseeEventManager.removeListener(fWidget);
         }
      });
      XSiblingActionBar actionBar = new XSiblingActionBar(this);
      ToolBar toolBar = actionBar.createTaskActionBar(tableComp);
      return toolBar;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItems() {
      Collection<IAtsTeamWorkflow> teamWfs = atsApi.getWorkItemService().getTeams(teamWf.getParentAction());
      teamWfs.remove(teamWf);
      return Collections.castAll(teamWfs);
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

   public IAtsTeamWorkflow getTeamWf() {
      return teamWf;
   }

   @Override
   public Pair<Integer, String> getExtraInfoString() {
      return new Pair<Integer, String>(SWT.COLOR_BLACK, "Edit (most) fields here or double-click to open.");
   }

   @Override
   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            superRefresh();
         }
      });
   }

   private void superRefresh() {
      super.refresh();
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(AtsUtilClient.getAtsBranchFilter());
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      try {
         // Handle case where new sibling created/deleted
         if (artifactEvent.isHasEvent((Artifact) teamWf.getParentAction().getStoreObject())) {
            refresh();
            return;
         }
         // Handle case where sibling changed
         for (IAtsTeamWorkflow siblingWf : atsApi.getActionService().getSiblingTeamWorkflows(teamWf)) {
            if (artifactEvent.isHasEvent((Artifact) siblingWf.getStoreObject())) {
               refresh();
               return;
            }
            if (artifactEvent.isReloaded((Artifact) siblingWf.getStoreObject())) {
               refresh();
               return;
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
