/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class IdentityServiceImpl implements IdentityService {

   private static final String SELECT_ALL = "select * from osee_type_id_map";
   private static final String INSERT_SQL = "insert into osee_type_id_map (remote_id, local_id) values (?,?)";

   private final Map<Long, Integer> universalIdToLocalId = new ConcurrentHashMap<Long, Integer>();
   private final Map<Integer, Long> localIdToUniversalId = new ConcurrentHashMap<Integer, Long>();
   private final Set<Long> persistedIds = new ConcurrentSkipListSet<Long>();

   private IOseeDatabaseService service;
   private volatile boolean ensurePopulatedRanOnce;

   public void start() {
      //nothing to do 
   }

   public void stop() {
      //nothing to do 
   }

   public void setOseeDatabaseService(IOseeDatabaseService service) {
      this.service = service;
   }

   @Override
   public Integer getLocalId(Long universalId) throws OseeCoreException {
      ensurePopulate();
      Conditions.checkNotNull(universalId, "universalId");
      Integer localId = universalIdToLocalId.get(universalId);
      if (localId == null) {
         reloadCache();
         localId = universalIdToLocalId.get(universalId);
         if (localId == null) {
            localId = service.getSequence().getNextLocalTypeId();
            cache(universalId, localId);
         }
      }
      return localId;
   }

   @Override
   public Long getUniversalId(Integer localId) throws OseeCoreException {
      ensurePopulate();
      Conditions.checkNotNull(localId, "localId");
      Long remoteId = localIdToUniversalId.get(localId);
      if (remoteId == null) {
         throw new OseeCoreException("Remote id for local id [%s] was not found", remoteId);
      }
      return remoteId;

   }

   private synchronized void reloadCache() throws OseeCoreException {
      IOseeStatement chStmt = null;
      try {
         chStmt = service.getStatement();
         chStmt.runPreparedQuery(1000, SELECT_ALL);
         while (chStmt.next()) {
            Long remoteId = chStmt.getLong("remote_id");
            Integer localId = chStmt.getInt("local_id");
            cache(remoteId, localId);
            persistedIds.add(remoteId);
         }
      } finally {
         Lib.close(chStmt);
      }
   }

   @Override
   public synchronized void clear() {
      universalIdToLocalId.clear();
      localIdToUniversalId.clear();
      persistedIds.clear();
      ensurePopulatedRanOnce = false;
   }

   private void cache(Long remoteId, Integer localId) {
      universalIdToLocalId.put(remoteId, localId);
      localIdToUniversalId.put(localId, remoteId);
   }

   private synchronized void ensurePopulate() throws OseeCoreException {
      if (!ensurePopulatedRanOnce) {
         clear();
         ensurePopulatedRanOnce = true;
         reloadCache();
      }
   }

   @Override
   public void store(Collection<Long> universalIds) throws OseeCoreException {
      ensurePopulate();
      List<Object[]> data = new ArrayList<Object[]>();
      List<Long> toPersist = Collections.setComplement(universalIds, persistedIds);
      for (Long remoteId : toPersist) {
         Integer localId = getLocalId(remoteId);
         data.add(new Object[] {remoteId, localId});
      }
      if (!data.isEmpty() && !toPersist.isEmpty()) {
         service.runBatchUpdate(INSERT_SQL, data);
         persistedIds.addAll(toPersist);
      }
   }

   @Override
   public int getLocalId(Identity<Long> identity) throws OseeCoreException {
      return getLocalId(identity.getGuid());
   }

}
