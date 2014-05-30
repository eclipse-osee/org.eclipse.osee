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

package org.eclipse.osee.ats.navigate;

import java.util.logging.Level;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ClearAtsConfigCache extends XNavigateItemAction {

   public ClearAtsConfigCache(XNavigateItem parent) {
      super(parent, "Clear ATS Config Cache", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      XResultData rd = new XResultData(false);

      rd.log("Pre-reload");
      try {
         AtsClientService.get().getConfig().getReport(rd);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      AtsClientService.get().invalidateConfigCache();

      rd.log("\n\nPost-reload");
      try {
         AtsClientService.get().getConfig().getReport(rd);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      XResultDataUI.report(rd, getName());
   }
}
