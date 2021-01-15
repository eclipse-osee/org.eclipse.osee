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

package org.eclipse.osee.ats.api.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.event.AbstractTopicEvent;
import org.eclipse.osee.framework.core.event.EventType;

/**
 * Topic Events are the new mechanism for communicating both local and remote clients. This replaces the old
 * ArtifactEvent from a transaction persist. Listeners should handle the topic event only, if possible. Or, realize that
 * both events can come through. remotely. Eventually, everything will be done through these topic events.<br/>
 * <br/>
 * Example: IDE A initiates transition > Server transitions and persists > IDE A reloads workitems and kicks
 * WORK_ITEM_MODIFIED remotely and WORK_ITEM_RELOADED locally. <br/>
 * <br/>
 * IDE A listeners either react to ArtifactEvent from the reload (deprecated) or listens for WORK_ITEM_RELOADED to
 * refresh.<br/>
 * <br/>
 * IDE B,C,D listeners listens for WORK_ITEM_MODIFIED (one handler to reload), reloads and kicks WORK_ITEM_RELOADED.
 * ArtifactEvent goes out from reload along with WORK_ITEM_RELOADED like IDE A.
 *
 * @author Donald G. Dunne
 */
public class AtsTopicEvent extends AbstractTopicEvent {

   public static Map<String, AtsTopicEvent> idToEvent = new HashMap<String, AtsTopicEvent>();

   /**
    * Remote event to notify other clients that work items were modified. This handles the case where changes are made
    * on server and reloaded locally, but other clients need to be notified to reload and refresh. There should only be
    * ONE listener for this event.
    */
   public static final AtsTopicEvent WORK_ITEM_MODIFIED =
      new AtsTopicEvent(EventType.RemoteOnly, "ats/workitem/modified");

   /**
    * Local event to notify listeners that work items were reloaded and they may need to refresh.
    */
   public static final AtsTopicEvent WORK_ITEM_RELOADED =
      new AtsTopicEvent(EventType.LocalOnly, "ats/workitem/reloaded");

   /**
    * Specific event for only transitions. No reloaded is done through this event for transitioning so the events don't
    * collide.
    */
   public static final AtsTopicEvent WORK_ITEM_TRANSITIONED =
      new AtsTopicEvent(EventType.LocalAndRemote, "ats/workitem/transitioned");
   public static final AtsTopicEvent WORK_ITEM_TRANSITION_FAILED =
      new AtsTopicEvent(EventType.LocalOnly, "ats/workitem/transition/failed");

   // semi-colon delimited long ids
   public static final String WORK_ITEM_IDS_KEY = "workItemIds";
   public static final String WORK_ITEM_ATTR_TYPE_IDS_KEY = "workItemAttrTypeIds";
   public static final String WORK_ITEM_REL_TYPE_IDS_KEY = "workItemRelTypeIds";

   public static final String TARGETED_VERSION_MODIFIED = "ats/workitem/targetedversion/modified";
   public static final String NEW_ATS_VERSION_ID = "atsVersionId";
   public static final String PREVIOUS_ATS_VERSION_ID = "previousAtsVersionId";

   private AtsTopicEvent(EventType eventType, String topic) {
      super(eventType, TransactionToken.SENTINEL, topic);
      idToEvent.put(topic, this);
   }

   public static AtsTopicEvent get(String topic) {
      return idToEvent.get(topic);
   }

}
