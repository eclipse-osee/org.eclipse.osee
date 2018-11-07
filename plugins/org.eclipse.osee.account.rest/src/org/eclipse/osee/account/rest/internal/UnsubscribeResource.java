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
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;

/**
 * @author Roberto E. Escobar
 */
@PermitAll
@Path("/unsubscribe")
public class UnsubscribeResource {

   private final SubscriptionAdmin manager;

   public UnsubscribeResource(SubscriptionAdmin manager) {
      this.manager = manager;
   }

   /**
    * Gets an Unsubscribe page to allow the user to select to unsubscribe
    */
   @Path("/ui/{subscription-uuid}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getUnsubscribePage(@PathParam("subscription-uuid") String subscriptionUuid) {
      Subscription subscription = manager.getSubscriptionsByEncodedId(subscriptionUuid);
      ViewModel page;
      if (subscription.isActive()) {
         String uuid = subscription.getGuid();
         URI unsubscribeUri = UriBuilder.fromPath("{subscription-uuid}").path("confirm").build(uuid);
         page = newUnsubscribePage(unsubscribeUri, subscription.getName(), subscription.getAccountName());
      } else {
         page = newUnsubscribeNoSubscriptionFoundPage(subscription.getName(), subscription.getAccountName());
      }
      return page;
   }

   /**
    * Gets an Unsubscribe page to allow the user to select to unsubscribe
    */
   @Path("/ui/{subscription-uuid}/confirm")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.TEXT_HTML)
   public ViewModel processUnsubscribePage(@Encoded @PathParam("subscription-uuid") String subscriptionUuid) {
      Subscription subscription = manager.getSubscriptionsByEncodedId(subscriptionUuid);
      boolean modified = manager.setSubscriptionActive(subscription, false);
      ViewModel page;
      if (modified) {
         page = newUnsubscribeSuccessPage(subscription.getName(), subscription.getAccountName());
      } else {
         page = newUnsubscribeNoSubscriptionFoundPage(subscription.getName(), subscription.getAccountName());
      }
      return page;
   }

   protected static final String UNSUBSCRIBE_TEMPLATE = "unsubscribe.html";
   protected static final String UNSUBSCRIBE_SUCCESS_TEMPLATE = "unsubscribe_success.html";
   protected static final String UNSUBSCRIBE_NO_SUBSCRIPTION_TEMPLATE = "unsubscribe_no_subscription.html";

   protected static final String ACCOUNT_DISPLAY_NAME_TAG = "accountDisplayName";
   protected static final String SUBSCRIPTION_NAME_TAG = "subscriptionName";
   protected static final String UNSUBSCRIBE_URL = "unsubscribeUrl";

   private ViewModel newUnsubscribePage(URI unsubscribeUri, String subscriptionName, String accountName) {
      return new ViewModel(UNSUBSCRIBE_TEMPLATE) //
         .param(UNSUBSCRIBE_URL, unsubscribeUri) //
         .param(ACCOUNT_DISPLAY_NAME_TAG, accountName) //
         .param(SUBSCRIPTION_NAME_TAG, subscriptionName);
   }

   private ViewModel newUnsubscribeNoSubscriptionFoundPage(String subscriptionName, String accountName) {
      return new ViewModel(UNSUBSCRIBE_NO_SUBSCRIPTION_TEMPLATE) //
         .param(ACCOUNT_DISPLAY_NAME_TAG, accountName) //
         .param(SUBSCRIPTION_NAME_TAG, subscriptionName);
   }

   private ViewModel newUnsubscribeSuccessPage(String subscriptionName, String accountName) {
      return new ViewModel(UNSUBSCRIBE_SUCCESS_TEMPLATE) //
         .param(ACCOUNT_DISPLAY_NAME_TAG, accountName) //
         .param(SUBSCRIPTION_NAME_TAG, subscriptionName);
   }
}