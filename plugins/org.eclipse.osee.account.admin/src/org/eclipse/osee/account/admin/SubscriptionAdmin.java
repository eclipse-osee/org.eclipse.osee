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
package org.eclipse.osee.account.admin;

import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface SubscriptionAdmin {

   ResultSet<Subscription> getSubscriptionsByAccountUniqueField(String accountUniqueField);

   ResultSet<Subscription> getSubscriptionsByGuid(String guid);

   Subscription getSubscription(String subscriptionUuid);

   boolean setSubscriptionActive(Subscription subscription, boolean active);

   boolean setSubscriptionActive(String subscriptionUuid, boolean active);

   ResultSet<SubscriptionGroup> getSubscriptionGroups();

   ResultSet<SubscriptionGroup> getSubscriptionGroupByUniqueField(String groupUniqueField);

   ResultSet<SubscriptionGroup> getSubscriptionGroupByLocalId(long groupId);

   ResultSet<SubscriptionGroup> getSubscriptionGroupByName(String groupName);

   ResultSet<SubscriptionGroup> getSubscriptionGroupByGuid(String groupUuid);

   SubscriptionGroup createSubscriptionGroup(String groupName);

   boolean deleteSubscriptionGroupByUniqueField(String groupId);

   ResultSet<Account> getSubscriptionGroupMembersByUniqueField(String groupUniqueField);

   ResultSet<Account> getSubscriptionGroupMembersByLocalId(long groupId);

   ResultSet<Account> getSubscriptionGroupMembersByName(String groupName);

   ResultSet<Account> getSubscriptionGroupMembersByGuid(String groupUuid);

}
