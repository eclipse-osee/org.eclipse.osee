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

package org.eclipse.osee.framework.ui.skynet.widgets.xresults;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class XResultView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView";
   private static String HELP_CONTEXT_ID = "xResultView";
   private static File errorImageFile = null;
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(XResultView.class);
   private XResultPage currentPage;
   private List<XResultPage> pages = new ArrayList<XResultPage>();
   private Label errorLabel;
   private XResultsComposite xResultsComp;

   /**
    * @author Donald G. Dunne
    */
   public XResultView() {
      if (errorImageFile == null) {
         try {
            errorImageFile = SkynetGuiPlugin.getInstance().getPluginFile("images/bug.gif");
         } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   /**
    * There is no way to keep track of the items in an menu to display them correctly. Thus, we need to remove all and
    * re-add from ordered list we have kept.
    */
   public void updateActionList() {
      IMenuManager manager = getViewSite().getActionBars().getMenuManager();
      manager.removeAll();

      int x = 1;
      for (final XResultPage fPage : pages) {
         // Add to pulldown
         Action action = new Action(x++ + ") " + fPage.getTitle(), Action.AS_CHECK_BOX) {

            public void run() {
               displayPage(fPage);
               // Redisplay so check the current action
               updateActionList();
            }
         };
         action.setToolTipText(fPage.getTitle());
         action.setId(fPage.getId());
         if (fPage == currentPage) action.setChecked(true);
         manager.add(action);
      }
      manager.update(true);
   }

   public void addResultPage(XResultPage page) {
      currentPage = page;

      XResultView xResultView = getResultView();
      if (xResultView == null) return;

      pages.add(0, page);

      updateActionList();

      // Display page
      displayPage(page);

   }

   public void displayPage(XResultPage page) {
      displayPage(page, false);
   }

   public void displayPage(XResultPage page, boolean print) {

      currentPage = page;

      // Create report title with errors/warnings if appropriate
      StringBuffer sb = new StringBuffer();
      sb.append("  " + page.getTitle());
      sb.append(String.format("\n  Errors: %s    Warnings: %s", page.getNumErrors(), page.getNumWarnings()));
      errorLabel.setText(sb.toString());
      errorLabel.getParent().layout();

      // Display results in browser
      xResultsComp.setHtmlText(page.getManipulatedHtml(), page.getTitle());

   }

   public static XResultView getResultView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (XResultView) page.showView(VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch Results View " + e1.getMessage());
      }
      return null;
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   public void createPartControl(Composite parent) {
      /*
       * Create a grid layout object so the text and treeviewer are layed out the way I want.
       */
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      errorLabel = new Label(parent, SWT.NONE);

      xResultsComp = new XResultsComposite(parent, SWT.BORDER);

      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);
      SkynetGuiPlugin.getInstance().setHelp(xResultsComp.getBrowser(), HELP_CONTEXT_ID);

      createActions();
   }

   protected void createActions() {

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      Action action = new Action("Remove Result") {

         public void run() {
            if (currentPage == null) {
               AWorkbench.popup("ERROR", "Nothing to remove");
               return;
            }
            if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Remove Result",
                  "Remove Result\n\nAre you sure?")) {
               if (pages.contains(currentPage)) pages.remove(currentPage);
               updateActionList();
               xResultsComp.getBrowser().setText(AHTML.simplePage("Select page to display."));
               setContentDescription("");
               if (pages.size() == 0) currentPage = null;
            }
         }
      };
      action.setToolTipText("Remove the current result");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("remove.gif"));
      toolbarManager.add(action);

      action = new Action("Remove All Results") {

         public void run() {
            if (currentPage == null) {
               AWorkbench.popup("ERROR", "Nothing to remove");
               return;
            }
            if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Remove All Results",
                  "Remove All Results\n\nAre you sure?")) {
               pages.clear();
               updateActionList();
               xResultsComp.getBrowser().setText(AHTML.simplePage(""));
               setContentDescription("");
               currentPage = null;
            }
         }
      };
      action.setToolTipText("Remove all the results");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("removeAll.gif"));
      toolbarManager.add(action);

      action = new Action("Print") {

         public void run() {
            printContents();
         }
      };
      action.setToolTipText("Print");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("print.gif"));
      toolbarManager.add(action);

      action = new Action("Email") {

         public void run() {
            if (currentPage == null) {
               AWorkbench.popup("ERROR", "Nothing to email");
               return;
            }
            Set<Manipulations> manipulations = new HashSet<Manipulations>();
            manipulations.add(Manipulations.ALL);
            manipulations.add(Manipulations.ERROR_WARNING_HEADER);
            Dialogs.emailDialog(getTitle(), currentPage.getManipulatedHtml(manipulations));
         }
      };
      action.setToolTipText("Email");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("email.gif"));
      toolbarManager.add(action);

      action = new Action("Export Table") {

         public void run() {
            if (currentPage == null) {
               AWorkbench.popup("ERROR", "Nothing to export");
               return;
            }
            currentPage.handleExport();
         }
      };
      action.setToolTipText("Export table into csv file");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("export.gif"));
      toolbarManager.add(action);

      action = new Action("Save Report") {

         public void run() {
            if (currentPage == null) {
               AWorkbench.popup("ERROR", "Nothing to export");
               return;
            }
            currentPage.saveToFile();
         }
      };
      action.setToolTipText("Save report to file");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("save.gif"));
      toolbarManager.add(action);

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Result View");

      // IMenuManager manager = getViewSite().getActionBars().getMenuManager();

   }

   public void printContents() {
      if (currentPage == null) {
         AWorkbench.popup("ERROR", "Nothing to print");
         return;
      }
      xResultsComp.getBrowser().setUrl("javascript:print()");
   }

   public String getActionDescription() {
      return "";
   }

}