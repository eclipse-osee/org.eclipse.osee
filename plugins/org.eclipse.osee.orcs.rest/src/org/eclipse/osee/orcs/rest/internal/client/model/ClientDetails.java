/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
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
