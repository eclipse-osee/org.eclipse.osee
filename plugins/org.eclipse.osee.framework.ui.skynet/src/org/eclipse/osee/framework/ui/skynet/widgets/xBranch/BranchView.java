/*******************************************************************************
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

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.action.ColumnMultiEditAction;
import org.eclipse.nebula.widgets.xviewer.action.TableCustomizationAction;
import org.eclipse.nebula.widgets.xviewer.action.ViewSelectedCellDataAction;
import org.eclipse.nebula.widgets.xviewer.action.ViewSelectedCellDataAction.Option;
import org.eclipse.nebula.widgets.xviewer.action.ViewTableReportAction;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.EditTransactionComment;
import org.eclipse.osee.framework.ui.skynet.action.ITransactionRecordSelectionProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays persisted changes made to an artifact.
 * 
 * @author Jeff C. Phillips
 */
public class BranchView extends ViewPart implements IActionable, IBranchEventListener, ITransactionEventListener, ITransactionRecordSelectionProvider {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView";
   private BranchViewPresentationPreferences branchViewPresentationPreferences;
   public static final String BRANCH_ID = "branchId";
   private XBranchWidget xBranchWidget;
   private final Clipboard clipboard = new Clipboard(null);

   public BranchView() {
      OseeEventManager.addListener(this);
   }

   @Override
   public void dispose() {
      super.dispose();
      branchViewPresentationPreferences.setDisposed(true);
      OseeEventManager.removeListener(this);
      clipboard.dispose();
   }

   @Override
   public void setFocus() {
      // do nothing
   }

   @Override
   public void createPartControl(Composite parent) {
      setPartName("Branch Manager");

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      xBranchWidget = new XBranchWidget();
      xBranchWidget.setDisplayLabel(false);
      xBranchWidget.createWidgets(parent, 1);

      branchViewPresentationPreferences = new BranchViewPresentationPreferences(this);
      xBranchWidget.loadData();
      final BranchView fBranchView = this;

      final XViewer branchWidget = xBranchWidget.getXViewer();

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
            menuManager.add(new TableCustomizationAction(branchWidget));
            menuManager.add(new ViewTableReportAction(branchWidget));
            menuManager.add(new ViewSelectedCellDataAction(branchWidget, clipboard, Option.Copy));
            menuManager.add(new ViewSelectedCellDataAction(branchWidget, null, Option.View));
            try {
               if (AccessControlManager.isOseeAdmin()) {
                  menuManager.add(new ColumnMultiEditAction(branchWidget));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

   }

   @Override
   public String getActionDescription() {
      return "";
   }

   public static void revealBranch(Branch branch) throws OseeCoreException {
      try {
         BranchView branchView = (BranchView) AWorkbench.getActivePage().showView(VIEW_ID);
         branchView.reveal(branch);
      } catch (PartInitException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void reveal(Branch branch) {
      xBranchWidget.reveal(branch);
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               if (branchEvent.getEventType() == BranchEventType.Renamed) {
                  xBranchWidget.refresh();
               } else {
                  xBranchWidget.loadData();
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   @Override
   public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
      if (transEvent.getEventType() == TransactionEventType.Purged) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  xBranchWidget.refresh();
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }
   }

   public void changePresentation(BranchOptionsEnum branchViewPresKey, boolean state) {
      if (branchViewPresentationPreferences != null) {
         Preferences pref = branchViewPresentationPreferences.getViewPreference();
         pref.putBoolean(branchViewPresKey.origKeyName, state);
      }
   }

   public XBranchWidget getXBranchWidget() {
      return xBranchWidget;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public ArrayList<TransactionRecord> getSelectedTransactionRecords() {
      return xBranchWidget.getSelectedTransactionRecords();
   }

   @Override
   public void refreshUI(ArrayList<TransactionRecord> records) {
      xBranchWidget.getXViewer().update(records.toArray(new TransactionRecord[records.size()]), null);
   }

}