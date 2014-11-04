/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast;

import java.util.Properties;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.vcast.internal.SqliteDbInfo;
import org.eclipse.osee.vcast.internal.SqliteStatementProvider;
import org.eclipse.osee.vcast.internal.VCastDataStoreImpl;
import org.eclipse.osee.vcast.internal.VCastDataStoreImpl.StatementProvider;

/**
 * @author Roberto E. Escobar
 */
public class VCastClient {

   private VCastClient() {
      // Static Factory
   }

   public static VCastDataStore newDataStore(IOseeDatabaseService dbService, String dbPath) throws OseeCoreException {
      String connectionId = GUID.create();
      SqliteDbInfo dbInfo = new SqliteDbInfo(connectionId, dbPath, new Properties());
      StatementProvider provider = new SqliteStatementProvider(dbService, dbInfo);
      return new VCastDataStoreImpl(provider);
   }

}
