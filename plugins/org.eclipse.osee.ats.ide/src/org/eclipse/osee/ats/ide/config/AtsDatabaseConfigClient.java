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
package org.eclipse.osee.ats.ide.config;

import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsDatabaseConfigClient implements IDbInitializationTask {

   @Override
   public void run() {

      XResultData results = AtsClientService.getConfigEndpoint().atsDbInit();
      if (results.isErrors()) {
         throw new OseeStateException(results.toString());
      }

   }

}