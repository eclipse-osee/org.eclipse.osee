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
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
@PermitAll
@Path("/unsubscribe")
public class UnsubscribeResource {

   public static interface UnsubscribePageWriter {

      String newUnsubscribePage(URI unsubscribeUri, String subscriptionName, String accountName);

      String newUnsubscribeNoSubscriptionFoundPage(String subscriptionName, String accountName);

      String newUnsubscribeSuccessPage(String subscriptionName, String accountName);

   }

   private final SubscriptionAdmin manager;
   private final UnsubscribePageWriter writer;

   public UnsubscribeResource(SubscriptionAdmin manager, UnsubscribePageWriter writer) {
      this.manager = manager;
      this.writer = writer;
   }

   /**
    * Gets an Unsubscribe page to allow the user to select to unsubscribe
    * 
    * @param subscriptionUuid
    */
   @Path("/ui/{subscription-uuid}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getUnsubscribePage(@PathParam("subscription-uuid") String subscriptionUuid) {
      Subscription subscription = manager.getSubscription(subscriptionUuid);
      String page;
      if (subscription.isActive()) {
         String uuid = subscription.getGuid();
         URI unsubscribeUri = UriBuilder.fromPath("{subscription-uuid}").path("confirm").build(uuid);
         page = writer.newUnsubscribePage(unsubscribeUri, subscription.getName(), subscription.getAccountName());
      } else {
         page = writer.newUnsubscribeNoSubscriptionFoundPage(subscription.getName(), subscription.getAccountName());
      }
      return page;
   }

   /**
    * Gets an Unsubscribe page to allow the user to select to unsubscribe
    * 
    * @param subscriptionUuid
    */
   @Path("/ui/{subscription-uuid}/confirm")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.TEXT_HTML)
   public String processUnsubscribePage(@Encoded @PathParam("subscription-uuid") String subscriptionUuid) {
      Subscription subscription = manager.getSubscription(subscriptionUuid);
      boolean modified = manager.setSubscriptionActive(subscription, false);
      String page;
      if (modified) {
         page = writer.newUnsubscribeSuccessPage(subscription.getName(), subscription.getAccountName());
      } else {
         page = writer.newUnsubscribeNoSubscriptionFoundPage(subscription.getName(), subscription.getAccountName());
      }
      return page;
   }

   public static class UnsubscribePageWriterImpl implements UnsubscribePageWriter {
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

}