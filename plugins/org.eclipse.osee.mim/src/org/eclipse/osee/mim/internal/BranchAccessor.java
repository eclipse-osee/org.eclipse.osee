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
import org.eclipse.osee.mim.EnumerationSetEndpoint;
import org.eclipse.osee.mim.GetAllMIMRelatedObjectsEndpoint;
import org.eclipse.osee.mim.IcdEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionEndpoint;
import org.eclipse.osee.mim.InterfaceElementEndpoint;
import org.eclipse.osee.mim.InterfaceElementSearchEndpoint;
import org.eclipse.osee.mim.InterfaceGraphEndpoint;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.InterfaceMessageFilterEndpoint;
import org.eclipse.osee.mim.InterfaceNodeEndpoint;
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
      return new PlatformTypesEndpointImpl(branch, accountId, mimApi.getInterfacePlatformTypeApi(),
         mimApi.getInterfaceEnumerationSetApi(), mimApi.getInterfaceEnumerationApi());
   }

   @Path("{branch}/types/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public PlatformTypesFilterEndpoint getPlatformTypesFilter(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new PlatformTypesFilterEndpointImpl(branch, accountId, mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/connections/{connectionId}/messages")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceMessageEndpoint getMessageEndpoint(@PathParam("branch") BranchId branch, @PathParam("connectionId") ArtifactId connectionId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceMessageEndpointImpl(branch, connectionId, accountId, mimApi.getInterfaceMessageApi(),
         mimApi.getInterfaceSubMessageApi(), mimApi.getInterfaceNodeViewApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceMessageFilterEndpoint getMessageFilterEndpoint(@PathParam("branch") BranchId branch, @PathParam("connectionId") ArtifactId connectionId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceMessageFilterEndpointImpl(branch, connectionId, accountId, mimApi.getInterfaceMessageApi(),
         mimApi.getInterfaceSubMessageApi(), mimApi.getInterfaceNodeViewApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceSubMessageEndpoint getSubMessageEndpoint(@PathParam("branch") BranchId branch, @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceSubMessageEndpointImpl(branch, accountId, messageId, mimApi.getInterfaceSubMessageApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages/{submessageId}/structures")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureEndpoint getStructureEndpoint(@PathParam("branch") BranchId branch, @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId, @PathParam("submessageId") ArtifactId subMessageId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceStructureEndpointImpl(branch, accountId, messageId, subMessageId,
         mimApi.getInterfaceStructureApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages/{submessageId}/structures/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureFilterEndpoint getStructureFilterEndpoint(@PathParam("branch") BranchId branch, @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId, @PathParam("submessageId") ArtifactId subMessageId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceStructureFilterEndpointImpl(branch, accountId, messageId, subMessageId,
         mimApi.getInterfaceStructureApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages/{submessageId}/structures/{structureId}/elements")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceElementEndpoint getElementEndpoint(@PathParam("branch") BranchId branch, @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId, @PathParam("submessageId") ArtifactId subMessageId, @PathParam("structureId") ArtifactId structureId, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceElementEndpointImpl(branch, accountId, messageId, subMessageId, structureId,
         mimApi.getInterfaceElementApi());
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
      return new InterfaceStructureSearchEndpointImpl(branch, accountId, mimApi.getInterfaceStructureApi());
   }

   @Path("{branch}/graph")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceGraphEndpoint getGraphEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceGraphEndpointImpl(branch, accountId, mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi());
   }

   @Path("{branch}/nodes")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceNodeEndpoint getNodeEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceNodeEndpointImpl(branch, accountId, mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi());
   }

   @Path("{branch}/connections")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceConnectionEndpoint getConnectionEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new InterfaceConnectionEndpointImpl(branch, accountId, mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi());
   }

   @Path("{branch}/enumerations")
   @Produces(MediaType.APPLICATION_JSON)
   public EnumerationSetEndpoint getEnumerationSetEndpoint(@PathParam("branch") BranchId branch, @HeaderParam(OSEE_ACCOUNT_ID) UserId accountId) {
      return new EnumerationSetEndpointImpl(branch, mimApi.getInterfaceEnumerationSetApi(),
         mimApi.getInterfaceEnumerationApi());
   }

   @Path("{branch}/all")
   @Produces(MediaType.APPLICATION_JSON)
   public GetAllMIMRelatedObjectsEndpoint getRelated(@PathParam("branch") BranchId branch) {
      return new GetAllMIMRelatedObjectsEndpointImpl(branch, mimApi.getInterfaceStructureApi(),
         mimApi.getInterfaceMessageApi(), mimApi.getInterfaceSubMessageApi(), mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi(), mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(),
         mimApi.getInterfacePlatformTypeApi());
   }

   /**
    * @return Xml workbook which contains interface messages/submessages/structure info for a given connection
    * node1_node2
    */
   @Path("{branch}/view/{viewId}/icd/{id}")
   @Produces(MediaType.APPLICATION_XML)
   public IcdEndpoint getIcd(@PathParam("branch") BranchId branch, @PathParam("viewId") ArtifactId viewId, @PathParam("id") ArtifactId connectionId) {

      return new IcdEndpointImpl(branch, viewId, connectionId, mimApi.getOrcsApi());
   }

   @Path("{branch}/icd/{id}")
   @Produces(MediaType.APPLICATION_XML)
   public IcdEndpoint getIcd(@PathParam("branch") BranchId branch, @PathParam("id") ArtifactId connectionId) {

      return new IcdEndpointImpl(branch, ArtifactId.SENTINEL, connectionId, mimApi.getOrcsApi());
   }
}
