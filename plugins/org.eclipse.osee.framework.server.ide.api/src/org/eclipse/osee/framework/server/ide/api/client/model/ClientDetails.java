/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.server.ide.api.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.IdeClientSession;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ClientDetails {

   public List<IdeClientSession> sessions = new LinkedList<>();
   public Map<String, Integer> releaseCount = new HashMap<>();
   public Map<String, Collection<String>> releaseToUserId = new HashMap<>(10);
   public Set<String> networkReleaseUserIds = new HashSet<>();

   public ClientDetails() {
   }

   public List<IdeClientSession> getSessions() {
      return sessions;
   }

   public void setSessions(List<IdeClientSession> sessions) {
      this.sessions = sessions;
   }

   public Map<String, Integer> getReleaseCount() {
      return releaseCount;
   }

   public void setReleaseCount(Map<String, Integer> releaseCount) {
      this.releaseCount = releaseCount;
   }

   public Set<String> getNetworkReleaseUserIds() {
      return networkReleaseUserIds;
   }

   public void setNetworkReleaseUserIds(Set<String> networkReleaseUserIds) {
      this.networkReleaseUserIds = networkReleaseUserIds;
   }

   public Map<String, Collection<String>> getReleaseToUserId() {
      return releaseToUserId;
   }

   public void setReleaseToUserId(Map<String, Collection<String>> releaseToUserId) {
      this.releaseToUserId = releaseToUserId;
   }

}
