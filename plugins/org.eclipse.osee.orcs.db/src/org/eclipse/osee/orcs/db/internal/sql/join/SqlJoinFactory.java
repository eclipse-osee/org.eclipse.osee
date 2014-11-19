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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.SystemPreferences;

/**
 * @author Roberto E. Escobar
 */
public class SqlJoinFactory {

   private IOseeDatabaseService service;
   private SystemPreferences preferences;
   private Random random;

   public void setDatabaseService(IOseeDatabaseService service) {
      this.service = service;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void start() {
      random = new Random();
   }

   public void stop() {
      random = null;
   }

   private int getNewQueryId() {
      return random.nextInt();
   }

   private IJoinAccessor createAccessor() {
      return new DatabaseJoinAccessor(service);
   }

   private IJoinAccessor createAccessor(String sessionId) {
      return new DatabaseJoinAccessor(service, sessionId);
   }

   public TransactionJoinQuery createTransactionJoinQuery() {
      return new TransactionJoinQuery(createAccessor(), getNewQueryId());
   }

   public IdJoinQuery createIdJoinQuery() {
      return new IdJoinQuery(createAccessor(), getNewQueryId());
   }

   public IdJoinQuery createIdJoinQuery(String sessionId) {
      return new IdJoinQuery(createAccessor(sessionId), getNewQueryId());
   }

   public ArtifactJoinQuery createArtifactJoinQuery() {
      return new ArtifactJoinQuery(createAccessor(), getNewQueryId(), getMaxArtifactJoinSize());
   }

   public TagQueueJoinQuery createTagQueueJoinQuery() {
      return new TagQueueJoinQuery(createAccessor(), getNewQueryId());
   }

   public ExportImportJoinQuery createExportImportJoinQuery() {
      return new ExportImportJoinQuery(createAccessor(), getNewQueryId());
   }

   public CharJoinQuery createCharJoinQuery(String sessionId) {
      return new CharJoinQuery(createAccessor(sessionId), getNewQueryId());
   }

   public CharJoinQuery createCharJoinQuery() {
      return new CharJoinQuery(createAccessor(), getNewQueryId());
   }

   private int getMaxArtifactJoinSize() {
      int toReturn = Integer.MAX_VALUE;
      String maxSize = preferences.getCachedValue("artifact.join.max.size");
      if (Strings.isNumeric(maxSize)) {
         toReturn = Integer.parseInt(maxSize);
      }
      return toReturn;
   }

}
