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
package org.eclipse.osee.account.rest.internal;

import java.net.URI;
import org.eclipse.osee.account.rest.internal.UnsubscribeResource.UnsubscribePageWriter;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
public class PageWriter implements UnsubscribePageWriter {
   private static final ResourceToken UNSUBSCRIBE_TEMPLATE = createToken("unsubscribe.html");
   private static final ResourceToken UNSUBSCRIBE_SUCCESS_TEMPLATE = createToken("unsubscribe_success.html");
   private static final ResourceToken UNSUBSCRIBE_NO_SUBSCRIPTION_TEMPLATE =
      createToken("unsubscribe_no_subscription.html");

   private static final String ACCOUNT_DISPLAY_NAME_TAG = "accountDisplayName";
   private static final String SUBSCRIPTION_NAME_TAG = "subscriptionName";
   private static final String UNSUBSCRIBE_URL = "unsubscribeUrl";

   private IResourceRegistry registry;

   private IResourceRegistry getRegistry() {
      if (registry == null) {
         registry = new ResourceRegistry();
         registry.registerResource(-1L, UNSUBSCRIBE_TEMPLATE);
         registry.registerResource(-2L, UNSUBSCRIBE_SUCCESS_TEMPLATE);
         registry.registerResource(-3L, UNSUBSCRIBE_NO_SUBSCRIPTION_TEMPLATE);
      }
      return registry;
   }

   @Override
   public String newUnsubscribePage(URI unsubscribeUri, String subscriptionName, String accountName) {
      return PageFactory.realizePage(getRegistry(), UNSUBSCRIBE_TEMPLATE, //
         UNSUBSCRIBE_URL, unsubscribeUri.toASCIIString(), //
         ACCOUNT_DISPLAY_NAME_TAG, accountName, //
         SUBSCRIPTION_NAME_TAG, subscriptionName);
   }

   @Override
   public String newUnsubscribeNoSubscriptionFoundPage(String subscriptionName, String accountName) {
      return PageFactory.realizePage(getRegistry(), UNSUBSCRIBE_NO_SUBSCRIPTION_TEMPLATE, //
         ACCOUNT_DISPLAY_NAME_TAG, accountName, //
         SUBSCRIPTION_NAME_TAG, subscriptionName);
   }

   @Override
   public String newUnsubscribeSuccessPage(String subscriptionName, String accountName) {
      return PageFactory.realizePage(getRegistry(), UNSUBSCRIBE_SUCCESS_TEMPLATE,//
         ACCOUNT_DISPLAY_NAME_TAG, accountName, //
         SUBSCRIPTION_NAME_TAG, subscriptionName);
   }

   private static ResourceToken createToken(String fileName) {
      return new ClassBasedResourceToken(fileName, UnsubscribeResource.class);
   }
}