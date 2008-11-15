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

package org.eclipse.osee.framework.ui.service.control.wizards.launcher.data;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class ServiceItem implements Comparable<ServiceItem> {
   public static final String EXEC_SEPARATOR = "@";
   public static final String JINI_GROUP_FIELD = "$JINI_GROUP";
   private ArrayList<String> hosts;
   private String standAloneExecution;
   private String eclipseAppExecution;
   private String remoteExecution;
   private boolean isEclipseAppAllowed;
   private boolean isStandAloneAllowed;
   private boolean isRemoteAllowed;
   private boolean isLocalAllowed;
   private String serviceName;
   private String zipFileName;
   private String unzipLocation;
   private String pluginId;
   private String jiniGroup;
   private boolean isJiniGroupRequired;

   public ServiceItem(String serviceName) {
      this.serviceName = serviceName;
      this.isStandAloneAllowed = false;
      this.isEclipseAppAllowed = false;
      this.isRemoteAllowed = false;
      this.isLocalAllowed = false;
      this.standAloneExecution = "";
      this.eclipseAppExecution = "";
      this.remoteExecution = "";
      this.zipFileName = "";
      this.unzipLocation = "";
      this.pluginId = "";
      this.isJiniGroupRequired = false;
      this.jiniGroup = "";
      this.hosts = new ArrayList<String>();
   }

   public String getLocalExecution() {
      return updateForJiniGroup(eclipseAppExecution);
   }

   public String getStandAloneExecution() {
      return updateForJiniGroup(standAloneExecution);
   }

   public String getRemoteExecution() {
      return updateForJiniGroup(remoteExecution);
   }

   private String updateForJiniGroup(String source) {
      String jiniVmArg = "";
      if (false != isJiniGroupRequired()) {
         jiniVmArg =
               String.format("-D%s=\"%s\"", OseeProperties.getOseeJiniServiceGroups(),
                     Strings.isValid(getJiniGroup()) ? getJiniGroup() : JINI_GROUP_FIELD);
      }
      return source.replace(JINI_GROUP_FIELD, jiniVmArg);
   }

   public boolean isRemoteAllowed() {
      return isRemoteAllowed;
   }

   public boolean isStandAloneAllowed() {
      return isStandAloneAllowed;
   }

   public boolean isEclipseAppAllowed() {
      return isEclipseAppAllowed;
   }

   public boolean isLocalAllowed() {
      return isLocalAllowed;
   }

   public ArrayList<String> getHosts() {
      return hosts;
   }

   public String toString() {
      String hostList = "\n";
      for (String temp : hosts) {
         hostList += " Host: " + temp + "\n";
      }

      String localExecution = "\n Local Allowed: " + isLocalAllowed + "\n";
      if (isLocalAllowed()) {
         localExecution += "\t" + "EclipseAppAllowed: " + isEclipseAppAllowed() + "\n";
         if (isEclipseAppAllowed()) {
            localExecution += "\t" + "EclipseAppExecute: " + this.getLocalExecution() + "\n";
         }

         localExecution += "\t" + "StandAloneExecution: " + isStandAloneAllowed() + "\n";
         if (isStandAloneAllowed()) {
            localExecution += "\t" + "StandAloneExecution: " + this.getStandAloneExecution() + "\n";
         }
      }

      String remoteExecution = " Remote Allowed: " + isRemoteAllowed() + "\n";
      if (isRemoteAllowed()) {
         remoteExecution += "\t" + "RemoteExecution: " + this.getRemoteExecution() + "\n";
      }

      return String.format(
            "[ Class: %s\n Name: %s %s %s Destination: %s\n Zip Name: %s\n Plugin Directory: %s %s" + "\n JiniGroup: %s]\n",
            this.getClass().getSimpleName(), getName(), localExecution, remoteExecution, getUnzipLocation(),
            getZipName(), getPlugin(), hostList, isJiniGroupRequired() ? jiniGroup : "NOT REQUIRED");
   }

   public int compareTo(ServiceItem other) {
      if (other != null) {
         String name1 = this.getName();
         String name2 = other.getName();
         name1 = (name1 != null ? name1 : "");
         name2 = (name2 != null ? name2 : "");
         return name1.compareTo(name2);
      }
      return 0;
   }

   public String getName() {
      return serviceName;
   }

   public String getZipName() {
      return zipFileName;
   }

   public String getPlugin() {
      return pluginId;
   }

   public String getUnzipLocation() {
      return unzipLocation;
   }

   protected void setUnzipLocation(String unzipLocation) {
      this.unzipLocation = unzipLocation;
   }

   protected void setPlugin(String pluginId) {
      this.pluginId = pluginId;
   }

   protected void setZipName(String zipFileName) {
      this.zipFileName = zipFileName;
   }

   protected void setLocalAllowed(boolean isLocalAllowed) {
      this.isLocalAllowed = isLocalAllowed;
   }

   protected void setStandAloneExecution(String value) {
      this.standAloneExecution = value;
      this.isStandAloneAllowed = true;
   }

   protected void setEclipseAppExecution(String value) {
      this.eclipseAppExecution = value;
      this.isEclipseAppAllowed = true;
   }

   protected void setRemoteExecution(String value) {
      this.remoteExecution = value;
      this.isRemoteAllowed = true;
   }

   public void setJiniGroupRequired(boolean booleanValue) {
      this.isJiniGroupRequired = booleanValue;
   }

   public boolean isJiniGroupRequired() {
      return isJiniGroupRequired;
   }

   public void setJiniGroup(String jiniGroup) {
      this.jiniGroup = jiniGroup;
   }

   public String getJiniGroup() {
      return jiniGroup;
   }
}
