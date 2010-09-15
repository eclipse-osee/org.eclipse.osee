/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.cm;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public final class OseeCm {

   private static IOseeCmService oseeCmInstance;

   public static IOseeCmService getInstance() {
      try {
         if (Platform.getExtensionRegistry() == null) {
            return null;
         }
         oseeCmInstance = SkynetGuiPlugin.getInstance().getOseeCmService();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
      }
      return oseeCmInstance;
   }
}
