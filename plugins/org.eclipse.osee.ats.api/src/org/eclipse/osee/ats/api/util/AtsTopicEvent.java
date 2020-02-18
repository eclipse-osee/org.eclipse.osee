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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.event.AbstractTopicEvent;
import org.eclipse.osee.framework.core.event.EventType;

/**
 * @author Donald G. Dunne
 */
public class AtsTopicEvent extends AbstractTopicEvent {

   public static Map<String, AtsTopicEvent> idToEvent = new HashMap<String, AtsTopicEvent>();

   public static final AtsTopicEvent WORK_ITEM_MODIFIED =
      new AtsTopicEvent(EventType.LocalAndRemote, "ats/workitem/modified");

   public static final AtsTopicEvent WORK_ITEM_TRANSITIONED =
      new AtsTopicEvent(EventType.LocalAndRemote, "ats/workitem/transitioned");
   public static final AtsTopicEvent WORK_ITEM_TRANSITION_FAILED =
      new AtsTopicEvent(EventType.LocalOnly, "ats/workitem/transition/failed");

   // semi-colon delimited long ids
   public static final String WORK_ITEM_IDS_KEY = "workItemIds";
   public static final String WORK_ITEM_ATTR_TYPE_IDS_KEY = "workItemAttrTypeIds";

   public static final String TARGETED_VERSION_MODIFIED = "ats/workitem/targetedversion/modified";
   public static final String NEW_ATS_VERSION_ID = "atsVersionId";
   public static final String PREVIOUS_ATS_VERSION_ID = "previousAtsVersionId";

   public static final String SAVED_SEARCHES_MODIFIED = "saveSearchesModified";

   private AtsTopicEvent(EventType eventType, String topic) {
      super(eventType, topic);
      idToEvent.put(topic, this);
   }

   public static AtsTopicEvent get(String topic) {
      return idToEvent.get(topic);
   }

}
