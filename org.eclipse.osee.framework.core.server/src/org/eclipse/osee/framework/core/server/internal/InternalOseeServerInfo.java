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
package org.eclipse.osee.framework.core.server.internal;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
class InternalOseeServerInfo extends OseeServerInfo {
   private static final long serialVersionUID = -8623296027967886344L;
   private transient final Set<String> defaultVersions;
   private transient boolean isRegistered;
   private transient MutableBoolean updateFromStore;

   public InternalOseeServerInfo(String serverId, String serverAddress, int port, Timestamp dateStarted, boolean isAcceptingRequests) {
      super(serverId, serverAddress, port, new String[0], dateStarted, isAcceptingRequests);
      this.isRegistered = false;
      this.defaultVersions = new HashSet<String>();
      this.updateFromStore = new MutableBoolean(true);
      initializeDefaultVersions();
   }

   private void initializeDefaultVersions() {
      String[] userSpecifiedVersion = OseeServerProperties.getOseeVersion();
      if (userSpecifiedVersion != null && userSpecifiedVersion.length > 0) {
         defaultVersions.addAll(Arrays.asList(userSpecifiedVersion));
      } else {
         defaultVersions.add(OseeCodeVersion.getVersion());
      }
   }

   private void checkVersionArgument(String version) throws OseeCoreException {
      if (!Strings.isValid(version)) throw new OseeArgumentException(String.format("Osee version argument is invalid"));
   }

   private void updateVersionsFromDataStore() {
      Set<String> supportedVersions = new HashSet<String>();
      try {
         supportedVersions.addAll(ApplicationServerDataStore.getOseeVersionsByServerId(getServerId()));
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.SEVERE, ex);
      }
      if (!supportedVersions.containsAll(defaultVersions)) {
         supportedVersions.addAll(defaultVersions);
      }
      backingData.put(VERSION, supportedVersions.toArray(new String[supportedVersions.size()]));
   }

   @Override
   public String[] getVersion() {
      if (updateFromStore.getValue()) {
         updateVersionsFromDataStore();
      }
      return super.getVersion();
   }

   void addVersion(String version) throws OseeCoreException {
      synchronized (updateFromStore) {
         checkVersionArgument(version);
         Set<String> supportedVersions = new HashSet<String>(Arrays.asList(getVersion()));
         supportedVersions.add(version);
         backingData.put(VERSION, supportedVersions.toArray(new String[supportedVersions.size()]));
         updateFromStore.setValue(false);
         writeToDataStore();
         updateFromStore.setValue(true);
      }
   }

   void removeVersion(String version) throws OseeCoreException {
      synchronized (updateFromStore) {
         checkVersionArgument(version);
         if (defaultVersions.contains(version)) throw new OseeArgumentException(String.format(
               "Unable to remove default Osee version [%s]", version));
         Set<String> supportedVersions = new HashSet<String>(Arrays.asList(getVersion()));
         if (supportedVersions.contains(version)) {
            isRegistered = false;
            updateFromStore.setValue(false);
            ApplicationServerDataStore.deregisterWithDb(this);
            supportedVersions.remove(version);
            backingData.put(VERSION, supportedVersions.toArray(new String[supportedVersions.size()]));
            isRegistered = ApplicationServerDataStore.registerWithDb(this);
            updateFromStore.setValue(true);
         } else {
            throw new OseeStateException(String.format("Not part of the supported version [%s]", version));
         }
      }
   }

   private void writeToDataStore() {
      isRegistered = false;
      ApplicationServerDataStore.deregisterWithDb(this);
      isRegistered = ApplicationServerDataStore.registerWithDb(this);
   }

   boolean updateRegistration() {
      synchronized (updateFromStore) {
         updateVersionsFromDataStore();
         updateFromStore.setValue(false);
         writeToDataStore();
         updateFromStore.setValue(true);
      }
      return isRegistered;
   }
}
