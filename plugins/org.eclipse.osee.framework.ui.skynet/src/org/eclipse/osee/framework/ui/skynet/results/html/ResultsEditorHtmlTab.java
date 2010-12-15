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
package org.eclipse.osee.framework.ui.skynet.results.html;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.browser.BrowserPrintAction;
import org.eclipse.osee.framework.ui.skynet.action.browser.IBrowserActionHandler;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorHtmlTab implements IResultsEditorHtmlTab, IBrowserActionHandler {

   private final String tabName;
   private XResultsComposite xResultsComposite;
   private ResultsEditor resultsEditor;
   private XResultPage xResultPage;

   public ResultsEditorHtmlTab(XResultPage xResultPage) {
      this.xResultPage = xResultPage;
      tabName = "Results";
   }

   public ResultsEditorHtmlTab(String tabName) {
      this.tabName = tabName;
   }

   public ResultsEditorHtmlTab(String title, String tabName, String html) {
      this(tabName);
      org.eclipse.core.runtime.Assert.isNotNull(tabName);
      setHtml(title, html);
   }

   public void setHtml(String title, String html) {
      org.eclipse.core.runtime.Assert.isNotNull(html);
      xResultPage = new XResultPage(title, html, Manipulations.NONE);
   }

   @Override
   public String getReportHtml() {
      return xResultPage.getManipulatedHtml();
   }

   @Override
   public String getTabName() {
      return tabName;
   }

   @Override
   public Composite createTab(Composite parent, ResultsEditor resultsEditor) {
      this.resultsEditor = resultsEditor;

      Composite comp = ALayout.createCommonPageComposite(parent);
      ToolBar toolBar = resultsEditor.createToolBar(comp);
      createToolbar(toolBar);

      GridData gd = new GridData(GridData.FILL_BOTH);
      xResultsComposite = new XResultsComposite(comp, SWT.BORDER);
      xResultsComposite.setLayoutData(gd);
      xResultsComposite.setHtmlText(xResultPage.getManipulatedHtml(Arrays.asList(Manipulations.NONE)));

      HelpUtil.setHelp(xResultsComposite, OseeHelpContext.RESULTS_VIEW);
      HelpUtil.setHelp(xResultsComposite.getBrowser(), OseeHelpContext.RESULTS_VIEW);
      return comp;
   }

   private void createToolbar(ToolBar toolBar) {

      new ActionContributionItem(new BrowserPrintAction(this)).fill(toolBar, -1);

      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.EMAIL));
      item.setToolTipText("Email");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            Set<Manipulations> manipulations = new HashSet<Manipulations>();
            manipulations.add(Manipulations.NONE);
            Dialogs.emailDialog(resultsEditor.getTitle(), xResultPage.getManipulatedHtml(manipulations));
         }
      });

      new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("csv"));
      item.setToolTipText("Export as CSV");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            xResultPage.handleExport();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("html"));
      item.setToolTipText("Export as HTML");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            xResultPage.saveToFile();
         }
      });

      new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.FOLDER));
      item.setToolTipText("Import Saved Results Report");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            try {
               final FileDialog dialog = new FileDialog(Displays.getActiveShell().getShell(), SWT.OPEN);
               dialog.setFilterExtensions(new String[] {"*.html"});
               String filename = dialog.open();
               if (!Strings.isValid(filename)) {
                  return;
               }
               String html = AFile.readFile(filename);
               if (html == null) {
                  throw new IllegalStateException("Can't load file");
               }
               if (html.equals("")) {
                  throw new IllegalStateException("Empty file");
               }
               resultsEditor.addResultsTab(new ResultsEditorHtmlTab(new XResultPage(filename, html,
                  Manipulations.RAW_HTML)));
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   @Override
   public Browser getBrowser() {
      return xResultsComposite.getBrowser();
   }

}
