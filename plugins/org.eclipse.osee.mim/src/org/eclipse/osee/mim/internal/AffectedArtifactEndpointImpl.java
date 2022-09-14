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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.AffectedArtifactEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.ArtifactMatch;

public class AffectedArtifactEndpointImpl implements AffectedArtifactEndpoint {

   private final BranchId branch;
   private final InterfaceConnectionViewApi connectionApi;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;
   private final InterfaceStructureApi structureApi;
   private final InterfaceElementApi elementApi;
   private final InterfacePlatformTypeApi typeApi;
   private final InterfaceEnumerationSetApi enumSetApi;
   private final InterfaceEnumerationApi enumApi;

   public AffectedArtifactEndpointImpl(BranchId branch, InterfaceConnectionViewApi interfaceConnectionApi, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfacePlatformTypeApi interfacePlatformTypeApi, InterfaceEnumerationSetApi enumSetApi, InterfaceEnumerationApi enumApi) {
      this.branch = branch;
      this.connectionApi = interfaceConnectionApi;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
      this.structureApi = interfaceStructureApi;
      this.elementApi = interfaceElementApi;
      this.typeApi = interfacePlatformTypeApi;
      this.enumSetApi = enumSetApi;
      this.enumApi = enumApi;
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromType(ArtifactId affectedArtifactId) {
      return typeApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromElement(ArtifactId affectedArtifactId) {
      return elementApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromStructure(ArtifactId affectedArtifactId) {
      return structureApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromSubMessage(ArtifactId affectedArtifactId) {
      return subMessageApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromMessage(ArtifactId affectedArtifactId) {
      return messageApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromEnum(ArtifactId affectedArtifactId) {
      return enumApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromEnumSet(ArtifactId affectedArtifactId) {
      return enumSetApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedFromConnection(ArtifactId affectedArtifactId) {
      return connectionApi.getAffectedArtifacts(branch, affectedArtifactId);
   }

}
