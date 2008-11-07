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
package org.eclipse.osee.framework.ui.skynet.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class ConfigurationDetails extends PreferencePage implements IWorkbenchPreferencePage {
   private static final String HTML_HEADER =
         "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html14/loose.dtd\">\n";

   private static final String CSS_SHEET =
         "<style type=\"text/css\"> table.oseeTable { font: 0.7em \"arial\", serif; border-width: 1px 1px 1px 1px; border-spacing: 2px; border-style: solid solid solid solid; border-color: blue blue blue blue; border-collapse: separate; background-color: rgb(255, 250, 250); } " + " table.oseeTable th { border-width: 1px 1px 1px 1px; padding: 4px 4px 4px 4px; border-style: solid solid solid solid; border-color: black black black black; background-color: white; -moz-border-radius: 0px 0px 0px 0px; } " + " table.oseeTable td { border-width: 1px 1px 1px 1px; padding: 4px 4px 4px 4px; border-style: solid solid solid solid; border-color: black black black black; background-color: white; -moz-border-radius: 0px 0px 0px 0px; } </style>\n";

   private static final String PAGE_TEMPLATE =
         HTML_HEADER + "<html>\n<head>\n" + CSS_SHEET + "</head>\n<body>\n%s</body>\n</html>";

   private Browser browser;

   public ConfigurationDetails() {
      super();
      this.browser = null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetActivator.getInstance().getPreferenceStore());
      setDescription("See below for OSEE configuration details.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createContents(Composite parent) {
      Composite content = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      content.setLayout(layout);
      content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Group composite = new Group(content, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Connections");

      browser = new Browser(composite, SWT.READ_ONLY | SWT.BORDER);
      browser.setLayout(new FillLayout());
      browser.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            generatePage();
         }
      });
      return content;
   }

   private void generatePage() {
      StringBuilder builder = new StringBuilder();
      builder.append("<table class=\"oseeTable\" width=\"100%\">");
      builder.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Info", "Status"}));

      for (IHealthStatus status : OseeLog.getStatus()) {
         builder.append(AHTML.addRowMultiColumnTable(new String[] {
               "<b>" + status.getSourceName() + "</b>",
               status.getMessage().replaceAll("]", "]<br/>"),
               status.isOk() ? "<font color=\"green\"><b>Ok</b></font>" : "<font color=\"red\"><b>Unavailable</b></font>"}));
      }
      builder.append(AHTML.endMultiColumnTable());
      browser.setText(String.format(PAGE_TEMPLATE, builder.toString()));
   }
}
