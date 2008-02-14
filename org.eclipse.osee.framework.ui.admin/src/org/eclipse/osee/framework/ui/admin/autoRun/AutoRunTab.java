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
import org.eclipse.osee.framework.ui.admin.OseeClientsTab;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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

   public AutoRunTab(TabFolder tabFolder) {
      super();
      this.whoAmI = SkynetAuthentication.getInstance().getAuthenticatedUser();
      this.mainComposite = null;
      createControl(tabFolder);
      mainComposite.setEnabled(isUserAllowedToOperate(whoAmI));
   }

   private void createControl(TabFolder tabFolder) {
      mainComposite = new Composite(tabFolder, SWT.NONE);
      mainComposite.setLayout(new GridLayout());
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

   private void updateEnablement() {
      prodDbConfigText.getStyledText().setEnabled(launchWBCheckBox.isSelected());
      prodDbConfigText.getLabelWidget().setEnabled(launchWBCheckBox.isSelected());
      testDbConfigText.getStyledText().setEnabled(launchWBCheckBox.isSelected());
      testDbConfigText.getLabelWidget().setEnabled(launchWBCheckBox.isSelected());
      xAutoRunViewer.getXViewer().refresh();
   }

   private boolean isUserAllowedToOperate(User user) {
      return OseeProperties.getInstance().isDeveloper();
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
