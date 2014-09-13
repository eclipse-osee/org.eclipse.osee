package org.eclipse.osee.ote.master;

import java.util.List;
import java.util.UUID;

public interface OTELookup {
   List<OTELookupServerEntry> getAvailableServers();

   void addServer(OTELookupServerEntry server);

   void removeServer(OTELookupServerEntry server);

   void removeServer(UUID fromString);
}
