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

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.AffectedArtifactEndpoint;
import org.eclipse.osee.mim.CrossReferenceEndpoint;
import org.eclipse.osee.mim.EnumerationSetEndpoint;
import org.eclipse.osee.mim.GetAllMIMRelatedObjectsEndpoint;
import org.eclipse.osee.mim.IcdEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionEndpoint;
import org.eclipse.osee.mim.InterfaceDifferenceReportEndpoint;
import org.eclipse.osee.mim.InterfaceElementEndpoint;
import org.eclipse.osee.mim.InterfaceElementSearchEndpoint;
import org.eclipse.osee.mim.InterfaceGraphEndpoint;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.InterfaceNodeEndpoint;
import org.eclipse.osee.mim.InterfaceStructureCountEndpoint;
import org.eclipse.osee.mim.InterfaceStructureEndpoint;
import org.eclipse.osee.mim.InterfaceStructureSearchEndpoint;
import org.eclipse.osee.mim.InterfaceSubMessageEndpoint;
import org.eclipse.osee.mim.InterfaceSubMessageFilterEndpoint;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.PlatformTypesEndpoint;
import org.eclipse.osee.mim.PlatformTypesFilterEndpoint;
import org.eclipse.osee.mim.QueryMIMResourcesEndpoint;
import org.eclipse.osee.mim.TransportTypeEndpoint;

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
   public PlatformTypesEndpoint getPlatformTypes(@PathParam("branch") BranchId branch) {
      return new PlatformTypesEndpointImpl(branch, mimApi.getInterfacePlatformTypeApi(),
         mimApi.getInterfaceEnumerationSetApi(), mimApi.getInterfaceEnumerationApi());
   }

   @Path("{branch}/types/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public PlatformTypesFilterEndpoint getPlatformTypesFilter(@PathParam("branch") BranchId branch) {
      return new PlatformTypesFilterEndpointImpl(branch, mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/connections/{connectionId}/messages")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceMessageEndpoint getMessageEndpoint(@PathParam("branch") BranchId branch,
      @PathParam("connectionId") ArtifactId connectionId) {
      return new InterfaceMessageEndpointImpl(branch, connectionId, mimApi.getInterfaceMessageApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceSubMessageEndpoint getSubMessageEndpoint(@PathParam("branch") BranchId branch,
      @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId) {
      return new InterfaceSubMessageEndpointImpl(branch, messageId, mimApi.getInterfaceSubMessageApi());
   }

   @Path("{branch}/submessages/filter")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceSubMessageFilterEndpoint getSubMessageFilterEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceSubMessageFilterEndpointImpl(branch, mimApi.getInterfaceSubMessageApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages/{submessageId}/structures")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureEndpoint getStructureEndpoint(@PathParam("branch") BranchId branch,
      @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId,
      @PathParam("submessageId") ArtifactId subMessageId) {
      return new InterfaceStructureEndpointImpl(branch, connectionId, messageId, subMessageId,
         mimApi.getInterfaceStructureApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages/{submessageId}/structures/count")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureCountEndpoint getStructureCountEndpoint(@PathParam("branch") BranchId branch,
      @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId,
      @PathParam("submessageId") ArtifactId subMessageId) {
      return new InterfaceStructureCountEndpointImpl(branch, messageId, subMessageId,
         mimApi.getInterfaceStructureApi());
   }

   @Path("{branch}/connections/{connectionId}/messages/{messageId}/submessages/{submessageId}/structures/{structureId}/elements")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceElementEndpoint getElementEndpoint(@PathParam("branch") BranchId branch,
      @PathParam("connectionId") ArtifactId connectionId, @PathParam("messageId") ArtifactId messageId,
      @PathParam("submessageId") ArtifactId subMessageId, @PathParam("structureId") ArtifactId structureId) {
      return new InterfaceElementEndpointImpl(branch, messageId, subMessageId, structureId,
         mimApi.getInterfaceElementApi());
   }

   @Path("{branch}/elements")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceElementSearchEndpoint getElementSearchEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceElementSearchEndpointImpl(branch, mimApi.getInterfaceElementApi(),
         mimApi.getInterfaceElementArrayApi(), mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/structures")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceStructureSearchEndpoint getStructureSearchEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceStructureSearchEndpointImpl(branch, mimApi.getInterfaceStructureApi());
   }

   @Path("{branch}/graph")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceGraphEndpoint getGraphEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceGraphEndpointImpl(branch, mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi());
   }

   @Path("{branch}/nodes")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceNodeEndpoint getNodeEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceNodeEndpointImpl(branch, mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi());
   }

   @Path("{branch}/connections")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceConnectionEndpoint getConnectionEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceConnectionEndpointImpl(branch, mimApi.getInterfaceConnectionViewApi());
   }

   @Path("{branch}/enumerations")
   @Produces(MediaType.APPLICATION_JSON)
   public EnumerationSetEndpoint getEnumerationSetEndpoint(@PathParam("branch") BranchId branch) {
      return new EnumerationSetEndpointImpl(branch, mimApi.getInterfaceEnumerationSetApi());
   }

   @Path("{branch}/all")
   @Produces(MediaType.APPLICATION_JSON)
   public GetAllMIMRelatedObjectsEndpoint getRelated(@PathParam("branch") BranchId branch) {
      return new GetAllMIMRelatedObjectsEndpointImpl(branch, mimApi.getInterfaceStructureApi(),
         mimApi.getInterfaceMessageApi(), mimApi.getInterfaceSubMessageApi(), mimApi.getInterfaceNodeViewApi(),
         mimApi.getInterfaceConnectionViewApi(), mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(),
         mimApi.getInterfacePlatformTypeApi());
   }

   @Path("{branch}/query")
   @Produces(MediaType.APPLICATION_JSON)
   public QueryMIMResourcesEndpoint getQueryEndpoint(@PathParam("branch") BranchId branch) {
      return new QueryMIMResourcesEndpointImpl(branch, mimApi.getInterfaceConnectionViewApi(),
         mimApi.getInterfaceNodeViewApi(), mimApi.getInterfaceMessageApi(), mimApi.getInterfaceSubMessageApi(),
         mimApi.getInterfaceStructureApi(), mimApi.getInterfaceElementApi(), mimApi.getInterfaceElementArrayApi(),
         mimApi.getInterfacePlatformTypeApi(), mimApi.getInterfaceEnumerationApi(),
         mimApi.getInterfaceEnumerationSetApi());
   }

   @Path("{branch}/diff")
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceDifferenceReportEndpoint getDifferencesEndpoint(@PathParam("branch") BranchId branch) {
      return new InterfaceDifferenceReportEndpointImpl(branch, mimApi.getInterfaceDifferenceReportApi());
   }

   @Path("{branch}/affected")
   @Produces(MediaType.APPLICATION_JSON)
   public AffectedArtifactEndpoint getAffectedArtifacts(@PathParam("branch") BranchId branch) {
      return new AffectedArtifactEndpointImpl(branch, mimApi.getInterfaceConnectionViewApi(),
         mimApi.getInterfaceMessageApi(), mimApi.getInterfaceSubMessageApi(), mimApi.getInterfaceStructureApi(),
         mimApi.getInterfaceElementApi(), mimApi.getInterfacePlatformTypeApi(), mimApi.getInterfaceEnumerationSetApi(),
         mimApi.getInterfaceEnumerationApi());
   }

   @Path("{branch}/transportTypes")
   @Produces(MediaType.APPLICATION_JSON)
   public TransportTypeEndpoint getTransportTypeEndpoint(@PathParam("branch") BranchId branch) {
      return new TransportTypeEndpointImpl(branch, mimApi.getTransportTypeApi());
   }

   @Path("{branch}/crossReference")
   @Produces(MediaType.APPLICATION_JSON)
   public CrossReferenceEndpoint getCrossReferenceEndpoint(@PathParam("branch") BranchId branch) {
      return new CrossReferenceEndpointImpl(branch, mimApi.getCrossReferenceApi());
   }

   /**
    * @return Xml workbook which contains interface messages/submessages/structure info for a given connection
    * node1_node2
    */
   @Path("{branch}/view/{viewId}/icd/{id}")
   @Produces(MediaType.APPLICATION_XML)
   public IcdEndpoint getIcd(@PathParam("branch") BranchId branch, @PathParam("viewId") ArtifactId viewId,
      @PathParam("id") ArtifactId connectionId) {

      return new IcdEndpointImpl(branch, viewId, connectionId, mimApi);
   }

   @Path("{branch}/icd/{id}")
   @Produces(MediaType.APPLICATION_XML)
   public IcdEndpoint getIcd(@PathParam("branch") BranchId branch, @PathParam("id") ArtifactId connectionId) {

      return new IcdEndpointImpl(branch, ArtifactId.SENTINEL, connectionId, mimApi);
   }
}
