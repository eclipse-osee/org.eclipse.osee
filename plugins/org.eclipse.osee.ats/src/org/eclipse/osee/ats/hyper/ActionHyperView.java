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
package org.eclipse.osee.ats.hyper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.artifact.AbstractAtsArtifact;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class ActionHyperView extends HyperView implements IActionable, IArtifactEventListener, IPerspectiveListener2 {

   public final static String VIEW_ID = "org.eclipse.osee.ats.hyper.ActionHyperView";
   private final static String HELP_CONTEXT_ID = "atsActionView";
   private AbstractAtsArtifact currentArtifact;
   private Cursor cursor;

   public ActionHyperView() {
      super();
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
      HyperView.setNodeColor(whiteColor);
      HyperView.setCenterColor(whiteColor);
      setVerticalSelection(45);
   }

   @Override
   public void createPartControl(Composite top) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(top)) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(this);
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePerspectiveListener(this);
         return;
      }
      AtsBulkLoad.loadConfig(false);
      super.createPartControl(top);
      OseeUiActions.addBugToViewToolbar(this, this, AtsPlugin.PLUGIN_ID, VIEW_ID, "SkyWalker");
      HelpUtil.setHelp(top, HELP_CONTEXT_ID, "org.eclipse.osee.ats.help.ui");
      HelpUtil.setHelp(composite, HELP_CONTEXT_ID, "org.eclipse.osee.ats.help.ui");
      OseeEventManager.addListener(this);
   }

   public static ActionHyperView getArtifactHyperView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         IViewReference ivr = page.findViewReference(ActionHyperView.VIEW_ID);
         if (ivr != null && page.isPartVisible(ivr.getPart(false))) {
            return (ActionHyperView) page.showView(ActionHyperView.VIEW_ID);
         }
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't Get OSEE Hyper View " + e1.getMessage());
      }
      return null;
   }

   @Override
   public boolean provideBackForwardActions() {
      return false;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void handleItemDoubleClick(HyperViewItem hvi) {
      if (hvi instanceof ActionHyperItem) {
         Artifact art = ((ActionHyperItem) hvi).getArtifact();
         if (art == null) {
            ((ActionHyperItem) hvi).handleDoubleClick(hvi);

         } else if (art.isDeleted()) {
            AWorkbench.popup("ERROR", "Artifact has been deleted");
            return;
         } else {
            AtsUtil.openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         }
      }
   }

   private boolean tasksReviewsCreated = false;

   @Override
   public void display() {
      try {
         if (cursor == null) {
            cursor = new Cursor(null, SWT.NONE);
         }
         getContainer().setCursor(cursor);
         if (currentArtifact == null || currentArtifact.isDeleted()) {
            return;
         }
         boolean reviewsCreated = false;
         tasksReviewsCreated = false;
         AbstractAtsArtifact topArt = getTopArtifact(currentArtifact);
         if (topArt == null || topArt.isDeleted()) {
            return;
         }
         ActionHyperItem topAHI = new ActionHyperItem(topArt);
         if (topArt instanceof ActionArtifact) {
            for (TeamWorkFlowArtifact team : ((ActionArtifact) topArt).getTeamWorkFlowArtifacts()) {
               ActionHyperItem teamAHI = new ActionHyperItem(team);
               teamAHI.setRelationToolTip("Team");
               topAHI.addBottom(teamAHI);
               for (AbstractReviewArtifact rev : ReviewManager.getReviews(team)) {
                  reviewsCreated = true;
                  ActionHyperItem reviewAHI = new ActionHyperItem(rev);
                  reviewAHI.setRelationToolTip("Review");
                  teamAHI.addBottom(reviewAHI);
                  addTasksAHIs(reviewAHI, rev);
               }
               addTasksAHIs(teamAHI, team);
            }
         }
         if (topArt instanceof GoalArtifact) {
            List<TaskArtifact> taskArts = new ArrayList<TaskArtifact>();
            for (Artifact member : topArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member)) {
               if (member instanceof TaskArtifact) {
                  taskArts.add((TaskArtifact) member);
               } else if (member instanceof IATSArtifact) {
                  ActionHyperItem teamAHI = new ActionHyperItem((IATSArtifact) member);
                  teamAHI.setRelationToolTip("Member");
                  topAHI.addBottom(teamAHI);
               }
            }
            if (taskArts.size() > 0) {
               topAHI.addBottom(new TasksActionHyperItem(taskArts));
            }
         }

         if (activeEditorIsActionEditor()) {
            topAHI.calculateCurrent(currentArtifact);
         }
         if (tasksReviewsCreated) {
            setVerticalSelection(50);
         } else if (reviewsCreated) {
            setVerticalSelection(47);
         } else {
            setVerticalSelection(45);
         }
         create(topAHI);
      } catch (OseeCoreException ex) {
         clear();
      }
   }

   private void addTasksAHIs(ActionHyperItem parentAHI, AbstractAtsArtifact artifact) throws OseeCoreException {
      if (!(artifact instanceof AbstractTaskableArtifact)) {
         return;
      }
      if (((AbstractTaskableArtifact) artifact).getTaskArtifacts().size() > 0) {
         if (artifact instanceof AbstractReviewArtifact) {
            tasksReviewsCreated = true;
         }
         parentAHI.addBottom(new TasksActionHyperItem(((AbstractTaskableArtifact) artifact).getTaskArtifacts()));
      }
   }

   public AbstractAtsArtifact getTopArtifact(AbstractAtsArtifact art) throws OseeCoreException {
      AbstractAtsArtifact artifact = art;
      if (artifact instanceof TaskArtifact) {
         artifact = ((TaskArtifact) artifact).getParentSMA();
      }
      if (artifact instanceof TeamWorkFlowArtifact) {
         artifact = ((TeamWorkFlowArtifact) artifact).getParentActionArtifact();
      }
      if (artifact instanceof AbstractReviewArtifact && ((AbstractReviewArtifact) artifact).getParentActionArtifact() != null) {
         artifact = ((AbstractReviewArtifact) artifact).getParentActionArtifact();
      }
      if (artifact == null) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Unknown parent " + art.getHumanReadableId());
      }
      return artifact;
   }

   public boolean activeEditorIsActionEditor() {
      IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
      if (page == null) {
         return false;
      }
      return page.getActiveEditor() instanceof SMAEditor;
   }

   public void processWindowActivated() {
      if (!this.getSite().getPage().isPartVisible(this)) {
         return;
      }
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (page != null) {
         IEditorPart editor = page.getActiveEditor();
         if (editor instanceof SMAEditor) {
            currentArtifact = ((SMAEditor) editor).getSma();
            display();
         } else if (currentArtifact != null && currentArtifact.isDeleted()) {
            super.clear();
         }
      }
   }

   public void processWindowDeActivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partActivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partBroughtToTop(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partClosed(IWorkbenchPart part) {
      if (part.equals(this)) {
         dispose();
      } else {
         processWindowActivated();
      }
   }

   @Override
   public void partDeactivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partOpened(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public String getActionDescription() {
      if (currentArtifact != null && currentArtifact.isDeleted()) {
         return String.format("Current Artifact - %s - %s", currentArtifact.getGuid(), currentArtifact.getName());
      }
      return "";
   }

   @Override
   public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      processWindowActivated();
   }

   @Override
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
      processWindowActivated();
   }

   @Override
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
      processWindowActivated();
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtil.getAtsObjectEventFilters();
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (currentArtifact == null) {
         return;
      }
      if (artifactEvent.isDeletedPurged(currentArtifact)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               clear();
            }
         });
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            display();
         }
      });
   }
}
