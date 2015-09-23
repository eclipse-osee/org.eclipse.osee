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
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class SubscriptionResolver {

   private final Validator validator;
   private final SubscriptionAdmin subscriptionsAdmin;

   public SubscriptionResolver(Validator validator, SubscriptionAdmin subscriptionsAdmin) {
      super();
      this.validator = validator;
      this.subscriptionsAdmin = subscriptionsAdmin;
   }

   private long parseLocalId(String uniqueFieldValue) {
      return Long.valueOf(uniqueFieldValue);
   }

   public ResultSet<SubscriptionGroup> resolveSubscriptionGroup(String uniqueFieldValue) {
      Conditions.checkNotNullOrEmpty(uniqueFieldValue, "subscription group unique field value");
      ResultSet<SubscriptionGroup> toReturn;
      AccountField type = validator.guessFormatType(uniqueFieldValue);
      switch (type) {
         case LOCAL_ID:
            long id = parseLocalId(uniqueFieldValue);
            toReturn = subscriptionsAdmin.getSubscriptionGroupByLocalId(id);
            break;
         case GUID:
            toReturn = subscriptionsAdmin.getSubscriptionGroupByGuid(uniqueFieldValue);
            break;
         case SUBSCRIPTION_GROUP_NAME:
            toReturn = subscriptionsAdmin.getSubscriptionGroupByName(uniqueFieldValue);
            break;
         default:
            toReturn = ResultSets.emptyResultSet();
            break;
      }
      return toReturn;
   }

   public ResultSet<Account> resolveSubscriptionGroupMembersByGroupUniqueField(String uniqueFieldValue) {
      Conditions.checkNotNullOrEmpty(uniqueFieldValue, "subscription group unique field value");
      ResultSet<Account> toReturn;
      AccountField type = validator.guessFormatType(uniqueFieldValue);
      switch (type) {
         case LOCAL_ID:
            long id = parseLocalId(uniqueFieldValue);
            toReturn = subscriptionsAdmin.getSubscriptionGroupMembersByLocalId(id);
            break;
         case GUID:
            toReturn = subscriptionsAdmin.getSubscriptionGroupMembersByGuid(uniqueFieldValue);
            break;
         case SUBSCRIPTION_GROUP_NAME:
            toReturn = subscriptionsAdmin.getSubscriptionGroupMembersByName(uniqueFieldValue);
            break;
         default:
            toReturn = ResultSets.emptyResultSet();
            break;
      }
      return toReturn;
   }
}
