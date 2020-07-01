/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.function.Function;
import org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Ryan D. Brooks
 */
public class TupleTypeImpl extends BaseId implements TupleTypeToken {
   public static final Function<Long, String> KeyedString = l -> null;
   public static final Function<Long, ArtifactTypeToken> ArtifactType = l -> null;
   public static final Function<Long, AttributeTypeToken> AttributeType = l -> null;
   public static final Function<Long, RelationTypeToken> RelationType = l -> null;
   public static final Function<Long, ArtifactTypeJoin> ArtifactTypeJoin = l -> null;
   public static final Function<Long, AttributeTypeJoin> AttributeTypeJoin = l -> null;
   public static final Function<Long, RelationTypeJoin> RelationTypeJoin = l -> null;

   private final TupleFamilyId family;

   public TupleTypeImpl(Long tupleTypeId) {
      this(CoreTupleFamilyTypes.DefaultFamily, tupleTypeId);
   }

   public TupleTypeImpl(TupleFamilyId family, Long tupleTypeId) {
      super(tupleTypeId);
      this.family = family;
   }

   @Override
   public TupleFamilyId getFamily() {
      return family;
   }
}