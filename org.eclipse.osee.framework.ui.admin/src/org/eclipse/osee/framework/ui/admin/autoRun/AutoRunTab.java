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
package org.eclipse.osee.framework.ui.admin.autoRun;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.admin.AdminPlugin;
import org.eclipse.osee.framework.ui.admin.OseeClientsTab;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtons;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class AutoRunTab {

   private User whoAmI;
   private Composite mainComposite;
   XText testDbConfigText;
   XText prodDbConfigText;
   XCheckBox launchWBCheckBox;
   XAutoRunViewer xAutoRunViewer;
   XRadioButtons emailResultsRadio;
   XText emailResultsText;
   private static String EMAIL_RESULTS_TO_CONFIGURED_EMAILS = "Configured Emails";
   private static String EMAIL_RESULTS_TO_ENTERED_EMAILS = "Email Address(s)";

   public AutoRunTab(TabFolder tabFolder) {
      super();
      this.whoAmI = SkynetAuthentication.getUser();
      this.mainComposite = null;
      createControl(tabFolder);
      mainComposite.setEnabled(isUserAllowedToOperate(whoAmI));
   }

   private void createControl(TabFolder tabFolder) {
      mainComposite = new Composite(tabFolder, SWT.NONE);
      mainComposite.setLayout(ALayout.getZeroMarginLayout());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite dbConfigComp = new Composite(mainComposite, SWT.NONE);
      dbConfigComp.setLayout(new GridLayout(6, false));
      dbConfigComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      launchWBCheckBox = new XCheckBox("Launch New Workbench");
      launchWBCheckBox.createWidgets(dbConfigComp, 2);
      launchWBCheckBox.set(true);
      launchWBCheckBox.setToolTip("If selected, tasks will be run in a newly launched WB;\nElse, tasks will be run in current workbench.");
      launchWBCheckBox.addXModifiedListener(new XModifiedListener() {
         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
          */
         public void widgetModified(XWidget widget) {
            updateEnablement();
         }
      });

      prodDbConfigText = new XText("Production DefaultDbConnection");
      prodDbConfigText.createWidgets(dbConfigComp, 1);
      prodDbConfigText.setText("oracle7");

      testDbConfigText = new XText("Test DefaultDbConnection");
      testDbConfigText.createWidgets(dbConfigComp, 1);
      testDbConfigText.setText("postgresqlLocalhost");

      Composite emailResultsComp = new Composite(mainComposite, SWT.NONE);
      emailResultsComp.setLayout(ALayout.getZeroMarginLayout(6, false));
      emailResultsComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      emailResultsRadio = new XRadioButtons("Email Results To", "");
      emailResultsRadio.addButton(EMAIL_RESULTS_TO_CONFIGURED_EMAILS);
      emailResultsRadio.addButton(EMAIL_RESULTS_TO_ENTERED_EMAILS);
      emailResultsRadio.createWidgets(emailResultsComp, 2);
      emailResultsRadio.setRequiredEntry(true);
      emailResultsRadio.setToolTip("Select who to email results to.");
      emailResultsRadio.addXModifiedListener(new XModifiedListener() {
         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
          */
         public void widgetModified(XWidget widget) {
            updateEnablement();
         }
      });

      emailResultsText = new XText("");
      emailResultsText.createWidgets(emailResultsComp, 1);
      try {
         emailResultsText.setText(SkynetAuthentication.getUser().getEmail());
      } catch (Exception ex) {
         OSEELog.logException(AdminPlugin.class, ex, false);
      }

      TabItem tab = new TabItem(tabFolder, SWT.NONE);
      tab.setControl(mainComposite);
      tab.setText("Auto Run Tasks");

      if (true != isUserAllowedToOperate(whoAmI)) {
         OseeClientsTab.createDefaultWarning(mainComposite);
      } else {
         xAutoRunViewer = new XAutoRunViewer(this);
         xAutoRunViewer.setDisplayLabel(false);
         xAutoRunViewer.createWidgets(mainComposite, 1);
      }
      updateEnablement();
   }

   public String getOverriddenEmail() {
      return emailResultsText.getText();
   }

   public boolean isEmailOverridden() {
      return !isEmailConfiguredEmails();
   }

   public boolean isEmailConfiguredEmails() {
      return emailResultsRadio.getSelectedNames().contains(EMAIL_RESULTS_TO_CONFIGURED_EMAILS);
   }

   public Result isRunnable() {
      if (emailResultsRadio.getSelectedNames().size() == 0) return new Result(
            "Must select \"Email Results To\" option.");
      if (isEmailConfiguredEmails()) {
         if (emailResultsText.getText().equals("")) return new Result("Must enter Email Address");
      }
      if (xAutoRunViewer.getXViewer().getRunList().size() == 0) {
         return new Result("No Tasks Selected");
      }
      return Result.TrueResult;
   }

   private void updateEnablement() {
      prodDbConfigText.getStyledText().setEnabled(launchWBCheckBox.isSelected());
      prodDbConfigText.getLabelWidget().setEnabled(launchWBCheckBox.isSelected());
      testDbConfigText.getStyledText().setEnabled(launchWBCheckBox.isSelected());
      testDbConfigText.getLabelWidget().setEnabled(launchWBCheckBox.isSelected());

      if (isEmailConfiguredEmails()) {
         emailResultsText.setEnabled(false);
      } else {
         emailResultsText.setEnabled(true);
      }
      emailResultsRadio.refresh();

      if (xAutoRunViewer != null) xAutoRunViewer.getXViewer().refresh();
   }

   private boolean isUserAllowedToOperate(User user) {
      return OseeProperties.isDeveloper();
   }

   /**
    * @return the testDbConfigText
    */
   public XText getTestDbConfigText() {
      return testDbConfigText;
   }

   /**
    * @return the prodDbConfigText
    */
   public XText getProdDbConfigText() {
      return prodDbConfigText;
   }

   /**
    * @return the launchWBCheckBox
    */
   public XCheckBox getLaunchWBCheckBox() {
      return launchWBCheckBox;
   }

}
