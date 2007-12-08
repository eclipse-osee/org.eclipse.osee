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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class XResultsComposite extends Composite {

   protected Browser browser;
   private String htmlText;
   private String title = "";

   /**
    * @param parent
    * @param style
    */
   public XResultsComposite(Composite parent, int style) {
      super(parent, style);

      setLayout(new GridLayout(1, false));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      browser = new Browser(this, SWT.BORDER);
      browser.addLocationListener(new ResultBrowserListener());
      browser.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
      browser.setMenu(getPopup(parent));

   }

   public Menu getPopup(Composite comp) {
      Menu menu = new Menu(comp);

      MenuItem item = new MenuItem(menu, SWT.NONE);
      item.setText("View Source");
      item.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            if (htmlText == null || htmlText.equals("")) {
               AWorkbench.popup("ERROR", "Nothing to view");
               return;
            }
            String fileName = System.getProperty("user.home") + File.separator + "out.html";
            AFile.writeFile(fileName, htmlText);
            Program.launch(fileName);
         }
      });

      item = new MenuItem(menu, SWT.NONE);
      item.setText("Print");
      item.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            if (htmlText == null || htmlText.equals("")) {
               AWorkbench.popup("ERROR", "Nothing to print");
               return;
            }
            browser.setUrl("javascript:print()");
         }
      });

      item = new MenuItem(menu, SWT.NONE);
      item.setText("Email");
      item.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            if (htmlText == null || htmlText.equals("")) {
               AWorkbench.popup("ERROR", "Nothing to email");
               return;
            }
            Set<Manipulations> manipulations = new HashSet<Manipulations>();
            manipulations.add(Manipulations.ALL);
            manipulations.add(Manipulations.ERROR_WARNING_HEADER);
            Dialogs.emailDialog(title, htmlText);
         }
      });
      item = new MenuItem(menu, SWT.NONE);
      item.setText("Export Table");
      item.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            Dialogs.exportHtmlTableDialog(title, htmlText, true);
         }
      });
      item = new MenuItem(menu, SWT.NONE);
      item.setText("Save to File");
      item.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            Dialogs.saveHtmlDialog(htmlText, true);
         }
      });
      return menu;
   }

   /**
    * @return the browser
    */
   public Browser getBrowser() {
      return browser;
   }

   /**
    * @return the htmlText
    */
   public String getHtmlText() {
      return htmlText;
   }

   /**
    * @param htmlText the htmlText to set
    */
   public void setHtmlText(String htmlText) {
      this.htmlText = htmlText;
   }

   /**
    * @param title
    * @param htmlText
    */
   public void setHtmlText(String htmlText, String title) {
      this.htmlText = htmlText;
      this.title = title;
      if (browser != null && !browser.isDisposed()) browser.setText(htmlText);
   }

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }
}
