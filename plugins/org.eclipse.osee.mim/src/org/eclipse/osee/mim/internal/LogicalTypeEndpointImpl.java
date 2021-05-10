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

import java.util.Collection;
import java.util.Optional;
import org.eclipse.osee.mim.LogicalTypeEndpoint;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeGeneric;

/**
 * @author Audrey E Denk
 */
public class LogicalTypeEndpointImpl implements LogicalTypeEndpoint {

   private final MimApi mimApi;

   public LogicalTypeEndpointImpl(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Override
   public Collection<? extends InterfaceLogicalTypeGeneric> getLogicalTypes() {
      return mimApi.getLogicalTypes().values();
   }

   @Override
   public InterfaceLogicalTypeGeneric getLogicalTypeFields(String type) {
      Optional<? extends InterfaceLogicalTypeGeneric> findAny =
         getLogicalTypes().stream().filter(t -> t.getIdString().equals(type)).findAny();
      InterfaceLogicalTypeGeneric rtn = null;
      if (findAny.isPresent()) {
         rtn = findAny.get();
      }
      return rtn;
   }

}
