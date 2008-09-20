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

package org.eclipse.osee.framework.ui.skynet.branch;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.ConflictDetectionException;
import org.eclipse.osee.framework.skynet.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Files;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.TreeViewerReport;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.export.ExportBranchJob;
import org.eclipse.osee.framework.ui.skynet.export.ImportBranchJob;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.swt.ColumnSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class BranchView extends ViewPart implements IActionable, IBranchEventListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.branch.BranchView";
   private static final String BRANCH_ID = "branchId";
   private static final IParameter[] BRANCH_PARAMETER_DEF = new IParameter[] {new BranchIdParameter()};
   private static final String FAVORITE_KEY = "favorites_first";
   private static final String SHOW_TRANSACTIONS = "show_transactions";
   private static final String SHOW_MERGE_BRANCHES = "show_merge_branches";
   private static final String FLAT_KEY = "flat";
   private static final String[] columnNames = {"", "Short Name", "Time Stamp", "Author", "Comment"};
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchView.class);

   private IHandlerService handlerService;
   private final IPreferencesService preferencesService;
   private IPreferenceChangeListener preferenceChangeListener = null;

   private TreeViewer branchTable;
   private TreeEditor myTreeEditor;
   private Text myTextBeingRenamed;
   private Text filterText;
   private BranchNameFilter nameFilter;
   private FavoritesSorter sorter;
   private boolean disposed;
   private Action hideTransactions;
   private Action hideMergeBranches;

   private synchronized IPreferenceChangeListener getSingleton() {
      if (preferenceChangeListener == null) {
         preferenceChangeListener = new IPreferenceChangeListener() {

            public void preferenceChange(PreferenceChangeEvent event) {
               if (disposed) {
                  ((IEclipsePreferences) event.getNode()).removePreferenceChangeListener(this);
               } else {
                  String propertyName = event.getKey();

                  if (propertyName.equals(FLAT_KEY)) {
                     setPresentation(getViewPreference().getBoolean(FLAT_KEY, true));
                     refresh();
                  }
                  if (propertyName.equals(SHOW_TRANSACTIONS)) {
                     setShowTransactions(getViewPreference().getBoolean(SHOW_TRANSACTIONS, true));
                     branchTable.refresh();
                  }
                  if (propertyName.equals(SHOW_MERGE_BRANCHES)) {
                     setShowMergeBranches(getViewPreference().getBoolean(SHOW_MERGE_BRANCHES, true));
                     branchTable.refresh();
                  }
                  if (propertyName.equals(FAVORITE_KEY)) {
                     if (sorter != null) {
                        sorter.setFavoritesFirst(getViewPreference().getBoolean(FAVORITE_KEY, false));
                        branchTable.refresh();
                     }
                  }
               }
            }
         };
      }

      return preferenceChangeListener;
   }

   /**
    * 
    */
   public BranchView() {
      super();

      this.preferencesService = Platform.getPreferencesService();

      IEclipsePreferences instanceNode =
            (IEclipsePreferences) preferencesService.getRootNode().node(InstanceScope.SCOPE);

      try {
         if (instanceNode.nodeExists(VIEW_ID)) {
            ((IEclipsePreferences) instanceNode.node(VIEW_ID)).addPreferenceChangeListener(getSingleton());
         }
      } catch (BackingStoreException ex) {
         OSEELog.logException(BranchView.class, ex, true);
      }

      instanceNode.addNodeChangeListener(new IEclipsePreferences.INodeChangeListener() {

         public void added(NodeChangeEvent event) {
            if (event.getChild().name().equals(VIEW_ID)) {
               ((IEclipsePreferences) event.getChild()).addPreferenceChangeListener(getSingleton());
            }
         }

         public void removed(NodeChangeEvent event) {
            if (event.getChild().name().equals(VIEW_ID)) {
               ((IEclipsePreferences) event.getChild()).removePreferenceChangeListener(getSingleton());
            }
         }
      });
   }

   public static BranchView getBranchView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (BranchView) page.showView(VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch Branch View " + e1.getMessage());
      }
      return null;
   }

   @Override
   public void createPartControl(Composite parent) {
      try {

         if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

         PlatformUI.getWorkbench().getService(IHandlerService.class);
         handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

         parent.setLayout(new GridLayout());
         parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         createTableViewer(parent);
         createColumns();
         createFilter(parent);

         createActions();

         SkynetContributionItem.addTo(this, true);
         setHelpContexts();
         myTreeEditor = new TreeEditor(branchTable.getTree());
         myTreeEditor.horizontalAlignment = SWT.LEFT;
         myTreeEditor.grabHorizontal = true;
         myTreeEditor.minimumWidth = 50;

         forcePopulateView();
         OseeEventManager.addListener(this);
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   protected void createActions() {

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            try {
               forcePopulateView();
            } catch (SQLException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      };
      refreshAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Branch Manager");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(branchTable.getControl(), "branch_manager_table");
      SkynetGuiPlugin.getInstance().setHelp(filterText, "branch_manager_filtering");
   }

   private void createTableViewer(Composite parent) {
      ITableLabelProvider labelProvider = new BranchLabelProvider(null);

      branchTable = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
      branchTable.setContentProvider(new BranchContentProvider());
      branchTable.setLabelProvider(labelProvider);
      branchTable.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sorter = new FavoritesSorter(labelProvider);
      sorter.setFavoritesFirst(getViewPreference().getBoolean(FAVORITE_KEY, false));
      branchTable.setSorter(sorter);

      nameFilter = new BranchNameFilter();
      branchTable.addFilter(nameFilter);

      MenuManager menuManager = new MenuManager("#PopupMenu");
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            fillPopupMenu(manager);
         }
      });

      branchTable.getTree().setMenu(menuManager.createContextMenu(branchTable.getTree()));

      menuManager.add(new Separator());
      createOpenArtifactsMenuItem(menuManager);
      menuManager.add(new Separator());
      createSetDefaultCommand(menuManager);
      createBranchCommand(menuManager);
      createSelectivelyBranchCommand(menuManager);
      createCommitCommand(menuManager);
      createCommitIntoCommand(menuManager);
      menuManager.add(new Separator());
      createImportOntoBranchCommand(menuManager);
      createImportDescendantsOntoBranchCommand(menuManager);
      createExportBranchCommand(menuManager);
      createExportBranchDescendantsCommand(menuManager);
      menuManager.add(new Separator());
      createMarkAsFavoriteCommand(menuManager);
      menuManager.add(new Separator());
      createDeleteBranchCommand(menuManager);
      createDeleteTransactionCommand(menuManager);
      createMoveTransactionCommand(menuManager);
      createRenameBranchCommand(menuManager);
      createSetBranchShortNameCommand(menuManager);
      createSetAssociatedArtifactCommand(menuManager);
      createOpenAssociatedArtifactCommand(menuManager);
      menuManager.add(new Separator());
      createViewTableMenuItem(menuManager);
      menuManager.add(new Separator());
      createAccessControlCommand(menuManager);
      createMergeViewCommand(menuManager);
      createChangeViewCommand(menuManager);
      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.branch.BranchView", menuManager, branchTable);

      branchTable.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));

      getSite().setSelectionProvider(branchTable);

      IMenuManager toolbarManager = getViewSite().getActionBars().getMenuManager();
      toolbarManager.add(createFavoritesFirstAction());
      toolbarManager.add(createShowTransactionsAction());
      if (OseeProperties.isDeveloper()) {
         toolbarManager.add(createShowMergeBranchesAction());
      }
      toolbarManager.add(new ParentBranchAction(this));

      loadPreferences();
   }

   private void fillPopupMenu(IMenuManager Manager) {
      MenuManager menuManager = (MenuManager) Manager;
      menuManager.add(new Separator());
      addOpenArtifactsMenuItem(menuManager);
      menuManager.add(new Separator());
      addSetDefaultCommand(menuManager);
      addChangeViewCommand(menuManager);
      addMergeViewCommand(menuManager);
      addBranchCommand(menuManager);
      addSelectivelyBranchCommand(menuManager);
      addCommitCommand(menuManager);
      addCommitIntoCommand(menuManager);
      menuManager.add(new Separator());
      addImportOntoBranchCommand(menuManager);
      addImportDescendantsOntoBranchCommand(menuManager);
      addExportBranchCommand(menuManager);
      addExportBranchDescendantsCommand(menuManager);
      menuManager.add(new Separator());
      addMarkAsFavoriteCommand(menuManager);
      menuManager.add(new Separator());
      addDeleteBranchCommand(menuManager);
      addDeleteTransactionCommand(menuManager);
      addMoveTransactionCommand(menuManager);
      addRenameBranchCommand(menuManager);
      addSetBranchShortNameCommand(menuManager);
      addSetAssociatedArtifactCommand(menuManager);
      addOpenAssociatedArtifactCommand(menuManager);
      menuManager.add(new Separator());
      addViewTableMenuItem(menuManager);
      menuManager.add(new Separator());
      addAccessControlCommand(menuManager);
      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
   }

   class BranchArtifact implements IBranchArtifact {

      private final Branch branch;

      public BranchArtifact(Branch branch) {
         this.branch = branch;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact#getArtifact()
       */
      public Artifact getArtifact() {
         try {
            return branch.getAssociatedArtifact();
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact#getWorkingBranch()
       */
      public Branch getWorkingBranch() throws IllegalStateException, SQLException, MultipleBranchesExist {
         return branch;
      }

   }

   private String addMergeViewCommand(MenuManager menuManager) {
      CommandContributionItem mergeViewCommand =
            Commands.getLocalCommandContribution(getSite(), "mergeViewCommand", "Merge Manager", null, null, null, "M",
                  null, null);
      menuManager.add(mergeViewCommand);
      return mergeViewCommand.getId();
   }

   private void createMergeViewCommand(MenuManager menuManager) {

      handlerService.activateHandler(addMergeViewCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
            try {
               if (selectedBranch != null && (!(selectedBranch.getAssociatedArtifact() instanceof IATSArtifact)) && selectedBranch.getParentBranch() != null) {
                  MergeView.openView(selectedBranch, selectedBranch.getParentBranch(),
                        TransactionIdManager.getStartEndPoint(selectedBranch).getKey());
               }
            } catch (Exception ex) {
               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }

            return null;
         }

         @Override
         public boolean isEnabled() {
            try {
               IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
               if (!selection.isEmpty()) {
                  Object obj = ((JobbedNode) selection.getFirstElement()).getBackingData();
                  if (obj instanceof Branch) {
                     Branch selectedBranch = (Branch) obj;
                     return (selectedBranch != null && (!(selectedBranch.getAssociatedArtifact() instanceof IATSArtifact)) && selectedBranch.getParentBranch() != null);
                  }
                  return false;
               }
               return false;
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, false);
            }
            return false;
         }
      });
   }

   private String addChangeViewCommand(MenuManager menuManager) {
      CommandContributionItem changeViewCommand =
            Commands.getLocalCommandContribution(getSite(), "change2ViewCommand", "Change Report", null, null, null,
                  "M", null, null);
      menuManager.add(changeViewCommand);
      return changeViewCommand.getId();
   }

   private void createChangeViewCommand(MenuManager menuManager) {

      handlerService.activateHandler(addChangeViewCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
            try {
               ChangeView.open(selectedBranch);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }

            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private Preferences getViewPreference() {
      return preferencesService.getRootNode().node(InstanceScope.SCOPE).node(VIEW_ID);
   }

   private void loadPreferences() {
      setPresentation(getViewPreference().getBoolean(FLAT_KEY, true));
      setShowTransactions(getViewPreference().getBoolean(SHOW_TRANSACTIONS, true));
   }

   private void createFilter(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      Label label = new Label(composite, SWT.NONE);
      label.setText("Filter:");

      filterText = new Text(composite, SWT.BORDER);
      filterText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            nameFilter.setContains(((Text) e.getSource()).getText());
            branchTable.refresh();
            if (nameFilter.isFiltering())
               setContentDescription("Filtered for :\"" + nameFilter.getContains() + "\"");
            else
               setContentDescription("");
         }
      });
      filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
   }

   private String addOpenArtifactsMenuItem(MenuManager menuManager) {
      CommandContributionItem openArtifactsCommand =
            Commands.getLocalCommandContribution(getSite(), "openArtifactsCommand", "Open Artifact(s)...", null, null,
                  null, "O", null, null);
      menuManager.add(openArtifactsCommand);
      return openArtifactsCommand.getId();
   }

   private void createOpenArtifactsMenuItem(MenuManager menuManager) {

      branchTable.addDoubleClickListener(new IDoubleClickListener() {

         public void doubleClick(DoubleClickEvent event) {
            openArtifactHelper();
         }
      });

      handlerService.activateHandler(addOpenArtifactsMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            openArtifactHelper();
            return null;
         }

         @Override
         public boolean isEnabled() {
            boolean toReturn = false;
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            if (!selection.isEmpty()) {
               Iterator<?> iterator = selection.iterator();
               while (iterator.hasNext()) {
                  Object backingData = ((JobbedNode) iterator.next()).getBackingData();
                  if (backingData instanceof ArtifactChange) {
                     toReturn = true;
                     break;
                  }
               }
            }
            return toReturn;
         }
      });
   }

   private void openArtifactHelper() {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
         Object backingData = ((JobbedNode) iterator.next()).getBackingData();
         if (backingData instanceof ArtifactChange) {
            try {
               ArtifactEditor.editArtifact(((ArtifactChange) backingData).getArtifact());
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      }
   }

   private String addAccessControlCommand(MenuManager menuManager) {
      CommandContributionItem accessControlCommand =
            Commands.getLocalCommandContribution(getSite(), "accessControlCommand", "Access Control...", null, null,
                  null, "A", null, null);
      menuManager.add(accessControlCommand);
      return accessControlCommand.getId();
   }

   private void createAccessControlCommand(MenuManager menuManager) {

      handlerService.activateHandler(addAccessControlCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
            try {
               if (selectedBranch != null) {
                  PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), selectedBranch);
                  pd.open();
               }
            } catch (Exception ex) {
               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }

            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            if (!selection.isEmpty()) {
               return SkynetSelections.oneBranchSelected(selection) && (AccessControlManager.checkObjectPermission(
                     SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.FULLACCESS) || OseeProperties.isDeveloper());
            }
            return false;
         }
      });
   }

   private String addDeleteTransactionCommand(MenuManager menuManager) {
      CommandContributionItem deleteTransactionCommand =
            Commands.getLocalCommandContribution(getSite(), "deleteTransactionCommand", "Delete Transaction", null,
                  null, null, "D", null, null);
      menuManager.add(deleteTransactionCommand);
      return deleteTransactionCommand.getId();
   }

   private void createDeleteTransactionCommand(MenuManager menuManager) {

      handlerService.activateHandler(addDeleteTransactionCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            TransactionData selectedTransaction =
                  (TransactionData) ((JobbedNode) selection.getFirstElement()).getBackingData();

            if (MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Delete Transaction",
                  "Are you sure you want to delete the transaction: " + selectedTransaction.getTransactionNumber())) {
               BranchPersistenceManager.deleteTransactions(new JobChangeAdapter() {

                  /* (non-Javadoc)
                   * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
                   */
                  @Override
                  public void done(IJobChangeEvent event) {
                     if (event.getResult().getSeverity() == IStatus.OK) {
                        Display.getDefault().asyncExec(new Runnable() {
                           public void run() {
                              try {
                                 forcePopulateView();
                              } catch (SQLException ex) {
                                 OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                              }
                           }
                        });
                     }
                  }

               }, selectedTransaction.getTransactionNumber());
            }

            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneTransactionSelected(selection) && OseeProperties.isDeveloper();
         }
      });
   }

   private static class BranchIdParameter implements IParameter {
      public String getId() {
         return BRANCH_ID;
      }

      public String getName() {
         return "Branch Id";
      }

      public IParameterValues getValues() throws ParameterValuesException {
         return null;
      }

      public boolean isOptional() {
         return false;
      }
   }

   private void addBranchSelectionMenu(MenuManager menuManager) {
      try {
         for (Branch branch : BranchPersistenceManager.getBranches()) {

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(BRANCH_ID, Integer.toString(branch.getBranchId()));

            CommandContributionItem branchCommand =
                  Commands.getLocalCommandContribution(getSite(), menuManager.getId(), branch.getBranchName(),
                        BRANCH_PARAMETER_DEF, parameters, null, null, null, null);
            menuManager.add(branchCommand);
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   private void createBranchSelectionMenu(MenuManager menuManager, IHandler selectionHandler) {
      addBranchSelectionMenu(menuManager);
      handlerService.activateHandler(getSite().getId() + "." + menuManager.getId(), selectionHandler);
   }

   private class BranchSelectionHandler extends AbstractSelectionEnabledHandler {

      public BranchSelectionHandler(MenuManager menuManager) {
         super(menuManager);
      }

      @SuppressWarnings("unchecked")
      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         try {
            Branch toBranch = BranchPersistenceManager.getBranch(Integer.parseInt(event.getParameter(BRANCH_ID)));

            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Move Transactions",
                  "All selected transactions will be moved to branch " + toBranch.getBranchName())) {
               Iterator<JobbedNode> iter = selection.iterator();
               while (iter.hasNext()) {
                  TransactionData transactionData = (TransactionData) iter.next().getBackingData();
                  BranchPersistenceManager.moveTransaction(transactionData.getTransactionId(), toBranch);
               }
            }
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
         }

         return null;
      }

      @Override
      public boolean isEnabled() {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         return OseeProperties.isDeveloper() && SkynetSelections.transactionsSelected(selection);
      }
   };

   private void addMoveTransactionCommand(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Move Transaction To", "moveTransaction");
      menuManager.add(subMenuManager);
      addBranchSelectionMenu(subMenuManager);
   }

   private void createMoveTransactionCommand(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Move Transaction To", "moveTransaction");
      menuManager.add(subMenuManager);
      BranchSelectionHandler mySelectionHandler = new BranchSelectionHandler(menuManager);
      createBranchSelectionMenu(subMenuManager, mySelectionHandler);
   }

   private void addCommitIntoCommand(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Commit Into", "commitTransaction");
      menuManager.add(subMenuManager);
      addBranchSelectionMenu(subMenuManager);
   }

   private void createCommitIntoCommand(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Commit Into", "commitTransaction");
      menuManager.add(subMenuManager);
      createBranchSelectionMenu(subMenuManager, new CommitHandler(menuManager, false, false));
   }

   private String addDeleteBranchCommand(MenuManager menuManager) {
      CommandContributionItem deleteBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "deleteBranchCommand", "Delete Branch", null, null, null,
                  null, null, null);
      menuManager.add(deleteBranchCommand);
      return deleteBranchCommand.getId();
   }

   private void createDeleteBranchCommand(MenuManager menuManager) {
      handlerService.activateHandler(addDeleteBranchCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            MessageDialog dialog =
                  new MessageDialog(Display.getCurrent().getActiveShell(), "Delete Branch", null,
                        "Are you sure you want to delete the branch: " + selectedBranch.getBranchName(),
                        MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);

            if (dialog.open() == 0) {
               BranchPersistenceManager.deleteBranch(selectedBranch);
            }

            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return OseeProperties.isDeveloper() && SkynetSelections.oneBranchSelected(selection) && SkynetSelections.boilDownObject(selection.getFirstElement()) != BranchPersistenceManager.getDefaultBranch();
         }
      });
   }

   private String addRenameBranchCommand(MenuManager menuManager) {
      CommandContributionItem renameBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "renameBranchCommand", "Rename Branch", null, null, null,
                  null, null, null);
      menuManager.add(renameBranchCommand);
      return renameBranchCommand.getId();
   }

   private void createRenameBranchCommand(MenuManager menuManager) {

      handlerService.activateHandler(addRenameBranchCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            final Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
            TreeItem[] myTreeItemsSelected = branchTable.getTree().getSelection();
            if (myTreeItemsSelected.length != 1) {
               return null;
            }
            final TreeItem myTreeItem = myTreeItemsSelected[0];
            Control oldEditor = myTreeEditor.getEditor();
            if (oldEditor != null) {
               oldEditor.dispose();
            }
            myTextBeingRenamed = new Text(branchTable.getTree(), SWT.BORDER);
            myTextBeingRenamed.setText(selectedBranch.getBranchName());
            myTextBeingRenamed.addFocusListener(new FocusAdapter() {
               @Override
               public void focusLost(FocusEvent e) {
                  updateText(myTextBeingRenamed.getText(), selectedBranch);
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
                     updateText(myTextBeingRenamed.getText(), selectedBranch);
                     myTextBeingRenamed.dispose();
                  } else if (e.keyCode == SWT.ESC) {
                     myTextBeingRenamed.dispose();
                  }
               }
            });
            myTextBeingRenamed.selectAll();
            myTextBeingRenamed.setFocus();
            myTreeEditor.setEditor(myTextBeingRenamed, myTreeItem);
            return null;
         }

         private void updateText(String newLabel, Branch selectedBranch) {
            selectedBranch.setBranchName(newLabel);
            try {
               selectedBranch.rename(newLabel);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            branchTable.refresh();
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return OseeProperties.isDeveloper() && SkynetSelections.oneBranchSelected(selection) && SkynetSelections.boilDownObject(selection.getFirstElement()) != BranchPersistenceManager.getDefaultBranch();
         }
      });
   }

   private String addSetBranchShortNameCommand(MenuManager menuManager) {
      CommandContributionItem setBranchShortNameCommand =
            Commands.getLocalCommandContribution(getSite(), "setBranchShortNameCommand", "Set Branch Short Name", null,
                  null, null, null, null, null);
      menuManager.add(setBranchShortNameCommand);
      return setBranchShortNameCommand.getId();
   }

   private void createSetBranchShortNameCommand(MenuManager menuManager) {

      handlerService.activateHandler(addSetBranchShortNameCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            IInputValidator inputValidator = new IInputValidator() {
               public String isValid(String newText) {
                  if (newText == null || newText.length() == 0) {
                     return "The new branch name must not be blank"; // return error message
                  }
                  if (newText.length() > SkynetDatabase.BRANCH_SHORT_NAME_SIZE) {
                     return "The new branch name must not be longer than " + SkynetDatabase.BRANCH_SHORT_NAME_SIZE + " characters"; // return
                     // error
                     // message
                  }
                  return null; // to indicate the input is valid
               }
            };
            InputDialog dialog =
                  new InputDialog(Display.getCurrent().getActiveShell(), "Rename Branch Short Name",
                        "Enter new branch short name",
                        selectedBranch.getBranchShortName() != null ? selectedBranch.getBranchShortName() : "",
                        inputValidator);

            if (dialog.open() != Window.CANCEL) {
               try {
                  selectedBranch.setBranchShortName(dialog.getValue(), true);
               } catch (Exception ex) {
                  MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Renaming Branch short name",
                        ex.getMessage());
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
               refresh();
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneBranchSelected(selection);
         }
      });
   }

   private String addSetAssociatedArtifactCommand(MenuManager menuManager) {
      CommandContributionItem setBranchShortNameCommand =
            Commands.getLocalCommandContribution(getSite(), "setAssociatedArtifactCommand",
                  "Set Branch Associated Artifact", null, null, null, null, null, null);
      menuManager.add(setBranchShortNameCommand);
      return setBranchShortNameCommand.getId();
   }

   private void createSetAssociatedArtifactCommand(MenuManager menuManager) {

      handlerService.activateHandler(addSetAssociatedArtifactCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            try {
               EntryDialog ed =
                     new EntryDialog(
                           "Set Associated Artifact",
                           "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getBranchName() + "\"" + (selectedBranch.getAssociatedArtifact() != null ? "\n\nCurrently: " + selectedBranch.getAssociatedArtifact() : ""));
               if (selectedBranch.getAssociatedArtifact() != null) ed.setEntry(String.valueOf(selectedBranch.getAssociatedArtifactId()));
               if (ed.open() == 0) {
                  String artId = ed.getEntry();
                  Artifact associatedArtifact =
                        ArtifactQuery.getArtifactFromId(Integer.parseInt(artId),
                              BranchPersistenceManager.getAtsBranch());
                  if (MessageDialog.openConfirm(
                        Display.getCurrent().getActiveShell(),
                        "Set Associated Artifact",
                        "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getBranchName() + "\"\nto\nArtifact: " + associatedArtifact)) {
                     selectedBranch.setAssociatedArtifact(associatedArtifact);
                  }
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            if (!OseeProperties.isDeveloper()) return false;
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneBranchSelected(selection);
         }
      });
   }

   private String addOpenAssociatedArtifactCommand(MenuManager menuManager) {
      CommandContributionItem setBranchShortNameCommand =
            Commands.getLocalCommandContribution(getSite(), "openAssociatedArtifactCommand",
                  "Open Associated ATS Action", null, null, null, null, null, null);
      menuManager.add(setBranchShortNameCommand);
      return setBranchShortNameCommand.getId();
   }

   private void createOpenAssociatedArtifactCommand(MenuManager menuManager) {

      handlerService.activateHandler(addOpenAssociatedArtifactCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch selectedBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
            try {
               if (selectedBranch.getAssociatedArtifact() == null) {
                  AWorkbench.popup("Open Associated Artifact", "No artifact associated with branch " + selectedBranch);
                  return null;
               }
               if (AccessControlManager.checkObjectPermission(SkynetAuthentication.getUser(),
                     selectedBranch.getAssociatedArtifact(), PermissionEnum.READ)) {
                  if (selectedBranch.getAssociatedArtifact() instanceof IATSArtifact)
                     OseeAts.openATSArtifact(selectedBranch.getAssociatedArtifact());
                  else
                     ArtifactEditor.editArtifact(selectedBranch.getAssociatedArtifact());
               } else {
                  OSEELog.logInfo(
                        SkynetGuiPlugin.class,
                        "The user " + SkynetAuthentication.getUser() + " does not have read access to " + selectedBranch.getAssociatedArtifact(),
                        true);
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneBranchSelected(selection);
         }
      });
   }

   private void createColumns() {
      Tree tree = branchTable.getTree();

      tree.setHeaderVisible(true);
      TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
      column1.setWidth(400);
      column1.setText(columnNames[0]);
      column1.addSelectionListener(new ColumnSelectionListener(0));

      TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
      column2.setWidth(100);
      column2.setText(columnNames[1]);
      column2.addSelectionListener(new ColumnSelectionListener(1));

      TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
      column3.setWidth(150);
      column3.setText(columnNames[2]);
      column3.addSelectionListener(new ColumnSelectionListener(2));

      TreeColumn column4 = new TreeColumn(tree, SWT.LEFT);
      column4.setWidth(150);
      column4.setText(columnNames[3]);
      column4.addSelectionListener(new ColumnSelectionListener(3));

      TreeColumn column5 = new TreeColumn(tree, SWT.LEFT);
      column5.setWidth(300);
      column5.setText(columnNames[4]);
      column5.addSelectionListener(new ColumnSelectionListener(4));
   }

   private class ColumnSelectionListener extends SelectionAdapter {
      private final int index;

      /**
       * @param index
       */
      public ColumnSelectionListener(int index) {
         super();
         this.index = index;
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
         sorter.setColumnToSort(index);
         branchTable.refresh();
      }
   }

   @Override
   public void saveState(IMemento memento) {
      // Ask to save the user in case any changes to favorite branches have been made
      if (SkynetGuiPlugin.areOSEEServicesAvailable().isTrue()) {
         try {
            SkynetAuthentication.getUser().persistAttributes();
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
   }

   private String addSetDefaultCommand(MenuManager menuManager) {
      CommandContributionItem setBranchDefaultCommand =
            Commands.getLocalCommandContribution(getSite(), "setBranchDefaultCommand", "Set Default Branch", null,
                  null, null, "S", null, "branch_manager_default_branch_menu");
      menuManager.add(setBranchDefaultCommand);
      return setBranchDefaultCommand.getId();
   }

   private void createSetDefaultCommand(MenuManager menuManager) {

      handlerService.activateHandler(addSetDefaultCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            setDefaultBranch((Branch) ((JobbedNode) selection.getFirstElement()).getBackingData());
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneBranchSelected(selection) && SkynetSelections.boilDownObject(selection.getFirstElement()) != BranchPersistenceManager.getDefaultBranch();
         }
      });

   }

   private void addMarkAsFavoriteCommand(MenuManager menuManager) {
      menuManager.add(new CompoundContributionItem() {
         @Override
         protected IContributionItem[] getContributionItems() {
            String markState = "Mark";

            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            if (SkynetSelections.oneBranchSelected(selection)) {
               if ((SkynetAuthentication.getUser().isFavoriteBranch((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())))) {
                  markState = "Unmark";
               }
            }
            return new IContributionItem[] {Commands.getLocalCommandContribution(getSite(), "markAsFavoriteCommand",
                  markState + " as Favorite", null, null, null, "T", null, "branch_manager_favorite_branch_menu")};
         }
      });
   }

   private void createMarkAsFavoriteCommand(MenuManager menuManager) {
      addMarkAsFavoriteCommand(menuManager);
      handlerService.activateHandler(getSite().getId() + ".markAsFavoriteCommand",

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch branch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
            User user = SkynetAuthentication.getUser();

            user.toggleFavoriteBranch(branch);

            if (sorter.isFavoritesFirst())
               branchTable.refresh();
            else
               branchTable.update(selection.getFirstElement(), null);

            // Saving of this change is done in saveState()

            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            boolean oneBranchSelected = SkynetSelections.oneBranchSelected(selection);

            if (oneBranchSelected && SkynetAuthentication.getUser().isFavoriteBranch(
                  (Branch) SkynetSelections.boilDownObject(selection.getFirstElement()))) {
               // make the text correct somehow somewhere so it says Mark/Unmark in context
            }

            return oneBranchSelected;
         }
      });
   }

   public static CheckBoxDialog createCommitDialog() {
      return new CheckBoxDialog(
            Display.getCurrent().getActiveShell(),
            "Confirm Commit",
            null,
            "Committing a branch that has conflicts reported in the Change Report can result in the overwriting of data" + " on the branch being committed to. All conflicts should be addressed accordingly to prevent data loss.",
            "I accept responsibility for the results of this action", MessageDialog.QUESTION, 0);
   }

   private class CommitHandler extends AbstractSelectionEnabledHandler {
      private final boolean useParentBranch;
      private final boolean archiveSourceBranch;

      public CommitHandler(MenuManager menuManager, boolean useParentBranch, boolean archiveSourceBranch) {
         super(menuManager);
         this.useParentBranch = useParentBranch;
         this.archiveSourceBranch = archiveSourceBranch;

      }

      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

         Branch fromBranch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();
         Branch toBranch = null;

         try {
            if (useParentBranch) {
               toBranch = fromBranch.getParentBranch();
            } else {
               toBranch = BranchPersistenceManager.getBranch(Integer.parseInt(event.getParameter(BRANCH_ID)));
            }
            BranchPersistenceManager.commitBranch(fromBranch, toBranch, archiveSourceBranch, false);
         } catch (ConflictDetectionException ex) {
            MessageDialog dialog;
            if (OseeProperties.isDeveloper()) {
               dialog =
                     new MessageDialog(
                           Display.getCurrent().getActiveShell(),
                           "Commit Failed",
                           null,
                           "Commit Failed Due To Unresolved Conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manager to resolve conflicts\n  Force the commit",
                           MessageDialog.QUESTION, new String[] {"Cancel", "Launch Merge Manager", "Force Commit"}, 0);
            } else {
               dialog =
                     new MessageDialog(
                           Display.getCurrent().getActiveShell(),
                           "Commit Failed",
                           null,
                           "Commit Failed Due To Unresolved Conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manager to resolve conflicts",
                           MessageDialog.QUESTION, new String[] {"Cancel", "Launch Merge Manager"}, 0);

            }

            try {
               int result = dialog.open();
               if (result == 1) {
                  MergeView.openView(fromBranch, toBranch, TransactionIdManager.getStartEndPoint(fromBranch).getKey());
               } else if (result == 2) {
                  BranchPersistenceManager.commitBranch(fromBranch, toBranch, true, true);
               }
            } catch (Exception exc) {
               logger.log(Level.SEVERE, "Commit Branch Failed", exc);
            }

         } catch (Exception ex) {
            logger.log(Level.SEVERE, "Commit Branch Failed", ex);
         }

         return null;
      }

      @Override
      public boolean isEnabled() {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         boolean validBranchSelected;
         try {
            validBranchSelected = SkynetSelections.oneDescendantBranchSelected(selection) && useParentBranch;
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
            validBranchSelected = false;
         }

         if (validBranchSelected) {
            validBranchSelected &=
                  !((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())).isChangeManaged();
         }
         return (validBranchSelected) || (!useParentBranch && OseeProperties.isDeveloper() && SkynetSelections.oneBranchSelected(selection));
      }
   }

   private void addCommitCommand(MenuManager menuManager) {
      menuManager.add(new CompoundContributionItem() {
         @Override
         protected IContributionItem[] getContributionItems() {
            String parentBranchName = "";
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            try {
               if (SkynetSelections.oneDescendantBranchSelected(selection)) {
                  Branch parent =
                        ((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())).getParentBranch();
                  parentBranchName = parent.getBranchName();
               }
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
            IContributionItem[] myIContributionItems =
                  new IContributionItem[] {Commands.getLocalCommandContribution(getSite(), "commitIntoParentCommand",
                        "Commit Into Parent Branch: " + parentBranchName, null, null, null, null, null, null)};

            return myIContributionItems;
         }
      });
   }

   private void createCommitCommand(MenuManager menuManager) {
      addCommitCommand(menuManager);
      handlerService.activateHandler(getSite().getId() + ".commitIntoParentCommand", new CommitHandler(menuManager,
            true, true));
   }

   private String addBranchCommand(MenuManager menuManager) {
      CommandContributionItem createBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "createBranchCommand", "Branch", null, null, null, "B",
                  null, null);
      menuManager.add(createBranchCommand);
      return createBranchCommand.getId();
   }

   private void createBranchCommand(MenuManager menuManager) {
      handlerService.activateHandler(addBranchCommand(menuManager), new BranchCreationHandler(menuManager, branchTable,
            false));
   }

   private String addSelectivelyBranchCommand(MenuManager menuManager) {
      CommandContributionItem createSelectiveBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "createSelectiveBranchCommand", "Selectively Branch", null,
                  null, null, "S", null, null);
      menuManager.add(createSelectiveBranchCommand);
      return createSelectiveBranchCommand.getId();
   }

   private void createSelectivelyBranchCommand(MenuManager menuManager) {
      handlerService.activateHandler(addSelectivelyBranchCommand(menuManager), new BranchCreationHandler(menuManager,
            branchTable, true));
   }

   private String addViewTableMenuItem(MenuManager menuManager) {
      CommandContributionItem viewTableReportCommand =
            Commands.getLocalCommandContribution(getSite(), "viewTableReportCommand", "View Branch Table Report", null,
                  null, null, "V", null, null);
      menuManager.add(viewTableReportCommand);
      return viewTableReportCommand.getId();
   }

   private void createViewTableMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addViewTableMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            (new TreeViewerReport(branchTable)).open();
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            return !selection.isEmpty();
         }
      });
   }

   private String addImportOntoBranchCommand(MenuManager menuManager) {
      CommandContributionItem importOntoBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "importOntoBranchCommand", "Import Onto Branch...", null,
                  null, null, "I", null, null);
      menuManager.add(importOntoBranchCommand);
      return importOntoBranchCommand.getId();
   }

   private void createImportOntoBranchCommand(MenuManager menuManager) {

      handlerService.activateHandler(addImportOntoBranchCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch branch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            File file = Files.selectFile(getSite().getShell(), SWT.OPEN, "*.zip");
            if (file != null) {
               Jobs.startJob(new ImportBranchJob(file, branch, true, true));
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            return SkynetSelections.oneBranchSelected(selection) && AccessControlManager.checkObjectPermission(
                  SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.WRITE);
         }
      });
   }

   private String addImportDescendantsOntoBranchCommand(MenuManager menuManager) {
      CommandContributionItem importDescendantsOntoBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "importDescendantsOntoBranchCommand",
                  "Import Descendants Onto Branch...", null, null, null, "m", null, null);
      menuManager.add(importDescendantsOntoBranchCommand);
      return importDescendantsOntoBranchCommand.getId();
   }

   private void createImportDescendantsOntoBranchCommand(MenuManager menuManager) {

      handlerService.activateHandler(addImportDescendantsOntoBranchCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch branch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            File file = Files.selectFile(getSite().getShell(), SWT.OPEN, "*.zip");
            if (file != null) {
               Jobs.startJob(new ImportBranchJob(file, branch, false, true));
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            return SkynetSelections.oneBranchSelected(selection) && AccessControlManager.checkObjectPermission(
                  SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.WRITE);
         }
      });
   }

   private String addExportBranchCommand(MenuManager menuManager) {
      CommandContributionItem exportBranchAndDescendantsCommand =
            Commands.getLocalCommandContribution(getSite(), "exportBranchAndDescendantsCommand",
                  "Export Branch and Descendants...", null, null, null, "x", null, null);
      menuManager.add(exportBranchAndDescendantsCommand);
      return exportBranchAndDescendantsCommand.getId();
   }

   private void createExportBranchCommand(MenuManager menuManager) {

      handlerService.activateHandler(addExportBranchCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch branch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            File file = Files.selectFile(getSite().getShell(), SWT.SAVE, "*.zip");
            if (file != null) {
               Jobs.startJob(new ExportBranchJob(file, branch, false));
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            return SkynetSelections.oneBranchSelected(selection) && AccessControlManager.checkObjectPermission(
                  SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.READ);
         }
      });
   }

   private String addExportBranchDescendantsCommand(MenuManager menuManager) {
      CommandContributionItem exportDescendantsCommand =
            Commands.getLocalCommandContribution(getSite(), "exportDescendantsCommand",
                  "Export Branch Descendants Only...", null, null, null, "D", null, null);
      menuManager.add(exportDescendantsCommand);
      return exportDescendantsCommand.getId();
   }

   private void createExportBranchDescendantsCommand(MenuManager menuManager) {

      handlerService.activateHandler(addExportBranchDescendantsCommand(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Branch branch = (Branch) ((JobbedNode) selection.getFirstElement()).getBackingData();

            File file = Files.selectFile(getSite().getShell(), SWT.SAVE, "*.xml");
            if (file != null) {
               Jobs.startJob(new ExportBranchJob(file, branch, true));
            }
            return null;
         }
      });
   }

   private Action createFavoritesFirstAction() {
      Action favoritesFirst = new Action("Show Favorites First", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            getViewPreference().putBoolean(FAVORITE_KEY, isChecked());
         }
      };
      favoritesFirst.setChecked(sorter.isFavoritesFirst());
      favoritesFirst.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));

      return favoritesFirst;
   }

   private Action createShowTransactionsAction() {
      hideTransactions = new Action("Show Transactions", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            getViewPreference().putBoolean(SHOW_TRANSACTIONS, isChecked());
         }
      };
      return hideTransactions;
   }

   private Action createShowMergeBranchesAction() {
      hideMergeBranches = new Action("Show Merge Branches", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            getViewPreference().putBoolean(SHOW_MERGE_BRANCHES, isChecked());
         }
      };
      return hideMergeBranches;
   }

   public String getActionDescription() {
      return "";
   }

   private void refresh() {
      if (branchTable != null && !branchTable.getTree().isDisposed()) {
         branchTable.refresh();
      }
   }

   public void forcePopulateView() throws SQLException {
      if (branchTable != null && !branchTable.getTree().isDisposed()) {
         BranchPersistenceManager.refreshBranches();
         branchTable.setInput(BranchPersistenceManager.getInstance());
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
    */
   @Override
   public void setFocus() {
      if (branchTable != null) branchTable.getControl().setFocus();
   }

   private class BranchNameFilter extends ViewerFilter {
      private String contains = null;
      private boolean flat = false;

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (!isFiltering()) return true;

         Object backingData = ((JobbedNode) element).getBackingData();
         if (backingData instanceof Branch) {
            return descendantBranchContains((Branch) backingData);
         }
         return true;
      }

      private boolean descendantBranchContains(Branch branch) {
         if (branch.getBranchName().toLowerCase().contains(contains.toLowerCase())) {
            return true;

         }
         // Recurse for hierarchical display
         else if (!flat) {
            try {
               for (Branch childBranch : branch.getChildBranches()) {
                  if (descendantBranchContains(childBranch)) {
                     return true;
                  }
               }
            } catch (SQLException ex) {
               OSEELog.logException(BranchView.class, ex, false);
               return true; // Don't limit displayed data over an exception
            }
         }
         return false;
      }

      /**
       * @param contains The contains to set.
       */
      public void setContains(String contains) {
         this.contains = contains;
      }

      /**
       * @return Returns the contains.
       */
      public String getContains() {
         return contains;
      }

      public boolean isFiltering() {
         return contains != null && contains.length() > 0;
      }

      /**
       * @param flat the flat to set
       */
      public void setFlat(boolean flat) {
         this.flat = flat;
      }
   }

   private class FavoritesSorter extends ColumnSorter {
      private boolean favoritesFirst;

      /**
       * @param labelProvider
       */
      public FavoritesSorter(ITableLabelProvider labelProvider) {
         super(labelProvider);

         this.favoritesFirst = false;
      }

      @Override
      public int compare(Viewer viewer, Object o1, Object o2) {
         Object backing1 = ((JobbedNode) o1).getBackingData();
         Object backing2 = ((JobbedNode) o2).getBackingData();

         if (favoritesFirst && backing1 instanceof Branch && backing2 instanceof Branch) {
            User user = SkynetAuthentication.getUser();
            boolean fav1 = user.isFavoriteBranch((Branch) backing1);
            boolean fav2 = user.isFavoriteBranch((Branch) backing2);

            if (fav1 ^ fav2) return fav1 ? -1 : 1;
         } else if (backing1 instanceof Branch && !(backing2 instanceof Branch)) {
            return -1;
         } else if (!(backing1 instanceof Branch) && backing2 instanceof Branch) {
            return 1;
         }
         return super.compare(viewer, o1, o2);
      }

      /**
       * @return Returns the favoritesFirst.
       */
      public boolean isFavoritesFirst() {
         return favoritesFirst;
      }

      /**
       * @param favoritesFirst The favoritesFirst to set.
       */
      public void setFavoritesFirst(boolean favoritesFirst) {
         this.favoritesFirst = favoritesFirst;
      }
   }

   public void reveal(Branch branch) {
      for (Object obj : ((BranchContentProvider) branchTable.getContentProvider()).getElements(null)) {
         if (((JobbedNode) obj).getBackingData() == branch) {
            branchTable.reveal(obj);
            branchTable.setSelection(new StructuredSelection(obj), true);
            return;
         }
      }
   }

   /**
    * Reveal a branch in the viewer and select it.
    */
   public static void revealBranch(Branch branch) {
      IWorkbenchPage page = AWorkbench.getActivePage();
      BranchView branchView;
      try {
         branchView = (BranchView) page.showView(VIEW_ID);
         branchView.reveal(branch);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      disposed = true;
      OseeEventManager.removeListener(this);

      try {
         getViewPreference().flush();
      } catch (BackingStoreException ex) {
         OSEELog.logException(BranchView.class, ex, true);
      }

      super.dispose();
   }

   public void presentAsHierarchy() {
      getViewPreference().putBoolean(FLAT_KEY, false);
   }

   public void presentAsFlat() {
      getViewPreference().putBoolean(FLAT_KEY, true);
   }

   private void setShowTransactions(boolean showTransactions) {
      if (branchTable != null && branchTable.getContentProvider() != null) {
         hideTransactions.setChecked(showTransactions);

         BranchContentProvider myBranchContentProvider = (BranchContentProvider) branchTable.getContentProvider();
         myBranchContentProvider.setShowTransactions(showTransactions);
         myBranchContentProvider.refresh();
      }
   }

   private void setShowMergeBranches(boolean showMergeBranches) {
      if (branchTable != null && branchTable.getContentProvider() != null) {
         hideMergeBranches.setChecked(showMergeBranches);

         BranchContentProvider myBranchContentProvider = (BranchContentProvider) branchTable.getContentProvider();
         myBranchContentProvider.setShowMergeBranches(showMergeBranches);
         myBranchContentProvider.refresh();
      }
   }

   private void setPresentation(boolean flat) {
      if (branchTable != null && branchTable.getContentProvider() != null) {
         BranchContentProvider provider = (BranchContentProvider) branchTable.getContentProvider();

         // No effect if going to the same state
         if (provider.isShowChildBranchesAtMainLevel() != flat || provider.isShowChildBranchesUnderParents() != !flat) {
            nameFilter.setFlat(flat);
            provider.setShowChildBranchesAtMainLevel(flat);
            provider.setShowChildBranchesUnderParents(!flat);

            provider.refresh();
         }
      }
   }

   public void setDefaultBranch(Branch newDefaultBranch) {
      Branch oldDefaultBranch = BranchPersistenceManager.getDefaultBranch();
      BranchPersistenceManager.setDefaultBranch(newDefaultBranch);
      branchTable.update(new Object[] {oldDefaultBranch, newDefaultBranch}, null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.DefaultBranchChanged || branchModType == BranchEventType.Renamed) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               refresh();
            }
         });
      } else if (branchModType == BranchEventType.Added || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Committed) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  forcePopulateView();
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, false);
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
}
