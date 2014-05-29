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
package org.eclipse.osee.cluster.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.cluster.ClusterService;
import org.eclipse.osee.cluster.Member;

/**
 * A service for clients to obtain information about the cluster.
 * 
 * @author Roberto E. Escobar
 */
@ApplicationPath("cluster")
public class ClusterRestApplication extends Application {

   private static ClusterService clusterService;

   public void setClusterService(ClusterService clusterService) {
      ClusterRestApplication.clusterService = clusterService;
   }

   public static ClusterService getClusterService() {
      return clusterService;
   }

   public static Set<Member> getMembers() {
      ClusterService service = ClusterRestApplication.getClusterService();
      return service.getCluster().getMembers();
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> resources = new HashSet<Class<?>>();
      resources.add(MembersResource.class);
      resources.add(JobsResource.class);
      return resources;
   }
}
