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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.world.mini.XMiniWorldWidget;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Table showing siblings and roll-up for Team Workflow
 *
 * @author Donald G. Dunne
 */
public class XSiblingWorldWidget extends XMiniWorldWidget implements ArtifactWidget {

   public static final String WIDGET_ID = XSiblingWorldWidget.class.getSimpleName();
   private IAtsTeamWorkflow teamWf;

   public XSiblingWorldWidget() {
      super("Sibling Workflows", new XSiblingXViewerFactory());
   }

   @Override
   public ToolBar createActionBar(Composite tableComp) {
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

}
