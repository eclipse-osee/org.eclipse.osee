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
package org.eclipse.osee.framework.database.core;

import java.util.Random;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class JoinUtility {

   private static final Random random = new Random();

   private JoinUtility() {
      // Utility Class
   }

   private static int getNewQueryId() {
      return random.nextInt();
   }

   private static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return Activator.getInstance().getOseeDatabaseService();
   }

   private static IJoinAccessor createAccessor() throws OseeDataStoreException {
      IOseeDatabaseService service = getDatabase();
      return new DatabaseJoinAccessor(service);
   }

   private static IJoinAccessor createAccessor(String sessionId) throws OseeDataStoreException {
      IOseeDatabaseService service = getDatabase();
      return new DatabaseJoinAccessor(service, sessionId);
   }

   public static TransactionJoinQuery createTransactionJoinQuery() throws OseeDataStoreException {
      return new TransactionJoinQuery(createAccessor(), getNewQueryId());
   }

   public static IdJoinQuery createIdJoinQuery() throws OseeDataStoreException {
      return new IdJoinQuery(createAccessor(), getNewQueryId());
   }

   public static ArtifactJoinQuery createArtifactJoinQuery() throws OseeDataStoreException {
      return new ArtifactJoinQuery(createAccessor(), getNewQueryId());
   }

   public static SearchTagJoinQuery createSearchTagJoinQuery() throws OseeDataStoreException {
      return new SearchTagJoinQuery(createAccessor(), getNewQueryId());
   }

   public static TagQueueJoinQuery createTagQueueJoinQuery() throws OseeDataStoreException {
      return new TagQueueJoinQuery(createAccessor(), getNewQueryId());
   }

   public static ExportImportJoinQuery createExportImportJoinQuery() throws OseeDataStoreException {
      return new ExportImportJoinQuery(createAccessor(), getNewQueryId());
   }

   public static CharJoinQuery createCharJoinQuery(String sessionId) throws OseeDataStoreException {
      return new CharJoinQuery(createAccessor(sessionId), getNewQueryId());
   }
}
