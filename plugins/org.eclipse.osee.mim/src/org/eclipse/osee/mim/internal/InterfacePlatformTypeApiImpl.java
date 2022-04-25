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
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfacePlatformTypeApiImpl implements InterfacePlatformTypeApi {

   private ArtifactAccessor<PlatformTypeToken> accessor;

   InterfacePlatformTypeApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new PlatformTypeAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<PlatformTypeToken> getAccessor() {
      return accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   private void setAccessor(ArtifactAccessor<PlatformTypeToken> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<PlatformTypeToken> query(BranchId branch, MimAttributeQuery query) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<PlatformTypeToken>();
   }

}
