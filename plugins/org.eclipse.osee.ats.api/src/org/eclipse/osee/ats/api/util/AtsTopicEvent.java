/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.framework.core.event.AbstractTopicEvent;
import org.eclipse.osee.framework.core.event.EventType;

/**
 * @author Donald G. Dunne
 */
public class AtsTopicEvent extends AbstractTopicEvent {

   public static final AtsTopicEvent WORK_ITEM_MODIFIED =
      new AtsTopicEvent(EventType.LocalAndRemote, "ats/workitem/modified");
   public static final String WORK_ITEM_UUDS_KEY = "workItemUuids";

   private AtsTopicEvent(EventType eventType, String topic) {
      super(eventType, topic);
   }

}
