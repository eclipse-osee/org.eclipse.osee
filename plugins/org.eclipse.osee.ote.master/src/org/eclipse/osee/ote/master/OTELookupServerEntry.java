package org.eclipse.osee.ote.master;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

public class OTELookupServerEntry {

   private final UUID uuid;
   private final String name;
   private final Date startTime;
   private final OTEServerType type;
   private final URI uri;

   private Date lastUpdate;

   public OTELookupServerEntry(UUID uuid, URI uri, String name, OTEServerType type, Date startTime) {
      this.name = name;
      this.uuid = uuid;
      this.startTime = startTime;
      this.type = type;
      this.uri = uri;
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
      return uri;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
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
}
