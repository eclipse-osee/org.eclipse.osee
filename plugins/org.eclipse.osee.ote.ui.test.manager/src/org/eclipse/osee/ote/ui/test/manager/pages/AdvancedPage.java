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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.widgets.IPropertyStoreBasedControl;
import org.eclipse.osee.framework.ui.plugin.widgets.PropertyStoreControlContributions;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class AdvancedPage extends TestManagerPage {

   public static final OseeUiActivator plugin = TestManagerPlugin.getInstance();
   private static final String pageName = "Advanced";
   private final List<IPropertyStoreBasedControl> contributions;
   private volatile boolean contributionsInitialized;

   public AdvancedPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
      this.contributions = new ArrayList<IPropertyStoreBasedControl>();
      contributionsInitialized = false;
   }

   private synchronized List<IPropertyStoreBasedControl> getContributions() {
      if (!contributionsInitialized) {
         contributionsInitialized = true;
         contributions.addAll(PropertyStoreControlContributions.getContributions(TestManagerPlugin.PLUGIN_ID));
      }
      return contributions;
   }

   @Override
   public void createPage() {
      super.createPage();
      Composite parent = (Composite) getContent();
      Composite extensionPanel = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginWidth = 0;
      gL.marginHeight = 0;
      extensionPanel.setLayout(gL);
      extensionPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      Collections.sort(getContributions(), new Comparator<IPropertyStoreBasedControl>() {

         @Override
         public int compare(IPropertyStoreBasedControl o1, IPropertyStoreBasedControl o2) {
            return Integer.valueOf(o1.getPriority()).compareTo(Integer.valueOf(o2.getPriority()));
         }
      });
      for (IPropertyStoreBasedControl widget : getContributions()) {

         try {
            widget.createControl(extensionPanel);
         } catch (Throwable e) {
            TestManagerPlugin.log(Level.SEVERE, "problem creating advance page contribution", e);
         }
      }

      createBlankArea(parent, 0, true);
      computeScrollSize();

      // TODO: Change to use OteHelpContext
      HelpUtil.setHelp(this, "test_manager_advanced_page", "org.eclipse.osee.ote.help.ui");
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

   @Override
   public void saveData() {
      IPropertyStore propertyStore = getTestManager().getPropertyStore();
      for (IPropertyStoreBasedControl contribution : getContributions()) {
         contribution.save(propertyStore);
      }
   }

   @Override
   public void restoreData() {
      IPropertyStore propertyStore = getTestManager().getPropertyStore();
      for (IPropertyStoreBasedControl contribution : getContributions()) {
         contribution.load(propertyStore);
      }
   }

   @Override
   public boolean areSettingsValidForRun() {
      boolean result = true;
      for (IPropertyStoreBasedControl contribution : getContributions()) {
         result &= contribution.areSettingsValid();
      }
      return result;
   }

   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      for (IPropertyStoreBasedControl contribution : getContributions()) {
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

   @Override
   public boolean onConnectionLost() {
      return false;
   }
}
