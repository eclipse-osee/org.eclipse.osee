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

package org.eclipse.osee.ats.ide.walker;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.walker.action.ActionWalkerLayoutAction;
import org.eclipse.osee.ats.ide.walker.action.ActionWalkerRefreshAction;
import org.eclipse.osee.ats.ide.walker.action.ActionWalkerShowAllAction;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;

/**
 * @author Donald G. Dunne
 */
public class ActionWalkerView extends GenericViewPart implements IPartListener, IArtifactEventListener, IPerspectiveListener2 {
   public static final String VIEW_ID = "org.eclipse.osee.ats.ide.ActionWalkerView";
   protected GraphViewer viewer;
   private Composite viewerComp;
   private AbstractWorkflowArtifact activeAwa;
   private IActionWalkerItem activeGraphItem;
   private Artifact topAtsArt;

   private boolean showAll = false;
   private final WalkerLayoutManager layoutMgr;

   public ActionWalkerView() {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
      layoutMgr = new WalkerLayoutManager(this);
   }

   @Override
   public void createPartControl(Composite parent) {

      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
         viewerComp = new Composite(parent, SWT.BORDER);
         viewerComp.setLayout(new FillLayout());

         viewer = new GraphViewer(viewerComp, ZestStyles.NONE);
         viewer.setContentProvider(new ActionWalkerContentProvider(this));
         viewer.setLabelProvider(new ActionWalkerLabelProvider());
         viewer.setConnectionStyle(ZestStyles.CONNECTIONS_SOLID);
         viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
         viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
               handleItemDoubleClick(event);
            }

         });
         createActions();
         layoutMgr.start();
         refresh();

         setFocusWidget(viewer.getControl());
         HelpUtil.setHelp(viewer.getControl(), AtsHelpContext.ACTION_VIEW);
         OseeEventManager.addListener(this);
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   public void handleItemDoubleClick(DoubleClickEvent event) {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Iterator<?> itemsIter = selection.iterator();
      while (itemsIter.hasNext()) {
         Object obj = itemsIter.next();
         if (obj instanceof Artifact) {
            Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact(obj);
            if (art.isDeleted()) {
               AWorkbench.popup("ERROR", "Artifact has been deleted");
               return;
            } else {
               AtsEditors.openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
            }
         } else if (obj instanceof IActionWalkerItem) {
            ((IActionWalkerItem) obj).handleDoubleClick();
         }
      }
   }

   public Artifact getTopArtifact(Artifact art) {
      Artifact artifact = null;
      try {
         if (art.isOfType(AtsArtifactTypes.Goal)) {
            artifact = art;
         } else if (art.isOfType(AtsArtifactTypes.Action)) {
            artifact = art;
         } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            artifact = ((TeamWorkFlowArtifact) art).getParentActionArtifact();
         } else if (art.isOfType(AtsArtifactTypes.AgileSprint)) {
            artifact = art;
         } else if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            if (!PeerToPeerReviewManager.isStandAlongReview(art)) {
               Artifact parentArtifact = ((AbstractWorkflowArtifact) art).getParentAWA();
               if (parentArtifact != null && parentArtifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  artifact = ((TeamWorkFlowArtifact) parentArtifact).getParentActionArtifact();
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE, "Unknown parent " + AtsClientService.get().getAtsId(art));
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return artifact;
   }

   private void explore(AbstractWorkflowArtifact awa) {
      if (awa == null) {
         return;
      }
      this.activeAwa = awa;
      this.topAtsArt = getTopArtifact(awa);
      viewer.setInput(topAtsArt);
      highlightActiveAwa();
      setTooltips();
      setPartName("Action View (" + activeAwa.getName() + ")");
   }

   private void setTooltips() {
      try {
         if (!activeAwa.isOfType(AtsArtifactTypes.Goal) && !PeerToPeerReviewManager.isStandAlongReview(activeAwa)) {
            ActionArtifact actionArt = activeAwa.getParentActionArtifact();
            if (actionArt != null) {
               for (TeamWorkFlowArtifact teamArt : actionArt.getTeams()) {
                  GraphItem item = viewer.findGraphItem(teamArt);
                  if (item != null && item instanceof GraphNode) {
                     GraphNode node = (GraphNode) item;
                     node.setTooltip(new Label(getToolTip(teamArt)));
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void highlightActiveAwa() {
      GraphItem item = viewer.findGraphItem(activeAwa);
      if (item != null && item instanceof GraphNode) {
         GraphNode node = (GraphNode) item;
         node.setBackgroundColor(Displays.getSystemColor(SWT.COLOR_CYAN));
         viewer.update(node, null);
      } else if (activeGraphItem != null) {
         item = viewer.findGraphItem(activeGraphItem);
         if (item != null && item instanceof GraphNode) {
            GraphNode node = (GraphNode) item;
            node.setBackgroundColor(Displays.getSystemColor(SWT.COLOR_CYAN));
            viewer.update(node, null);
         }
      }
   }

   private void createActions() {
      IActionBars bars = getViewSite().getActionBars();
      // IMenuManager mm = bars.getMenuManager();
      IToolBarManager tbm = bars.getToolBarManager();

      tbm.add(new ActionWalkerLayoutAction(this));
      tbm.add(new ActionWalkerShowAllAction(this));
      tbm.add(new ActionWalkerRefreshAction(this));
      bars.updateActionBars();
   }

   public void refresh() {
      explore(activeAwa);
   }

   public void processWindowActivated() {
      if (!this.getSite().getPage().isPartVisible(this)) {
         return;
      }
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (page != null) {
         IEditorPart editor = page.getActiveEditor();
         if (editor instanceof WorkflowEditor) {
            explore(((WorkflowEditor) editor).getAwa());
         }
      }
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
      return AtsUtilClient.getAtsObjectEventFilters();
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (sender.isRemote()) {
         return;
      }
      if (activeAwa == null) {
         return;
      }
      if (artifactEvent.isModifiedReloaded(activeAwa) ||
      //
         artifactEvent.isRelAddedChangedDeleted(activeAwa)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               explore(activeAwa);
            }
         });
      }
   }

   public void setShowAll(boolean showAll) {
      this.showAll = showAll;
      refresh();
   }

   public boolean isShowAll() {
      return showAll;
   }

   public WalkerLayoutManager getLayoutMgr() {
      return layoutMgr;
   }

   public void setLayout(LayoutAlgorithm algorithm) {
      viewer.setLayoutAlgorithm(algorithm);
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      layoutMgr.init(site, memento);
   }

   @Override
   public void saveState(IMemento memento) {
      layoutMgr.saveState(memento);
   }

   public String getToolTip(Artifact artifact) {
      if (artifact.isDeleted()) {
         return "";
      }
      StringBuilder builder = new StringBuilder();
      builder.append(" Name: " + artifact.getName());
      builder.append("\n Type: " + artifact.getArtifactTypeName());
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         if (awa instanceof TeamWorkFlowArtifact) {
            try {
               builder.append("\n Team: " + ((TeamWorkFlowArtifact) awa).getTeamDefinition());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         builder.append("\n State: " + ((AbstractWorkflowArtifact) artifact).getStateMgr().getCurrentStateName());
         builder.append("\n Assignee: " + getAssignee(artifact));
         builder.append("\n Version: " + getTargetedVersion(artifact));
      }
      return builder.toString();
   }

   public String getAssignee(Artifact artifact) {
      try {
         if (artifact instanceof AbstractWorkflowArtifact) {
            return AtsObjects.toString("; ", ((AbstractWorkflowArtifact) artifact).getStateMgr().getAssignees());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
      return "";
   }

   private String getTargetedVersion(Artifact artifact) {
      try {
         if (artifact instanceof IAtsWorkItem) {
            String str =
               Versions.getTargetedVersionStr((IAtsWorkItem) artifact, AtsClientService.get().getVersionService());
            return str.isEmpty() ? "" : str;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
      return "";
   }

   public Artifact getTopAtsArt() {
      return topAtsArt;
   }

   public void setActiveGraphItem(IActionWalkerItem activeGraphItem) {
      this.activeGraphItem = activeGraphItem;
   }

   public AbstractWorkflowArtifact getActiveAwa() {
      return activeAwa;
   }

}
