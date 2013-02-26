/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config.internal;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemFactory implements IActionableItemFactory {

   private final AtsConfigCache cache;

   public ActionableItemFactory(AtsConfigCache cache) {
      this.cache = cache;
   }

   @Override
   public IAtsActionableItem createActionableItem(String guid, String aiName) {
      return createActionableItem(aiName, guid, HumanReadableId.generate());
   }

   public IAtsActionableItem createActionableItem(String aiName, String guid, String humanReadableId) {
      if (guid == null) {
         throw new IllegalArgumentException("guid can not be null");
      }
      IAtsActionableItem ai = new ActionableItem(aiName, guid, humanReadableId);
      cache.cache(ai);
      return ai;
   }

   @Override
   public IAtsActionableItem getOrCreate(String guid, String aiName) {
      IAtsActionableItem ai = cache.getSoleByGuid(guid, IAtsActionableItem.class);
      if (ai == null) {
         ai = createActionableItem(guid, aiName);
      }
      return ai;
   }
}
