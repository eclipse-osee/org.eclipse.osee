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
package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.Random;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Strings;

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
      return new ArtifactJoinQuery(createAccessor(service), getNewQueryId(), getMaxArtifactJoinSize(service));
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

   private static int getMaxArtifactJoinSize(IOseeDatabaseService service) {
      int toReturn = Integer.MAX_VALUE;
      String maxSize = OseeInfo.getCachedValue(service, "artifact.join.max.size");
      if (Strings.isNumeric(maxSize)) {
         toReturn = Integer.parseInt(maxSize);
      }
      return toReturn;
   }

}
