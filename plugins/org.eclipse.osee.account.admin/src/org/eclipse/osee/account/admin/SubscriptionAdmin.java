/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.account.admin;

import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface SubscriptionAdmin {

   Subscription getSubscriptionsByEncodedId(String encodedId);

   ResultSet<Subscription> getSubscriptionsByAccountId(ArtifactId accountId);

   SubscriptionGroup getSubscriptionGroupById(SubscriptionGroupId subscriptionId);

   boolean setSubscriptionActive(Subscription subscription, boolean active);

   ResultSet<SubscriptionGroup> getSubscriptionGroups();

   SubscriptionGroupId createSubscriptionGroup(String groupName);

   boolean deleteSubscriptionById(SubscriptionGroupId subscriptionId);

   ResultSet<Account> getSubscriptionMembersOfSubscriptionById(SubscriptionGroupId groupId);
}
