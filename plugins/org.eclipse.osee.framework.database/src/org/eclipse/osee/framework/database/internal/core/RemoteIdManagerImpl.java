/*
 * Created on Aug 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.database.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.RemoteIdManager;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class RemoteIdManagerImpl implements RemoteIdManager {

   private static final String SELECT_ALL = "select * from osee_type_id_map";
   private static final String INSERT_SQL = "insert into osee_type_id_map (remote_id, local_id) values (?,?)";

   private final Map<Long, Integer> remoteIdToLocalId = new ConcurrentHashMap<Long, Integer>();
   private final Map<Integer, Long> localIdToRemoteId = new ConcurrentHashMap<Integer, Long>();
   private final Set<Long> persistedIds = new ConcurrentSkipListSet<Long>();

   private final IOseeDatabaseService service;
   private volatile boolean ensurePopulatedRanOnce;

   public RemoteIdManagerImpl(IOseeDatabaseService service) {
      super();
      this.service = service;
   }

   @Override
   public Integer getLocalId(Long remoteId) throws OseeCoreException {
      ensurePopulate();
      Conditions.checkNotNull(remoteId, "remoteId");
      Integer localId = remoteIdToLocalId.get(remoteId);
      if (localId == null) {
         reloadCache();
         localId = remoteIdToLocalId.get(remoteId);
         if (localId == null) {
            localId = service.getSequence().getNextLocalTypeId();
            cache(remoteId, localId);
         }
      }
      return localId;
   }

   @Override
   public Long getRemoteId(Integer localId) throws OseeCoreException {
      ensurePopulate();
      Conditions.checkNotNull(localId, "localId");
      Long remoteId = localIdToRemoteId.get(localId);
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
         clear();
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
   public void clear() {
      remoteIdToLocalId.clear();
      localIdToRemoteId.clear();
      persistedIds.clear();
   }

   private void cache(Long remoteId, Integer localId) {
      remoteIdToLocalId.put(remoteId, localId);
      localIdToRemoteId.put(localId, remoteId);
   }

   private synchronized void ensurePopulate() throws OseeCoreException {
      if (!ensurePopulatedRanOnce) {
         ensurePopulatedRanOnce = true;
         reloadCache();
      }
   }

   @Override
   public void store(Collection<Long> remoteIds) throws OseeCoreException {
      ensurePopulate();
      List<Object[]> data = new ArrayList<Object[]>();
      List<Long> toPersist = Collections.setComplement(remoteIds, persistedIds);
      for (Long remoteId : toPersist) {
         Integer localId = getLocalId(remoteId);
         data.add(new Object[] {remoteId, localId});
      }
      service.runBatchUpdate(INSERT_SQL, data);
      persistedIds.addAll(toPersist);
   }

   @Override
   public int getLocalId(Identity<Long> identity) throws OseeCoreException {
      return getLocalId(identity.getGuid());
   }

}
