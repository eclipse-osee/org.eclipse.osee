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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import java.util.List;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceItem;

public class ServiceLaunchingInformation {

   private List<String> availableHosts;

   private SecureRemoteAccess sshConnection;

   private String selectedHost;
   private String user;
   private String[] execCmds;
   private boolean upload;
   private boolean isLocal;
   private String unzipLocation;

   private ServiceItem selectedServiceItem;

   private ServiceLaunchDataPersist serviceLaunchData;

   public ServiceLaunchingInformation() {
      serviceLaunchData = ServiceLaunchDataPersist.getInstance();
      availableHosts = serviceLaunchData.getHosts();
      unzipLocation = "";
   }

   public List<String> getAvailableHosts() {
      return availableHosts;
   }

   public void setAvailableHosts(List<String> availableHosts) {
      this.availableHosts = availableHosts;
   }

   public String[] getExecCmds() {
      return execCmds;
   }

   public void setExecCmds(String[] execCmds) {
      this.execCmds = execCmds;
   }

   public String getSelectedHost() {
      return selectedHost;
   }

   public String getUser() {
      return user;
   }

   public void setUser(String user) {
      this.user = user;
   }

   public boolean isUpload() {
      return upload;
   }

   public void setUpload(boolean upload) {
      this.upload = upload;
   }

   public boolean canFinish() {
      return (selectedHost != null && user != null && execCmds != null || isLocal());
   }

   public ServiceItem getServiceItem() {
      return selectedServiceItem;
   }

   public void setServiceItem(ServiceItem serviceItem) {
      this.selectedServiceItem = serviceItem;
   }

   public void setSelectedHost(String selectedHost) {
      this.selectedHost = selectedHost;
   }

   public void connectToRemoteHost() throws Exception {
      sshConnection = SecureRemoteAccess.getRemoteAccessAuthenticateWithPassword(selectedHost, user);
   }

   public SecureRemoteAccess getSSHConnection() {
      return sshConnection;
   }

   public void setIsLocal(boolean isLocal) {
      this.isLocal = isLocal;
   }

   public boolean isLocal() {
      return isLocal;
   }

   public String getUnzipLocation() {
      return unzipLocation;
   }

   public void setUnzipLocation(String unzipLocation) {
      this.unzipLocation = unzipLocation;
   }

}
