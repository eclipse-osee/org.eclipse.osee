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
package org.eclipse.osee.cluster.admin.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.osee.cluster.admin.ClusterAdmin;
import org.eclipse.osee.cluster.admin.Member;

/**
 * A service for clients to obtain information about the cluster.
 * 
 * @author Roberto E. Escobar
 */
public class ClusterAdminApplication extends Application {

   private static ClusterAdmin clusterAdmin;

   public void setClusterAdmin(ClusterAdmin clusterAdmin) {
      ClusterAdminApplication.clusterAdmin = clusterAdmin;
   }

   public static ClusterAdmin getClusterAdmin() {
      return clusterAdmin;
   }

   public static Set<Member> getMembers() {
      ClusterAdmin admin = ClusterAdminApplication.getClusterAdmin();
      return admin.getCluster().getMembers();
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> resources = new HashSet<Class<?>>();
      resources.add(MembersResource.class);
      resources.add(JobsResource.class);
      return resources;
   }
}
