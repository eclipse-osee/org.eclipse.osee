package org.eclipse.osee.ote.master.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OTEServer {

   private String uuid;
   private String type;
   private String startTime;
   private String name;
   private String station;
   private String version;
   private String comment;
   private String owner;
   private String oteRestServer;
   private String oteActivemqServer;
   private String connectedUsers;
   
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

   public String getOteRestServer() {
      return oteRestServer;
   }

   public void setOteRestServer(String oteRestServer) {
      this.oteRestServer = oteRestServer;
   }

   public String getOteActivemqServer() {
      return oteActivemqServer;
   }

   public void setOteActivemqServer(String oteActivemqServer) {
      this.oteActivemqServer = oteActivemqServer;
   }

   public String getConnectedUsers() {
      return connectedUsers;
   }

   public void setConnectedUsers(String connectedUsers) {
      this.connectedUsers = connectedUsers;
   }

   public void setUUID(String uuid){
      this.uuid = uuid;
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
