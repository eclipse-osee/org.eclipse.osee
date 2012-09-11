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
package org.eclipse.osee.framework.manager.servlet;

public class UnsubscribeData {
   private final String groupId;
   private final String userId;

   public UnsubscribeData(String groupId, String userId) {
      super();
      this.groupId = groupId;
      this.userId = userId;
   }

   public int getGroupId() {
      return Integer.parseInt(groupId);
   }

   public int getUserId() {
      return Integer.parseInt(userId);
   }

}