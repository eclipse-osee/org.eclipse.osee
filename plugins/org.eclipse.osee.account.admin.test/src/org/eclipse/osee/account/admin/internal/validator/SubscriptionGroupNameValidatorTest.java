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

import static org.eclipse.osee.account.admin.AccountConstants.SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link SubscriptionGroupNameValidator}
 * 
 * @author Roberto E. Escobar
 */
public class SubscriptionGroupNameValidatorTest extends AbstractConfigurableValidatorTest<SubscriptionGroupNameValidator> {

   private static final String VALID_GROUPNAME = "1231244112123";
   private static final String INVALID_GROUPNAME = "";
   private static final String INVALID_GROUP_FOR_PATTERN = "asdasda";

   // @formatter:off
   @Mock private SubscriptionStorage storage;
   // @formatter:on

   public SubscriptionGroupNameValidatorTest() {
      super(AccountField.SUBSCRIPTION_GROUP_NAME, VALID_GROUPNAME, INVALID_GROUPNAME, INVALID_GROUP_FOR_PATTERN,
         SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN);
   }

   @Override
   public void testSetup() {
      super.testSetup();

      when(storage.subscriptionGroupNameExists(getValidValue())).thenReturn(false);
   }

   @Override
   protected SubscriptionGroupNameValidator createValidator() {
      return new SubscriptionGroupNameValidator(storage);
   }

   @Override
   protected Pattern createCustomPattern() {
      return Pattern.compile("\\d+");
   }

   @Override
   @Test
   public void testExists() {
      when(storage.subscriptionGroupNameExists(getValidValue())).thenReturn(true);

      boolean status = getValidator().exists(getValidValue());
      assertTrue(status);

      verify(storage).subscriptionGroupNameExists(getValidValue());
   }

   @Override
   @Test
   public void testNotExists() {
      when(storage.subscriptionGroupNameExists(getValidValue())).thenReturn(false);

      boolean status = getValidator().exists(getValidValue());
      assertFalse(status);

      verify(storage).subscriptionGroupNameExists(getValidValue());
   }

   @Override
   @Test
   public void testValidateFailNotUnique() {
      when(storage.subscriptionGroupNameExists(getValidValue())).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid [" + getExpectedName() + "] - [" + getValidValue() + "] is already in use");
      getValidator().validate(getValidValue());
   }

}
