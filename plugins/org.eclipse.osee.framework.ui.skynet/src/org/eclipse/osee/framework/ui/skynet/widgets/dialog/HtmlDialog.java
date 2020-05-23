/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.io.File;
import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class HtmlDialog extends MessageDialog {
   protected Browser b;
   private LocationListener listener;
   private final String html;

   public HtmlDialog(String title, String message, String html) {
      super(Displays.getActiveShell(), title, null, message, SWT.NONE, new String[] {"OK", "Cancel"}, 0);
      this.html = html;
   }

   /**
    * Add listener to browser widget.
    */
   public void addLocationListener(LocationListener listener) {
      this.listener = listener;
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite c = (Composite) super.createDialogArea(parent);
      b = new Browser(c, SWT.BORDER);
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      b.setLayoutData(gd);
      b.setText(html);
      b.setSize(500, 500);
      if (listener != null) {
         b.addLocationListener(listener);
      }
      b.setMenu(pageOverviewGetPopup());

      c.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (Widgets.isAccessible(b)) {
               b.dispose();
            }
         }
      });

      return c;
   }

   public Menu pageOverviewGetPopup() {
      Menu menu = new Menu(b.getShell());
      MenuItem item = new MenuItem(menu, SWT.NONE);
      item.setText("View Source");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            String file = System.getProperty("user.home") + File.separator + "out.html";
            try {
               Lib.writeStringToFile(html, new File(file));
            } catch (IOException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            Program.launch(file);
         }
      });
      return menu;
   }

}
