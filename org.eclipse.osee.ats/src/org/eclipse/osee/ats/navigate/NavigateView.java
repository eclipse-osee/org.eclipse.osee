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
package org.eclipse.osee.ats.navigate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.ats.world.search.MultipleHridSearchItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class NavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.ats.navigate.NavigateView";
   public static final String HELP_CONTEXT_ID = "atsNavigator";
   private ActionDebug debug = new ActionDebug(false, "NavigateView");
   private AtsNavigateComposite xNavComp;

   /**
    * The constructor.
    */
   public NavigateView() {
   }

   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   public void createPartControl(Composite parent) {
      debug.report("createPartControl");

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      SkynetContributionItem.addTo(this, false);

      xNavComp = new AtsNavigateComposite(new AtsNavigateViewItems(), parent, SWT.NONE);

      AtsPlugin.getInstance().setHelp(xNavComp, HELP_CONTEXT_ID);
      createActions();
      xNavComp.refresh();
      xNavComp.getFilteredTree().getFilterControl().setFocus();
   }

   protected void createActions() {
      debug.report("createActions");

      Action collapseAction = new Action("Collapse All") {

         public void run() {
            xNavComp.getFilteredTree().getViewer().collapseAll();
         }
      };
      collapseAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("collapseAll.gif"));
      collapseAction.setToolTipText("Collapse All");

      Action refreshAction = new Action("Refresh") {

         public void run() {
            xNavComp.refresh();
         }
      };
      refreshAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      Action openByIdAction = new Action("Open by Id") {

         public void run() {
            MultipleHridSearchItem srch = new MultipleHridSearchItem();
            try {
               Collection<Artifact> artifacts = srch.performSearchGetResults(true);
               final Set<Artifact> addedArts = new HashSet<Artifact>();
               for (Artifact artifact : artifacts) {
                  if ((!(artifact instanceof ActionArtifact)) && (!(artifact instanceof StateMachineArtifact))) {
                     ArtifactEditor.editArtifact(artifact);
                     continue;
                  } else
                     addedArts.add(artifact);
               }
               WorldView.getWorldView().load("Open by Id: \"" + srch.getEnteredIds() + "\"", addedArts);
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };
      openByIdAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("openId.gif"));
      openByIdAction.setToolTipText("Open by Id");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(collapseAction);
      toolbarManager.add(refreshAction);
      toolbarManager.add(openByIdAction);
      toolbarManager.add(new NewAction());

      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "ATS Navigator");

   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) return String.format("Currently Selected - %s",
            ((XNavigateItem) sel.iterator().next()).getName());
      return "";
   }

}