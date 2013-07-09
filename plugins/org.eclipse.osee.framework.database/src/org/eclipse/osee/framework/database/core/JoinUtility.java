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
import org.eclipse.osee.framework.database.internal.ServiceUtil;

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

   private static IJoinAccessor createAccessor(IOseeDatabaseService service) {
      return new DatabaseJoinAccessor(service);
   }

   private static IJoinAccessor createAccessor(IOseeDatabaseService service, String sessionId) {
      return new DatabaseJoinAccessor(service, sessionId);
   }

   public static TransactionJoinQuery createTransactionJoinQuery(IOseeDatabaseService service) {
      return new TransactionJoinQuery(createAccessor(service), getNewQueryId());
   }

   public static IdJoinQuery createIdJoinQuery(IOseeDatabaseService service) {
      return new IdJoinQuery(createAccessor(service), getNewQueryId());
   }

   public static IdJoinQuery createIdJoinQuery(IOseeDatabaseService service, String sessionId) {
      return new IdJoinQuery(createAccessor(service, sessionId), getNewQueryId());
   }

   public static ArtifactJoinQuery createArtifactJoinQuery(IOseeDatabaseService service) {
      return new ArtifactJoinQuery(createAccessor(service), getNewQueryId());
   }

   public static SearchTagJoinQuery createSearchTagJoinQuery(IOseeDatabaseService service) {
      return new SearchTagJoinQuery(createAccessor(service), getNewQueryId());
   }

   public static TagQueueJoinQuery createTagQueueJoinQuery(IOseeDatabaseService service) {
      return new TagQueueJoinQuery(createAccessor(service), getNewQueryId());
   }

   public static ExportImportJoinQuery createExportImportJoinQuery(IOseeDatabaseService service) {
      return new ExportImportJoinQuery(createAccessor(service), getNewQueryId());
   }

   public static CharJoinQuery createCharJoinQuery(IOseeDatabaseService service, String sessionId) {
      return new CharJoinQuery(createAccessor(service, sessionId), getNewQueryId());
   }

   public static CharJoinQuery createCharJoinQuery(IOseeDatabaseService service) {
      return new CharJoinQuery(createAccessor(service), getNewQueryId());
   }

   ////////////////// Static Legacy Calls /////////////////////////
   private static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return ServiceUtil.getDatabaseService();
   }

   public static TransactionJoinQuery createTransactionJoinQuery() throws OseeDataStoreException {
      return new TransactionJoinQuery(createAccessor(getDatabase()), getNewQueryId());
   }

   public static IdJoinQuery createIdJoinQuery() throws OseeDataStoreException {
      return new IdJoinQuery(createAccessor(getDatabase()), getNewQueryId());
   }

   public static ArtifactJoinQuery createArtifactJoinQuery() throws OseeDataStoreException {
      return new ArtifactJoinQuery(createAccessor(getDatabase()), getNewQueryId());
   }

   public static SearchTagJoinQuery createSearchTagJoinQuery() throws OseeDataStoreException {
      return new SearchTagJoinQuery(createAccessor(getDatabase()), getNewQueryId());
   }

   public static TagQueueJoinQuery createTagQueueJoinQuery() throws OseeDataStoreException {
      return new TagQueueJoinQuery(createAccessor(getDatabase()), getNewQueryId());
   }

   public static ExportImportJoinQuery createExportImportJoinQuery() throws OseeDataStoreException {
      return new ExportImportJoinQuery(createAccessor(getDatabase()), getNewQueryId());
   }

}
