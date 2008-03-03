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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.preferences.PreferenceConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class HttpServerPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

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
      String defaultRemoteAddress = preference.getDefaultString(PreferenceConstants.OSEE_REMOTE_HTTP_SERVER);
      addField(new DefaultWithStringAndIntegerFields(PreferenceConstants.OSEE_REMOTE_HTTP_SERVER, defaultRemoteAddress,
            "Enter Address:", "Enter Port:", parent));

   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetActivator.getInstance().getPreferenceStore());
      setDescription("Select an HTTP server from the drop down or specify a server address and port in the entry box below.");
   }

}
