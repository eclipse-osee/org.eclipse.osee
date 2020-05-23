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

package org.eclipse.osee.account.admin.internal;

import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.account.admin.internal.validator.Validators;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SubscriptionAdminImpl implements SubscriptionAdmin {

   private Log logger;
   private SubscriptionStorage storage;

   private Validator validator;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSubscriptionStorage(SubscriptionStorage storage) {
      this.storage = storage;
   }

   public void start(Map<String, Object> props) {
      logger.trace("Starting SubscriptionsAdminImpl...");
      validator = Validators.newSubscriptionValidator(logger, storage);
      update(props);
   }

   public void stop() {
      logger.trace("Stopping SubscriptionsAdminImpl...");
   }

   public void update(Map<String, Object> props) {
      validator.configure(props);
   }

   private SubscriptionStorage getStorage() {
      return storage;
   }

   protected Validator getValidator() {
      return validator;
   }

   @Override
   public SubscriptionGroup getSubscriptionGroupById(SubscriptionGroupId subscriptionId) {
      return getStorage().getSubscriptionGroupById(subscriptionId);
   }

   @Override
   public ResultSet<Subscription> getSubscriptionsByAccountId(ArtifactId accountId) {
      return getStorage().getSubscriptionsByAccountId(accountId);
   }

   @Override
   public Subscription getSubscriptionsByEncodedId(String encodedId) {
      return getStorage().getSubscriptionByEncodedId(encodedId);
   }

   @Override
   public boolean setSubscriptionActive(Subscription subscription, boolean active) {
      boolean modified = false;
      if (subscription != null && subscription.isActive() != active) {
         getStorage().updateSubscription(subscription, active);
         modified = true;
      }
      return modified;
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroups() {
      return getStorage().getSubscriptionGroups();
   }

   @Override
   public SubscriptionGroupId createSubscriptionGroup(String groupName) {
      Conditions.checkNotNull(groupName, "group name");

      Validator validator = getValidator();
      validator.validate(AccountField.SUBSCRIPTION_GROUP_NAME, groupName);

      return getStorage().createSubscriptionGroup(groupName);
   }

   @Override
   public boolean deleteSubscriptionById(SubscriptionGroupId subscriptionId) {
      return getStorage().deleteSubscriptionGroup(subscriptionId);
   }

   @Override
   public ResultSet<Account> getSubscriptionMembersOfSubscriptionById(SubscriptionGroupId subscriptionId) {
      return getStorage().getMembersOfSubscriptionGroupById(subscriptionId);
   }

}
