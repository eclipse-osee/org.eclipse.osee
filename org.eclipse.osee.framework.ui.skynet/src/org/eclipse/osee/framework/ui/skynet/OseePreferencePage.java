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
package org.eclipse.osee.framework.ui.skynet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a
 * page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main
 * plug-in class. That way, preferences can be accessed directly via the preference store.
 */

public class OseePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OseePreferencePage.class);
   public static final String INETADDRESS_KEY =
         "org.eclipse.osee.framework.jdk.core.OseePreferencePage.InetAddressDefault";
   public static final String WORDWRAP_KEY = "org.eclipse.osee.framework.jdk.core.OseePreferencePage.WordWrap";
   private HashMap<InetAddress, Button> networkButtons;
   private Button wordWrapChkBox;
   private OseeUiActivator plugin = SkynetGuiPlugin.getInstance();

   private void createNetworkAdapterArea(Composite parent) {
      addDialogControls(parent);
      setupWordWrapChkButton();
   }

   private void addDialogControls(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout(2, false));

      GridData gd = new GridData();
      gd.verticalAlignment = GridData.FILL;
      gd.horizontalAlignment = GridData.FILL;
      gd.grabExcessHorizontalSpace = true;
      group.setLayoutData(gd);

      wordWrapChkBox = new Button(group, SWT.CHECK);
      wordWrapChkBox.setText("Use alternate hyperlink drag method");

      // setup the default network selection
      Group networkAdapter = new Group(parent, SWT.NONE);
      networkAdapter.setLayout(new GridLayout());
      networkAdapter.setText("Select a Default Network Adaptor");

      gd = new GridData();
      gd.verticalAlignment = GridData.FILL;
      gd.horizontalAlignment = GridData.FILL;
      gd.grabExcessHorizontalSpace = true;
      networkAdapter.setLayoutData(gd);

      setupInetAddressButtons(networkAdapter);
   }

   private void setupInetAddressButtons(Group group) {
      InetAddress[] addrs = null;
      try {
         addrs = Network.getLocalNetworkAdapters();
      } catch (UnknownHostException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }

      networkButtons = new HashMap<InetAddress, Button>();

      Button defaultButton = new Button(group, SWT.RADIO);
      defaultButton.setText("Default");
      for (int i = 0; addrs != null && i < addrs.length; i++) {
         Button button = new Button(group, SWT.RADIO);
         button.setText(addrs[i].getHostAddress() + "  " + addrs[i].getHostName());
         networkButtons.put(addrs[i], button);
      }

      IPreferenceStore prefStore = plugin.getPreferenceStore();
      String inetaddress = prefStore.getString(INETADDRESS_KEY);

      boolean addressSelected = false;
      if (inetaddress != null && !inetaddress.equals("")) {
         for (InetAddress address : networkButtons.keySet()) {
            if (address.getHostAddress().equals(inetaddress)) {
               networkButtons.get(address).setSelection(true);
               addressSelected = true;
            }
         }
      }

      if (!addressSelected) {
         defaultButton.setSelection(true);
      }
   }

   private void setupWordWrapChkButton() {
      IPreferenceStore prefStore = plugin.getPreferenceStore();
      wordWrapChkBox.setSelection(prefStore.getString(WORDWRAP_KEY) != null && prefStore.getString(WORDWRAP_KEY).equals(
            IPreferenceStore.TRUE));
   }

   private void createBlankArea(Composite parent, int height, boolean allVertical) {
      Composite blank = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      GridData gd = new GridData();
      gd.minimumHeight = height;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = allVertical;
      blank.setLayout(gridLayout);
      blank.setLayoutData(gd);
   }

   protected Control createContents(Composite parent) {
      createNetworkAdapterArea(parent);
      createBlankArea(parent, 300, true);

      return parent;
   }

   public static InetAddress getDefaultInetAddress() throws UnknownHostException {
      IPreferenceStore prefStore = SkynetGuiPlugin.getInstance().getPreferenceStore();
      String inetaddress = prefStore.getString(INETADDRESS_KEY);
      if (inetaddress != null && !inetaddress.equals("")) {
         return InetAddress.getByName(inetaddress);
      }
      return Network.getValidIP();
   }

   /**
    * initialize the preference store to use with the workbench
    */
   public void init(IWorkbench workbench) {
      // Initialize the preference store we wish to use
      setPreferenceStore(plugin.getPreferenceStore());
   }

   protected void performDefaults() {
   }

   protected void performApply() {
      performOk();
   }

   public boolean performOk() {
      IPreferenceStore prefStore = plugin.getPreferenceStore();

      prefStore.putValue(INETADDRESS_KEY, "");
      for (InetAddress address : networkButtons.keySet()) {
         if (networkButtons.get(address).getSelection()) {
            prefStore.putValue(INETADDRESS_KEY, address.getHostAddress());
            break;
         }
      }

      prefStore.putValue(WORDWRAP_KEY, wordWrapChkBox.getSelection() ? IPreferenceStore.TRUE : IPreferenceStore.FALSE);

      return super.performOk();
   }
}