/*********************************************************************
 * Copyright (c) 2023 Boeing
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
/**
@author gg949e
*/
package org.eclipse.osee.mim.internal;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureCountEndpoint;

public class InterfaceStructureCountEndpointImpl implements InterfaceStructureCountEndpoint {

   private final BranchId branch;
   private final ArtifactId subMessageId;
   private final InterfaceStructureApi interfaceStructureApi;

   public InterfaceStructureCountEndpointImpl(BranchId branch, ArtifactId messageId, ArtifactId subMessageId, InterfaceStructureApi interfaceStructureApi) {
      this.branch = branch;
      this.subMessageId = subMessageId;
      this.interfaceStructureApi = interfaceStructureApi;
   }

   @Override
   public int getStructures(String filter) {
      if (Strings.isValid(filter)) {
         return this.interfaceStructureApi.getAllRelatedAndFilterCount(branch, subMessageId, filter);
      } else {
         return this.interfaceStructureApi.getAllRelatedCount(branch, subMessageId);
      }
   }

}
