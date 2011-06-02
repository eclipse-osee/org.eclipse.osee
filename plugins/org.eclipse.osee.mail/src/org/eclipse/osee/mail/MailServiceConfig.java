/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mail;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <pre>
 *  Gmail Example:
 *  host = "smtp.gmail.com;
 *  transport = "smpts";
 *  requireAuthentication = true;
 * 
 *  Yahoo Example:
 *  host = "smtp.mail.yahoo.com";
 *  transport = "smpts";
 *  requireAuthentication = true;
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class MailServiceConfig {

   private String userName = "";
   private String password = "";
   private String host = "";
   private int port = 25;
   private String transport = "smtp";
   private boolean debug = false;
   private boolean authenticationRequired = false;
   private String systemAdminEmailAddress = "";
   private boolean mailStatsEnabled = false;

   public MailServiceConfig() {
      super();
   }

   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public int getPort() {
      return port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public boolean isDebug() {
      return debug;
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

   public String getTransport() {
      return transport;
   }

   public void setTransport(String transport) {
      this.transport = transport;
   }

   public boolean isAuthenticationRequired() {
      return authenticationRequired;
   }

   public void setAuthenticationRequired(boolean authenticationRequired) {
      this.authenticationRequired = authenticationRequired;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getPassword() {
      return password;
   }

   public String getSystemAdminEmailAddress() {
      return systemAdminEmailAddress;
   }

   public void setSystemAdminEmailAddress(String systemAdminEmailAddress) {
      this.systemAdminEmailAddress = systemAdminEmailAddress;
   }

   public boolean isMailStatsEnabled() {
      return mailStatsEnabled;
   }

   public void setMailStatsEnabled(boolean mailStatsEnabled) {
      this.mailStatsEnabled = mailStatsEnabled;
   }

   public void setTo(MailServiceConfig other) {
      if (other != null) {
         this.setHost(other.getHost());
         this.setPassword(other.getPassword());
         this.setPort(other.getPort());
         this.setSystemAdminEmailAddress(other.getSystemAdminEmailAddress());
         this.setTransport(other.getTransport());
         this.setUserName(other.getUserName());
      }
   }

   @Override
   public String toString() {
      return "MailServiceConfig [userName=" + userName + ", password=" + password + ", host=" + host + ", port=" + port + ", transport=" + transport + ", debug=" + debug + ", authenticationRequired=" + authenticationRequired + ", systemAdminEmailAddress=" + systemAdminEmailAddress + ", mailStatsEnabled=" + mailStatsEnabled + "]";
   }
}
