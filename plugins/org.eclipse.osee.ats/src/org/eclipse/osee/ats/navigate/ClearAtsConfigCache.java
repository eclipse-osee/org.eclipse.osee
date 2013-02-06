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

import org.eclipse.osee.ats.core.client.config.AtsLoadConfigArtifactsOperation;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
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
      AtsConfigCache.instance.getReport(rd);

      AtsConfigCache.instance.clearCaches();

      AtsLoadConfigArtifactsOperation opt = new AtsLoadConfigArtifactsOperation(true);
      try {
         Operations.executeWorkAndCheckStatus(opt);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      rd.log("\n\nPost-reload");
      AtsConfigCache.instance.getReport(rd);

      XResultDataUI.report(rd, getName());
   }
}
