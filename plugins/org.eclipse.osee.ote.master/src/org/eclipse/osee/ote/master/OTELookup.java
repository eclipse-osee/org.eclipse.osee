package org.eclipse.osee.ote.master;

import java.util.List;

public interface OTELookup {
   List<OTELookupServerEntry> getAvailableServers();

   void addServer(OTELookupServerEntry server);

   void removeServer(OTELookupServerEntry server);
}
