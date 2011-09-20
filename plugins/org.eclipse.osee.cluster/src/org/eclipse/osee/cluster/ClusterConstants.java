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
package org.eclipse.osee.cluster;

/**
 * @author Roberto E. Escobar
 */
public class ClusterConstants {

   public static final String CLUSTER_CONFIG_URL = "cluster.config.url";

   public static final String CLUSTER_TOPIC_PREFIX = "org/eclipse/osee/cluster/admin/event/";
   public static final String CLUSTER_REGISTRATION_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_MEMBER_REGISTRATION";
   public static final String CLUSTER_DEREGISTRATION_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_MEMBER_DEREGISTRATION";

   public static final String CLUSTER_LIFECYCLE_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_LIFECYCLE_STATE_CHANGED";
   public static final String CLUSTER_LIFECYCLE_STATE = "cluster.state";

   public static enum ClusterInstanceState {
      STARTING,
      STARTED,
      RESTARTING,
      RESTARTED,
      PAUSING,
      PAUSED,
      RESUMING,
      RESUMED,
      SHUTTING_DOWN,
      SHUTDOWN,
      CLIENT_CONNECTION_LOST,
      CLIENT_CONNECTION_OPENING,
      CLIENT_CONNECTION_OPENED,
      UNKNOWN;
   }

   public static final String CLUSTER_INSTANCE_CREATED_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_INSTANCE_CREATED";
   public static final String CLUSTER_INSTANCE_DESTROYED_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_INSTANCE_DESTROYED";
   public static final String CLUSTER_OBJECT_TYPE = "cluster.object.type";
   public static final String CLUSTER_OBJECT_ID = "cluster.object.id";
   public static final String CLUSTER_OBJECT = "cluster.object";

   public static final String CLUSTER_MEMBER_ADDED_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_MEMBER_ADDED";
   public static final String CLUSTER_MEMBER_REMOVED_EVENT = CLUSTER_TOPIC_PREFIX + "CLUSTER_MEMBER_REMOVED";
   public static final String CLUSTER_MEMBER = "cluster.member";
}
