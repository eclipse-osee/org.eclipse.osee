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
package org.eclipse.osee.ote.ui.test.manager.configuration;

import java.io.File;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.StatusWindowWidget;
import org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage;
import org.eclipse.osee.ote.ui.test.manager.util.Dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class LoadWidget {
   public static final OseeUiActivator plugin = TestManagerPlugin.getInstance();
   private StatusWindowWidget statusWindow;
   private TestManagerPage tmPage;

   public LoadWidget(TestManagerPage tmPage) {
      this.tmPage = tmPage;
      this.statusWindow = null;
   }

   public void createToolItem(final ToolBar toolBar) {
      final Shell shell = toolBar.getShell();

      final Menu menu = new Menu(shell, SWT.POP_UP);

      MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
      menuItem.setText("Load File...");
      menuItem.setImage(ImageManager.getImage(OteTestManagerImage.FLDR_OBJ));
      menuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            String[] filterExtensions = {"*.xml"};
            FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
            fileDialog.setText("Load " + tmPage.getPageName() + "Page Settings From...");
            fileDialog.setFilterExtensions(filterExtensions);

            String defaultConfigDir = statusWindow.getValue(ScriptPage.UpdateableLabel.CONFIGPATHLABEL.name());
            File dir = new File(defaultConfigDir);
            if (dir.isFile() || dir.isDirectory()) {
               fileDialog.setFilterPath(defaultConfigDir);
            } else {
               fileDialog.setFilterPath("Y:\\");
            }
            String result = fileDialog.open();

            if (result != null && !result.equals("")) {
               File newFile = new File(result);
               if (newFile.exists() && newFile.isFile()) {
                  LoadConfigurationOperation.load(tmPage.getTestManager(), newFile);
               } else {
                  handleFileSelectError();
               }
            }
         }
      });

      final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
      item.setImage(ImageManager.getImage(OteTestManagerImage.LOAD_CONFIG));
      item.setToolTipText("Load current page configuration from file");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            // If they clicked the arrow, we show the list
            if (event.detail == SWT.ARROW) {
               // Determine where to put the dropdown list
               ToolItem item = (ToolItem) event.widget;
               Rectangle rect = item.getBounds();
               Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
               menu.setLocation(pt.x, pt.y + rect.height);
               menu.setVisible(true);
            } else {
               LoadConfigurationOperation.reLoad(tmPage.getTestManager());
            }
         }
      });
      toolBar.pack();
   }

   public void setStatusLabel(StatusWindowWidget statusWindow) {
      this.statusWindow = statusWindow;
   }

   private void handleFileSelectError() {
      Dialogs.popupError("Load Configuration File", "Invalid Configuration File Selected.");
   }
}
