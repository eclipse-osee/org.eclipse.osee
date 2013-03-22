package org.eclipse.osee.ote.core;

import java.util.Set;
import java.util.UUID;

public interface OTESessionManager {
   void add(UUID sessionId, IUserSession session);
   void remove(UUID sessionId);
   IUserSession get(UUID sessionId);
   Set<UUID> get();
   IUserSession getActiveUser();//??
   void setActiveUser(UUID sessionId);//??
}
