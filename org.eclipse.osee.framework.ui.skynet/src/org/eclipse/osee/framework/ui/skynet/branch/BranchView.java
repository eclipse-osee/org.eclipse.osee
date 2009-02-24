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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.TreeViewerReport;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
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
public class BranchView extends ViewPart implements IActionable {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.branch.BranchView";
   BranchListComposite branchListComposite;
   private static final IParameter[] BRANCH_PARAMETER_DEF = new IParameter[] {new BranchIdParameter()};
   private static final String FAVORITE_KEY = "favorites_first";
   private static final String SHOW_TRANSACTIONS = "show_transactions";
   private static final String SHOW_MERGE_BRANCHES = "show_merge_branches";
   private static final String FLAT_KEY = "flat";
   static final String BRANCH_ID = "branchId";
   private final IPreferencesService preferencesService;
   private IPreferenceChangeListener preferenceChangeListener = null;
   private boolean disposed;
   private Action hideTransactions;
   private Action hideMergeBranches;
   private IHandlerService handlerService;
   private TreeViewer branchTable;
   private Text myTextBeingRenamed;

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
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

   public void forcePopulateView() throws OseeCoreException {
      branchListComposite.forcePopulateView();
   }

   public void presentAsHierarchy() {
      getViewPreference().putBoolean(FLAT_KEY, false);
   }

   public void presentAsFlat() {
      getViewPreference().putBoolean(FLAT_KEY, true);
   }

   private static class BranchIdParameter implements IParameter {
      public String getId() {
         return BRANCH_ID;
      }

      public String getName() {
         return "Branch Id";
      }

      public IParameterValues getValues() throws ParameterValuesException {
         throw new ParameterValuesException("Branch View has no parameters", null);
      }

      public boolean isOptional() {
         return false;
      }
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(branchListComposite.getBranchTable().getControl(), "branch_manager_table");
      SkynetGuiPlugin.getInstance().setHelp(branchListComposite.getFilterText(), "branch_manager_filtering");
   }

   public String getActionDescription() {
      return "";
   }

   protected void createActions() {

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            try {
               branchListComposite.forcePopulateView();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      refreshAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Branch Manager");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);
   }

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
                  }
                  if (propertyName.equals(SHOW_TRANSACTIONS)) {
                     setShowTransactions(getViewPreference().getBoolean(SHOW_TRANSACTIONS, true));
                  }
                  if (propertyName.equals(SHOW_MERGE_BRANCHES)) {
                     setShowMergeBranches(getViewPreference().getBoolean(SHOW_MERGE_BRANCHES, true));
                  }
                  if (propertyName.equals(FAVORITE_KEY)) {
                     branchListComposite.setFavoritesFirst(getViewPreference().getBoolean(FAVORITE_KEY, false));
                  }
               }
            }
         };
      }

      return preferenceChangeListener;
   }

   private void setPresentation(boolean flat) {
      branchListComposite.setPresentation(flat);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
    */
   @Override
   public void setFocus() {
      if (branchListComposite != null) branchListComposite.setFocus();
   }

   private void setShowMergeBranches(boolean showMergeBranches) {
      branchListComposite.setShowMergeBranches(showMergeBranches);
      if (branchTable != null && branchTable.getContentProvider() != null) {
         hideMergeBranches.setChecked(showMergeBranches);
      }
   }

   private void setShowTransactions(boolean showTransactions) {
      branchListComposite.setShowTransactions(showTransactions);
      if (branchTable != null && branchTable.getContentProvider() != null) {
         hideTransactions.setChecked(showTransactions);
      }
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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      disposed = true;
      if (branchListComposite != null) {
         branchListComposite.disposeComposite();
      }

      try {
         getViewPreference().flush();
      } catch (BackingStoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      super.dispose();
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

   public void reveal(Branch branch) {
      branchListComposite.reveal(branch);
   }

   @Override
   public void createPartControl(Composite parent) {
      try {
         if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

         PlatformUI.getWorkbench().getService(IHandlerService.class);
         handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

         parent.setLayout(new GridLayout());
         parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         branchListComposite = new BranchListComposite(parent);
         branchTable = branchListComposite.getBranchTable();

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
         createCommitCommand(menuManager);
         createCommitIntoCommand(menuManager);
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

         getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.branch.BranchView", menuManager,
               branchTable);

         branchTable.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));

         getSite().setSelectionProvider(branchTable);

         IMenuManager toolbarManager = getViewSite().getActionBars().getMenuManager();
         toolbarManager.add(createFavoritesFirstAction());
         toolbarManager.add(createShowTransactionsAction());
         if (AccessControlManager.isOseeAdmin()) {
            toolbarManager.add(createShowMergeBranchesAction());
         }
         toolbarManager.add(new ParentBranchAction(this));
         loadPreferences();

         createActions();

         setHelpContexts();

         OseeContributionItem.addTo(this, true);

         branchListComposite.getFilterText().addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
               if (branchListComposite.isFiltering())
                  setContentDescription("Filtered for :\"" + branchListComposite.getFilterText().getText() + "\"");
               else
                  setContentDescription("");
            }
         });
         branchListComposite.setFavoritesFirst(getViewPreference().getBoolean(FAVORITE_KEY, false));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
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

   private void addBranchSelectionMenu(MenuManager menuManager) {
      try {
         for (Branch branch : BranchManager.getNormalBranches()) {

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(BRANCH_ID, Integer.toString(branch.getBranchId()));

            CommandContributionItem branchCommand =
                  Commands.getLocalCommandContribution(getSite(), menuManager.getId(), branch.getBranchName(),
                        BRANCH_PARAMETER_DEF, parameters, null, null, null, null);
            menuManager.add(branchCommand);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private void addMergeViewCommand(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Merge Manager", "mergeViewCommand");
      menuManager.add(subMenuManager);
      addMergeSelectionMenu(subMenuManager);
   }

   private void createMergeViewCommand(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Merge Manager", "mergeViewCommand");
      menuManager.add(subMenuManager);
      createMergeSelectionMenu(subMenuManager, new MergeSelectionHandler(menuManager));
   }

   private void addMergeSelectionMenu(MenuManager menuManager) {
      if (branchTable != null) {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         if (selection != null && selection.getFirstElement() != null) {
            Object backingData = ((JobbedNode) selection.getFirstElement()).getBackingData();
            if (backingData instanceof Branch) {
               Branch selectedBranch = (Branch) backingData;
               if (selectedBranch != null) {
                  try {
                     Collection<Integer> destBranches =
                           ConflictManagerInternal.getDestinationBranchesMerged(selectedBranch.getBranchId());
                     try {
                        if (selectedBranch.getParentBranch() != null && !destBranches.contains(selectedBranch.getParentBranch().getBranchId())) {
                           destBranches.add(selectedBranch.getParentBranch().getBranchId());
                        }
                     } catch (BranchDoesNotExist ex) {
                        destBranches.add(0);
                     }
                     for (Integer branch : destBranches) {

                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put(BRANCH_ID, Integer.toString(branch));

                        CommandContributionItem mergeCommand =
                              Commands.getLocalCommandContribution(
                                    getSite(),
                                    menuManager.getId(),
                                    branch == 0 ? "Can't Merge a Root Branch" : BranchManager.getBranch(branch).getBranchName(),
                                    BRANCH_PARAMETER_DEF, parameters, null, null, null, null);
                        menuManager.add(mergeCommand);
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      }
   }

   private void createMergeSelectionMenu(MenuManager menuManager, IHandler selectionHandler) {
      addMergeSelectionMenu(menuManager);
      handlerService.activateHandler(getSite().getId() + "." + menuManager.getId(), selectionHandler);
   }

   private class MergeSelectionHandler extends AbstractSelectionEnabledHandler {

      public MergeSelectionHandler(MenuManager menuManager) {
         super(menuManager);
      }

      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         Object backingData = ((JobbedNode) selection.getFirstElement()).getBackingData();
         Branch selectedBranch = (Branch) backingData;
         try {
            Branch toBranch = BranchManager.getBranch(Integer.parseInt(event.getParameter(BRANCH_ID)));
            if (selectedBranch != null && toBranch != null) {
               MergeView.openView(selectedBranch, toBranch,
                     TransactionIdManager.getStartEndPoint(selectedBranch).getKey());
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         return null;
      }

      @Override
      public boolean isEnabledWithException() throws OseeCoreException {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         if (!selection.isEmpty()) {
            Object obj = ((JobbedNode) selection.getFirstElement()).getBackingData();
            if (obj instanceof Branch) {
               Branch selectedBranch = (Branch) obj;
               if (selectedBranch != null && !ConflictManagerInternal.getDestinationBranchesMerged(
                     selectedBranch.getBranchId()).isEmpty()) {
                  return true;
               }
               return (selectedBranch != null && (!(selectedBranch.getAssociatedArtifact() instanceof IATSArtifact)) && selectedBranch.hasParentBranch());
            }
            return false;
         }
         return false;
      }

   };

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
            Object selectedObject = ((JobbedNode) selection.getFirstElement()).getBackingData();
            try {
               if (selectedObject instanceof TransactionId) {
                  ChangeView.open((TransactionId)selectedObject);
               } else if (selectedObject instanceof Branch) {
                  ChangeView.open((Branch)selectedObject);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }

            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean enabled = true;
            
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            Object selectedObject = ((JobbedNode) selection.getFirstElement()).getBackingData();
            
            if (selectedObject instanceof TransactionId){
               enabled = ((TransactionId)selectedObject).getTxType() != TransactionDetailsType.Baselined;
            }
            
            return enabled;
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
         public boolean isEnabledWithException() throws OseeCoreException {
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
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }

            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            if (!selection.isEmpty()) {
               return SkynetSelections.oneBranchSelected(selection) && (AccessControlManager.checkObjectPermission(
                     SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.FULLACCESS) || AccessControlManager.isOseeAdmin());
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
            TransactionId selectedTransaction =
                  (TransactionId) ((JobbedNode) selection.getFirstElement()).getBackingData();

            if (MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Delete Transaction",
                  "Are you sure you want to delete the transaction: " + selectedTransaction.getTransactionNumber())) {
               BranchManager.deleteTransactions(new JobChangeAdapter() {

                  /* (non-Javadoc)
                   * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
                   */
                  @Override
                  public void done(IJobChangeEvent event) {
                     if (event.getResult().getSeverity() == IStatus.OK) {
                        Display.getDefault().asyncExec(new Runnable() {
                           public void run() {
                              try {
                                 branchListComposite.forcePopulateView();
                              } catch (OseeCoreException ex) {
                                 OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneTransactionSelected(selection) && AccessControlManager.isOseeAdmin();
         }
      });
   }

   @Override
   public void saveState(IMemento memento) {
      // Ask to save the user in case any changes to favorite branches have been made
      if (SkynetGuiPlugin.areOSEEServicesAvailable().isTrue()) {
         try {
            UserManager.getUser().persistAttributes();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneBranchSelected(selection) && SkynetSelections.boilDownObject(selection.getFirstElement()) != BranchManager.getDefaultBranch();
         }
      });

   }

   public void setDefaultBranch(Branch newDefaultBranch) {
      try {
         branchListComposite.setDefaultBranch(newDefaultBranch);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void addMarkAsFavoriteCommand(MenuManager menuManager) {
      menuManager.add(new CompoundContributionItem() {
         @Override
         protected IContributionItem[] getContributionItems() {
            String markState = "Mark";

            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            if (SkynetSelections.oneBranchSelected(selection)) {
               try {
                  if ((UserManager.getUser().isFavoriteBranch((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())))) {
                     markState = "Unmark";
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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
            try {
               UserManager.getUser().toggleFavoriteBranch(branch);

               if (branchListComposite.isFavoritesFirst()) {
                  branchTable.refresh();
               } else {
                  branchTable.update(selection.getFirstElement(), null);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }

            // Saving of this change is done in saveState()

            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            boolean oneBranchSelected = SkynetSelections.oneBranchSelected(selection);

            if (oneBranchSelected && UserManager.getUser().isFavoriteBranch(
                  (Branch) SkynetSelections.boilDownObject(selection.getFirstElement()))) {
               // make the text correct somehow somewhere so it says Mark/Unmark in context
            }

            return oneBranchSelected;
         }
      });
   }

   private void addCommitCommand(MenuManager menuManager) {
      menuManager.add(new CompoundContributionItem() {
         @Override
         protected IContributionItem[] getContributionItems() {
            String parentBranchName = "";
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            try {
               if (SkynetSelections.oneDescendantBranchSelected(selection)) {
                  parentBranchName =
                        ((Branch) SkynetSelections.boilDownObject(selection.getFirstElement())).getParentBranch().getBranchName();
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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
            true, true, branchTable));
   }

   private String addBranchCommand(MenuManager menuManager) {
      CommandContributionItem createBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "createBranchCommand", "Branch", null, null, null, "B",
                  null, null);
      menuManager.add(createBranchCommand);
      return createBranchCommand.getId();
   }

   private void createBranchCommand(MenuManager menuManager) {
      handlerService.activateHandler(addBranchCommand(menuManager), new BranchCreationHandler(menuManager, branchTable));
   }

   private String addSelectivelyBranchCommand(MenuManager menuManager) {
      CommandContributionItem createSelectiveBranchCommand =
            Commands.getLocalCommandContribution(getSite(), "createSelectiveBranchCommand", "Selectively Branch", null,
                  null, null, "S", null, null);
      menuManager.add(createSelectiveBranchCommand);
      return createSelectiveBranchCommand.getId();
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
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

            return !selection.isEmpty();
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
      favoritesFirst.setChecked(branchListComposite.isFavoritesFirst());
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
            Branch toBranch = BranchManager.getBranch(Integer.parseInt(event.getParameter(BRANCH_ID)));

            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Move Transactions",
                  "All selected transactions will be moved to branch " + toBranch.getBranchName())) {
               Iterator<JobbedNode> iter = selection.iterator();
               while (iter.hasNext()) {
                  TransactionId transaction = (TransactionId) iter.next().getBackingData();
                  BranchManager.moveTransaction(transaction, toBranch);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }

         return null;
      }

      @Override
      public boolean isEnabledWithException() throws OseeCoreException {
         IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
         return AccessControlManager.isOseeAdmin() && SkynetSelections.transactionsSelected(selection);
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
      createBranchSelectionMenu(subMenuManager, new CommitHandler(menuManager, false, false, branchTable));
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
               BranchManager.deleteBranch(selectedBranch);
            }

            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return AccessControlManager.isOseeAdmin() && SkynetSelections.oneBranchSelected(selection);
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
            Control oldEditor = branchListComposite.getMyTreeEditor().getEditor();
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
            branchListComposite.getMyTreeEditor().setEditor(myTextBeingRenamed, myTreeItem);
            return null;
         }

         private void updateText(String newLabel, Branch selectedBranch) {
            selectedBranch.setBranchName(newLabel);
            try {
               selectedBranch.rename(newLabel);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            branchTable.refresh();
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return AccessControlManager.isOseeAdmin() && SkynetSelections.oneBranchSelected(selection);
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
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
               branchListComposite.refresh();
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
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
                        ArtifactQuery.getArtifactFromId(Integer.parseInt(artId), BranchManager.getCommonBranch());
                  if (MessageDialog.openConfirm(
                        Display.getCurrent().getActiveShell(),
                        "Set Associated Artifact",
                        "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getBranchName() + "\"\nto\nArtifact: " + associatedArtifact)) {
                     selectedBranch.setAssociatedArtifact(associatedArtifact);
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            if (!AccessControlManager.isOseeAdmin()) return false;
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
               if (AccessControlManager.checkObjectPermission(selectedBranch.getAssociatedArtifact(),
                     PermissionEnum.READ)) {
                  if (selectedBranch.getAssociatedArtifact() instanceof IATSArtifact)
                     OseeAts.openATSArtifact(selectedBranch.getAssociatedArtifact());
                  else
                     ArtifactEditor.editArtifact(selectedBranch.getAssociatedArtifact());
               } else {
                  OseeLog.log(
                        SkynetGuiPlugin.class,
                        OseeLevel.SEVERE_POPUP,
                        "The user " + UserManager.getUser() + " does not have read access to " + selectedBranch.getAssociatedArtifact());
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
            return SkynetSelections.oneBranchSelected(selection);
         }
      });
   }

}
