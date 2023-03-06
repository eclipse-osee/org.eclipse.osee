/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.markedit.html;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.markedit.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class OmeBrowserMenu {

   private final Browser browser;
   private final AbstractOmeData omeData;

   public OmeBrowserMenu(AbstractOmeData omeData, Browser browser) {
      this.omeData = omeData;
      this.browser = browser;
   }

   public Menu getPopup(Composite comp) {
      Menu menu = new Menu(comp);

      MenuItem item = new MenuItem(menu, SWT.NONE);
      item.setText("View Source");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (!Strings.isValid(browser.getText())) {
               AWorkbench.popup("ERROR", "Nothing to view");
               return;
            }
            String fileName = System.getProperty("user.home") + File.separator + "out.html";
            try {
               Lib.writeStringToFile(browser.getText(), new File(fileName));
               Program.launch(fileName);
            } catch (IOException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      item = new MenuItem(menu, SWT.NONE);
      item.setText("Print");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (!Strings.isValid(browser.getText())) {
               AWorkbench.popup("ERROR", "Nothing to print");
               return;
            }
            browser.setUrl("javascript:print()");
         }
      });

      item = new MenuItem(menu, SWT.NONE);
      item.setText("Email");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (!Strings.isValid(browser.getText())) {
               AWorkbench.popup("ERROR", "Nothing to email");
               return;
            }
            Set<Manipulations> manipulations = new HashSet<>();
            manipulations.add(Manipulations.ALL);
            manipulations.add(Manipulations.ERROR_WARNING_HEADER);
            Dialogs.emailDialog(omeData.getEditorName(), browser.getText());
         }
      });
      item = new MenuItem(menu, SWT.NONE);
      item.setText("Export Table");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Dialogs.exportHtmlTableDialog(omeData.getEditorName(), browser.getText(), true);
         }
      });
      item = new MenuItem(menu, SWT.NONE);
      item.setText("Save to File");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Dialogs.saveHtmlDialog(browser.getText(), true);
         }
      });
      return menu;
   }

}
