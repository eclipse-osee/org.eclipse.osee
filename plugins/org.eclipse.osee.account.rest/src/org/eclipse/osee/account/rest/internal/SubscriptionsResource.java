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

package org.eclipse.osee.account.rest.internal;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.SubscriptionData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
@Path("/subscriptions")
public class SubscriptionsResource {

   private final SubscriptionAdmin manager;

   public SubscriptionsResource(SubscriptionAdmin manager) {
      super();
      this.manager = manager;
   }

   /**
    * Get All account subscriptions
    *
    * @return accountSubscriptions
    */
   @Path("/for-account/{account-id}")
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public SubscriptionData[] getSubscriptions(@PathParam("account-id") Long accountId) {
      ArtifactId artId = ArtifactId.valueOf(accountId);
      ResultSet<Subscription> subscriptions = manager.getSubscriptionsByAccountId(artId);
      SubscriptionData[] toReturn = new SubscriptionData[subscriptions.size()];
      int index = 0;
      for (Subscription subscription : subscriptions) {
         toReturn[index++] = AccountDataUtil.asAccountSubscriptionData(subscription);
      }
      return toReturn;
   }

   /**
    * Get account subscription info
    *
    * @return accountSubscription
    */
   @Path("/{subscription-uuid}")
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public SubscriptionData getSubscriptionByEncoded(@PathParam("subscription-uuid") String encodedId) {
      Subscription accountSubscription = manager.getSubscriptionsByEncodedId(encodedId);
      return AccountDataUtil.asAccountSubscriptionData(accountSubscription);
   }

   /**
    * Set subscription status to active
    *
    * @return response
    * @response.representation.200.doc subscription status set to active
    * @response.representation.304.doc subscription active status not modified
    */
   @Path("/{subscription-uuid}/active")
   @PUT
   public Response setSubscriptionActive(@PathParam("subscription-uuid") String subscriptionUuid) {
      ResponseBuilder builder;
      Subscription subcription = manager.getSubscriptionsByEncodedId(subscriptionUuid);
      boolean modified = manager.setSubscriptionActive(subcription, true);
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

   /**
    * Set subscription status to inactive
    *
    * @return response
    * @response.representation.200.doc subscription status set to inactive
    * @response.representation.304.doc subscription status not modified
    */
   @Path("/{subscription-uuid}/active")
   @DELETE
   public Response setSubscriptionInactive(@PathParam("subscription-uuid") String subscriptionUuid) {
      ResponseBuilder builder;
      Subscription subcription = manager.getSubscriptionsByEncodedId(subscriptionUuid);
      boolean modified = manager.setSubscriptionActive(subcription, false);
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

   /**
    * Get all subscription groups
    *
    * @return subscription groups
    */
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Path("/groups")
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public SubscriptionGroupData[] getSubscriptionGroups() {
      ResultSet<SubscriptionGroup> groups = manager.getSubscriptionGroups();
      SubscriptionGroupData[] toReturn = new SubscriptionGroupData[groups.size()];
      int index = 0;
      for (SubscriptionGroup group : groups) {
         toReturn[index++] = AccountDataUtil.asSubscriptionGroupData(group);
      }
      return toReturn;
   }

   /**
    * Get subscription group
    *
    * @return subscription group
    */
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Path("/groups/{group-id}")
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public SubscriptionGroupData getSubscriptionGroup(@PathParam("group-id") Long groupId) {
      SubscriptionGroup subscription = manager.getSubscriptionGroupById(new SubscriptionGroupId(groupId));
      return AccountDataUtil.asSubscriptionGroupData(subscription);
   }

   /**
    * Get subscription group
    *
    * @return subscription group
    */
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Path("/groups/{group-id}/members")
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public AccountInfoData[] getSubscriptionGroupMembers(@PathParam("group-id") Long groupId) {
      ResultSet<Account> accounts = manager.getSubscriptionMembersOfSubscriptionById(new SubscriptionGroupId(groupId));
      AccountInfoData[] toReturn = new AccountInfoData[accounts.size()];
      int index = 0;
      for (Account group : accounts) {
         toReturn[index++] = AccountDataUtil.asAccountData(group);
      }
      return toReturn;
   }

   /**
    * Create a subscription group
    *
    * @return subscription group
    */
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Path("/groups/{group-name}")
   @POST
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public SubscriptionGroupData createSubscriptionGroup(@PathParam("group-name") String groupName) {
      SubscriptionGroupId groupId = manager.createSubscriptionGroup(groupName);
      SubscriptionGroup subscription = manager.getSubscriptionGroupById(groupId);
      return AccountDataUtil.asSubscriptionGroupData(subscription);
   }

   /**
    * Delete subscription group
    *
    * @return subscription group
    */
   @RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
   @Path("/groups/{group-id}")
   @DELETE
   public Response deleteSubscriptionGroup(@PathParam("group-id") Long groupId) {
      ResponseBuilder builder;
      boolean modified = manager.deleteSubscriptionById(new SubscriptionGroupId(groupId));
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

}
