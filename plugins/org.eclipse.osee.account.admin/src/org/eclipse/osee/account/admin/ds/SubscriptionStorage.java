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
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface SubscriptionStorage {

   ResultSet<Subscription> getSubscriptionsByAccountId(ArtifactId accountId);

   SubscriptionGroup getSubscriptionGroupById(SubscriptionGroupId subscriptionId);

   Subscription getSubscriptionByEncodedId(String encodedId);

   void updateSubscription(Subscription subscription, boolean activate);

   ResultSet<SubscriptionGroup> getSubscriptionGroups();

   boolean subscriptionGroupNameExists(String groupName);

   SubscriptionGroupId createSubscriptionGroup(String name);

   ResultSet<Account> getMembersOfSubscriptionGroupById(SubscriptionGroupId subscriptionId);

   boolean deleteSubscriptionGroup(SubscriptionGroupId subscriptionId);

   ResultSet<Account> getSubscriptionMembersById(SubscriptionGroupId subscriptionId);

}
