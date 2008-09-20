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
package org.eclipse.osee.framework.ui.skynet.changeReport;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.CHANGE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.LabelSorter;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.TreeViewerReport;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchContentProvider;
import org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactPreviewMenu;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactClipboard;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.ShowAttributeAction;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.swt.ITreeNode;
import org.eclipse.osee.framework.ui.swt.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays the changes made between two transaction points on a single branch.
 * 
 * @author Robert A. Fisher
 */
public class ChangeReportView extends ViewPart implements IActionable, IBranchEventListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView";
   private static final String INPUT = "input";

   private static final ArtifactClipboard artifactClipboard = new ArtifactClipboard(VIEW_ID);
   private static final String[] columnNames = {"", "Name", "", ""};
   private static final String SHOW_FINAL_VERSION_TXT = "Show Final &Version";
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private Action sortAction = null;
   private Collection<Integer> attributeModifiedArtifactIds = null;

   private TreeViewer changeTable;
   private MenuItem diffMenuItem;
   private MenuItem diffOnParentBranchMenuItem;
   private MenuItem diffConflictsMenuItem;
   private MenuItem revertMenuItem;
   private MenuItem changeReportMenuItem;
   private MenuItem revisionMenuItem;
   private MenuItem showFinalVersionMenuItem;
   private MenuItem copyMenuItem;
   private MenuItem compressWordAttributes;
   private MenuItem showInExplorer;

   private TransactionId baseParentTransactionId;
   private TransactionId baseTransactionId;
   private TransactionId toTransactionId;

   private ChangeReportInput priorInput;

   private ShowAttributeAction attributesAction;

   /**
    * 
    */
   public ChangeReportView() {
      super();

      this.baseParentTransactionId = null;
      this.baseTransactionId = null;
      this.toTransactionId = null;
      this.priorInput = null;

      OseeEventManager.addListener(this);
   }

   public TreeViewer getChangeTableTreeViewer() {
      return changeTable;
   }

   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      PlatformUI.getWorkbench().getService(IHandlerService.class);
      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.grabExcessHorizontalSpace = true;

      parent.setLayoutData(gridData);

      changeTable = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
      changeTable.setContentProvider(new BranchContentProvider());

      attributesAction = new ShowAttributeAction(changeTable, SkynetGuiPlugin.CHANGE_REPORT_ATTRIBUTES_PREF);
      attributesAction.addToView(this);

      changeTable.setLabelProvider(new BranchLabelProvider(attributesAction));
      changeTable.setSorter(new LabelSorter());

      createColumns();
      changeTable.addDoubleClickListener(new ChangeRepolrt2ClickListener());
      changeTable.getTree().addKeyListener(new keySelectedListener());
      changeTable.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));
      Tree changeTree = changeTable.getTree();

      if (true) {
         defineMenusNewerWay();
      } else {
         defineMenusOlderWay(parent, changeTree);
      }
      new ChangeReportDragAndDrop(changeTree, VIEW_ID);

      createActions();

      SkynetContributionItem.addTo(this, true);

      setHelpContexts();

      if (priorInput != null) {
         explore(priorInput);
      }
   }

   /**
    * 
    */
   private void defineMenusNewerWay() {
      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator("Top"));
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });
      changeTable.getTree().setMenu(menuManager.createContextMenu(changeTable.getTree()));
      menuManager.add(new Separator("Top"));
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView", menuManager,
            changeTable);
      changeTable.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));
      getSite().setSelectionProvider(changeTable);
   }

   /**
    * @param parent
    * @param changeTree
    */
   private void defineMenusOlderWay(Composite parent, Tree changeTree) {
      Menu popupMenu = new Menu(parent);
      popupMenu.addMenuListener(new MenuEnablingListener());

      createDiffMenuItem(popupMenu);
      createDiffConflictsMenuItem(popupMenu);
      createParentDiffMenuItem(popupMenu);

      new MenuItem(popupMenu, SWT.SEPARATOR);
      createViewFinalVersionMenuItem(popupMenu);
      ArtifactPreviewMenu.createPreviewMenuItem(popupMenu, changeTable);

      new MenuItem(popupMenu, SWT.SEPARATOR);
      createHistoryMenuItem(popupMenu);
      createShowInExplorerMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createRevertMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createWordChangeReportMenuItem(popupMenu);
      createChangeReport(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createCopyMenu(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createCompressWordMenu(popupMenu);
      new GlobalMenu(popupMenu, globalMenuHelper);
      changeTree.setMenu(popupMenu);
   }

   IGlobalMenuHelper globalMenuHelper = new IGlobalMenuHelper() {

      public java.util.Collection<Artifact> getArtifacts() {
         return getHeadArtifactsForSelection();
      };

      public Collection<GlobalMenuItem> getValidMenuItems() {
         return java.util.Collections.emptyList();
      }

   };

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(changeTable.getControl(), "change_report_table");
   }

   private void createActions() {

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            Object input = changeTable.getInput();
            if (input != null) {
               try {
                  ((ChangeReportInput) input).setForceRefresh(true);
                  ((BranchContentProvider) changeTable.getContentProvider()).refresh(true);
                  refreshContentDescription();
                  changeTable.refresh();
               } catch (OseeCoreException ex) {
                  OSEELog.logException(getClass(), ex, true);
               } catch (SQLException ex) {
                  OSEELog.logException(getClass(), ex, true);
               }
            }
         }
      };
      refreshAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      sortAction = new Action("Sort", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (sortAction.isChecked()) {
               if (attributeModifiedArtifactIds == null) {
                  attributeModifiedArtifactIds = new ArrayList<Integer>();
                  try {
                     TransactionId baseTransId = ((ChangeReportInput) changeTable.getInput()).getBaseTransaction();
                     TransactionId toTransId = ((ChangeReportInput) changeTable.getInput()).getToTransaction();
                     for (Artifact artifact : RevisionManager.getInstance().getNewAndModifiedArtifacts(baseTransId,
                           toTransId, false)) {
                        attributeModifiedArtifactIds.add(artifact.getArtId());
                     }
                  } catch (SQLException ex) {
                     OSEELog.logSevere(SkynetGuiPlugin.class, "Error getting modified artifacts", true);
                  }
               }
               ((BranchLabelProvider) changeTable.getLabelProvider()).setShowChangeType(true,
                     attributeModifiedArtifactIds);
               changeTable.setSorter(viewerSorter);
            } else {
               ((BranchLabelProvider) changeTable.getLabelProvider()).setShowChangeType(false, new ArrayList<Integer>());
               changeTable.setSorter(new LabelSorter());
            }
         }
      };
      sortAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("sort.gif"));
      sortAction.setToolTipText("Sort changes by Modified, Modified by Relation Only, New and Deleted");
      Action expandAllAction = new Action("Expand All") {

         @Override
         public void run() {
            changeTable.expandAll();
         }
      };
      expandAllAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("expandAll.gif"));
      expandAllAction.setToolTipText("Expand All");
      Action collapseAllAction = new Action("Collapse All") {

         @Override
         public void run() {
            changeTable.collapseAll();
         }
      };
      collapseAllAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("collapseAll.gif"));
      collapseAllAction.setToolTipText("Collapse All");
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(expandAllAction);
      toolbarManager.add(collapseAllAction);
      toolbarManager.add(refreshAction);
      toolbarManager.add(sortAction);
      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Change Report");
   }

   ViewerSorter viewerSorter = new ViewerSorter() {

      @Override
      @SuppressWarnings("unchecked")
      public int compare(Viewer viewer, Object o1, Object o2) {
         if ((o1 instanceof TreeNode) && (o2 instanceof TreeNode)) {
            if ((((TreeNode) o1).getBackingData() instanceof ArtifactChange) && (((TreeNode) o2).getBackingData() instanceof ArtifactChange)) {
               ArtifactChange artChg1 = (ArtifactChange) ((TreeNode) o1).getBackingData();
               ArtifactChange artChg2 = (ArtifactChange) ((TreeNode) o2).getBackingData();
               if (artChg1.getModType() == artChg2.getModType()) {
                  boolean art1RelChgOnly = false;
                  boolean art2RelChgOnly = false;
                  try {
                     art1RelChgOnly = !attributeModifiedArtifactIds.contains(artChg1.getArtifact());
                     art2RelChgOnly = !attributeModifiedArtifactIds.contains(artChg2.getArtifact());
                     // sort relation change only artifacts last
                     if ((art1RelChgOnly && art2RelChgOnly) || (!art1RelChgOnly && !art2RelChgOnly))
                        getComparator().compare(artChg1.getName(), artChg2.getName());
                     else if (art1RelChgOnly)
                        return 1;
                     else
                        return -1;
                  } catch (Exception ex) {
                     // do nothing since this is comparator, errors will
                     // be too many
                  }
                  return getComparator().compare(artChg1.getName(), artChg2.getName());
               } else if (artChg1.getModType() == ModificationType.CHANGE)
                  return -1;
               else if (artChg2.getModType() == ModificationType.CHANGE)
                  return 1;
               else if (artChg1.getModType() == ModificationType.NEW)
                  return -1;
               else if (artChg2.getModType() == ModificationType.NEW)
                  return 1;
               else if (artChg1.getModType() == ModificationType.DELETED)
                  return -1;
               else if (artChg2.getModType() == ModificationType.DELETED)
                  return 1;
               else
                  return getComparator().compare(artChg1.getName(), artChg2.getName());
            }
         }
         return 0;
      }

   };

   private void createColumns() {
      Tree tree = changeTable.getTree();

      tree.setHeaderVisible(true);
      TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
      column1.setWidth(300);
      column1.setText(columnNames[0]);

      TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
      column2.setWidth(140);
      column2.setText(columnNames[1]);

      TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
      column3.setWidth(200);
      column3.setText(columnNames[2]);

      TreeColumn column4 = new TreeColumn(tree, SWT.LEFT);
      column4.setWidth(200);
      column4.setText(columnNames[3]);
   }

   /**
    * Compares the first and last version for the range of the selected artifact.
    * 
    * @param popupMenu
    */
   private void createDiffMenuItem(Menu popupMenu) {
      diffMenuItem = new MenuItem(popupMenu, SWT.PUSH);
      diffMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent event) {
            ArtifactChange selectedItem =
                  (ArtifactChange) ((ITreeNode) ((IStructuredSelection) changeTable.getSelection()).getFirstElement()).getBackingData();

            try {
               if (selectedItem.getArtifact() != null) {
                  Artifact firstArtifact =
                        selectedItem.getModType() == NEW ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                              selectedItem.getArtifact().getArtId(), selectedItem.getBaselineTransactionId());
                  Artifact secondArtifact =
                        selectedItem.getModType() == DELETED ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                              selectedItem.getArtifact().getArtId(), selectedItem.getToTransactionId());

                  RendererManager.getInstance().compareInJob(firstArtifact, secondArtifact, DIFF_ARTIFACT);
               }
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, false);
            }
         }

         public void widgetDefaultSelected(SelectionEvent ev) {
         }
      });
   }

   private void createWordChangeReportMenuItem(Menu popupMenu) {
      MenuItem wordChangeMenuItem = new MenuItem(popupMenu, SWT.PUSH);
      wordChangeMenuItem.setText("View Word Change Report");

      wordChangeMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent event) {
            IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();
            Iterator<?> iterator = selection.iterator();
            int listSize = selection.size();
            ArtifactChange selectedItem = null;

            ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>(listSize);
            ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>(listSize);

            while (iterator.hasNext()) {
               selectedItem = (ArtifactChange) ((ITreeNode) iterator.next()).getBackingData();

               try {
                  Artifact baseArtifact =
                        selectedItem.getModType() == NEW ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                              selectedItem.getArtifact().getArtId(), selectedItem.getBaselineTransactionId());
                  Artifact newerArtifact =
                        selectedItem.getModType() == DELETED ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                              selectedItem.getArtifact().getArtId(), selectedItem.getToTransactionId());

                  baseArtifacts.add(baseArtifact);
                  newerArtifacts.add(newerArtifact);
               } catch (Exception e1) {
                  OSEELog.logException(getClass(), e1, true);
               }
            }
            WordRenderer renderer =
                  (WordRenderer) RendererManager.getInstance().getRendererById(WordRenderer.WORD_RENDERER_EXTENSION);

            try {
               renderer.compareArtifacts(baseArtifacts, newerArtifacts, DIFF_ARTIFACT, null,
                     selectedItem.getBaselineTransactionId().getBranch());
            } catch (CoreException ex) {
               OSEELog.logException(getClass(), ex, true);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });
   }

   private void createParentDiffMenuItem(Menu popupMenu) {
      diffOnParentBranchMenuItem = new MenuItem(popupMenu, SWT.PUSH);
      diffOnParentBranchMenuItem.setText("Word changes made to parent branch since creating current branch");
      diffOnParentBranchMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent event) {
            ArtifactChange selectedItem =
                  (ArtifactChange) ((ITreeNode) ((IStructuredSelection) changeTable.getSelection()).getFirstElement()).getBackingData();

            try {
               Artifact firstArtifact =
                     selectedItem.getModType() == NEW ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                           selectedItem.getArtifact().getArtId(), selectedItem.getBaselineTransactionId());

               Artifact secondArtifact = null;
               Branch parentBranch = firstArtifact.getBranch().getParentBranch();

               secondArtifact =
                     selectedItem.getModType() == DELETED ? null : ArtifactQuery.getArtifactFromId(
                           selectedItem.getArtifact().getArtId(), parentBranch);

               RendererManager.getInstance().compareInJob(firstArtifact, secondArtifact, DIFF_ARTIFACT);

            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      });
   }

   /**
    * Compares the first and last version for the range of the selected artifact.
    * 
    * @param popupMenu
    */
   private void createDiffConflictsMenuItem(Menu popupMenu) {
      diffConflictsMenuItem = new MenuItem(popupMenu, SWT.PUSH);
      diffConflictsMenuItem.setText("Word differences between current/parent branches");
      diffConflictsMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent event) {
            ArtifactChange selectedItem =
                  (ArtifactChange) ((ITreeNode) ((IStructuredSelection) changeTable.getSelection()).getFirstElement()).getBackingData();

            try {
               Artifact secondArtifact =
                     ArtifactPersistenceManager.getInstance().getArtifactFromId(selectedItem.getArtifact().getArtId(),
                           toTransactionId);
               RendererManager.getInstance().compareInJob(selectedItem.getConflictingModArtifact(), secondArtifact,
                     DIFF_ARTIFACT);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, false);
            }
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });
   }

   private void createHistoryMenuItem(Menu parentMenu) {
      revisionMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      revisionMenuItem.setText("&Show Resource History ");
      revisionMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            ArtifactChange selectedItem =
                  (ArtifactChange) ((ITreeNode) ((IStructuredSelection) changeTable.getSelection()).getFirstElement()).getBackingData();

            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               Artifact selectedArtifact = selectedItem.getArtifact();

               RevisionHistoryView revisionHistoryView =
                     (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, selectedArtifact.getGuid(),
                           IWorkbenchPage.VIEW_VISIBLE);
               revisionHistoryView.explore(selectedArtifact);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      });
   }

   private void createViewFinalVersionMenuItem(Menu parentMenu) {
      showFinalVersionMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      showFinalVersionMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            ArtifactChange selectedItem =
                  (ArtifactChange) ((ITreeNode) ((IStructuredSelection) changeTable.getSelection()).getFirstElement()).getBackingData();
            Artifact selectedArtifact;
            try {
               selectedArtifact = selectedItem.getArtifact();
               ArtifactEditor.editArtifact(selectedArtifact);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      });
   }

   private void createRevertMenuItem(Menu parentMenu) {
      revertMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      revertMenuItem.setText("&Revert Artifact...");
      revertMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            ArtifactChange selectedItem =
                  (ArtifactChange) ((ITreeNode) ((IStructuredSelection) changeTable.getSelection()).getFirstElement()).getBackingData();

            // This is serious stuff, make sure the user understands the
            // impact.
            if (MessageDialog.openConfirm(
                  changeTable.getTree().getShell(),
                  "Confirm Revert of " + selectedItem.getName(),
                  "All attribute changes for the artifact and all link changes that involve the artifact on this branch will be reverted." + "\n\nTHIS IS IRREVERSIBLE" + "\n\nOSEE must be restarted after all reverting is finished to see the results")) {

               Jobs.startJob(new RevertJob(selectedItem.getName(), selectedItem.getArtifact().getArtId()));
            }
         }
      });
   }

   private void createChangeReport(Menu parentMenu) {
      changeReportMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      changeReportMenuItem.setText("View &Change Report");
      changeReportMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            (new TreeViewerReport(changeTable)).open();
         }
      });
   }

   private void createCopyMenu(Menu parentMenu) {
      copyMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      copyMenuItem.setText("Copy \tCtrl+C");
      copyMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performCopy();
         }
      });
   }

   private void performCopy() {
      IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();
      ArrayList<Artifact> artifactTransferData = new ArrayList<Artifact>();
      ArrayList<String> textTransferData = new ArrayList<String>();
      Artifact artifact;

      if (selection != null && !selection.isEmpty()) {
         for (Object object : selection.toArray()) {

            if (object instanceof ITreeNode && ((ITreeNode) object).getBackingData() instanceof ArtifactChange) {
               try {
                  artifact = ((ArtifactChange) ((ITreeNode) object).getBackingData()).getArtifact();
                  if (AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                     artifactTransferData.add(artifact);
                     textTransferData.add(artifact.getDescriptiveName());
                  }
               } catch (Exception ex) {
                  OSEELog.logException(getClass(), ex, true);
               }
            }
         }

         if (artifactTransferData.size() > 0) artifactClipboard.setArtifactsToClipboard(artifactTransferData,
               textTransferData);
      }
   }

   private void createCompressWordMenu(Menu parentMenu) {
      compressWordAttributes = new MenuItem(parentMenu, SWT.PUSH);
      compressWordAttributes.setText("Co&mpress Word Attributes");
      compressWordAttributes.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               performCompression();
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      });
   }

   private List<Artifact> getHeadArtifactsForSelection() {
      IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();
      if (selection != null && !selection.isEmpty()) {
         Object[] selectedObjects = selection.toArray();
         List<Artifact> artifacts = new ArrayList<Artifact>(selectedObjects.length);

         for (Object object : selectedObjects) {
            if (object instanceof ITreeNode && ((ITreeNode) object).getBackingData() instanceof ArtifactChange) {
               try {
                  Artifact artifact = ((ArtifactChange) ((ITreeNode) object).getBackingData()).getArtifact();
                  Artifact headArtifact = ArtifactQuery.getArtifactFromId(artifact.getGuid(), artifact.getBranch());
                  artifacts.add(headArtifact);
               } catch (Exception ex) {
                  OSEELog.logException(getClass(), ex, true);
               }
            }
         }
         return artifacts;
      }

      return new ArrayList<Artifact>(0);
   }

   private void createShowInExplorerMenuItem(Menu parentMenu) {
      showInExplorer = new MenuItem(parentMenu, SWT.CASCADE);
      showInExplorer.setText("Show in Artifact Explorer");
      showInExplorer.setEnabled(true);
      showInExplorer.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            for (Artifact artifact : getHeadArtifactsForSelection()) {
               ArtifactExplorer.revealArtifact(artifact);
            }
         }
      });
   }

   private void performCompression() throws OseeCoreException, SQLException {
      IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();
      final Collection<Integer> artifacts = new LinkedList<Integer>();
      Branch aBranch = null;
      if (selection != null && !selection.isEmpty()) {
         for (Object object : selection.toArray()) {

            if (object instanceof ITreeNode) {
               Artifact artifact = ((ArtifactChange) ((ITreeNode) object).getBackingData()).getArtifact();
               artifacts.add(artifact.getArtId());
               if (aBranch == null) {
                  aBranch = artifact.getBranch();
               }
            }
         }
      }

      final Branch branch = aBranch;

      Jobs.startJob(new Job("Compress Word Attributes") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final int total = artifacts.size();
               int count = 0;

               monitor.beginTask("Analyzing attributes", total);

               for (Integer artifact : artifacts) {
                  if (WordUtil.revertNonusefulWordChanges(artifact, branch, "osee_compression_gammas")) count++;
                  monitor.worked(1);
                  if (monitor.isCanceled()) {
                     monitor.done();
                     return Status.CANCEL_STATUS;
                  }
               }

               final int finalCount = count;
               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     MessageDialog.openInformation(changeTable.getControl().getShell(), "Compression Data",
                           finalCount + " of the " + total + " artifacts need compression");
                  }
               });

               monitor.done();
               return Status.OK_STATUS;
            } catch (SQLException ex) {
               return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
            }
         }

      });
   }

   @Override
   public void setFocus() {
      if (changeTable != null) changeTable.getControl().setFocus();
   }

   public static void openViewUpon(final Branch branch) {
      Job job = new Job("Open Change Report") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               Pair<TransactionId, TransactionId> transactionToFrom = TransactionIdManager.getStartEndPoint(branch);
               if (transactionToFrom.getKey().equals(transactionToFrom.getValue())) {
                  AWorkbench.popup("Information", "There are no changes on this branch.");
                  monitor.done();
                  return Status.OK_STATUS;
               }

               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     try {
                        IWorkbenchPage page = AWorkbench.getActivePage();
                        ChangeReportView changeReportView =
                              (ChangeReportView) page.showView(VIEW_ID, String.valueOf(branch.getBranchId()),
                                    IWorkbenchPage.VIEW_ACTIVATE);

                        changeReportView.explore(branch);
                     } catch (PartInitException ex) {
                        OSEELog.logException(ChangeReportView.class, ex, true);
                     }
                  }
               });
            } catch (SQLException ex) {
               OSEELog.logException(ChangeReportView.class, ex, true);
            } catch (OseeCoreException ex) {
               OSEELog.logException(ChangeReportView.class, ex, true);
            }

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   public static void openViewUpon(ChangeReportInput input) throws PartInitException {
      IWorkbenchPage page = AWorkbench.getActivePage();
      ChangeReportView changeReportView =
            (ChangeReportView) page.showView(VIEW_ID, String.valueOf(input.getToTransaction().getTransactionNumber()),
                  IWorkbenchPage.VIEW_ACTIVATE);
      changeReportView.explore(input);
   }

   /**
    * Explores the changes between to transactions. The transactions must not be null, must be on the same branch, and
    * the toTransaction must be after the baseTransaction. Conflicting changes against the origination branch can also
    * be reported. Conflicts can only be detected for a transactions on a branch that have a parent branch.
    */
   public void explore(final ChangeReportInput input) {
      this.baseParentTransactionId = input.getBaseParentTransactionId();
      this.baseTransactionId = input.getBaseTransaction();
      this.toTransactionId = input.getToTransaction();

      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            setPartName(input.getName());

            changeTable.setInput(input);

            try {
               attributesAction.setValidAttributeTypes(SkynetViews.loadAttrTypesFromPreferenceStore(
                     SkynetGuiPlugin.CHANGE_REPORT_ATTRIBUTES_PREF, baseTransactionId.getBranch()));
            } catch (SQLException ex) {
               OSEELog.logException(getClass(), ex, false);
            }
            int baseNum = baseTransactionId.getTransactionNumber();
            int toNum = toTransactionId.getTransactionNumber();
            if (input.isEmptyChange())
               setContentDescription("Changes on " + input.getBranch() + " at " + new Date());
            else if (baseParentTransactionId == null)
               setContentDescription("Changes on " + baseTransactionId.getBranch().getBranchName() + " from transaction " + baseNum + " to transaction " + toNum + " at " + new Date());
            else
               setContentDescription("Changes on " + baseTransactionId.getBranch().getBranchName() + " from transaction " + baseNum + " to transaction " + toNum + " against " + baseParentTransactionId.getBranch() + ":" + baseParentTransactionId.getTransactionNumber() + " at " + new Date());

            ((BranchContentProvider) changeTable.getContentProvider()).refresh(true);
         }
      });
   }

   public void refreshContentDescription() {
      int baseNum = this.baseTransactionId.getTransactionNumber();
      int toNum = this.toTransactionId.getTransactionNumber();
      if (this.baseParentTransactionId == null) {
         setContentDescription("Changes on " + this.baseTransactionId.getBranch().getBranchName() + " from transaction " + baseNum + " to transaction " + toNum + " at " + new Date());
      } else {
         setContentDescription("Changes on " + this.baseTransactionId.getBranch().getBranchName() + " from transaction " + baseNum + " to transaction " + toNum + " against " + baseParentTransactionId.getBranch() + ":" + baseParentTransactionId.getTransactionNumber() + " at " + new Date());
      }
   }

   /**
    * Explores the changes on the branch.
    * 
    * @param branch
    * @throws SQLException
    */
   public void explore(final Branch branch) {
      if (branch == null) throw new IllegalArgumentException("branch can not be null");

      Job job = new Job("Compute Change Report Input") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               ChangeReportInput input = new ChangeReportInput(branch);
               explore(input);
            } catch (SQLException ex) {
               OSEELog.logException(ChangeReportView.class, ex, true);
            } catch (OseeCoreException ex) {
               OSEELog.logException(ChangeReportView.class, ex, true);
            }

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   public String getActionDescription() {
      return "";
   }

   /**
    * @author Robert A. Fisher
    */
   public class MenuEnablingListener implements MenuListener {

      public void menuHidden(MenuEvent e) {
      }

      @SuppressWarnings("unchecked")
      public void menuShown(MenuEvent e) {
         boolean readPermission = true;
         boolean writePermission = true;
         IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();

         Iterator<ITreeNode> iter = selection.iterator();
         Object obj1 = null;
         Object obj2 = null;
         if (iter.hasNext()) {
            obj1 = iter.next().getBackingData();
            if (iter.hasNext()) {
               obj2 = iter.next().getBackingData();
            }
         }
         iter = selection.iterator();
         while (iter.hasNext() && (readPermission || writePermission)) {
            Object object = iter.next().getBackingData();
            if (object instanceof ArtifactChange) {
               try {
                  Artifact artifact = ((ArtifactChange) object).getArtifact();

                  if (artifact != null) {
                     readPermission &= AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ);
                     writePermission &= AccessControlManager.checkObjectPermission(artifact, PermissionEnum.WRITE);
                  }
               } catch (Exception ex) {
                  readPermission = false;
                  writePermission = false;
                  OSEELog.logException(getClass(), ex, false);
               }
            }
         }

         boolean artifactSelected = false;
         try {
            artifactSelected =
                  obj1 instanceof ArtifactChange && obj2 == null && ((ArtifactChange) obj1).getArtifact() != null;
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, false);
         }

         if (artifactSelected) {
            try {
               ArtifactChange change = (ArtifactChange) obj1;
               Artifact changedArtifact = change.getArtifact();
               Branch reportBranch = changedArtifact.getBranch();
               Branch parentBranch = reportBranch.getParentBranch();

               boolean wordArtifactSelected = artifactSelected && changedArtifact instanceof WordArtifact;
               boolean modifiedWordArtifactSelected = wordArtifactSelected && change.getModType() == CHANGE;
               boolean conflictedWordArtifactSelected =
                     modifiedWordArtifactSelected && change.getChangeType() == ChangeType.CONFLICTING;
               boolean validDiffParent = wordArtifactSelected && parentBranch != null;

               showInExplorer.setEnabled(artifactSelected && reportBranch == BranchPersistenceManager.getDefaultBranch());

               copyMenuItem.setEnabled(readPermission);
               // showFinalWordVersionMenuItem.setEnabled(wordArtifactSelected
               // && readPermission);
               diffMenuItem.setEnabled(wordArtifactSelected && readPermission);
               diffMenuItem.setText("Word changes made to \"" + reportBranch.getBranchShortName() + "\"");

               diffConflictsMenuItem.setEnabled(conflictedWordArtifactSelected && readPermission);
               if (conflictedWordArtifactSelected) {
                  diffConflictsMenuItem.setText("Word differences between \"" + reportBranch.getBranchShortName() + "\" and \"" + parentBranch.getBranchShortName() + "\"");
               }

               revertMenuItem.setEnabled(artifactSelected && writePermission);
               compressWordAttributes.setEnabled(artifactSelected && writePermission && OseeProperties.isDeveloper());

               diffOnParentBranchMenuItem.setEnabled(validDiffParent && modifiedWordArtifactSelected && readPermission);

               if (diffOnParentBranchMenuItem.getEnabled()) {
                  diffOnParentBranchMenuItem.setText("Word changes made to \"" + parentBranch.getBranchShortName() + "\" since creating \"" + reportBranch.getBranchShortName() + "\"");
               }

               if (showFinalVersionMenuItem.getEnabled()) {
                  showFinalVersionMenuItem.setText(SHOW_FINAL_VERSION_TXT + " (" + change.getToTransactionId().getTransactionNumber() + ")");
               } else {
                  showFinalVersionMenuItem.setText(SHOW_FINAL_VERSION_TXT);
               }

               // if (showFinalWordVersionMenuItem.getEnabled()) {
               // showFinalWordVersionMenuItem.setText(SHOW_FINAL_WORD_VERSION_TXT
               // + " ("
               // + change.getToTransactionId().getTransactionNumber() +
               // ")");
               // }
               // else {
               // showFinalWordVersionMenuItem.setText(SHOW_FINAL_WORD_VERSION_TXT);
               // }
            } catch (Exception ex) {
               compressWordAttributes.setEnabled(false);
               copyMenuItem.setEnabled(false);
               diffMenuItem.setEnabled(false);
               diffOnParentBranchMenuItem.setEnabled(false);
               diffConflictsMenuItem.setEnabled(false);
               revertMenuItem.setEnabled(false);
               // showFinalWordVersionMenuItem.setEnabled(false);

               OSEELog.logException(getClass(), ex, true);
            }
         } else { // Set menu items to false if a single artifacts is not
            // selected.
            compressWordAttributes.setEnabled(false);
            diffMenuItem.setEnabled(false);
            diffOnParentBranchMenuItem.setEnabled(false);
            diffConflictsMenuItem.setEnabled(false);
            revertMenuItem.setEnabled(false);
            // showFinalWordVersionMenuItem.setEnabled(false);
         }

         revisionMenuItem.setEnabled(artifactSelected && readPermission);
         showFinalVersionMenuItem.setEnabled(artifactSelected && readPermission);
         changeReportMenuItem.setEnabled(true);
      }
   }

   private class keySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
         if (e.keyCode == 'c' && e.stateMask == SWT.CONTROL) {
            performCopy();
         }
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);

      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               priorInput = ChangeReportInput.loadFromMemento(memento);
            }
         }
      } catch (Exception ex) {
         OSEELog.logWarning(getClass(), "Change report error on init: " + ex.getLocalizedMessage(), false);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);

      Object input = changeTable.getInput();
      if (input instanceof ChangeReportInput) {
         ChangeReportInput changeReportInput = (ChangeReportInput) input;

         memento = memento.createChild(INPUT);

         changeReportInput.saveToMemento(memento);
      }

   }

   public class ChangeReportDragAndDrop extends SkynetDragAndDrop {

      public ChangeReportDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }

      @Override
      public Artifact[] getArtifacts() throws OseeCoreException, SQLException {
         IStructuredSelection selection = (IStructuredSelection) changeTable.getSelection();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

         if (selection != null && !selection.isEmpty()) {
            for (Object object : selection.toArray()) {

               if (object instanceof ITreeNode) {
                  artifacts.add(((ArtifactChange) ((ITreeNode) object).getBackingData()).getArtifact());
               }
            }
         }
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }

      @Override
      public void performDrop(DropTargetEvent event) {
         if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
            event.detail = DND.DROP_MOVE;
         }
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
   }

   /**
    * Revert changes for a given artifact Id up to the transaction point being viewed by this report.<br/><br/> <i>Note
    * that this job is constrained to being able to only revert at most 1000 attribute and 1000 link changes do to its
    * direct use of the SQL IN clause.</i>
    * 
    * @author Robert A. Fisher
    */
   private class RevertJob extends Job {

      private final int artId;

      /**
       * @param name
       * @param artId
       */
      public RevertJob(String name, int artId) {
         super("Reverting Artifact " + name);
         this.artId = artId;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            new RevertDbTx(getName(), artId, monitor, baseTransactionId, toTransactionId).execute();
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, false);
         }
         return Status.OK_STATUS;
      }
   }

   public static final class RevertDbTx extends AbstractDbTxTemplate {

      private final IProgressMonitor monitor;
      private final int artId;
      private final String txName;
      private final TransactionId baseTransactionId;
      private final TransactionId toTransactionId;

      public RevertDbTx(String txName, int artId, IProgressMonitor monitor, TransactionId baseTransactionId, TransactionId toTransactionId) {
         this.monitor = monitor;
         this.txName = txName;
         this.artId = artId;
         this.baseTransactionId = baseTransactionId;
         this.toTransactionId = toTransactionId;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         monitor.beginTask(txName, 7);

         monitor.subTask("Calculating change set");

         Collection<RevisionChange> revisionChanges =
               RevisionManager.getInstance().getAllTransactionChanges(OUTGOING, baseTransactionId, toTransactionId,
                     artId, null);
         int worstSize = revisionChanges.size();
         Collection<Long> attributeGammas = new ArrayList<Long>(worstSize);
         Collection<Long> linkGammas = new ArrayList<Long>(worstSize);
         Collection<Long> artifactGammas = new ArrayList<Long>(worstSize);
         Collection<Long> allGammas = new ArrayList<Long>(worstSize);

         // Categorize all of the changes
         for (RevisionChange change : revisionChanges) {
            if (change instanceof AttributeChange) {
               attributeGammas.add(change.getGammaId());
            } else if (change instanceof RelationLinkChange) {
               linkGammas.add(change.getGammaId());
            } else if (change instanceof ArtifactChange) {
               artifactGammas.add(change.getGammaId());
            }
            allGammas.add(change.getGammaId());
         }

         monitor.worked(1);
         isCanceled();

         monitor.subTask("Cleaning up bookkeeping data");
         if (allGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + TRANSACTIONS_TABLE + " WHERE " + TRANSACTIONS_TABLE.column("gamma_id") + " IN" + Collections.toString(
                  allGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Reverting Artifact gammas");
         if (artifactGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + ARTIFACT_VERSION_TABLE + " WHERE " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " IN " + Collections.toString(
                  artifactGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Reverting attributes");
         if (attributeGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + " IN " + Collections.toString(
                  attributeGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Reverting links");
         if (linkGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + RELATION_LINK_VERSION_TABLE + " WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " IN " + Collections.toString(
                  linkGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Cleaning up empty transactions");
         ConnectionHandler.runPreparedUpdate(
               "DELETE FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " NOT IN " + "(SELECT " + TRANSACTIONS_TABLE.column("transaction_id") + " FROM " + TRANSACTIONS_TABLE + ")",
               baseTransactionId.getBranch().getBranchId());
         monitor.worked(1);

      }

      private boolean isCanceled() throws OseeCoreException {
         boolean toReturn = monitor.isCanceled();
         if (false != toReturn) {
            throw new IllegalStateException("User Cancelled Operation");
         }
         return toReturn;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
         monitor.done();
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Committed) {
         if (changeTable != null && changeTable.getTree().isDisposed() != true) {
            ChangeReportInput changeReportInput = (ChangeReportInput) changeTable.getInput();
            if (changeReportInput != null) {
               Branch branch = changeReportInput.getBranch();
               if (branch != null && branch.getBranchId() == branchId) {
                  changeTable.getTree().setEnabled(false);
               }
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }
}
