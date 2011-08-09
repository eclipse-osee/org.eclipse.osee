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
package org.eclipse.osee.cluster.admin.hazelcast.internal;

import java.util.Map;
import org.eclipse.osee.cluster.admin.ClusterAdminConstants;
import org.eclipse.osee.cluster.admin.ClusterAdminConstants.ClusterInstanceState;
import org.eclipse.osee.cluster.admin.ClusterServiceUtils;
import org.eclipse.osee.cluster.admin.Member;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import com.hazelcast.core.Instance;
import com.hazelcast.core.Instance.InstanceType;
import com.hazelcast.core.InstanceEvent;
import com.hazelcast.core.InstanceListener;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * @author Roberto E. Escobar
 */
public class ClusterEventNotifier implements LifecycleListener, InstanceListener, MembershipListener {

   private final String componentName;
   private final String contextName;
   private final EventAdmin eventAdmin;

   public ClusterEventNotifier(String componentName, String contextName, EventAdmin eventAdmin) {
      this.componentName = componentName;
      this.contextName = contextName;
      this.eventAdmin = eventAdmin;
   }

   @Override
   public void memberAdded(MembershipEvent event) {
      Map<String, Object> data = toMap(event);
      postEvent(ClusterAdminConstants.CLUSTER_MEMBER_ADDED_EVENT, data);
   }

   @Override
   public void memberRemoved(MembershipEvent event) {
      Map<String, Object> data = toMap(event);
      postEvent(ClusterAdminConstants.CLUSTER_MEMBER_REMOVED_EVENT, data);
   }

   @Override
   public void stateChanged(LifecycleEvent event) {
      Map<String, Object> data = ClusterServiceUtils.toMap(componentName, contextName);
      ClusterInstanceState state = mapState(event.getState());
      data.put(ClusterAdminConstants.CLUSTER_LIFECYCLE_STATE, state);
      postEvent(ClusterAdminConstants.CLUSTER_LIFECYCLE_EVENT, data);
   }

   @Override
   public void instanceCreated(InstanceEvent event) {
      Map<String, Object> data = toMap(event);
      postEvent(ClusterAdminConstants.CLUSTER_INSTANCE_CREATED_EVENT, data);
   }

   @Override
   public void instanceDestroyed(InstanceEvent event) {
      Map<String, Object> data = toMap(event);
      postEvent(ClusterAdminConstants.CLUSTER_INSTANCE_DESTROYED_EVENT, data);
   }

   public void notifyRegistration() {
      Map<String, Object> data = ClusterServiceUtils.toMap(componentName, contextName);
      postEvent(ClusterAdminConstants.CLUSTER_REGISTRATION_EVENT, data);
   }

   public void notifyDeRegistration() {
      Map<String, Object> data = ClusterServiceUtils.toMap(componentName, contextName);
      postEvent(ClusterAdminConstants.CLUSTER_DEREGISTRATION_EVENT, data);
   }

   private void postEvent(String topic, Map<String, Object> data) {
      eventAdmin.postEvent(new Event(topic, data));
   }

   private Map<String, Object> toMap(MembershipEvent membershipEvent) {
      Map<String, Object> data = ClusterServiceUtils.toMap(componentName, contextName);
      Member member = new MemberProxy(membershipEvent.getMember());
      data.put(ClusterAdminConstants.CLUSTER_MEMBER, member);
      return data;
   }

   private Map<String, Object> toMap(InstanceEvent event) {
      Map<String, Object> data = ClusterServiceUtils.toMap(componentName, contextName);
      InstanceType instanceType = event.getInstanceType();
      Instance instance = event.getInstance();
      Object objectId = instance.getId();

      data.put(ClusterAdminConstants.CLUSTER_OBJECT_TYPE, instanceType.name());
      data.put(ClusterAdminConstants.CLUSTER_OBJECT_ID, objectId);
      data.put(ClusterAdminConstants.CLUSTER_OBJECT, instance);
      return data;
   }

   private ClusterInstanceState mapState(LifecycleState state) {
      ClusterInstanceState toReturn;
      switch (state) {
         case STARTING:
            toReturn = ClusterInstanceState.STARTING;
            break;
         case STARTED:
            toReturn = ClusterInstanceState.STARTED;
            break;
         case RESTARTING:
            toReturn = ClusterInstanceState.RESTARTING;
            break;
         case RESTARTED:
            toReturn = ClusterInstanceState.RESTARTED;
            break;
         case PAUSING:
            toReturn = ClusterInstanceState.PAUSING;
            break;
         case PAUSED:
            toReturn = ClusterInstanceState.PAUSED;
            break;
         case RESUMING:
            toReturn = ClusterInstanceState.RESUMING;
            break;
         case RESUMED:
            toReturn = ClusterInstanceState.RESUMED;
            break;
         case SHUTTING_DOWN:
            toReturn = ClusterInstanceState.SHUTTING_DOWN;
            break;
         case SHUTDOWN:
            toReturn = ClusterInstanceState.SHUTDOWN;
            break;
         case CLIENT_CONNECTION_LOST:
            toReturn = ClusterInstanceState.CLIENT_CONNECTION_LOST;
            break;
         case CLIENT_CONNECTION_OPENING:
            toReturn = ClusterInstanceState.CLIENT_CONNECTION_OPENING;
            break;
         case CLIENT_CONNECTION_OPENED:
            toReturn = ClusterInstanceState.CLIENT_CONNECTION_OPENED;
            break;
         default:
            toReturn = ClusterInstanceState.UNKNOWN;
            break;
      }
      return toReturn;
   }

}
