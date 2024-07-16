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

package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactStructuredSelection;
import org.eclipse.osee.framework.ui.skynet.IArtifactExplorerEventHandler;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.explorer.menu.ArtifactExplorerMenu;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.TreeViewerUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactExplorer extends GenericViewPart implements IArtifactExplorerEventHandler, IRebuildMenuListener, IBranchEventListener, ISelectionProvider, IBranchProvider {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.ArtifactExplorer";
   private static final String ROOT_UUID = "artifact.explorer.last.root_uuid";
   private static final String ROOT_BRANCH = "artifact.explorer.last.root_branch";

   private TreeViewer treeViewer;
   private Artifact explorerRoot;
   private TreeEditor myTreeEditor;
   private XBranchSelectWidget branchSelect;
   private BranchToken branch = BranchToken.SENTINEL;

   private ArtifactExplorerDragAndDrop dragAndDropWorker;

   private Composite stackComposite;
   private BranchWarningComposite branchWarningComposite;
   private StackLayout stackLayout;
   private ArtifactDecorator artifactDecorator;
   public ArtifactExplorerMenu artifactExplorerMenu;
   private ArtifactExplorerToolbar artifactExplorerToolbar;
   private boolean refreshing = false;

   private ArtifactExplorerViewApplicability view;
   private ArtifactId viewId = ArtifactId.SENTINEL;

   public static void explore(Collection<Artifact> artifacts) {
      explore(artifacts, AWorkbench.getActivePage());
   }

   private void setErrorString(String str) {
      branchWarningComposite.updateLabel(str);
      stackLayout.topControl = branchWarningComposite;
      stackComposite.layout();
      stackComposite.getParent().layout();
   }

   public static void explore(Collection<Artifact> artifacts, IWorkbenchPage page) {
      Artifact sampleArtifact = null;
      BranchId inputBranch = BranchId.SENTINEL;
      if (artifacts != null && !artifacts.isEmpty()) {
         sampleArtifact = artifacts.iterator().next();
         inputBranch = sampleArtifact.getBranch();
      }
      ArtifactExplorer artifactExplorer = ArtifactExplorerUtil.findView(inputBranch, page);
      artifactExplorer.setPartName("Artifact Explorer");
      artifactExplorer.treeViewer.setInput(artifacts);
      artifactExplorer.initializeSelectionBox();
   }

   @Override
   public void createPartControl(Composite parent) {
      try {
         if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

            // TODO: Trigger User Loading to prevent lock up -- Need to remove this once accessControlService based
            UserManager.getUser();

            Composite comp = new Composite(parent, SWT.BORDER);
            comp.setLayout(new GridLayout(1, false));
            comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            branchSelect = new XBranchSelectWidget("");
            branchSelect.setDisplayLabel(false);
            branchSelect.setSelection(branch);
            branchSelect.createWidgets(comp, 1);

            branchSelect.addListener(new Listener() {
               @Override
               public void handleEvent(Event event) {
                  try {
                     BranchToken selectedBranch = branchSelect.getData();
                     if (selectedBranch != null && !BranchId.SENTINEL.equals(selectedBranch)) {
                        branch = selectedBranch;
                        dragAndDropWorker.updateBranch(branch);
                        explore(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch));
                        refreshView();
                     }
                  } catch (Exception ex) {
                     setErrorString("Error loading branch (see error log for details): " + ex.getLocalizedMessage());
                     OseeLog.log(getClass(), Level.SEVERE, ex);
                  }
               }

            });

            view = new ArtifactExplorerViewApplicability(comp, this);
            view.create();

            stackComposite = new Composite(comp, SWT.NONE);
            stackLayout = new StackLayout();
            stackComposite.setLayout(stackLayout);
            stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            branchWarningComposite = new BranchWarningComposite(stackComposite);

            treeViewer = new TreeViewer(stackComposite);
            Tree tree = treeViewer.getTree();
            final ArtifactExplorer fArtExplorere = this;
            tree.addDisposeListener(new DisposeListener() {

               @Override
               public void widgetDisposed(DisposeEvent e) {
                  ArtifactExplorerEventManager.remove(fArtExplorere);
               }
            });
            artifactDecorator = new ArtifactDecorator(Activator.ARTIFACT_EXPLORER_ATTRIBUTES_PREF);

            treeViewer.setContentProvider(new ArtifactContentProvider(artifactDecorator));
            treeViewer.setLabelProvider(new ArtifactLabelProvider(artifactDecorator));
            treeViewer.addDoubleClickListener(new ArtifactDoubleClick());
            treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

               @Override
               public void selectionChanged(SelectionChangedEvent event) {
                  Tree viewer = treeViewer.getTree();
                  if (viewer != null && !viewer.isDisposed()) {
                     viewer.redraw();
                  }
                  Control control = treeViewer.getControl();
                  if (control != null && !control.isDisposed()) {
                     control.redraw();
                  }
               }
            });

            /**
             * We can not use the hash lookup because an artifact may not have a good equals. This can be added back
             * once the content provider is converted over to use job node.
             */
            treeViewer.setUseHashlookup(false);

            treeViewer.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));

            artifactExplorerToolbar = new ArtifactExplorerToolbar(this);
            artifactExplorerToolbar.createToolbar();

            artifactDecorator.setViewer(treeViewer);
            artifactDecorator.addActions(getViewSite().getActionBars().getMenuManager(), this);

            getSite().setSelectionProvider(treeViewer);
            addExploreSelection();

            artifactExplorerMenu = new ArtifactExplorerMenu(this);
            artifactExplorerMenu.setupPopupMenu();

            myTreeEditor = new TreeEditor(getTreeViewer().getTree());
            myTreeEditor.horizontalAlignment = SWT.LEFT;
            myTreeEditor.grabHorizontal = true;
            myTreeEditor.minimumWidth = 50;

            dragAndDropWorker = new ArtifactExplorerDragAndDrop(treeViewer, VIEW_ID, this, branch);

            OseeStatusContributionItemFactory.addTo(this, false);

            artifactExplorerToolbar.updateEnablement();
            HelpUtil.setHelp(treeViewer.getControl(), OseeHelpContext.ARTIFACT_EXPLORER);

            refreshBranchWarning();

            getViewSite().getActionBars().updateActionBars();
            setFocusWidget(treeViewer.getControl());

            OseeEventManager.addListener(this);
            ArtifactExplorerEventManager.add(this);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   public void refreshBranchWarning() {
      ArtifactExplorerUtil.refreshBranchWarning(this, treeViewer, getBranch(), branchWarningComposite);
   }

   /**
    * Reveal an artifact in the viewer and select it.
    */
   public static void exploreBranch(BranchId branch) {
      if (branch != null) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         ArtifactExplorerUtil.findView(branch, page);
      }
   }

   public void explore(Artifact artifact) {
      if (artifact == null) {
         throw new IllegalArgumentException("Can not explore a null artifact.");
      }

      setPartName("Artifact Explorer: " + artifact.getBranchToken().getShortName());
      if (branch.isValid() && !artifact.isOnBranch(branch)) {
         explore(Arrays.asList(artifact));
         return;
      }

      explorerRoot = artifact;
      branch = artifact.getBranchToken();

      if (dragAndDropWorker != null) {
         dragAndDropWorker.updateBranch(branch);
      }

      refreshBranchWarning();

      initializeSelectionBox();

      if (treeViewer != null) {
         Object objects[] = treeViewer.getExpandedElements();
         treeViewer.setInput(explorerRoot);
         artifactExplorerMenu.setupPopupMenu();
         artifactExplorerToolbar.updateEnablement();
         // Attempt to re-expand what was expanded
         treeViewer.setExpandedElements(objects);
      }
   }

   public void setExpandedArtifacts(Object... artifacts) {
      if (treeViewer != null) {
         treeViewer.setExpandedElements(artifacts);
      }
   }

   /**
    * Add the selection from the define explorer
    */
   private void addExploreSelection() {
      if (explorerRoot != null) {
         try {
            treeViewer.setInput(explorerRoot);
            initializeSelectionBox();
         } catch (IllegalArgumentException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (DbConnectionExceptionComposite.dbConnectionIsOk()) {
         try {
            if (memento != null && memento.getString(ROOT_UUID) != null && memento.getString(ROOT_BRANCH) != null) {
               BranchId branch = BranchId.valueOf(memento.getString(ROOT_BRANCH));

               if (BranchManager.branchExists(
                  branch) && !BranchManager.isArchived(branch) || ServiceUtil.accessControlService().isOseeAdmin()) {
                  Artifact previousArtifact =
                     ArtifactQuery.checkArtifactFromId(ArtifactId.valueOf(memento.getString(ROOT_UUID)), branch);
                  if (previousArtifact != null) {
                     explore(previousArtifact);
                  } else {
                     /*
                      * simply means that the previous artifact that was used as the root for the artiactExplorer does
                      * not exist because it was deleted or this workspace was last used with a different branch or
                      * database, so let the logic below get the default hierarchy root artifact
                      */
                  }
                  return;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (DbConnectionExceptionComposite.dbConnectionIsOk()) {
         if (explorerRoot != null) {
            memento.putString(ROOT_UUID, explorerRoot.getIdString());
            try {
               memento.putString(ROOT_BRANCH, explorerRoot.getBranch().getIdString());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      ArtifactExplorerEventManager.remove(this);
      if (artifactExplorerMenu != null) {
         artifactExplorerMenu.dispose();
      }
      super.dispose();
   }

   @Override
   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      treeViewer.addSelectionChangedListener(listener);
   }

   @Override
   public ArtifactStructuredSelection getSelection() {
      final List<Artifact> selectedItems = new LinkedList<>();
      TreeViewerUtility.getPreorderSelection(treeViewer, selectedItems);
      return new ArtifactStructuredSelection(selectedItems);
   }

   @Override
   public void removeSelectionChangedListener(ISelectionChangedListener listener) {
      treeViewer.removeSelectionChangedListener(listener);
   }

   @Override
   public void setSelection(ISelection selection) {
      treeViewer.setSelection(selection);
   }

   @Override
   public void rebuildMenu() {
      artifactExplorerMenu.setupPopupMenu();
   }

   public void setBranch(BranchToken branch) {
      this.branch = branch;
   }

   public void initializeSelectionBox() {
      if (branch != null && branchSelect != null && branch.notEqual(branchSelect.getData())) {
         branchSelect.setSelection(branch);
         refreshBranchWarning();
         refreshView();
      }
   }

   private void refreshView() {
      setViewId(branch.getViewId());
      if (view != null) {
         view.refresh();
      }
   }

   @Override
   public BranchToken getBranch() {
      return branch;
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      if (branch == null) {
         return;
      }
      if (branch.equals(branchEvent.getSourceBranch())) {
         if (branchEvent.getEventType() == BranchEventType.Committing || branchEvent.getEventType() == BranchEventType.Committed) {
            SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
         } else {
            refreshBranchWarning();
         }
      } else if (branch.equals(branchEvent.getDestinationBranch())) {
         if (branchEvent.getEventType() == BranchEventType.Committed) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  getTreeViewer().refresh();
               }
            });
         }
      }
   }

   public TreeViewer getTreeViewer() {
      return treeViewer;
   }

   public void setTreeViewer(TreeViewer treeViewer) {
      this.treeViewer = treeViewer;
   }

   @Override
   public ArtifactExplorer getArtifactExplorer() {
      return this;
   }

   public static List<ArtifactExplorer> getEditors() {
      List<ArtifactExplorer> results = new ArrayList<>();
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IViewReference views[] = page.getViewReferences();
      for (IViewReference view : views) {
         if (view.getPart(false) instanceof ArtifactExplorer) {
            results.add((ArtifactExplorer) view.getPart(false));
         }
      }
      return results;
   }

   @Override
   public boolean isDisposed() {
      return treeViewer.getTree() == null || treeViewer.getTree().isDisposed();
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      if (branch != null) {
         return OseeEventManager.getEventFiltersForBranch(branch);
      }
      return null;
   }

   public Artifact getExplorerRoot() {
      return explorerRoot;
   }

   public TreeEditor getMyTreeEditor() {
      return myTreeEditor;
   }

   public Label getBranchWarningLabel() {
      return branchWarningComposite.getBranchWarningLabel();
   }

   public StackLayout getStackLayout() {
      return stackLayout;
   }

   public Composite getStackComposite() {
      return stackComposite;
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   public void setViewId(ArtifactId viewId) {
      this.viewId = viewId;
   }

   public boolean isRefreshing() {
      return refreshing;
   }

   public void setRefreshing(boolean refreshing) {
      this.refreshing = refreshing;
   }

   public void resetMenu() {
      artifactExplorerMenu.resetMenu();
   }

}
