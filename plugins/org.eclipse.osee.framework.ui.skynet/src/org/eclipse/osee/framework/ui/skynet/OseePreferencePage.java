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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.core.client.CoreClientConstants;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.CorePreferences;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUrlClient;
import org.eclipse.osee.framework.skynet.core.preferences.PreferenceConstants;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * This class represents a preference page that is contributed to the Preferences dialog.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main
 * plug-in class. That way, preferences can be accessed directly via the preference store.
 */

public class OseePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
   public static String PAGE_ID = "org.eclipse.osee.framework.ui.skynet.OseePreferencePage";
   private HashMap<InetAddress, Button> networkButtons;
   private Button wordWrapChkBox;
   private Button baseUrlCheckBox;
   private Button connectedUrlCheckBox;

   private void createNetworkAdapterArea(Composite parent) {
      addDialogControls(parent);
      setupWordWrapChkButton();
   }

   private void addDialogControls(Composite parent) {
      Group networkAdapter = new Group(parent, SWT.NONE);
      networkAdapter.setLayout(new GridLayout());
      networkAdapter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      networkAdapter.setText("Select a Default Network Adapter");

      setupInetAddressButtons(networkAdapter);

      Group linksGroup = new Group(parent, SWT.NONE);
      linksGroup.setLayout(new GridLayout());
      linksGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      linksGroup.setText("Select a Permanent Hyperlink Base Url");
      setupUrlLinkButtons(linksGroup);

      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      wordWrapChkBox = new Button(group, SWT.CHECK);
      wordWrapChkBox.setText("Use alternate hyperlink drag method");
   }

   private void setupInetAddressButtons(Group group) {
      InetAddress[] addrs = null;
      try {
         addrs = Network.getLocalNetworkAdapters();
      } catch (UnknownHostException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      networkButtons = new HashMap<>();

      Button defaultButton = new Button(group, SWT.RADIO);
      defaultButton.setText("Default");
      for (int i = 0; addrs != null && i < addrs.length; i++) {
         Button button = new Button(group, SWT.RADIO);
         button.setText(addrs[i].getHostAddress() + "  " + addrs[i].getHostName());
         networkButtons.put(addrs[i], button);
      }

      String inetaddress = getPreferenceStore().getString(CorePreferences.INETADDRESS_KEY);

      boolean addressSelected = false;
      if (Strings.isValid(inetaddress)) {
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
      IPreferenceStore prefStore = getPreferenceStore();
      wordWrapChkBox.setSelection(prefStore.getString(PreferenceConstants.WORDWRAP_KEY) != null && prefStore.getString(
         PreferenceConstants.WORDWRAP_KEY).equals(IPreferenceStore.TRUE));
   }

   private void setupUrlLinkButtons(Group group) {
      baseUrlCheckBox = new Button(group, SWT.RADIO);
      try {
         baseUrlCheckBox.setText("Permanent Link: " + new ArtifactUrlClient().getPermanentBaseUrl());
      } catch (OseeCoreException ex) {
         baseUrlCheckBox.setText("Permanent Link");
      }

      connectedUrlCheckBox = new Button(group, SWT.RADIO);
      try {
         connectedUrlCheckBox.setText(
            "Temporary Server Link: " + HttpUrlBuilderClient.getInstance().getApplicationServerPrefix());
      } catch (OseeCoreException ex) {
         connectedUrlCheckBox.setText("Temporary Server Link");
      }

      boolean useConnected =
         getPreferenceStore().getBoolean(HttpUrlBuilderClient.USE_CONNECTED_SERVER_URL_FOR_PERM_LINKS);
      connectedUrlCheckBox.setSelection(useConnected);
      baseUrlCheckBox.setSelection(!useConnected);
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

   @Override
   protected Control createContents(Composite parent) {
      createNetworkAdapterArea(parent);
      createBlankArea(parent, 300, true);

      return parent;
   }

   /**
    * initialize the preference store to use with the workbench
    */
   @SuppressWarnings("deprecation")
   @Override
   public void init(IWorkbench workbench) {
      // Initialize the preference store we wish to use
      IPreferenceStore preferenceStore =
         new ScopedPreferenceStore(new InstanceScope(), CoreClientConstants.getBundleId());
      setPreferenceStore(preferenceStore);
   }

   @Override
   protected void performDefaults() {
      // do nothing
   }

   @Override
   protected void performApply() {
      performOk();
   }

   @Override
   public boolean performOk() {
      getPreferenceStore().setValue(CorePreferences.INETADDRESS_KEY, "");
      for (InetAddress address : networkButtons.keySet()) {
         if (networkButtons.get(address).getSelection()) {
            getPreferenceStore().setValue(CorePreferences.INETADDRESS_KEY, address.getHostAddress());
            break;
         }
      }

      getPreferenceStore().setValue(HttpUrlBuilderClient.USE_CONNECTED_SERVER_URL_FOR_PERM_LINKS,
         connectedUrlCheckBox.getSelection());

      getPreferenceStore().putValue(PreferenceConstants.WORDWRAP_KEY,
         wordWrapChkBox.getSelection() ? IPreferenceStore.TRUE : IPreferenceStore.FALSE);

      return super.performOk();
   }
}