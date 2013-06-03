package org.eclipse.osee.ote.master.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OTEServer {

   private String uuid;
   private String uri;
   private String type;
   private String startTime;
   private String name;
   
   public void setUUID(String uuid){
      this.uuid = uuid;
   }
   
   public String getUri() {
      return uri;
   }
   public void setUri(String uri) {
      this.uri = uri;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getStartTime() {
      return startTime;
   }
   public void setStartTime(String startTime) {
      this.startTime = startTime;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getUUID() {
      return uuid;
   }
}
