package org.eclipse.osee.ote.core.internal;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OTESessionManager;

public class OTESessionManagerImpl implements OTESessionManager {

   private Map<UUID, IUserSession> userMap;
   
   public OTESessionManagerImpl(){
      userMap = new ConcurrentHashMap<UUID, IUserSession>();
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
      return userMap.get(sessionId);
   }

   @Override
   public Set<UUID> get() {
      return userMap.keySet();
   }

   @Override
   public IUserSession getActiveUser() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setActiveUser(UUID sessionId) {
      // TODO Auto-generated method stub

   }

}
