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
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
class InternalOseeServerInfo extends OseeServerInfo {
   private static final long serialVersionUID = -8623296027967886344L;
   private transient final Set<String> supportedVersions;
   private transient final Set<String> defaultVersions;
   private transient boolean isRegistered;

   public InternalOseeServerInfo(String serverId, String serverAddress, int port, Timestamp dateStarted, boolean isAcceptingRequests) {
      super(serverId, serverAddress, port, new String[0], dateStarted, isAcceptingRequests);
      this.isRegistered = false;
      this.defaultVersions = new HashSet<String>();
      String[] userSpecifiedVersion = OseeServerProperties.getOseeVersion();
      if (userSpecifiedVersion != null && userSpecifiedVersion.length > 0) {
         defaultVersions.addAll(Arrays.asList(userSpecifiedVersion));
      } else {
         defaultVersions.add(OseeCodeVersion.getVersion());
      }

      this.supportedVersions = new HashSet<String>();
      supportedVersions.addAll(defaultVersions);
   }

   private void checkVersionArgument(String version) throws OseeCoreException {
      if (!Strings.isValid(version)) throw new OseeArgumentException(String.format("Osee version argument is invalid"));
   }

   void addVersion(String version) throws OseeCoreException {
      checkVersionArgument(version);
      if (supportedVersions.add(version)) {
         updateRegistration();
      }
   }

   void removeVersion(String version) throws OseeCoreException {
      checkVersionArgument(version);

      if (defaultVersions.contains(version)) throw new OseeArgumentException(String.format(
            "Unable to remove default Osee version [%s]", version));

      if (supportedVersions.contains(version)) {
         isRegistered = false;
         ApplicationServerDataStore.deregisterWithDb(this);
         supportedVersions.remove(version);
         backingData.put(VERSION, supportedVersions.toArray(new String[supportedVersions.size()]));
         isRegistered = ApplicationServerDataStore.registerWithDb(this);
      } else {
         throw new OseeStateException(String.format("Not part of the supported version [%s]", version));
      }
   }

   boolean updateRegistration() {
      isRegistered = false;
      backingData.put(VERSION, supportedVersions.toArray(new String[supportedVersions.size()]));
      ApplicationServerDataStore.deregisterWithDb(this);
      isRegistered = ApplicationServerDataStore.registerWithDb(this);
      return isRegistered;
   }
}
