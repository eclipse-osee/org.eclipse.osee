package org.eclipse.osee.ote.core.internal;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OTESessionManager;

public class OTESessionManagerImpl implements OTESessionManager {

   private Map<UUID, IUserSession> userMap;
   private UUID activeUserId;
   
   public OTESessionManagerImpl(){
      userMap = new ConcurrentHashMap<>();
   }
   
   @Override
   public void add(UUID sessionId, IUserSession session) {
      userMap.put(sessionId, session);
   }

   @Override
   public void remove(UUID sessionId) {
      userMap.remove(sessionId);
   }

   @Override
   public IUserSession get(UUID sessionId) {
      if(sessionId == null){
         return null;
      }
      return userMap.get(sessionId);
   }

   @Override
   public Set<UUID> get() {
      return userMap.keySet();
   }

   @Override
   public IUserSession getActiveUser() {
      IUserSession session = get(activeUserId);
      if(session == null){
         if(userMap.size() > 0){
            UUID id = userMap.keySet().iterator().next();
            session = get(id);
         }
      }
      return session;
   }

   @Override
   public void setActiveUser(UUID sessionId) {
      this.activeUserId = sessionId;
   }

}
