package org.eclipse.osee.ote.master.rest.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.eclipse.osee.ote.master.OTELookupServerEntry;
import org.eclipse.osee.ote.master.OTEServerType;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

class Util {

   
   static OTEServer convert(OTELookupServerEntry entry){
      OTEServer server = new OTEServer();
      server.setComment(entry.getComment());
      server.setConnectedUsers(entry.getConnectedUsers());
      server.setName(entry.getName());
      server.setOteActivemqServer(entry.getOteActivemqServer().toString());
      server.setOteRestServer(entry.getOteRestServer().toString());
      server.setOwner(entry.getOwner());
      server.setStartTime(entry.getStartTime().toString());
      server.setType(entry.getType().getName());
      server.setStation(entry.getStation());
      server.setUUID(entry.getUUID().toString());
      server.setVersion(entry.getVersion());
      return server;
   }

   public static OTELookupServerEntry convert(OTEServer server) throws ParseException, URISyntaxException {
      UUID uuid = UUID.fromString(server.getUUID());
      URI uri = new URI(server.getOteActivemqServer());
      DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
      Date date = df.parse(server.getStartTime());
      
      OTELookupServerEntry entry = new OTELookupServerEntry(uuid, uri, server.getName(), new OTEServerTypeGeneric(server.getType()) ,date);
      entry.setComment(server.getComment());
      entry.setConnectedUsers(server.getConnectedUsers());
      entry.setOteActivemqServer(new URI(server.getOteActivemqServer()));
      entry.setOteRestServer(new URI(server.getOteRestServer()));
      entry.setOwner(server.getOwner());
      entry.setStation(server.getStation());
      entry.setVersion(server.getVersion());
      return entry;
   }
   
   private static class OTEServerTypeGeneric implements OTEServerType{
      private final String type;
      
      public OTEServerTypeGeneric(String type) {
         this.type = type;
      }
      
      @Override
      public String getName(){
         return this.type;
      }
   }
}
