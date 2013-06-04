package org.eclipse.osee.ote.master;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

public class OTELookupServerEntry {

   private final UUID uuid;
   private final OTEServerType type;
   private final Date startTime;
   private final String name;
   private String station;
   private String version;
   private String comment;
   private String owner;
   private URI oteRestServer;
   private URI oteActivemqServer;
   private String connectedUsers;

   private Date lastUpdate;

   public OTELookupServerEntry(UUID uuid, URI oteActivemqServer, String name, OTEServerType type, Date startTime) {
      this.name = name;
      this.uuid = uuid;
      this.startTime = startTime;
      this.type = type;
      this.oteActivemqServer = oteActivemqServer;
   }

   public UUID getUUID() {
      return uuid;
   }

   public String getName() {
      return name;
   }

   public Date getStartTime() {
      return startTime;
   }

   public OTEServerType getType() {
      return type;
   }

   public URI getURI() {
      return oteActivemqServer;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((oteActivemqServer == null) ? 0 : oteActivemqServer.hashCode());
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OTELookupServerEntry other = (OTELookupServerEntry) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (startTime == null) {
         if (other.startTime != null)
            return false;
      } else if (!startTime.equals(other.startTime))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.getName().equals(other.type.getName()))
         return false;
      if (oteActivemqServer == null) {
         if (other.oteActivemqServer != null)
            return false;
      } else if (!oteActivemqServer.equals(other.oteActivemqServer))
         return false;
      if (uuid == null) {
         if (other.uuid != null)
            return false;
      } else if (!uuid.equals(other.uuid))
         return false;
      return true;
   }

   synchronized public void setUpdateTime(Date date) {
      lastUpdate = date;
   }

   synchronized public Date getUpdateTime() {
      return lastUpdate;
   }
   
   public String getStation() {
      return station;
   }

   public void setStation(String station) {
      this.station = station;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public URI getOteRestServer() {
      return oteRestServer;
   }

   public void setOteRestServer(URI oteRestServer) {
      this.oteRestServer = oteRestServer;
   }

   public URI getOteActivemqServer() {
      return oteActivemqServer;
   }

   public void setOteActivemqServer(URI oteActivemqServer) {
      this.oteActivemqServer = oteActivemqServer;
   }

   public String getConnectedUsers() {
      return connectedUsers;
   }

   public void setConnectedUsers(String connectedUsers) {
      this.connectedUsers = connectedUsers;
   }

   public Date getLastUpdate() {
      return lastUpdate;
   }

   public void setLastUpdate(Date lastUpdate) {
      this.lastUpdate = lastUpdate;
   }

   public UUID getUuid() {
      return uuid;
   }
}
