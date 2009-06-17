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
package org.eclipse.osee.ote.message.tool;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * @author Ken J. Aguilar
 */
public class SubscriptionDetails implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -3968654375167145281L;

   private final SubscriptionKey key;
   private final byte[] currentData;
   private final EnumSet<MemType> availableMemTypes;

   public SubscriptionDetails(SubscriptionKey key, byte[] currentData, Set<MemType> availableMemTypes) {
      this.key = key;
      this.currentData = currentData;
      this.availableMemTypes = EnumSet.noneOf(MemType.class);
      this.availableMemTypes.addAll(availableMemTypes);
   }

   public SubscriptionKey getKey() {
      return key;
   }

   public byte[] getCurrentData() {
      return currentData;
   }

   public EnumSet<MemType> getAvailableMemTypes() {
      return availableMemTypes;
   }
}
