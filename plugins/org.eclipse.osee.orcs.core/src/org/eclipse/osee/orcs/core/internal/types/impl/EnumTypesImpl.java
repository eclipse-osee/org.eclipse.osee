/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndexProvider;
import org.eclipse.osee.orcs.data.EnumType;
import org.eclipse.osee.orcs.data.EnumTypes;

/**
 * @author John Misinco
 */
public class EnumTypesImpl implements EnumTypes {

   private final OrcsTypesIndexProvider indexProvider;

   public EnumTypesImpl(OrcsTypesIndexProvider indexProvider) {
      this.indexProvider = indexProvider;
   }

   @Override
   public Collection<EnumType> getAll()  {
      return getIndex().getAllTokens();
   }

   @Override
   public EnumType get(Id id) {
      return getIndex().get(id);
   }

   @Override
   public EnumType get(Long id) {
      return getIndex().get(id);
   }

   @Override
   public boolean exists(Id id)  {
      return getIndex().exists(id);
   }

   @Override
   public boolean isEmpty()  {
      return getAll().isEmpty();
   }

   @Override
   public int size()  {
      return getAll().size();
   }

   private EnumTypeIndex getIndex()  {
      return indexProvider.getEnumTypeIndex();
   }

}
