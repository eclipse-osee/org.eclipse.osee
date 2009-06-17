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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class AdvancedPage extends TestManagerPage {


   public static final OseeUiActivator plugin = TestManagerPlugin.getInstance();
   private static final String pageName = "Advanced";

   public AdvancedPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
   }

   public void createPage() {
      super.createPage();
      Composite parent = (Composite) getContent();
      Composite extensionPanel = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginWidth = 0;
      gL.marginHeight = 0;
      extensionPanel.setLayout(gL);
      extensionPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      List<IAdvancedPageContribution> contributions =
            getTestManager().getContributions().getAdvancedPageContributions();
      Collections.sort(contributions, new Comparator<IAdvancedPageContribution>() {

         @Override
         public int compare(IAdvancedPageContribution o1, IAdvancedPageContribution o2) {
            return Integer.valueOf(o1.getPriority()).compareTo(Integer.valueOf(o2.getPriority()));
         }
      });
      for (IAdvancedPageContribution widget : contributions) {
	  
         try {
		widget.createControl(extensionPanel);
	    } catch (Throwable e) {
		TestManagerPlugin.log(Level.SEVERE,
			"problem creating advance page contribution", e);
	    }
      }

      createBlankArea(parent, 0, true);
      computeScrollSize();
      TestManagerPlugin.getInstance().setHelp(this, "tm_advanced_page");
   }

   @Override
   public String getPageName() {
      return pageName;
   }

   private Control createBlankArea(Composite parent, int height, boolean allVertical) {
      Composite blank = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      GridData gd = new GridData();
      gd.minimumHeight = height;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = allVertical;
      blank.setLayout(gridLayout);
      blank.setLayoutData(gd);
      return parent;
   }

   protected void createAreaDefaultLayout(Composite parent, boolean allHorizontal, boolean allVertical) {
      GridLayout layout = new GridLayout();
      GridData data = new GridData(GridData.FILL_BOTH);
      data.grabExcessHorizontalSpace = allHorizontal;
      data.grabExcessVerticalSpace = allVertical;
      parent.setLayout(layout);
      parent.setLayoutData(data);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#saveData()
    */
   @Override
   public void saveData() {
      IPropertyStore propertyStore = getTestManager().getPropertyStore();
      List<IAdvancedPageContribution> contributions =
            getTestManager().getContributions().getAdvancedPageContributions();
      for (IAdvancedPageContribution contribution : contributions) {
         contribution.save(propertyStore);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#restoreData()
    */
   @Override
   public void restoreData() {
      IPropertyStore propertyStore = getTestManager().getPropertyStore();
      List<IAdvancedPageContribution> contributions =
            getTestManager().getContributions().getAdvancedPageContributions();
      for (IAdvancedPageContribution contribution : contributions) {
         contribution.load(propertyStore);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#areSettingsValidForRun()
    */
   @Override
   public boolean areSettingsValidForRun() {
      boolean result = true;
      List<IAdvancedPageContribution> contributions =
            getTestManager().getContributions().getAdvancedPageContributions();
      for (IAdvancedPageContribution contribution : contributions) {
         result &= contribution.areSettingsValidForRun();
      }
      return result;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#getErrorMessage()
    */
   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      List<IAdvancedPageContribution> contributions =
            getTestManager().getContributions().getAdvancedPageContributions();
      for (IAdvancedPageContribution contribution : contributions) {
         String message = contribution.getErrorMessage();
         if (Strings.isValid(message)) {
            if (builder.length() > 0) {
               builder.append("\n");
            }
            builder.append(message);
         }
      }
      return builder.toString();
   }


   @Override
    public boolean onConnection(ConnectionEvent event) {
	return false;
    }

    @Override
    public boolean onDisconnect(ConnectionEvent event) {
	return false;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#onConnectionLost(org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment)
	 */
	@Override
	public boolean onConnectionLost(IHostTestEnvironment testHost) {
		return false;
	}
}
