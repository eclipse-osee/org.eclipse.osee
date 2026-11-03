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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;

/**
 * @author Donald G. Dunne
 */
public class HtmlDialog extends MessageDialog {
   protected Browser browser;
   private LocationListener listener;
   private final String html;
   private int preferredWidth = 600;
   private int preferredHeight = 400;

   public HtmlDialog(String title, String message, String html) {
      super(Displays.getActiveShell(), title, null, message, SWT.NONE, new String[] {"OK", "Cancel"}, 0);
      this.html = html;
   }

   /**
    * Computes a preferred dialog size based on the HTML content length.
    */
   private void computePreferredSize() {
      if (html == null || html.isEmpty()) {
         return;
      }
      // Estimate size based on content - strip HTML tags for rough character count
      String textOnly = html.replaceAll("<[^>]*>", "");
      int lineCount = html.split("<br>|<br/>|<BR>|<BR/>|\n|<p>|<P>|<tr>|<TR>|<li>|<LI>").length;

      // Scale width based on longest line (rough estimate)
      int maxLineLength = 0;
      for (String line : textOnly.split("\n")) {
         maxLineLength = Math.max(maxLineLength, line.length());
      }
      preferredWidth = Math.min(Math.max(400, maxLineLength * 5), 800);
      preferredHeight = Math.min(Math.max(300, lineCount * 20 + 100), 800);

      // Ensure we don't exceed screen bounds
      Monitor monitor = browser.getMonitor() != null ? browser.getMonitor() : Displays.getActiveShell().getMonitor();
      Rectangle screenBounds = monitor.getClientArea();
      preferredWidth = Math.min(preferredWidth, screenBounds.width - 100);
      preferredHeight = Math.min(preferredHeight, screenBounds.height - 100);
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
   protected Point getInitialSize() {
      return new Point(preferredWidth, preferredHeight);
   }

   /**
    * Sets the preferred dialog size. Call before open().
    */
   public void setPreferredSize(int width, int height) {
      this.preferredWidth = width;
      this.preferredHeight = height;
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite comp = (Composite) super.createDialogArea(parent);
      browser = new Browser(comp, SWT.BORDER);
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = preferredWidth - 50;
      gd.heightHint = preferredHeight - 150;
      browser.setLayoutData(gd);
      browser.setText(html);
      if (listener != null) {
         browser.addLocationListener(listener);
      }
      browser.setMenu(pageOverviewGetPopup());

      comp.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (Widgets.isAccessible(browser)) {
               browser.dispose();
            }
         }
      });
      computePreferredSize();
      return comp;
   }

   public Menu pageOverviewGetPopup() {
      Menu menu = new Menu(browser.getShell());
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
