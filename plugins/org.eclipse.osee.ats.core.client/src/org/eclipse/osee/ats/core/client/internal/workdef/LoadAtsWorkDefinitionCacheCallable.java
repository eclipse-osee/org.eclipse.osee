/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.workdef;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionCache;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class LoadAtsWorkDefinitionCacheCallable implements Callable<AtsWorkDefinitionCache> {
   private static String NAME = "Loading ATS Work Definitions";

   private final IAtsWorkDefinitionService workDefinitionService;

   public LoadAtsWorkDefinitionCacheCallable(IAtsWorkDefinitionService workDefinitionService) {
      super();
      this.workDefinitionService = workDefinitionService;
   }

   @Override
   public AtsWorkDefinitionCache call() throws Exception {
      AtsWorkDefinitionCache cache = new AtsWorkDefinitionCache();

      XResultData resultData = new XResultData(false);
      workDefinitionService.getAllWorkDefinitions(resultData);

      if (!resultData.isEmpty()) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error " + NAME + resultData.toString());
      }

      return cache;
   }

}