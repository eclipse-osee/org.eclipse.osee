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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;
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
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.plugin.util.Wizards;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportWizard;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactTreeViewerGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactClipboard;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.HierarchicalReportDialog;
import org.eclipse.osee.framework.ui.skynet.util.HtmlReportJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.ShowAttributeAction;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.MenuItems;
import org.eclipse.osee.framework.ui.swt.TreeViewerUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
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
public class ArtifactExplorer extends ViewPart implements IRebuildMenuListener, IAccessControlEventListener, IRelationModifiedEventListener, IArtifactModifiedEventListener, IFrameworkTransactionEventListener, IBranchEventListener, IArtifactsPurgedEventListener, IArtifactsChangeTypeEventListener, IActionable, ISelectionProvider {
   private static final Image ACCESS_DENIED_IMAGE = SkynetGuiPlugin.getInstance().getImage("lockkey.gif");
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.ArtifactExplorer";
   private static final String ROOT_GUID = "artifact.explorer.last.root_guid";
   private static final ArtifactClipboard artifactClipboard = new ArtifactClipboard(VIEW_ID);
   private static final LinkedList<Tree> trees = new LinkedList<Tree>();

   private TreeViewer treeViewer;
   private Action upAction;
   private Artifact exploreRoot;
   private MenuItem openMenuItem;
   private MenuItem massEditMenuItem;
   private MenuItem skywalkerMenuItem;
   private MenuItem createMenuItem;
   private MenuItem reportMenuItem;
   private MenuItem openWithMenuItem;
   private MenuItem accessControlMenuItem;
   private MenuItem lockMenuItem;
   private MenuItem goIntoMenuItem;
   private MenuItem copyMenuItem;
   private MenuItem pasteMenuItem;
   private MenuItem renameArtifactMenuItem;
   private NeedArtifactMenuListener needArtifactListener;
   private NeedProjectMenuListener needProjectListener;
   private Tree myTree;
   private TreeEditor myTreeEditor;
   private Text myTextBeingRenamed;
   private Action showArtIds;
   private Action showArtType;
   private Action showArtVersion;
   private Action newArtifactExplorer;
   private Action collapseAllAction;
   private ShowAttributeAction attributesAction;
   IGlobalMenuHelper globalMenuHelper;

   private Composite stackComposite;
   private Control branchUnreadableWarning;
   private StackLayout stackLayout;

   public ArtifactExplorer() {
   }

   public static void explore(Collection<Artifact> artifacts) {
      IWorkbenchPage page = AWorkbench.getActivePage();
      ArtifactExplorer artifactExplorer;
      try {
         artifactExplorer =
               (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID, new GUID().toString(),
                     IWorkbenchPage.VIEW_ACTIVATE);
         artifactExplorer.setPartName("Artifacts");
         artifactExplorer.setContentDescription("These artifact must be handled individually");
         artifactExplorer.treeViewer.setInput(artifacts);
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
      image.setImage(ACCESS_DENIED_IMAGE);
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
      Control control = branchUnreadableWarning;
      if (false != (new GlobalMenuPermissions(globalMenuHelper)).isDefaultBranchReadable()) {
         control = treeViewer.getTree();
      }
      stackLayout.topControl = control;
      stackComposite.layout();
      stackComposite.getParent().layout();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */

   @Override
   public void createPartControl(Composite parent) {
      try {
         if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

         GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
         gridData.heightHint = 1000;
         gridData.widthHint = 1000;

         parent.setLayout(new GridLayout(1, false));
         parent.setLayoutData(gridData);

         stackComposite = new Composite(parent, SWT.NONE);
         stackLayout = new StackLayout();
         stackComposite.setLayout(stackLayout);
         stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         branchUnreadableWarning = createDefaultWarning(stackComposite);

         treeViewer = new TreeViewer(stackComposite);
         myTree = treeViewer.getTree();
         Tree tree = treeViewer.getTree();
         treeViewer.setContentProvider(new ArtifactContentProvider());
         treeViewer.setLabelProvider(new ArtifactLabelProvider(this));
         treeViewer.addDoubleClickListener(new ArtifactDoubleClick());
         treeViewer.getControl().setLayoutData(gridData);

         // We can not use the hash lookup because an artifact may not have a
         // good equals.
         // This can be added back once the content provider is converted over to
         // use job node.
         treeViewer.setUseHashlookup(false);

         treeViewer.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));
         globalMenuHelper = new ArtifactTreeViewerGlobalMenuHelper(treeViewer);

         createCollapseAllAction();
         createUpAction();
         createShowArtVersionAction();
         createShowArtTypeAction();
         createAttributesAction();
         createNewArtifactExplorerAction();

         getSite().setSelectionProvider(treeViewer);
         addExploreSelection();

         setupPopupMenu();

         myTreeEditor = new TreeEditor(myTree);
         myTreeEditor.horizontalAlignment = SWT.LEFT;
         myTreeEditor.grabHorizontal = true;
         myTreeEditor.minimumWidth = 50;

         new ArtifactExplorerDragAndDrop(tree, VIEW_ID);
         parent.layout();

         if (AccessControlManager.isOseeAdmin()) {
            createShowArtIdsAction();
         }
         createSetDefaultBranchAction();
         OseeAts.addBugToViewToolbar(this, this, SkynetActivator.getInstance(), VIEW_ID, "Artifact Explorer");

         OseeContributionItem.addTo(this, false);
         getViewSite().getActionBars().updateActionBars();

         updateEnablementsEtAl();
         trees.add(tree);
         setHelpContexts();

         checkBranchReadable();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      OseeEventManager.addListener(this);
   }

   /**
    * Reveal an artifact in the viewer and select it.
    * 
    * @param artifact TODO
    */
   public static void revealArtifact(Artifact artifact) {
      try {

         if (artifact.isDeleted()) {
            OSEELog.logSevere(SkynetGuiPlugin.class,
                  "The artifact " + artifact.getDescriptiveName() + " has been deleted.", true);
         } else {
            if (artifact.isHistorical()) {
               artifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), artifact.getBranch(), false);
            }
            if (artifact.isOrphan()) {
               OSEELog.logSevere(SkynetGuiPlugin.class,
                     "The artifact " + artifact.getDescriptiveName() + " does not have a parent (orphan).", true);
            } else {
               IWorkbenchPage page = AWorkbench.getActivePage();
               ArtifactExplorer artifactExplorer = (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID);
               artifactExplorer.treeViewer.setSelection(new StructuredSelection(artifact), true);
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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
      createNewItemMenuItem(popupMenu);
      createGoIntoMenuItem(popupMenu);
      createMassEditMenuItem(popupMenu);
      createSkywalkerMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      new GlobalMenu(popupMenu, globalMenuHelper);
      createRenameArtifactMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createReportMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createHistoryMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createImportExportMenuItems(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createLockMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createCopyMenuItem(popupMenu);
      createPasteMenuItem(popupMenu);
      createExpandAllMenuItem(popupMenu);
      createSelectAllMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createAccessControlMenuItem(popupMenu);
      treeViewer.getTree().setMenu(popupMenu);
   }

   protected void createUpAction() {
      upAction = new Action("View Parent") {
         @Override
         public void run() {
            try {
               Artifact parent = exploreRoot.getParent();

               if (parent == null) return;

               Object[] expanded = treeViewer.getExpandedElements();
               Object[] expandedPlus = new Object[expanded.length + 1];
               for (int i = 0; i < expanded.length; i++)
                  expandedPlus[i] = expanded[i];
               expandedPlus[expandedPlus.length - 1] = exploreRoot;

               explore(parent);

               treeViewer.setExpandedElements(expandedPlus);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      };

      upAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("up.gif"));
      upAction.setToolTipText("View Parent");
      updateEnablementsEtAl();

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(upAction);
   }

   protected void createShowArtIdsAction() {

      showArtIds = new Action("Show Artifact Ids") {
         @Override
         public void run() {
            setChecked(!isChecked());
            updateShowArtIdText();
            treeViewer.refresh();
         }
      };

      showArtIds.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      updateShowArtIdText();

      IMenuManager toolbarManager = getViewSite().getActionBars().getMenuManager();
      toolbarManager.add(showArtIds);
   }

   private void createSetDefaultBranchAction() {
      Action setDefaultBranch = new Action("Set Default Branch", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            BranchSelectionDialog branchSelection = new BranchSelectionDialog("Set Default Branch", false);
            int result = branchSelection.open();
            if (result == Window.OK) {
               try {
                  BranchManager.setDefaultBranch(branchSelection.getSelection());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      };
      setDefaultBranch.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change.gif"));
      IMenuManager toolbarManager = getViewSite().getActionBars().getMenuManager();
      toolbarManager.add(setDefaultBranch);
   }

   protected void createShowArtTypeAction() {

      showArtType = new Action("Show Artifact Type") {
         @Override
         public void run() {
            setChecked(!isChecked());
            updateShowArtTypeText();
            treeViewer.refresh();
         }
      };

      showArtType.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      updateShowArtTypeText();

      IMenuManager toolbarManager = getViewSite().getActionBars().getMenuManager();
      toolbarManager.add(showArtType);
   }

   protected void createShowArtVersionAction() {

      showArtVersion = new Action("Show Artifact Version") {
         @Override
         public void run() {
            setChecked(!isChecked());
            updateShowArtVersionText();
            treeViewer.refresh();
         }
      };

      showArtVersion.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      updateShowArtVersionText();

      IMenuManager toolbarManager = getViewSite().getActionBars().getMenuManager();
      toolbarManager.add(showArtVersion);
   }

   private void createNewArtifactExplorerAction() {

      newArtifactExplorer = new Action("New Artifact Explorer") {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            ArtifactExplorer artifactExplorer;
            try {
               artifactExplorer =
                     (ArtifactExplorer) page.showView(ArtifactExplorer.VIEW_ID, GUID.generateGuidStr(),
                           IWorkbenchPage.VIEW_ACTIVATE);
               artifactExplorer.explore(ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(BranchManager.getDefaultBranch()));
               artifactExplorer.setExpandedArtifacts(treeViewer.getExpandedElements());
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      newArtifactExplorer.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("artifact_explorer.gif"));

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(newArtifactExplorer);
   }

   private void createCollapseAllAction() {

      collapseAllAction = new Action("Collapse All") {
         @Override
         public void run() {
            if (treeViewer != null) {
               treeViewer.collapseAll();
            }
         }
      };

      collapseAllAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("collapseAll.gif"));

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(collapseAllAction);
   }

   private void updateShowArtIdText() {
      showArtIds.setText((showArtIds.isChecked() ? "Hide" : "Show") + " Artifact Ids");
   }

   private void updateShowArtTypeText() {
      showArtType.setText((showArtType.isChecked() ? "Hide" : "Show") + " Artifact Type");
   }

   private void updateShowArtVersionText() {
      showArtVersion.setText((showArtVersion.isChecked() ? "Hide" : "Show") + " Artifact Version");
   }

   protected void createAttributesAction() {
      try {
         attributesAction = new ShowAttributeAction(treeViewer, SkynetGuiPlugin.ARTIFACT_EXPLORER_ATTRIBUTES_PREF);
         attributesAction.addToView(this);
         attributesAction.setValidAttributeTypes(SkynetViews.loadAttrTypesFromPreferenceStore(
               SkynetGuiPlugin.ARTIFACT_EXPLORER_ATTRIBUTES_PREF, BranchManager.getDefaultBranch()));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public String getSelectedAttributeData(Artifact artifact) throws Exception {
      return attributesAction.getSelectedAttributeData(artifact);
   }

   private void createOpenWithMenuItem(Menu parentMenu) {
      openWithMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openWithMenuItem.setText("&Open With");
      final Menu submenu = new Menu(openWithMenuItem);
      openWithMenuItem.setMenu(submenu);
      parentMenu.addMenuListener(new OpenWithMenuListener(submenu, treeViewer, this));
   }

   //   public class OpenListener extends SelectionAdapter {
   //      @Override
   //      public void widgetSelected(SelectionEvent event) {
   //         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
   //         Iterator<?> itemsIter = selection.iterator();
   //
   //         ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
   //         while (itemsIter.hasNext()) {
   //            artifacts.add((Artifact) itemsIter.next());
   //         }
   //
   //         RendererManager.openInJob(artifacts, PresentationType.SPECIALIZED_EDIT);
   //      }
   //   }

   private void createNewItemMenuItem(Menu parentMenu) {
      SelectionAdapter listener = new NewArtifactMenuListener();
      createMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      Menu subMenu = new Menu(parentMenu.getShell(), SWT.DROP_DOWN);
      createMenuItem.setMenu(subMenu);
      needProjectListener.add(createMenuItem);
      createMenuItem.setText("&New Child");
      createMenuItem.setEnabled(true);

      try {
         Collection<ArtifactType> data = TypeValidityManager.getValidArtifactTypes(BranchManager.getDefaultBranch());
         List<ArtifactType> descriptors = new ArrayList<ArtifactType>(data);
         Collections.sort(descriptors);
         for (ArtifactType descriptor : descriptors) {
            if (!descriptor.getName().equals("Root Artifact")) {
               MenuItem item = new MenuItem(subMenu, SWT.PUSH);
               item.setText(descriptor.getName());
               item.setImage(descriptor.getImage());
               item.setData(descriptor);
               item.addSelectionListener(listener);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private class NewArtifactMenuListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent ev) {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Iterator<?> itemsIter = selection.iterator();
         ArtifactType descriptor = (ArtifactType) ((MenuItem) ev.getSource()).getData();

         EntryDialog ed =
               new EntryDialog("New \"" + descriptor.getName() + "\" Artifact",
                     "Enter name for \"" + descriptor.getName() + "\" Artifact");
         if (ed.open() != 0) return;
         try {
            // If nothing was selected, then the child belongs at the root
            if (!itemsIter.hasNext()) {
               exploreRoot.addNewChild(descriptor, ed.getEntry()).persistAttributesAndRelations();
               ;
            } else {
               while (itemsIter.hasNext()) {
                  ((Artifact) itemsIter.next()).addNewChild(descriptor, ed.getEntry()).persistAttributesAndRelations();
                  ;
               }
            }
            treeViewer.refresh();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         treeViewer.refresh(false);
      }
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
      myTextBeingRenamed.setText(((Artifact) myTreeItemObject).getDescriptiveName());
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
            if ((e.character == SWT.CR)) {
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
            myArtifact.persistAttributes();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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

            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               RevisionHistoryView revisionHistoryView =
                     (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, selectedArtifact.getGuid(),
                           IWorkbenchPage.VIEW_ACTIVATE);
               revisionHistoryView.explore(selectedArtifact);
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

   private void createReportMenuItem(Menu parentMenu) {
      reportMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      reportMenuItem.setText("&Hierarchical Report");
      reportMenuItem.addSelectionListener(new ReportListener());
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
                  if ((new GlobalMenuPermissions(object)).isLocked()) {
                     AccessControlManager.getInstance().unLockObject(object, UserManager.getUser());
                  } else {
                     AccessControlManager.getInstance().lockObject(object, UserManager.getUser());
                  }
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, true);
               }
            }
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });
   }

   private void createCopyMenuItem(Menu parentMenu) {
      copyMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      copyMenuItem.setText("Copy \tCtrl+C");
      copyMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            performCopy();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
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
               textTransferData.add(artifact.getDescriptiveName());
            }
         }
         artifactClipboard.setArtifactsToClipboard(artifactTransferData, textTransferData);
      }
   }

   private void createPasteMenuItem(Menu parentMenu) {
      pasteMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      pasteMenuItem.setText("Paste \tCtrl+V");
      pasteMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            performPaste();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });
   }

   /**
    * This method must be called from the display thread
    */
   private void performPaste() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

      if (selection != null && !selection.isEmpty()) {
         Object object = selection.getFirstElement();

         if (object instanceof Artifact) {
            try {
               artifactClipboard.pasteArtifactsFromClipboard((Artifact) object);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      }
   }

   private void createExpandAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setText("Expand All\tCtrl++");
      menuItem.addSelectionListener(new ExpandListener());
   }

   public class ExpandListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent event) {
         expandAll((IStructuredSelection) treeViewer.getSelection());
      }
   }

   public class ReportListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent event) {
         Iterator<?> iter = ((IStructuredSelection) treeViewer.getSelection()).iterator();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
         while (iter.hasNext()) {
            artifacts.add((Artifact) iter.next());
         }
         if (artifacts.size() > 0) {
            HierarchicalReportDialog ld = new HierarchicalReportDialog(Display.getCurrent().getActiveShell());
            int result = ld.open();
            if (result == 0) {
               HtmlReportJob job;
               try {
                  job =
                        new HtmlReportJob("Hierarchical Report", artifacts,
                              CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
                  job.setRecurseChildren(ld.isRecurseChildren());
                  Jobs.startJob(job);
               } catch (Exception ex) {
                  OSEELog.logException(getClass(), ex, true);
               }
            }
         }
      }
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#setFocus()
    */
   @Override
   public void setFocus() {
      if (treeViewer != null) treeViewer.getControl().setFocus();
   }

   public void explore(Artifact artifact) throws CoreException, IllegalArgumentException {
      if (artifact == null) {
         throw new IllegalArgumentException("Can not explore a null artifact.");
      }

      exploreRoot = artifact;

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
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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

      public void add(MenuItem item, Class<? extends Artifact> artifactClass) {
         menuItemMap.put(artifactClass, item);
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
         for (MenuItem item : items)
            if (!(item.getData() instanceof Exception)) // Only modify
            // enabling if no
            // error is
            // associated
            item.setEnabled(valid);
      }
   }

   /**
    * Add the selection from the define explorer
    */
   private void addExploreSelection() {
      if (exploreRoot != null) {
         try {
            treeViewer.setInput(exploreRoot);
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
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }

      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);

      if (SkynetGuiPlugin.areOSEEServicesAvailable().isFalse()) return;

      try {
         if (memento != null && memento.getString(ROOT_GUID) != null) {
            Artifact previousArtifact =
                  ArtifactQuery.getArtifactFromId(memento.getString(ROOT_GUID), BranchManager.getDefaultBranch());
            explore(previousArtifact);
            return;
         }
      } catch (ArtifactDoesNotExist ex) {
         /*
          * simply means that the previous artifact that was used as the root for the artiactExplorer does not exist
          * because it was deleted or this workspace was last used with a different branch or database, so let the logic
          * below get the default hierarchy root artifact
          */
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      try {
         explore(ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(BranchManager.getDefaultBranch()));
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (exploreRoot != null) {
         memento.putString(ROOT_GUID, exploreRoot.getGuid());
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
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

   public boolean showArtIds() {
      return showArtIds != null && showArtIds.isChecked();
   }

   public boolean showArtType() {
      return showArtType != null && showArtType.isChecked();
   }

   public boolean showArtVersion() {
      return showArtVersion != null && showArtVersion.isChecked();
   }

   private class ArtifactExplorerDragAndDrop extends SkynetDragAndDrop {

      public ArtifactExplorerDragAndDrop(Tree tree, String viewId) {
         super(tree, tree, viewId);
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Object[] objects = selection.toArray();
         Artifact[] artifacts = new Artifact[objects.length];

         for (int index = 0; index < objects.length; index++)
            artifacts[index] = (Artifact) objects[index];

         return artifacts;
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;

         if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
            event.detail = DND.DROP_COPY;
         } else if (isValidForArtifactDrop(event)) {
            event.detail = DND.DROP_MOVE;
         } else {
            event.detail = DND.DROP_NONE;
         }
      }

      private boolean isValidForArtifactDrop(DropTargetEvent event) {
         if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
            ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);

            if (artData != null) {

               Artifact parentArtifact = getSelectedArtifact(event);
               if (parentArtifact != null && artData.getSource().equals(VIEW_ID)) {
                  Artifact[] artifactsToBeRelated = artData.getArtifacts();

                  for (Artifact artifact : artifactsToBeRelated) {
                     if (parentArtifact.equals(artifact)) {
                        return false;
                     }
                  }
                  return true;
               }
            } else {
               // only occurs during the drag on some platforms
               return true;
            }
         }
         return false;
      }

      private Artifact getSelectedArtifact(DropTargetEvent event) {
         TreeItem selected = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));

         if (selected != null && selected.getData() instanceof Artifact) {
            return (Artifact) selected.getData();
         }
         return null;
      }

      @Override
      public void performDrop(final DropTargetEvent event) {
         final Artifact parentArtifact = getSelectedArtifact(event);

         if (parentArtifact != null) {

            if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType) && isValidForArtifactDrop(event) && MessageDialog.openQuestion(
                  getViewSite().getShell(),
                  "Confirm Move",
                  "Are you sure you want to make each of the selected artifacts a child of " + parentArtifact.getDescriptiveName() + "?")) {
               ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
               final Artifact[] artifactsToBeRelated = artData.getArtifacts();
               try {
                  SkynetTransaction transaction = new SkynetTransaction(parentArtifact.getBranch());
                  // Replace all of the parent relations
                  for (Artifact artifact : artifactsToBeRelated) {
                     artifact.setSoleRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT, parentArtifact);
                     artifact.persistAttributesAndRelations(transaction);
                  }
                  transaction.execute();
               } catch (Exception ex) {
                  OSEELog.logException(getClass(), ex, true);
               }
            }

            else if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
               Object object = FileTransfer.getInstance().nativeToJava(event.currentDataType);
               if (object instanceof String[]) {
                  String filename = ((String[]) object)[0];

                  ArtifactImportWizard wizard = new ArtifactImportWizard();
                  wizard.setImportResourceAndArtifactDestination(new File(filename), parentArtifact);

                  Wizards.initAndOpen(wizard, ArtifactExplorer.this);
               }
            }
         }
      }
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
      SkynetGuiPlugin.getInstance().setHelp(treeViewer.getControl(), "artifact_explorer_tree_viewer");
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
               writePermission = AccessControlManager.checkObjectPermission(mySelectedArtifact, PermissionEnum.WRITE);
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
         if (loadedArtifacts.isNotForDefaultBranch()) {
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            treeViewer.refresh();
         }
      });
   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.isNotForDefaultBranch()) {
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId != BranchManager.getDefaultBranch().getBranchId()) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            for (Artifact art : transData.cacheDeletedArtifacts) {
               treeViewer.remove(art);
            }
            try {
               treeViewer.update(transData.getArtifactsInRelations(ChangeType.Changed,
                     CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getRelationType()).toArray(), null);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
            try {
               Set<Artifact> parents = new HashSet<Artifact>();
               for (Artifact art : transData.getArtifactsInRelations(ChangeType.Added,
                     CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getRelationType())) {
                  if (!art.isDeleted() && art.getParent() != null) {
                     parents.add(art.getParent());
                  }
               }
               treeViewer.refresh(parents);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IRelationModifiedEventListener#handleRelationModifiedEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.RelationModType, org.eclipse.osee.framework.skynet.core.relation.RelationLink, org.eclipse.osee.framework.skynet.core.artifact.Branch, java.lang.String, java.lang.String)
    */
   @Override
   public void handleRelationModifiedEvent(Sender sender, RelationModType relationModType, final RelationLink link, Branch branch, String relationType) {
      try {
         if (!BranchManager.getDefaultBranch().equals(branch)) return;
         if (link.getRelationType().equals(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getRelationType())) {
            Displays.ensureInDisplayThread(new Runnable() {
               /* (non-Javadoc)
                * @see java.lang.Runnable#run()
                */
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
                     // do nothing
                  }
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IArtifactModifiedEventListener#handleArtifactModifiedEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ArtifactModType, org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void handleArtifactModifiedEvent(Sender sender, final ArtifactModType artifactModType, final Artifact artifact) {
      try {
         if (!artifact.getBranch().equals(BranchManager.getDefaultBranch())) {
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
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
                  treeViewer.update(artifact, null);
               } else if (artifactModType == ArtifactModType.Reverted) {
                  if (artifact.getParent() != null) {
                     treeViewer.refresh(artifact.getParent());
                  }
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.DefaultBranchChanged) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  Branch defaultBranch = BranchManager.getDefaultBranch();
                  Artifact candidateRoot = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(defaultBranch);

                  if (exploreRoot != null) {
                     try {
                        candidateRoot = ArtifactQuery.getArtifactFromId(exploreRoot.getGuid(), defaultBranch);
                     } catch (OseeCoreException ex) {
                        // this will happen if the previous root does not exist on this branch, so the DefaultHierarchyRootArtifact will be used if we do nothing
                     }
                  }

                  explore(candidateRoot);
                  updateEnablementsEtAl();
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }
      if (branchModType == BranchEventType.Committed) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  Object object = treeViewer.getInput();
                  if (object instanceof Artifact) {
                     Artifact artifact = (Artifact) object;
                     try {
                        explore(ArtifactQuery.getArtifactFromId(artifact.getGuid(), BranchManager.getDefaultBranch()));
                     } catch (CoreException ex) {
                        OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IAccessControlEventListener#handleAccessControlArtifactsEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.eventx.AccessControlModType, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlEventType, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.isNotForDefaultBranch()) {
            return;
         }
         if (accessControlEventType == AccessControlEventType.UserAuthenticated || accessControlEventType == AccessControlEventType.ArtifactsLocked || accessControlEventType == AccessControlEventType.ArtifactsLocked) {
            Displays.ensureInDisplayThread(new Runnable() {
               /* (non-Javadoc)
                * @see java.lang.Runnable#run()
                */
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener#rebuild()
    */
   @Override
   public void rebuildMenu() {
      setupPopupMenu();
   }

}
