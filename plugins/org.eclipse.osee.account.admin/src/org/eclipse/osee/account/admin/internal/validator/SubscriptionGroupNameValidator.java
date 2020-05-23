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

package org.eclipse.osee.account.admin.internal.validator;

import static org.eclipse.osee.account.admin.AccountConstants.DEFAULT_SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN;
import java.util.Map;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;

/**
 * @author Roberto E. Escobar
 */
public class SubscriptionGroupNameValidator extends AbstractConfigurableValidator {

   private final SubscriptionStorage storage;

   public SubscriptionGroupNameValidator(SubscriptionStorage storage) {
      super();
      this.storage = storage;
   }

   @Override
   public AccountField getFieldType() {
      return AccountField.SUBSCRIPTION_GROUP_NAME;
   }

   @Override
   public boolean exists(String value) {
      return storage.subscriptionGroupNameExists(value);
   }

   @Override
   public String getPatternFromConfig(Map<String, Object> props) {
      return get(props, SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN, DEFAULT_SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN);
   }

}
