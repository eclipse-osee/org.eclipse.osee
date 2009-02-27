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
package org.eclipse.osee.ats.navigate;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.config.AtsBulkLoadCache;
import org.eclipse.osee.ats.world.search.MultipleHridSearchItem;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class NavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.ats.navigate.NavigateView";
   public static final String HELP_CONTEXT_ID = "atsNavigator";
   private AtsNavigateComposite xNavComp;
   public Text searchArea;
   public XCheckBox completeCancelledCheck;
   private boolean includeCompleteCancelled = false;

   /**
    * The constructor.
    */
   public NavigateView() {
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;
      AtsBulkLoadCache.run(false);

      OseeContributionItem.addTo(this, false);

      xNavComp = new AtsNavigateComposite(new AtsNavigateViewItems(), parent, SWT.NONE);

      AtsPlugin.getInstance().setHelp(xNavComp, HELP_CONTEXT_ID);
      createActions();

      // add search text box      
      createSearchInputPart(xNavComp);

      if (savedFilterStr != null) {
         xNavComp.getFilteredTree().getFilterControl().setText(savedFilterStr);
      }
      xNavComp.refresh();
      xNavComp.getFilteredTree().getFilterControl().setFocus();

      Label label = new Label(xNavComp, SWT.None);
      String str = getWhoAmI();
      if (AtsPlugin.isAtsAdmin()) str += " - Admin";
      if (!str.equals("")) {
         if (AtsPlugin.isAtsAdmin()) {
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
         }
      }
      label.setText(str);
      label.setToolTipText(str);
      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
      gridData.heightHint = 15;
      label.setLayoutData(gridData);

   }

   public void createSearchInputPart(Composite parent) {
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(4, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      Label searchLabel = new Label(comp, SWT.NONE);
      searchLabel.setText("Search:");
      GridData gridData = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gridData.heightHint = 15;
      this.searchArea = new Text(comp, SWT.SINGLE | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      this.searchArea.setFont(parent.getFont());
      this.searchArea.setLayoutData(gd);
      this.searchArea.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent event) {
            if (event.character == '\r') {
               try {
                  xNavComp.handleDoubleClick(new SearchNavigateItem(null, new AtsNavigateQuickSearch(
                        "ATS Quick Search", searchArea.getText(), isIncludeCompleteCancelled())));
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      this.searchArea.setToolTipText("ATS Quick Search - Type in a search string.");
      this.completeCancelledCheck = new XCheckBox("IC");
      this.completeCancelledCheck.createWidgets(comp, 2);
      this.completeCancelledCheck.setToolTip("Include completed/cancelled ATS Artifacts");
      completeCancelledCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            includeCompleteCancelled = completeCancelledCheck.isSelected();
         };
      });
   }

   public boolean isIncludeCompleteCancelled() {
      return includeCompleteCancelled;
   }

   private String getWhoAmI() {
      try {
         String userName = UserManager.getUser().getName();
         return String.format("%s - %s:%s", userName, ClientSessionManager.getDataStoreName(),
               ClientSessionManager.getDataStoreLoginName());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   protected void createActions() {
      Action myWorldAction = new Action("My World") {

         @Override
         public void run() {
            try {
               xNavComp.handleDoubleClick(new SearchNavigateItem(null, new MyWorldSearchItem("My World",
                     UserManager.getUser())), TableLoadOption.None);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      myWorldAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("MyWorld.gif"));
      myWorldAction.setToolTipText("My World");

      Action collapseAction = new Action("Collapse All") {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      };
      collapseAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("collapseAll.gif"));
      collapseAction.setToolTipText("Collapse All");

      Action expandAction = new Action("Expand All") {

         @Override
         public void run() {
            xNavComp.getFilteredTree().getViewer().expandAll();
         }
      };
      expandAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("expandAll.gif"));
      expandAction.setToolTipText("Expand All");

      Action openByIdAction = new Action("Open by Id") {

         @Override
         public void run() {
            try {
               xNavComp.handleDoubleClick(new SearchNavigateItem(null, new MultipleHridSearchItem()),
                     TableLoadOption.None);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      openByIdAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("openId.gif"));
      openByIdAction.setToolTipText("Open by Id");

      Action openChangeReportById = new Action("Open Change Report by Id") {

         @Override
         public void run() {
            try {
               xNavComp.handleDoubleClick(new OpenChangeReportByIdItem(null), TableLoadOption.None);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      openChangeReportById.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change.gif"));
      openChangeReportById.setToolTipText("Open Change Report by Id");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(myWorldAction);
      toolbarManager.add(collapseAction);
      toolbarManager.add(expandAction);
      toolbarManager.add(openChangeReportById);
      toolbarManager.add(openByIdAction);
      toolbarManager.add(new NewAction());

      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "ATS Navigator");

   }

   /**
    * Provided for tests to be able to simulate a double-click
    * 
    * @param item
    */
   public void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      OseeLog.log(AtsPlugin.class, Level.INFO,
            "===> Simulating NavigateView Double-Click for \"" + item.getName() + "\"...");
      xNavComp.handleDoubleClick(item, tableLoadOptions);
   }

   public static NavigateView getNavigateView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (NavigateView) page.showView(NavigateView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch OSEE ATS NavigateView " + e1.getMessage());
      }
      return null;
   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) return String.format("Currently Selected - %s",
            ((XNavigateItem) sel.iterator().next()).getName());
      return "";
   }

   private static final String INPUT = "filter";
   private static final String FILTER_STR = "filterStr";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);

      if (xNavComp != null && xNavComp.getFilteredTree().getFilterControl() != null && !xNavComp.getFilteredTree().isDisposed()) {
         String filterStr = xNavComp.getFilteredTree().getFilterControl().getText();
         memento.putString(FILTER_STR, filterStr);
      }
   }
   private String savedFilterStr = null;

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               savedFilterStr = memento.getString(FILTER_STR);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "NavigateView error on init", ex);
      }
   }
}