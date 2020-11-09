/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.config;

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsDbConfigBaseIde implements IDbInitializationTask {

   @Override
   public void run() {

      XResultData results = AtsApiService.get().getServerEndpoints().getConfigEndpoint().atsDbInit();
      if (results.isErrors()) {
         throw new OseeStateException(results.toString());
      }

   }

}