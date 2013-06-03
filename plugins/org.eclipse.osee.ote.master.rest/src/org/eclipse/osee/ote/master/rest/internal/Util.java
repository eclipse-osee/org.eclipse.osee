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
      server.setName(entry.getName());
      server.setStartTime(entry.getStartTime().toString());
      server.setType(entry.getType().getName());
      server.setUri(entry.getURI().toString());
      return server;
   }

   public static OTELookupServerEntry convert(OTEServer server) throws ParseException, URISyntaxException {
      UUID uuid = UUID.fromString(server.getUUID());
      URI uri = new URI(server.getUri());
      DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
      Date date = df.parse(server.getStartTime());
      
      OTELookupServerEntry entry = new OTELookupServerEntry(uuid, uri, server.getName(), new OTEServerTypeGeneric(server.getType()) ,date);
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
