/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.crossbranch;

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.type.RelationType;

public class LoadedRelationTypes extends NamedIdentity implements IRelationEnumeration {

   private final RelationSide relationSide;

   public LoadedRelationTypes(RelationType relationType, boolean aSide) {
      this(aSide ? RelationSide.SIDE_A : RelationSide.SIDE_B, relationType.getGuid(), relationType.getName());
   }

   public LoadedRelationTypes(RelationSide side, String guid, String name) {
      super(guid, name);
      this.relationSide = side;
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}