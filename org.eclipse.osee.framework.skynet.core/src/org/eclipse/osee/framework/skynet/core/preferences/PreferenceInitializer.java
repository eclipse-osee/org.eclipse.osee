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

package org.eclipse.osee.framework.skynet.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Roberto E. Escobar
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
    */
   public void initializeDefaultPreferences() {
      IPreferenceStore store = SkynetActivator.getInstance().getPreferenceStore();

      String defaultRemoteAddress = ConfigUtil.getConfigFactory().getOseeConfig().getRemoteHttpServer();
      store.setDefault(PreferenceConstants.OSEE_REMOTE_HTTP_SERVER, defaultRemoteAddress);
   }
}
