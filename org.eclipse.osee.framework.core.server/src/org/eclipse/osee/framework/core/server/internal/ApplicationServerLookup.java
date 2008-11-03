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
package org.eclipse.osee.framework.core.server.internal;

import java.util.List;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerLookup implements IApplicationServerLookup {

   public OseeServerInfo getServerInfoBy(String version) throws OseeDataStoreException {
      OseeServerInfo result = null;
      List<OseeServerInfo> infos = ApplicationServerDataStore.getApplicationServerInfos(version);

      //TODO: query servers to see if load balancing is also needed
      for (OseeServerInfo info : infos) {
         if (info.isAcceptingRequests()) {
            result = info;
            break;
         }
      }
      return result;
   }
}
