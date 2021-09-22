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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.EnumerationSetEndpoint;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;

/**
 * @author Luciano T. Vaglienti
 */
public class EnumerationSetEndpointImpl implements EnumerationSetEndpoint {

   private final InterfaceEnumerationSetApi enumSetApi;
   private final InterfaceEnumerationApi enumApi;
   private final BranchId branch;
   public EnumerationSetEndpointImpl(BranchId branch, InterfaceEnumerationSetApi enumSetApi, InterfaceEnumerationApi enumApi) {
      this.enumSetApi = enumSetApi;
      this.enumApi = enumApi;
      this.branch = branch;
   }

   @Override
   public List<InterfaceEnumerationSet> getEnumSets() {
      try {
         List<InterfaceEnumerationSet> enumSet =
            (List<InterfaceEnumerationSet>) this.enumSetApi.getAccessor().getAll(branch, InterfaceEnumerationSet.class);
         for (InterfaceEnumerationSet set : enumSet) {
            set.setEnumerations((List<InterfaceEnumeration>) this.enumApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet, ArtifactId.valueOf(set.getId()),
               InterfaceEnumeration.class));
         }
         return enumSet;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

}
