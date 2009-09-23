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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorHtmlTab implements IResultsEditorHtmlTab {

   private final String tabName;
   private XResultsComposite xResultsComposite;
   private static String HELP_CONTEXT_ID = "xResultView";
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
   public String getReportHtml() throws OseeCoreException {
      return xResultPage.getManipulatedHtml();
   }

   @Override
   public String getTabName() {
      return tabName;
   }

   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {
      this.resultsEditor = resultsEditor;

      Composite comp = ALayout.createCommonPageComposite(parent);
      ToolBar toolBar = resultsEditor.createToolBar(comp);
      createToolbar(toolBar);

      GridData gd = new GridData(GridData.FILL_BOTH);
      xResultsComposite = new XResultsComposite(comp, SWT.BORDER);
      xResultsComposite.setLayoutData(gd);
      xResultsComposite.setHtmlText(xResultPage.getManipulatedHtml(Arrays.asList(Manipulations.NONE)));

      SkynetGuiPlugin.getInstance().setHelp(xResultsComposite, HELP_CONTEXT_ID, "org.eclipse.osee.ats.help.ui");
      SkynetGuiPlugin.getInstance().setHelp(xResultsComposite.getBrowser(), HELP_CONTEXT_ID,
            "org.eclipse.osee.ats.help.ui");
      return comp;
   }

   private void createToolbar(ToolBar toolBar) {
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.PRINT));
      item.setToolTipText("Print this tab");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            xResultsComposite.getBrowser().setUrl("javascript:print()");
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
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
      item.setImage(ImageManager.getImage(FrameworkImage.FOLDER));
      item.setToolTipText("Import Saved Results Report");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            try {
               final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.OPEN);
               dialog.setFilterExtensions(new String[] {"*.html"});
               String filename = dialog.open();
               if (filename == null || filename.equals("")) {
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

}
