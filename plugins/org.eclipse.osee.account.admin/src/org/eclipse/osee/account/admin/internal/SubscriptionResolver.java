/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.admin.internal;

import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class SubscriptionResolver {

   private final SubscriptionAdmin subscriptionsAdmin;

   public SubscriptionResolver(SubscriptionAdmin subscriptionsAdmin) {
      super();
      this.subscriptionsAdmin = subscriptionsAdmin;
   }

   public ResultSet<Account> getSubscriptionMembersById(SubscriptionGroupId groupId) {
      ResultSet<Account> toReturn;
      toReturn = subscriptionsAdmin.getSubscriptionMembersOfSubscriptionById(groupId);
      return toReturn;
   }
}
