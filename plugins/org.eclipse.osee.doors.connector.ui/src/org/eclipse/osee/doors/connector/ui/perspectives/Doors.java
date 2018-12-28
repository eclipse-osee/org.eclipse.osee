/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.perspectives;

import java.io.File;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.doors.connector.core.DoorsModel;
import org.eclipse.osee.doors.connector.ui.handler.DoorsCreator;
import org.eclipse.osee.doors.connector.ui.oauth.extension.DoorsOSLCDWAProviderInfoExtn;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Doors View to embed browser
 *
 * @author Chandan Bandemutt
 */
public class Doors extends ViewPart {

   private static final String JAVA_SCRIPT_HTML = "JavaScript.html";
   private Browser browser;
   private Composite composite;
   private File f;
   private String fileName = ResourcesPlugin.getWorkspace().getRoot().getLocation() + File.separator + JAVA_SCRIPT_HTML;

   /**
    */
   public Doors() {
      //
   }

   @Override
   public void createPartControl(final Composite parent) {

      this.composite = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout(1, false);
      this.composite.setLayout(gridLayout);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.widthHint = SWT.DEFAULT;
      gridData.heightHint = SWT.DEFAULT;
      this.composite.setLayoutData(gridData);
      this.browser = new Browser(this.composite, SWT.NONE);
      this.browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (this.fileName != null) {
         this.f = new File(this.fileName);
         setBrowserUrl(this.f);
      }
   }

   private void setBrowserUrl(final File f2) {
      if (f2.exists()) {
         new CustomFunction(this.browser, "theJavaFunction");
         this.browser.setUrl(f2.toURI().toString());
      }
   }

   class CustomFunction extends BrowserFunction {

      CustomFunction(final Browser browser, final String name) {
         super(browser, name);
      }

      @Override
      public Object function(final Object[] arguments) {
         if (arguments.length > 0) {
            for (Object object : arguments) {
               String string = (String) object;
               String[] split = string.split("\"");
               String url = null;
               String reqName = null;
               for (String string2 : split) {
                  if (string2.startsWith("http")) {
                     url = string2;
                  }
                  if (isValidName(string2)) {
                     reqName = string2;
                  }
               }
               if (url != null && reqName != null) {
                  BranchSelectionDialog bsd =
                     new BranchSelectionDialog("Select a branch to Import the Doors requirement(s)", true);
                  if (bsd.open() == Window.OK && bsd.getSelection() != null) {

                     DoorsCreator doorTypesCreator = new DoorsCreator();

                     doorTypesCreator.createCQRequirement(url, reqName, bsd.getSelection());

                  }
               }
            }
         }

         return null;
      }
   }

   /**
    * Checks if the name starts with numerals.
    * 
    * @param name Instance of string
    * @return boolean true: if it starts with numerals and false: Entered name does not start with numerals.
    */
   private boolean isValidName(final String name) {
      return Character.isDigit(name.charAt(0));
   }

   /**
    * Method to refresh the browser. In this method url is set to the browser
    */
   @SuppressWarnings("static-access")
   public void refresh() {
      if (this.browser == null) {
         this.browser = new Browser(this.composite, SWT.MOZILLA);
         this.browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      }
      DoorsOSLCDWAProviderInfoExtn config = new DoorsOSLCDWAProviderInfoExtn();

      String jSessionID = DoorsModel.getJSessionID();

      String dwaHostName = config.DWAHostName();
      String[] split2 = dwaHostName.split("//");
      String[] hostName = split2[1].split(":");
      Browser.setCookie("JSESSIONID=" + jSessionID + "; domain=" + hostName[0] + "; path=/", DoorsModel.getDialogUrl());

      if (!this.browser.isDisposed() && this.fileName != null) {
         File file = new File(this.fileName);
         setBrowserUrl(file);
      } else {
         this.fileName =
            ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + JAVA_SCRIPT_HTML;
         File file = new File(this.fileName);
         setBrowserUrl(file);
      }
   }

   /**
    * @return the SWT browser
    */
   public Browser getBrowser() {
      return this.browser;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setFocus() {
      if (this.composite != null) {
         this.composite.setFocus();
      }
   }
}
