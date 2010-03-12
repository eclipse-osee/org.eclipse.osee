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
package org.eclipse.osee.ote.ui.test.manager.pages;

import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author David Diepenbrock
 */
public abstract class TestManagerPage extends ScrolledComposite {

   private final TestManagerEditor testManager;
   private Composite mainComposite;

   /**
    * @param parent
    * @param style
    */
   public TestManagerPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style | SWT.H_SCROLL | SWT.V_SCROLL);
      this.testManager = parentTestManager;
   }

   protected void createPage() {
      GridLayout gridLayout = new GridLayout(1, true);
      this.setLayout(gridLayout);

      this.mainComposite = new Composite(this, SWT.NONE);
      this.mainComposite.setLayout(new GridLayout());
      this.mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      this.setContent(mainComposite);
      this.setExpandHorizontal(true);
      this.setExpandVertical(true);
   }

   public void computeScrollSize() {
      this.computeScrollSize(mainComposite);
   }

   private void computeScrollSize(Composite viewableArea) {
      this.setMinSize(viewableArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
   }

   public TestManagerEditor getTestManager() {
      return testManager;
   }

   public abstract String getPageName();

   public abstract void saveData();

   public abstract void restoreData();

   public abstract boolean areSettingsValidForRun();

   public abstract String getErrorMessage();

   /**
    * called when test manager is connected to a test host. A return value of true will cause test manager to prompt the
    * user to check the log. Thus any problems that occur during connection processing should be handled and logged.
    * 
    * @param event
    * @return true if there were problems during processing and false otherwise.
    */
   public abstract boolean onConnection(ConnectionEvent event);

   public abstract boolean onDisconnect(ConnectionEvent event);

   public abstract boolean onConnectionLost(IHostTestEnvironment testHost);

   @Override
   public void dispose() {
      super.dispose();
   }
}
