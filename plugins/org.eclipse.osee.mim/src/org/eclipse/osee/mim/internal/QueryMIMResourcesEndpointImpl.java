/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.QueryCapableMIMAPI;
import org.eclipse.osee.mim.QueryMIMResourcesEndpoint;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PLGenericDBObject;

public class QueryMIMResourcesEndpointImpl implements QueryMIMResourcesEndpoint {
   private final BranchId branch;
   private final InterfaceConnectionViewApi connectionApi;
   private final InterfaceNodeViewApi nodeApi;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;
   private final InterfaceStructureApi structureApi;
   private final InterfaceElementApi elementApi;
   private final InterfacePlatformTypeApi platformApi;
   private final InterfaceEnumerationApi enumerationApi;
   private final InterfaceEnumerationSetApi enumerationSetApi;

   public QueryMIMResourcesEndpointImpl(BranchId branch, InterfaceConnectionViewApi interfaceConnectionViewApi, InterfaceNodeViewApi interfaceNodeViewApi, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfaceElementArrayApi interfaceElementArrayApi, InterfacePlatformTypeApi interfacePlatformTypeApi, InterfaceEnumerationApi interfaceEnumerationApi, InterfaceEnumerationSetApi interfaceEnumerationSetApi) {
      this.branch = branch;
      this.connectionApi = interfaceConnectionViewApi;
      this.nodeApi = interfaceNodeViewApi;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
      this.structureApi = interfaceStructureApi;
      this.elementApi = interfaceElementApi;
      this.platformApi = interfacePlatformTypeApi;
      this.enumerationApi = interfaceEnumerationApi;
      this.enumerationSetApi = interfaceEnumerationSetApi;
   }

   @Override
   public Collection<? extends PLGenericDBObject> get(MimAttributeQuery query) {
      return getApi(query.getType()).query(branch, query);
   }

   private QueryCapableMIMAPI<? extends PLGenericDBObject> getApi(ArtifactTypeId type) {
      switch (type.getIdString()) {
         case "6039606571486514295":
            return this.nodeApi;
         case "126164394421696910":
            return this.connectionApi;
         case "2455059983007225775":
            return this.messageApi;
         case "126164394421696908":
            return this.subMessageApi;
         case "2455059983007225776":
            return this.structureApi;
         case "2455059983007225765":
            return this.elementApi;
         case "6360154518785980502":
            return this.elementApi;
         case "6360154518785980503":
            return this.platformApi;
         case "2455059983007225793":
            return this.enumerationApi;
         case "2455059983007225791":
            return this.enumerationSetApi;
         default:
            return null;
      }
   }

}
