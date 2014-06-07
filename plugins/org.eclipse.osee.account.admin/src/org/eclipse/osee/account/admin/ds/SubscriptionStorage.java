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
package org.eclipse.osee.account.admin.ds;

import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface SubscriptionStorage {

   ResultSet<Subscription> getSubscriptionsByAccountLocalId(long accountId);

   Subscription getSubscription(String subscriptionUuid);

   void updateSubscription(long accountId, long groupId, boolean activate);

   ResultSet<SubscriptionGroup> getSubscriptionGroups();

   ResultSet<SubscriptionGroup> getSubscriptionGroupByLocalId(long groupId);

   ResultSet<SubscriptionGroup> getSubscriptionGroupByName(String name);

   ResultSet<SubscriptionGroup> getSubscriptionGroupByUuid(String uuid);

   boolean subscriptionGroupNameExists(String groupName);

   SubscriptionGroup createSubscriptionGroup(String name);

   void deleteSubscriptionGroup(SubscriptionGroup group);

   ResultSet<Account> getSubscriptionGroupMembersByLocalId(long groupId);

   ResultSet<Account> getSubscriptionGroupMembersByName(String name);

   ResultSet<Account> getSubscriptionGroupMembersByUuid(String uuid);

}
