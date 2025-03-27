/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.action.EditTransactionComment;
import org.eclipse.osee.framework.ui.skynet.action.ITransactionRecordSelectionProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget.IBranchWidgetMenuListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.actions.SetAsFavoriteAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchQueryData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Jeff C. Phillips
 */
public class BranchView extends GenericViewPart implements IBranchWidgetMenuListener, IBranchEventListener, ITransactionEventListener, ITransactionRecordSelectionProvider, IPartListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView";
   public static final String BRANCH_ID = "branchUuid";
   public static final String NAME = "Branch Search";

   private final Clipboard clipboard = new Clipboard(null);

   private BranchViewPresentationPreferences branchViewPresentationPreferences;
   protected XBranchWidget xBranchWidget;
   private final AtomicBoolean refreshNeeded = new AtomicBoolean(false);
   private final AtomicBoolean processEvents = new AtomicBoolean(false);
   private XViewerCustomMenu customMenu;
   protected final BranchQueryData branchData = new BranchQueryData();
   protected AtomicBoolean loaded = new AtomicBoolean(false);
   BranchQueryData showBranchData = branchData;

   public BranchView() {
   }

   @Override
   public void dispose() {
      super.dispose();
      OseeEventManager.removeListener(this);
      if (branchViewPresentationPreferences != null) {
         branchViewPresentationPreferences.setDisposed(true);
      }
      if (clipboard != null) {
         clipboard.dispose();
      }
      if (xBranchWidget != null) {
         xBranchWidget.dispose();
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      setPartName(NAME);

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      AWorkbench.getActivePage().addPartListener(this);

      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         new BranchLoadComposite(this, parent, SWT.NONE);

         xBranchWidget = new XBranchWidget(this);
         xBranchWidget.setDisplayLabel(false);
         xBranchWidget.setBranchData(branchData);
         xBranchWidget.createWidgets(parent, 1);

         branchViewPresentationPreferences = new BranchViewPresentationPreferences(this);

         final BranchView fBranchView = this;

         final XViewer branchWidget = xBranchWidget.getXViewer();
         customMenu = new XViewerCustomMenu(xBranchWidget.getXViewer());
         customMenu.init(xBranchWidget.getXViewer());

         MenuManager menuManager = new MenuManager();
         menuManager.setRemoveAllWhenShown(true);
         menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
               MenuManager menuManager = (MenuManager) manager;
               branchWidget.setColumnMultiEditEnabled(true);
               menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
               menuManager.add(new EditTransactionComment(fBranchView));
               menuManager.add(new Separator());
               if (customMenu.isHeaderMouseClick()) {
                  customMenu.setupMenuForHeader(menuManager);
               } else {
                  customMenu.setupMenuForTable(menuManager);
               }
            }
         });

         menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         branchWidget.getTree().setMenu(menuManager.createContextMenu(branchWidget.getTree()));
         getSite().registerContextMenu(VIEW_ID, menuManager, branchWidget);
         getSite().setSelectionProvider(branchWidget);
         HelpUtil.setHelp(parent, OseeHelpContext.BRANCH_MANAGER);
         OseeStatusContributionItemFactory.addTo(this, true);
         getViewSite().getActionBars().updateActionBars();

         setFocusWidget(xBranchWidget.getControl());

         OseeEventManager.addListener(this);
      }
   }

   @Override
   public void updateMenuActionsForTable(MenuManager mm) {
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new SetAsFavoriteAction(xBranchWidget));
   }

   private void refreshIfNeeded() {
      if (refreshNeeded.compareAndSet(true, false)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  xBranchWidget.refresh();
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      refreshNeeded.set(true);
      if (isInitialized() && processEvents.get()) {
         refreshIfNeeded();
      }
   }

   @Override
   public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
      refreshNeeded.set(true);
      if (isInitialized() && processEvents.get()) {
         refreshIfNeeded();
      }
   }

   @Override
   public void partActivated(IWorkbenchPart part) {
      if (part == this) {
         processEvents.set(true);
         refreshIfNeeded();
      }
   }

   @Override
   public void partDeactivated(IWorkbenchPart part) {
      if (part == this) {
         processEvents.set(false);
      }
   }

   public void changePresentation(BranchOptionsEnum branchViewPresKey, boolean state) {
      if (isInitialized()) {
         Preferences pref = branchViewPresentationPreferences.getViewPreference();
         pref.putBoolean(branchViewPresKey.name(), state);
      }
   }

   public XBranchWidget getXBranchWidget() {
      return xBranchWidget;
   }

   private boolean isInitialized() {
      return xBranchWidget != null && branchViewPresentationPreferences != null;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public List<TransactionId> getSelectedTransactionRecords() {
      return isInitialized() ? xBranchWidget.getSelectedTransactionRecords() : Collections.emptyList();
   }

   @Override
   public void refreshUI(List<TransactionId> records) {
      if (isInitialized()) {
         xBranchWidget.getXViewer().update(records.toArray(new TransactionRecord[records.size()]), null);
      }
   }

   @Override
   public void partBroughtToTop(IWorkbenchPart part) {
      // do nothing
   }

   @Override
   public void partClosed(IWorkbenchPart part) {
      // do nothing
   }

   @Override
   public void partOpened(IWorkbenchPart part) {
      // do nothing
   }

   public BranchQueryData getBranchData() {
      return branchData;
   }

   public void handleQuerySearch() {
      // Clear results
      xBranchWidget.getXViewer().setInput(Collections.emptyList());
      List<Branch> branches = null;
      boolean exceptionCaught = false;
      try {
         xBranchWidget.setExtraInfoLabel("Searching... ");
         OseeClient oseeClient = ServiceUtil.getOseeClient();
         BranchEndpoint endpoint = oseeClient.getBranchEndpoint();
         if (branchData.isAsIds()) {
            if (Strings.isNumeric(branchData.getNamePattern())) {
               // Only want to search by id and ignore rest of criteria
               BranchQueryData idBranchData = new BranchQueryData();
               idBranchData.setAsIds(true);
               idBranchData.setBranchIds(Arrays.asList(BranchId.valueOf(branchData.getNamePattern())));
               if (UserManager.getUser().isOseeAdmin()) {
                  idBranchData.setIncludeArchived(true);
                  idBranchData.setIncludeDeleted(true);
               }
               showBranchData = idBranchData;
               branches = endpoint.getBranches(idBranchData);
            } else {
               xBranchWidget.setExtraInfoLabel("Must enter ID to search");
               return;
            }
         } else {
            showBranchData = branchData;
            branches = endpoint.getBranches(branchData);
         }
      } catch (Exception ex) {
         OseeLog.log(BranchManager.class, Level.SEVERE, ex);
         xBranchWidget.setExtraInfoLabel("Exception caught, see error log");
         exceptionCaught = true;
      }
      if (!exceptionCaught && branches != null) {
         if (branches.isEmpty()) {
            xBranchWidget.setExtraInfoLabel("No Branches Found");
         } else {
            loadData(branches);
            xBranchWidget.setExtraInfoLabel(String.format("%s Branches Found", branches.size()));
         }
      }
   }

   public static void revealBranch(BranchId branch) {
      try {
         BranchView branchView = (BranchView) AWorkbench.getActivePage().showView(VIEW_ID);
         branchView.reveal(branch);
      } catch (PartInitException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void reveal(BranchId branch) {
      if (isInitialized()) {
         xBranchWidget.reveal(branch);
      }
   }

   public void loadData() {
      xBranchWidget.loadData();
   }

   public void loadData(List<Branch> branches) {
      xBranchWidget.loadData(branches);
   }

   public BranchQueryData getShowQueryData() {
      return showBranchData;
   }

}
