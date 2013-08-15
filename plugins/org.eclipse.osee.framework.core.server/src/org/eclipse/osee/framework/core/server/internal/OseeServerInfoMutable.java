/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeServerInfo;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerInfoMutable extends OseeServerInfo {

   private static final long serialVersionUID = 4437012224149055646L;

   public OseeServerInfoMutable(String serverId, String uri, String[] version, Timestamp dateStarted, boolean isAcceptingRequests) {
      super(serverId, uri, version, dateStarted, isAcceptingRequests);
   }

   public void setAcceptingRequests(boolean value) {
      backingData.put(IS_ACCEPTING_REQUESTS, value);
   }

   public void setVersions(Set<String> versions) {
      backingData.put(VERSION, versions.toArray(new String[versions.size()]));
   }

   public void addVersion(String version) {
      Set<String> versions = getVersionSet();
      versions.add(version);
      setVersions(versions);
   }

   public Set<String> getVersionSet() {
      Set<String> versions = new HashSet<String>();
      for (String current : getVersion()) {
         versions.add(current);
      }
      return versions;
   }

}
