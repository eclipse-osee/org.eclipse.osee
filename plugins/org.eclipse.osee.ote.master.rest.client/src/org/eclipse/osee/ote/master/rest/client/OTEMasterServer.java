package org.eclipse.osee.ote.master.rest.client;

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.master.rest.model.OTEServer;

public interface OTEMasterServer {
   Future<OTEMasterServerAvailableNodes> getAvailableServers(URI uri);
   Future<OTEMasterServerResult> addServer(URI uri, OTEServer server);
   Future<OTEMasterServerResult> removeServer(URI uri, OTEServer server);
}
