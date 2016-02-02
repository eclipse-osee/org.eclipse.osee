/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

/**
 * @author Donald G. Dunne
 */
public class AccessTopicEventType extends AbstractTopicEventType {

   public static final AccessTopicEventType ACCESS_ARTIFACT_MODIFIED =
      new AccessTopicEventType(EventType.LocalAndRemote, "framework/access/artifact/modified");
   public static final AccessTopicEventType ACCESS_ARTIFACT_LOCK_MODIFIED =
      new AccessTopicEventType(EventType.LocalAndRemote, "framework/access/artifact/lock/modified");
   public static final AccessTopicEventType ACCESS_BRANCH_MODIFIED =
      new AccessTopicEventType(EventType.LocalAndRemote, "framework/access/branch/modified");

   private AccessTopicEventType(EventType eventType, String topic) {
      super(eventType, topic);
   }

}
