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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.preferences.PreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class HttpServerPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpServerPreferences.class);

   public HttpServerPreferences() {
      super(GRID);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
    */
   @Override
   protected void createFieldEditors() {
      Composite parent = getFieldEditorParent();
      IPreferenceStore preference = getPreferenceStore();

      Composite content = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      content.setLayout(layout);
      content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Group resourceGroup = new Group(content, SWT.NONE);
      resourceGroup.setLayout(new GridLayout());
      resourceGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      resourceGroup.setText("OSEE Application Server");

      Composite resourceComposite = new Composite(resourceGroup, SWT.NONE);
      resourceComposite.setLayout(new GridLayout(2, false));
      resourceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      String defaultApplicationServer = null;
      try {
         defaultApplicationServer = HttpUrlBuilder.getInstance().getApplicationServerPrefix();
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      Label label1 = new Label(resourceComposite, SWT.NONE);
      label1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
      label1.setText("Default: ");

      Label label2 = new Label(resourceComposite, SWT.NONE);
      label2.setText(defaultApplicationServer != null ? defaultApplicationServer : "");

      Group httpGroup = new Group(content, SWT.NONE);
      httpGroup.setLayout(new GridLayout());
      httpGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      httpGroup.setText("Http Server");

      String defaultRemoteAddress = preference.getDefaultString(PreferenceConstants.OSEE_REMOTE_HTTP_SERVER);
      addField(new DefaultWithStringAndIntegerFields(PreferenceConstants.OSEE_REMOTE_HTTP_SERVER, defaultRemoteAddress,
            "Enter Address:", "Enter Port:", httpGroup));
      httpGroup.setVisible(false);

   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetActivator.getInstance().getPreferenceStore());
      setDescription("Select an HTTP server or specify a server address and port in the entry boxes below.");
   }

}
