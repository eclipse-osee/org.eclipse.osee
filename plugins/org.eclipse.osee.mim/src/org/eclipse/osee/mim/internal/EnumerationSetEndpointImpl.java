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

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.EnumerationSetEndpoint;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;

/**
 * @author Luciano T. Vaglienti
 */
public class EnumerationSetEndpointImpl implements EnumerationSetEndpoint {

   private final InterfaceEnumerationSetApi enumSetApi;
   private final BranchId branch;
   public EnumerationSetEndpointImpl(BranchId branch, InterfaceEnumerationSetApi enumSetApi) {
      this.enumSetApi = enumSetApi;
      this.branch = branch;
   }

   @Override
   public List<InterfaceEnumerationSet> getEnumSets(AttributeTypeToken orderByAttributeTypeId) {
      AttributeTypeToken orderBy = orderByAttributeTypeId;
      if (orderByAttributeTypeId == null) {
         orderBy = AttributeTypeToken.SENTINEL;
      }
      return enumSetApi.getAll(branch, orderBy);
   }

   @Override
   public InterfaceEnumerationSet getEnumSet(ArtifactId id) {
      return enumSetApi.get(branch, id);
   }

}
