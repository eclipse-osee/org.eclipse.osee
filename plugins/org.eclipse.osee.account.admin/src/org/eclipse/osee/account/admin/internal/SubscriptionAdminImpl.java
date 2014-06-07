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

import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.account.admin.internal.validator.Validators;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SubscriptionAdminImpl implements SubscriptionAdmin {

   private Log logger;
   private SubscriptionStorage storage;
   private AccountAdmin accountAdmin;

   private SubscriptionResolver resolver;
   private Validator validator;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSubscriptionStorage(SubscriptionStorage storage) {
      this.storage = storage;
   }

   public void setAccountAdmin(AccountAdmin accountAdmin) {
      this.accountAdmin = accountAdmin;
   }

   public void start(Map<String, Object> props) {
      logger.trace("Starting SubscriptionsAdminImpl...");

      validator = Validators.newSubscriptionValidator(logger, storage);
      resolver = new SubscriptionResolver(validator, this);
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

   private SubscriptionResolver getResolver() {
      return resolver;
   }

   @Override
   public ResultSet<Subscription> getSubscriptionsByAccountUniqueField(String uniqueField) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(uniqueField);
      Account account = result.getExactlyOne();
      return getStorage().getSubscriptionsByAccountLocalId(account.getId());
   }

   @Override
   public Subscription getSubscription(String subscriptionUuid) {
      Conditions.checkNotNull(subscriptionUuid, "subscription uuid");
      return getStorage().getSubscription(subscriptionUuid);
   }

   @Override
   public boolean setSubscriptionActive(String subscriptionUuid, boolean active) {
      Subscription subscription = getSubscription(subscriptionUuid);
      return setSubscriptionActive(subscription, active);
   }

   @Override
   public boolean setSubscriptionActive(Subscription subscription, boolean active) {
      boolean modified = false;
      if (subscription != null && subscription.isActive() != active) {
         long accountId = subscription.getAccountId();
         long groupId = subscription.getGroupId();
         getStorage().updateSubscription(accountId, groupId, active);
         modified = true;
      }
      return modified;
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroups() {
      return getStorage().getSubscriptionGroups();
   }

   @Override
   public SubscriptionGroup createSubscriptionGroup(String groupName) {
      Conditions.checkNotNull(groupName, "group name");

      Validator validator = getValidator();
      validator.validate(AccountField.SUBSCRIPTION_GROUP_NAME, groupName);

      return getStorage().createSubscriptionGroup(groupName);
   }

   @Override
   public boolean deleteSubscriptionGroupByUniqueField(String groupId) {
      boolean modified = false;
      ResultSet<SubscriptionGroup> results = getSubscriptionGroupByUniqueField(groupId);
      SubscriptionGroup group = results.getOneOrNull();
      if (group != null) {
         getStorage().deleteSubscriptionGroup(group);
         modified = true;
      }
      return modified;
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByUniqueField(String groupUniqueField) {
      return getResolver().resolveSubscriptionGroup(groupUniqueField);
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByUniqueField(String groupUniqueField) {
      return getResolver().resolveSubscriptionGroupMembersByGroupUniqueField(groupUniqueField);
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByLocalId(long groupId) {
      return getStorage().getSubscriptionGroupByLocalId(groupId);
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByName(String groupName) {
      Conditions.checkNotNull(groupName, "group name");
      return getStorage().getSubscriptionGroupByName(groupName);
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByUuid(String groupUuid) {
      Conditions.checkNotNull(groupUuid, "group uuid");
      return getStorage().getSubscriptionGroupByUuid(groupUuid);
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByLocalId(long groupId) {
      return getStorage().getSubscriptionGroupMembersByLocalId(groupId);
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByName(String groupName) {
      Conditions.checkNotNull(groupName, "group name");
      return getStorage().getSubscriptionGroupMembersByName(groupName);
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByUuid(String groupUuid) {
      Conditions.checkNotNull(groupUuid, "group uuid");
      return getStorage().getSubscriptionGroupMembersByUuid(groupUuid);
   }

}
