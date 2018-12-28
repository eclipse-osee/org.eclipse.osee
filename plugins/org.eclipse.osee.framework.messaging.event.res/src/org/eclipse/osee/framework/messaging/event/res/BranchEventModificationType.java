/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.res;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class BranchEventModificationType {

   private final String guid;
   private static Map<String, BranchEventModificationType> guidToEventType =
      new HashMap<>(15);
   public static BranchEventModificationType New = new BranchEventModificationType("AbMBZMtQ304V3L3zdlgA");
   public static BranchEventModificationType Committed = new BranchEventModificationType("AbMBZDMIWATP9NaK0bAA");
   public static BranchEventModificationType Deleted = new BranchEventModificationType("AbMBZE3UESatk0iX0RgA");
   public static BranchEventModificationType MergeBranchConflictResolved =
      new BranchEventModificationType("AbMBZGShRVVx8IaAI3QA");
   public static BranchEventModificationType Purged = new BranchEventModificationType("AbMBZIfAPkGsNG26uZAA");
   public static BranchEventModificationType Renamed = new BranchEventModificationType("AbMBZKjRORW0CB45LVgA");

   public BranchEventModificationType(String guid) {
      this.guid = guid;
      guidToEventType.put(guid, this);
   }

   public static Collection<BranchEventModificationType> getTypes() {
      return guidToEventType.values();
   }

   public static BranchEventModificationType getType(String guid) {
      return guidToEventType.get(guid);
   }

   public String getGuid() {
      return guid;
   }
}
