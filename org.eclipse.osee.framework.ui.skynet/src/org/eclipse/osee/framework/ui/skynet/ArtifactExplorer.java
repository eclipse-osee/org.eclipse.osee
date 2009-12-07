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

package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData.ChangeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactNameConflictHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPasteOperation;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.dialogs.ArtifactPasteSpecialDialog;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactTreeViewerGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactClipboard;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTypeFilteredTreeEntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.swt.MenuItems;
import org.eclipse.osee.framework.ui.swt.TreeViewerUtility;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ExportResourcesAction;
import org.eclipse.ui.actions.ImportResourcesAction;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactExplorer extends ViewPart implements IRebuildMenuListener, IAccessControlEventListener, IRelationModifiedEventListener, IArtifactModifiedEventListener, IFrameworkTransactionEventListener, IBranchEventListener, IArtifactsPurgedEventListener, IArtifactsChangeTypeEventListener, IActionable, ISelectionProvider, IBranchProvider {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.ArtifactExplorer";
   private static final String ROOT_GUID = "artifact.explorer.last.root_guid";
   private static final String ROOT_BRANCH = "artifact.explorer.last.root_branch";
   private static final ArtifactClipboard artifactClipboard = new ArtifactClipboard(VIEW_ID);
   private static final LinkedList<Tree> trees = new LinkedList<Tree>();

   private TreeViewer treeViewer;
   private Action upAction;
   private Artifact exploreRoot;
   private MenuItem openMenuItem;
   private MenuItem massEditMenuItem;
   private MenuItem skywalkerMenuItem;
   private MenuItem createMenuItem;
   private MenuItem openWithMenuItem;
   private MenuItem accessControlMenuItem;
   private MenuItem lockMenuItem;
   private MenuItem goIntoMenuItem;
   private MenuItem copyMenuItem;
   private MenuItem pasteMenuItem;
   private MenuItem pasteSpecialMenuItem;
   private MenuItem renameArtifactMenuItem;
   private MenuItem findOnAnotherBranch;
   private NeedArtifactMenuListener needArtifactListener;
   private NeedProjectMenuListener needProjectListener;
   private Tree myTree;
   private TreeEditor myTreeEditor;
   private Text myTextBeingRenamed;
   private Action newArtifactExplorer;
   private Action collapseAllAction;
   private Action showChangeReport;
   private XBranchSelectWidget branchSelect;
   private Branch branch;
   private IGlobalMenuHelper globalMenuHelper;

   private Composite stackComposite;
   private Control branchUnreadableWarning;
   private StackLayout stackLayout;

   public ArtifactExplorer() {
   }

   public static void explore(Collection<Artifact> artifacts) {
      explore(artifacts, AWorkbench.getActivePage());
   }

   public static void explore(Collection<Artifact> artifacts, IWorkbenchPage page) {
      Artifact sampleArtifact = null;
      Branch inputBranch = null;
      if (artifacts != null && !artifacts.isEmpty()) {
         sampleArtifact = artifacts.iterator().next();
         inputBranch = sampleArtifact.getBranch();
      }
      ArtifactExplorer artifactExplorer = findView(inputBranch, page);

      artifactExplorer.setPartName("Artifacts");
      artifactExplorer.setContentDescription("These artifacts could not be handled");
      artifactExplorer.treeViewer.setInput(artifacts);
      artifactExplorer.initializeSelectionBox();
   }

   private static ArtifactExplorer findView(Branch inputBranch, IWorkbenchPage page) {
      for (IViewReference view : page.getViewReferences()) {
         if (view.getId().equals(ArtifactExplorer.VIEW_ID)) {
            if (view.getView(false) != null && inputBranch.equals(((ArtifactExplorer) view.getView(false)).branch)) {
               try {
                  return (ArtifactExplorer) page.showView(view.getId(), view.getSecondaryId(),
                        IWorkbenchPage.VIEW_ACTIVATE);
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            }
         }
      }
      try {
         ArtifactExplorer explorer =
               (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID, GUID.create(), IWorkbenchPage.VIEW_ACTIVATE);
         explorer.explore(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(inputBranch));
         return explorer;
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   private Control createDefaultWarning(Composite parent) {
      Composite composite = new Composite(parent, SWT.BORDER);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      Label image = new Label(composite, SWT.NONE);
      image.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
      image.setImage(ImageManager.getImage(FrameworkImage.LOCKED_NO_ACCESS));
      image.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      Label label = new Label(composite, SWT.NONE);
      Font font = new Font(PlatformUI.getWorkbench().getDisplay(), "Courier New", 10, SWT.BOLD);
      label.setFont(font);
      label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
      label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
      label.setText("Branch Read Access Denied.\nContact your administrator.");
      label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      return composite;
   }

   private void checkBranchReadable() throws OseeCoreException {
      if (treeViewer == null) {
         return;
      }
      Control control = branchUnreadableWarning;
      if (branch == null || new GlobalMenuPermissions(globalMenuHelper).isBranchReadable(branch)) {
         control = treeViewer.getTree();
      }
      stackLayout.topControl = control;
      stackComposite.layout();
      stackComposite.getParent().layout();
   }

   @Override
   public void createPartControl(Composite parent) {
      try {
         if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
            return;
         }

         GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
         gridData.heightHint = 1000;
         gridData.widthHint = 1000;

         parent.setLayout(new GridLayout(1, false));
         parent.setLayoutData(gridData);

         branchSelect = new XBranchSelectWidget("");
         branchSelect.setDisplayLabel(false);
         branchSelect.setSelection(branch);
         branchSelect.createWidgets(parent, 1);

         branchSelect.addListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
               try {
                  Branch selectedBranch = branchSelect.getData();
                  if (selectedBranch != null) {
                     branch = selectedBranch;
                     explore(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch));
                  }
               } catch (Exception ex) {
                  OseeLog.log(getClass(), Level.SEVERE, ex);
               }
            }

         });

         stackComposite = new Composite(parent, SWT.NONE);
         stackLayout = new StackLayout();
         stackComposite.setLayout(stackLayout);
         stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         branchUnreadableWarning = createDefaultWarning(stackComposite);

         treeViewer = new TreeViewer(stackComposite);
         myTree = treeViewer.getTree();
         Tree tree = treeViewer.getTree();
         treeViewer.setContentProvider(new ArtifactContentProvider());

         ArtifactDecorator artifactDecorator =
               new ArtifactDecorator(treeViewer, SkynetGuiPlugin.ARTIFACT_EXPLORER_ATTRIBUTES_PREF);
         treeViewer.setLabelProvider(new ArtifactLabelProvider(artifactDecorator));
         treeViewer.addDoubleClickListener(new ArtifactDoubleClick());
         treeViewer.getControl().setLayoutData(gridData);

         // We can not use the hash lookup because an artifact may not have a
         // good equals.
         // This can be added back once the content provider is converted over to
         // use job node.
         treeViewer.setUseHashlookup(false);

         treeViewer.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));
         globalMenuHelper = new ArtifactTreeViewerGlobalMenuHelper(treeViewer);

         IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
         createCollapseAllAction(toolbarManager);
         createUpAction(toolbarManager);
         createNewArtifactExplorerAction(toolbarManager);
         createShowChangeReportAction(toolbarManager);
         addOpenQuickSearchAction(toolbarManager);

         artifactDecorator.addActions(getViewSite().getActionBars().getMenuManager(), this);

         getSite().setSelectionProvider(treeViewer);
         addExploreSelection();

         setupPopupMenu();

         myTreeEditor = new TreeEditor(myTree);
         myTreeEditor.horizontalAlignment = SWT.LEFT;
         myTreeEditor.grabHorizontal = true;
         myTreeEditor.minimumWidth = 50;

         new ArtifactExplorerDragAndDrop(treeViewer, VIEW_ID, this);
         getViewSite().getActionBars().updateActionBars();

         OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Artifact Explorer");

         OseeContributionItem.addTo(this, false);

         updateEnablementsEtAl();
         trees.add(tree);
         setHelpContexts();

         checkBranchReadable();
         getViewSite().getActionBars().updateActionBars();

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      OseeEventManager.addListener(this);
   }

   /**
    * Reveal an artifact in the viewer and select it.
    * 
    * @param artifact
    */
   public static void revealArtifact(Artifact artifact) {
      try {
         if (artifact.isDeleted()) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                  "The artifact " + artifact.getName() + " has been deleted.");
         } else {
            if (artifact.isHistorical()) {
               artifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), artifact.getBranch(), false);
            }
            if (artifact.isOrphan()) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                     "The artifact " + artifact.getName() + " does not have a parent (orphan).");
            } else {
               IWorkbenchPage page = AWorkbench.getActivePage();
               ArtifactExplorer artifactExplorer = findView(artifact.getBranch(), page);
               artifactExplorer.treeViewer.setSelection(new StructuredSelection(artifact), true);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Reveal an artifact in the viewer and select it.
    * 
    * @param artifact
    */
   public static void exploreBranch(Branch branch) {
      if (branch != null) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         findView(branch, page);
      }
   }

   public void setupPopupMenu() {

      Menu popupMenu = new Menu(treeViewer.getTree().getParent());
      needArtifactListener = new NeedArtifactMenuListener();
      needProjectListener = new NeedProjectMenuListener();
      popupMenu.addMenuListener(needArtifactListener);
      popupMenu.addMenuListener(needProjectListener);

      createOpenMenuItem(popupMenu);
      createOpenWithMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createFindOnDifferentBranchItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createNewItemMenuItem(popupMenu);
      createGoIntoMenuItem(popupMenu);
      createMassEditMenuItem(popupMenu);
      createSkywalkerMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      new GlobalMenu(popupMenu, globalMenuHelper);
      createRenameArtifactMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createHistoryMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createImportExportMenuItems(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createLockMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createCopyMenuItem(popupMenu);
      createPasteMenuItem(popupMenu);
      createPasteSpecialMenuItem(popupMenu);
      createExpandAllMenuItem(popupMenu);
      createSelectAllMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createAccessControlMenuItem(popupMenu);
      treeViewer.getTree().setMenu(popupMenu);
   }

   private void addOpenQuickSearchAction(IToolBarManager toolbarManager) {
      Action openQuickSearch =
            new Action("Quick Search", ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_SEARCH)) {
         @Override
         public void run() {
            try {
               IViewPart viewPart =
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                     QuickSearchView.VIEW_ID);
               if (viewPart != null) {
                  Branch branch = getBranch();
                  if (branch != null) {
                     ((QuickSearchView) viewPart).setBranch(branch);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      openQuickSearch.setToolTipText("Open Quick Search View");
      toolbarManager.add(openQuickSearch);
   }

   protected void createUpAction(IToolBarManager toolbarManager) {
      upAction = new Action("View Parent") {
         @Override
         public void run() {
            try {
               Artifact parent = exploreRoot.getParent();

               if (parent == null) {
                  return;
               }

               Object[] expanded = treeViewer.getExpandedElements();
               Object[] expandedPlus = new Object[expanded.length + 1];
               for (int i = 0; i < expanded.length; i++) {
                  expandedPlus[i] = expanded[i];
               }
               expandedPlus[expandedPlus.length - 1] = exploreRoot;

               explore(parent);

               treeViewer.setExpandedElements(expandedPlus);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      upAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARROW_UP_YELLOW));
      upAction.setToolTipText("View Parent");
      updateEnablementsEtAl();
      toolbarManager.add(upAction);
   }

   private void createNewArtifactExplorerAction(IToolBarManager toolbarManager) {

      newArtifactExplorer = new Action("New Artifact Explorer") {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            ArtifactExplorer artifactExplorer;
            try {
               artifactExplorer =
                     (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID, GUID.create(),
                     IWorkbenchPage.VIEW_ACTIVATE);
               artifactExplorer.explore(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch));
               artifactExplorer.setExpandedArtifacts(treeViewer.getExpandedElements());
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      newArtifactExplorer.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EXPLORER));
      toolbarManager.add(newArtifactExplorer);
   }

   private void createShowChangeReportAction(IToolBarManager toolbarManager) {
      showChangeReport = new Action("Show Change Report") {
         @Override
         public void run() {
            try {
               ChangeView.open(branch);
            } catch (OseeArgumentException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
            }
         }
      };

      showChangeReport.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE));
      toolbarManager.add(showChangeReport);
   }

   private void createCollapseAllAction(IToolBarManager toolbarManager) {
      collapseAllAction = new Action("Collapse All") {
         @Override
         public void run() {
            if (treeViewer != null) {
               treeViewer.collapseAll();
            }
         }
      };

      collapseAllAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL));
      toolbarManager.add(collapseAllAction);
   }

   private void createOpenWithMenuItem(Menu parentMenu) {
      openWithMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openWithMenuItem.setText("&Open With");
      final Menu submenu = new Menu(openWithMenuItem);
      openWithMenuItem.setMenu(submenu);
      parentMenu.addMenuListener(new OpenWithMenuListener(submenu, treeViewer, this));
   }

   private void createNewItemMenuItem(Menu parentMenu) {
      createMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      needProjectListener.add(createMenuItem);
      createMenuItem.setText("&New Child");
      createMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            try {
               Collection<ArtifactType> data = ArtifactTypeManager.getConcreteArtifactTypes(branchSelect.getData());
               List<ArtifactType> descriptors = new ArrayList<ArtifactType>();
               for (ArtifactType descriptor : new ArrayList<ArtifactType>(data)) {
                  if (!descriptor.getName().equals("Root Artifact")) {
                     descriptors.add(descriptor);
                  }
               }
               ArtifactTypeFilteredTreeEntryDialog dialog =
                     new ArtifactTypeFilteredTreeEntryDialog("New Child",
                     "Enter name and select Artifact type to create", "Artifact Name");
               dialog.setInput(descriptors);
               if (dialog.open() == Window.OK) {

                  ArtifactType descriptor = dialog.getSelection();
                  String name = dialog.getEntryValue();

                  IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                  Iterator<?> itemsIter = selection.iterator();
                  // If nothing was selected, then the child belongs at the root
                  IRelationSorterId sorterId = RelationOrderBaseTypes.USER_DEFINED;

                  SkynetTransaction transaction = new SkynetTransaction(branch, "Create new item in artifact explorer");
                  if (!itemsIter.hasNext()) {
                     exploreRoot.addNewChild(sorterId, descriptor, name);
                     exploreRoot.persist(transaction);
                  } else {
                     while (itemsIter.hasNext()) {
                        Artifact parent = (Artifact) itemsIter.next();
                        parent.addNewChild(sorterId, descriptor, name);
                        parent.persist(transaction);
                     }
                  }
                  transaction.execute();

                  treeViewer.refresh();
                  treeViewer.refresh(false);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   private void createGoIntoMenuItem(Menu parentMenu) {
      goIntoMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      goIntoMenuItem.setText("&Go Into");
      needArtifactListener.add(goIntoMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      goIntoMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {

            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Iterator<?> itemsIter = selection.iterator();
            if (itemsIter.hasNext()) {
               try {
                  Object[] expanded = treeViewer.getExpandedElements();
                  explore((Artifact) itemsIter.next());
                  treeViewer.setExpandedElements(expanded);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }

   private void createOpenMenuItem(Menu parentMenu) {
      openMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      openMenuItem.setText("&Open");
      needArtifactListener.add(openMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      openMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {
            LinkedList<Artifact> selectedItems = new LinkedList<Artifact>();
            TreeViewerUtility.getPreorderSelection(treeViewer, selectedItems);
            try {
               if (StaticIdManager.hasValue(UserManager.getUser(),
                     EditorsPreferencePage.PreviewOnDoubleClickForWordArtifacts)) {
                  RendererManager.previewInJob(selectedItems);
               } else {
                  RendererManager.openInJob(selectedItems, PresentationType.GENERALIZED_EDIT);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   private void createFindOnDifferentBranchItem(Menu parentMenu) {
      findOnAnotherBranch = new MenuItem(parentMenu, SWT.PUSH);
      findOnAnotherBranch.setText("Reveal On Another Branch");
      needArtifactListener.add(findOnAnotherBranch);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      findOnAnotherBranch.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {
            LinkedList<Artifact> selectedItems = new LinkedList<Artifact>();
            TreeViewerUtility.getPreorderSelection(treeViewer, selectedItems);
            Branch branch = BranchSelectionDialog.getBranchFromUser();
            if (branch != null) {
               for (Artifact artifact : selectedItems) {
                  try {
                     ArtifactExplorer.revealArtifact(ArtifactQuery.getArtifactFromId(artifact.getArtId(), branch));
                  } catch (OseeCoreException ex) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, String.format(
                           "Could not find Artifact \'%s\' on Branch \'%s\'", artifact.getName(), branch.getName()));
                  }
               }

            }
         }
      });
   }

   private void createMassEditMenuItem(Menu parentMenu) {
      massEditMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      massEditMenuItem.setText("Mass Edit");
      needArtifactListener.add(massEditMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      massEditMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {
            LinkedList<Artifact> selectedItems = new LinkedList<Artifact>();
            TreeViewerUtility.getPreorderSelection(treeViewer, selectedItems);
            MassArtifactEditor.editArtifacts("", selectedItems);
         }
      });
   }

   private void createRenameArtifactMenuItem(Menu parentMenu) {
      renameArtifactMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      renameArtifactMenuItem.setText("Rename Artifact");
      needArtifactListener.add(renameArtifactMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      renameArtifactMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent mySelectionEvent) {
            handleRenameArtifactSelectionEvent(mySelectionEvent);
         }
      });
   }

   private void handleRenameArtifactSelectionEvent(SelectionEvent mySelectionEvent) {
      // Clean up any previous editor control
      Control oldEditor = myTreeEditor.getEditor();

      if (oldEditor != null) {
         oldEditor.dispose();
      }

      // Identify the selected row, only allow input if there is a single
      // selected row
      TreeItem[] selection = myTree.getSelection();

      if (selection.length != 1) {
         return;
      }

      final TreeItem myTreeItem = selection[0];

      if (myTreeItem == null) {
         return;
      }
      myTextBeingRenamed = new Text(myTree, SWT.BORDER);
      Object myTreeItemObject = myTreeItem.getData();
      myTextBeingRenamed.setText(((Artifact) myTreeItemObject).getName());
      myTextBeingRenamed.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent e) {
            updateText(myTextBeingRenamed.getText(), myTreeItem);
            myTextBeingRenamed.dispose();

         }

         @Override
         public void focusGained(FocusEvent e) {
         }
      });

      myTextBeingRenamed.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            if (e.character == SWT.CR) {
               updateText(myTextBeingRenamed.getText(), myTreeItem);
               myTextBeingRenamed.dispose();
            } else if (e.keyCode == SWT.ESC) {
               myTextBeingRenamed.dispose();
            }
         }
      });
      myTextBeingRenamed.selectAll();
      myTextBeingRenamed.setFocus();
      myTreeEditor.setEditor(myTextBeingRenamed, myTreeItem);
   }

   private void updateText(String newLabel, TreeItem item) {
      myTreeEditor.getItem().setText(newLabel);
      Object myTreeItemObject = item.getData();
      if (myTreeItemObject instanceof Artifact) {
         Artifact myArtifact = (Artifact) myTreeItemObject;
         try {
            myArtifact.setSoleAttributeValue("Name", newLabel);
            myArtifact.persist();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      treeViewer.refresh();
   }

   private void createSkywalkerMenuItem(Menu parentMenu) {
      skywalkerMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      skywalkerMenuItem.setText("Sky Walker");
      needArtifactListener.add(skywalkerMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      skywalkerMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {
            LinkedList<Artifact> selectedItems = new LinkedList<Artifact>();
            TreeViewerUtility.getPreorderSelection(treeViewer, selectedItems);
            SkyWalkerView.exploreArtifact(selectedItems.getFirst());
         }
      });
   }

   private void createSelectAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setText("&Select All\tCtrl+A");
      menuItem.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            treeViewer.getTree().selectAll();
         }
      });
   }

   private void createHistoryMenuItem(Menu parentMenu) {
      MenuItem revisionMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      revisionMenuItem.setText("&Show Resource History ");
      revisionMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Artifact selectedArtifact = (Artifact) selection.getFirstElement();

            try {
               HistoryView.open(selectedArtifact);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   private void createImportExportMenuItems(Menu parentMenu) {
      MenuItems.createMenuItem(parentMenu, SWT.PUSH, new ImportResourcesAction(getViewSite().getWorkbenchWindow()));
      MenuItems.createMenuItem(parentMenu, SWT.PUSH, new ExportResourcesAction(getViewSite().getWorkbenchWindow()));
   }

   private void createAccessControlMenuItem(Menu parentMenu) {
      accessControlMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      accessControlMenuItem.setImage(ImageManager.getImage(FrameworkImage.AUTHENTICATED));
      accessControlMenuItem.setText("&Access Control ");
      // accessControlMenuItem.setEnabled(false);
      accessControlMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Artifact selectedArtifact = (Artifact) selection.getFirstElement();
            try {
               if (selectedArtifact != null) {
                  PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), selectedArtifact);
                  pd.open();
                  checkBranchReadable();
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   private void createLockMenuItem(Menu parentMenu) {
      lockMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      lockMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Iterator<?> iterator = selection.iterator();

            while (iterator.hasNext()) {
               try {
                  Artifact object = (Artifact) iterator.next();
                  if (new GlobalMenuPermissions(object).isLocked()) {
                     AccessControlManager.unLockObject(object, UserManager.getUser());
                  } else {
                     AccessControlManager.lockObject(object, UserManager.getUser());
                  }
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });
   }

   private void createCopyMenuItem(Menu parentMenu) {
      copyMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      copyMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_COPY));
      copyMenuItem.setText("Copy \tCtrl+C");
      copyMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performCopy();
         }
      });
   }

   private void performCopy() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      ArrayList<Artifact> artifactTransferData = new ArrayList<Artifact>();
      ArrayList<String> textTransferData = new ArrayList<String>();
      Artifact artifact;

      if (selection != null && !selection.isEmpty()) {
         for (Object object : selection.toArray()) {
            if (object instanceof Artifact) {
               artifact = (Artifact) object;

               artifactTransferData.add(artifact);
               textTransferData.add(artifact.getName());
            }
         }
         artifactClipboard.setArtifactsToClipboard(artifactTransferData, textTransferData);
      }
   }

   private void createPasteMenuItem(Menu parentMenu) {
      pasteMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      pasteMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_PASTE));
      pasteMenuItem.setText("Paste \tCtrl+V");
      pasteMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performPasteOperation(false);
         }
      });
   }

   private void createPasteSpecialMenuItem(Menu parentMenu) {
      pasteSpecialMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      pasteSpecialMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_PASTE));
      pasteSpecialMenuItem.setText("Paste Special... \tCtrl+S");
      pasteSpecialMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performPasteOperation(true);
         }

      });
   }

   private void performPasteOperation(boolean isPasteSpecial) {
      boolean performPaste = true;
      Artifact destinationArtifact = null;
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      if (selection != null && !selection.isEmpty()) {
         Object object = selection.getFirstElement();

         if (object instanceof Artifact) {
            destinationArtifact = (Artifact) object;
         }
      }

      ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();

      if (isPasteSpecial) {
         Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
         List<Artifact> copiedArtifacts = artifactClipboard.getCopiedContents();
         ArtifactPasteSpecialDialog dialog =
               new ArtifactPasteSpecialDialog(shell, config, destinationArtifact, copiedArtifacts);
         performPaste = dialog.open() == Window.OK;
      }

      if (performPaste) {
         Operations.executeAsJob(new ArtifactPasteOperation(config, destinationArtifact,
               artifactClipboard.getCopiedContents(), new ArtifactNameConflictHandler()), true);
      }
   }

   private void createExpandAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setImage(ImageManager.getImage(FrameworkImage.EXPAND_ALL));
      menuItem.setText("Expand All\tCtrl++");
      menuItem.addSelectionListener(new ExpandListener());
   }

   public class ExpandListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent event) {
         expandAll((IStructuredSelection) treeViewer.getSelection());
      }
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
   }

   @Override
   public void setFocus() {
      if (treeViewer != null) {
         treeViewer.getControl().setFocus();
      }
   }

   public void explore(Artifact artifact) throws CoreException, IllegalArgumentException {
      if (artifact == null) {
         throw new IllegalArgumentException("Can not explore a null artifact.");
      }

      setPartName("Artifact Explorer: " + artifact.getBranch().getShortName());
      if (branch != null && branch != artifact.getBranch()) {
         explore(Arrays.asList(artifact));
         return;
      }
      try {
         checkBranchReadable();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
      }
      exploreRoot = artifact;
      branch = artifact.getBranch();

      initializeSelectionBox();

      if (treeViewer != null) {
         Object objects[] = treeViewer.getExpandedElements();
         treeViewer.setInput(exploreRoot);
         setupPopupMenu();
         updateEnablementsEtAl();
         // Attempt to re-expand what was expanded
         treeViewer.setExpandedElements(objects);
      }
   }

   public void setExpandedArtifacts(Object... artifacts) {
      if (treeViewer != null) {
         treeViewer.setExpandedElements(artifacts);
      }
   }

   private void updateEnablementsEtAl() {
      // The upAction may be null if this viewpart has not been layed out yet
      if (upAction != null) {
         try {
            upAction.setEnabled(exploreRoot != null && exploreRoot.hasParent());
         } catch (OseeCoreException ex) {
            upAction.setEnabled(false);
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private class NeedArtifactMenuListener implements MenuListener {
      private final HashCollection<Class<? extends Artifact>, MenuItem> menuItemMap;

      public NeedArtifactMenuListener() {
         this.menuItemMap = new HashCollection<Class<? extends Artifact>, MenuItem>();
      }

      public void add(MenuItem item) {
         menuItemMap.put(Artifact.class, item);
      }

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

         Object obj = selection.getFirstElement();
         if (obj != null) {
            Class<? extends Artifact> selectedClass = obj.getClass().asSubclass(Artifact.class);

            for (Class<? extends Artifact> artifactClass : menuItemMap.keySet()) {
               boolean valid = artifactClass.isAssignableFrom(selectedClass);

               for (MenuItem item : menuItemMap.getValues(artifactClass)) {
                  if (!(item.getData() instanceof Exception)) {
                     // Only modify enabling if no error is associated
                     item.setEnabled(valid);
                  }
               }
            }
         }
      }
   }

   private class NeedProjectMenuListener implements MenuListener {
      Collection<MenuItem> items;

      public NeedProjectMenuListener() {
         this.items = new LinkedList<MenuItem>();
      }

      public void add(MenuItem item) {
         items.add(item);
      }

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         boolean valid = treeViewer.getInput() != null;
         for (MenuItem item : items) {
            if (!(item.getData() instanceof Exception)) {
               // Only modify
               // enabling if no
               // error is
               // associated
               item.setEnabled(valid);
            }
         }
      }
   }

   /**
    * Add the selection from the define explorer
    */
   private void addExploreSelection() {
      if (exploreRoot != null) {
         try {
            treeViewer.setInput(exploreRoot);
            initializeSelectionBox();
         } catch (IllegalArgumentException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   /**
    * @author Jeff C. Phillips
    */
   public class ArtifactMenuListener implements MenuListener {

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         // Use this menu listener until all menu items can be moved to
         // GlobaMenu
         try {
            GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);

            lockMenuItem.setText((permiss.isLocked() ? "Unlock: (" + permiss.getSubjectFromLockedObjectName() + ")" : "Lock"));

            lockMenuItem.setEnabled(permiss.isWritePermission() && (!permiss.isLocked() || permiss.isAccessToRemoveLock()));
            openMenuItem.setEnabled(permiss.isReadPermission());
            createMenuItem.setEnabled(permiss.isWritePermission());
            openWithMenuItem.setEnabled(permiss.isReadPermission());
            goIntoMenuItem.setEnabled(permiss.isReadPermission());
            copyMenuItem.setEnabled(permiss.isReadPermission());
            pasteMenuItem.setEnabled(permiss.isWritePermission());

         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);

      if (SkynetGuiPlugin.areOSEEServicesAvailable().isFalse()) {
         return;
      }

      try {
         if (memento != null && memento.getString(ROOT_GUID) != null && memento.getString(ROOT_BRANCH) != null) {
            Branch branch = BranchManager.getBranch(Integer.parseInt(memento.getString(ROOT_BRANCH)));

            if (!branch.getArchiveState().isArchived() || AccessControlManager.isOseeAdmin()) {
               Artifact previousArtifact = ArtifactQuery.checkArtifactFromId(memento.getString(ROOT_GUID), branch);
               if (previousArtifact != null) {
                  explore(previousArtifact);
               } else {
                  /*
                   * simply means that the previous artifact that was used as the root for the artiactExplorer does not
                   * exist
                   * because it was deleted or this workspace was last used with a different branch or database, so let
                   * the logic
                   * below get the default hierarchy root artifact
                   */
               }
               return;
            }
         }

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (exploreRoot != null) {
         memento.putString(ROOT_GUID, exploreRoot.getGuid());
         memento.putString(ROOT_BRANCH, String.valueOf(exploreRoot.getBranch().getId()));
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
      if (treeViewer != null) {
         trees.remove(treeViewer.getTree());
      }
   }

   public String getActionDescription() {
      return "";
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      treeViewer.addSelectionChangedListener(listener);
   }

   public ISelection getSelection() {
      return treeViewer.getSelection();
   }

   public void removeSelectionChangedListener(ISelectionChangedListener listener) {
      treeViewer.removeSelectionChangedListener(listener);
   }

   public void setSelection(ISelection selection) {
      treeViewer.setSelection(selection);
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(treeViewer.getControl(), "artifact_explorer_tree_viewer",
            "org.eclipse.osee.framework.help.ui");
   }
   public class MenuEnablingListener implements MenuListener {

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         TreeItem[] myTreeItems = myTree.getSelection();
         if (myTreeItems.length != 1) {
            renameArtifactMenuItem.setEnabled(false);
            return;
         }
         Object myTreeItemObject = myTreeItems[0].getData();
         if (myTreeItemObject instanceof Artifact) {
            Artifact mySelectedArtifact = (Artifact) myTreeItemObject;
            boolean writePermission;
            try {
               writePermission = AccessControlManager.hasPermission(mySelectedArtifact, PermissionEnum.WRITE);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               writePermission = false;
            }

            renameArtifactMenuItem.setEnabled(writePermission);
         }
      }
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.isNotForBranch(branch)) {
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            treeViewer.refresh();
         }
      });
   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.isNotForBranch(branch)) {
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               Set<Artifact> parents = new HashSet<Artifact>();
               for (Artifact art : loadedArtifacts.getLoadedArtifacts()) {
                  if (art.getParent() != null) {
                     parents.add(art.getParent());
                  }
               }
               for (Artifact art : parents) {
                  treeViewer.refresh(art);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (branch == null || transData.branchId != branch.getId()) {
         return;
      }

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (treeViewer != null && Widgets.isAccessible(treeViewer.getTree())) {
               for (Artifact art : transData.cacheDeletedArtifacts) {
                  treeViewer.remove(art);
               }
               try {
                  treeViewer.update(transData.getArtifactsInRelations(ChangeType.Changed,
                        CoreRelationTypes.Default_Hierarchical__Child).toArray(), null);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
               try {
                  Set<Artifact> parents = new HashSet<Artifact>();
                  for (Artifact art : transData.getArtifactsInRelations(ChangeType.Added,
                        CoreRelationTypes.Default_Hierarchical__Child)) {
                     if (!art.isDeleted() && art.getParent() != null) {
                        parents.add(art.getParent());
                     }
                  }
                  treeViewer.refresh(parents);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }

   @Override
   public void handleRelationModifiedEvent(Sender sender, RelationEventType relationEventType, final RelationLink link, Branch branch, String relationType) {
      try {
         if (this.branch == null || !this.branch.equals(branch)) {
            return;
         }
         if (link.isOfType(CoreRelationTypes.Default_Hierarchical__Child)) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  try {
                     // Since this is always a local event, artifact will always be in cache
                     Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
                     if (aArtifact != null) {
                        treeViewer.refresh(aArtifact);
                     }
                     Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
                     if (bArtifact != null) {
                        treeViewer.refresh(bArtifact);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.INFO, ex);
                  }
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleArtifactModifiedEvent(Sender sender, final ArtifactModType artifactModType, final Artifact artifact) {
      try {
         if (branch == null || !artifact.getBranch().equals(branch)) {
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               if (artifactModType == ArtifactModType.Deleted) {
                  treeViewer.remove(artifact);
               } else if (artifactModType == ArtifactModType.Added) {
                  if (artifact.getParent() != null) {
                     treeViewer.refresh(artifact.getParent());
                  }
               } else if (artifactModType == ArtifactModType.Changed) {
                  treeViewer.refresh(artifact, true);
               } else if (artifactModType == ArtifactModType.Reverted) {
                  if (artifact.hasParent()) {
                     treeViewer.refresh(artifact.getParent());
                  }
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      });
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
      if (branchModType == BranchEventType.Committed && branch != null && branch.getId() == branchId) {
         SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
      }
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlEventType, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.isNotForBranch(branch)) {
            return;
         }
         if (accessControlEventType == AccessControlEventType.UserAuthenticated || accessControlEventType == AccessControlEventType.ArtifactsUnlocked || accessControlEventType == AccessControlEventType.ArtifactsLocked) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  treeViewer.refresh();
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void rebuildMenu() {
      setupPopupMenu();
   }

   public void setBranch(Branch branch) {
      this.branch = branch;
   }

   public void initializeSelectionBox() {
      if (branch != null && branchSelect != null && !branch.equals(branchSelect.getData())) {
         branchSelect.setSelection(branch);
         try {
            checkBranchReadable();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
   }

   public void refreshWidgets() {

   }

   public Branch getBranch() {
      return branch;
   }

}
