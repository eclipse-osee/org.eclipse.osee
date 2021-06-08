/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.internal;

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_ACCOUNT_ID;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.mim.InterfaceElementEndpoint;
import org.eclipse.osee.mim.InterfaceElementSearchEndpoint;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.InterfaceMessageFilterEndpoint;
import org.eclipse.osee.mim.InterfaceStructureEndpoint;
import org.eclipse.osee.mim.InterfaceStructureFilterEndpoint;
import org.eclipse.osee.mim.InterfaceStructureSearchEndpoint;
import org.eclipse.osee.mim.InterfaceSubMessageEndpoint;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.PlatformTypesEndpoint;
import org.eclipse.osee.mim.PlatformTypesFilterEndpoint;

/**
 * @author Luciano T. Vaglienti
 */
@Path("branch")
public class BranchAccessor {
   private final MimApi mimApi;

   public BranchAccessor(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Path("{branch}/types")
   @Produces(MediaType.APPLICATION_JSON)
   public PlatformTypesEndpoint getPlatformTypes(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new PlatformTypesEndpointImpl(branch, accountId, mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/types/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public PlatformTypesFilterEndpoint getPlatformTypesFilter(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new PlatformTypesFilterEndpointImpl(branch, accountId, mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/messages")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceMessageEndpoint getMessageEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceMessageEndpointImpl(branch, accountId, mimApi.getInterfaceMessageApi(),
         mimApi.getInterfaceSubMessageApi());
   }

   @Path("{branch}/messages/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceMessageFilterEndpoint getMessageFilterEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceMessageFilterEndpointImpl(branch, accountId, mimApi.getInterfaceMessageApi(),
         mimApi.getInterfaceSubMessageApi());
   }

   @Path("{branch}/messages/{messageId}/submessages")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceSubMessageEndpoint getSubMessageEndpoint(@PathParam("branch") BranchId branch, @PathParam("messageId") ArtifactId messageId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceSubMessageEndpointImpl(branch, accountId, messageId, mimApi.getInterfaceSubMessageApi());
   }

   @Path("{branch}/messages/{messageId}/submessages/{submessageId}/structures")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureEndpoint getStructureEndpoint(@PathParam("branch") BranchId branch, @PathParam("messageId") ArtifactId messageId, @PathParam("submessageId") ArtifactId subMessageId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceStructureEndpointImpl(branch, accountId, messageId, subMessageId,
         mimApi.getInterfaceStructureApi(), mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(),
         mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/messages/{messageId}/submessages/{submessageId}/structures/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureFilterEndpoint getStructureFilterEndpoint(@PathParam("branch") BranchId branch, @PathParam("messageId") ArtifactId messageId, @PathParam("submessageId") ArtifactId subMessageId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceStructureFilterEndpointImpl(branch, accountId, messageId, subMessageId,
         mimApi.getInterfaceStructureApi(), mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(),
         mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/messages/{messageId}/submessages/{submessageId}/structures/{structureId}/elements")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceElementEndpoint getElementEndpoint(@PathParam("branch") BranchId branch, @PathParam("messageId") ArtifactId messageId, @PathParam("submessageId") ArtifactId subMessageId, @PathParam("structureId") ArtifactId structureId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceElementEndpointImpl(branch, accountId, messageId, subMessageId, structureId,
         mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(), mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/elements")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceElementSearchEndpoint getElementSearchEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceElementSearchEndpointImpl(branch, accountId, mimApi.getInterfaceElementApi(),
         mimApi.getInterfaceElementArrayApi(), mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/structures")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureSearchEndpoint getStructureSearchEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceStructureSearchEndpointImpl(branch, accountId, mimApi.getInterfaceStructureApi(),
         mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(), mimApi.getInterfacePlatformTypeApi());
   }
}
