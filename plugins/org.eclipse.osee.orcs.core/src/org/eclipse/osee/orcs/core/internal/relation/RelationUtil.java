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
package org.eclipse.osee.orcs.core.internal.relation;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Parent;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public final class RelationUtil {

   public static final IRelationType DEFAULT_HIERARCHY =
      TokenFactory.createRelationType(Default_Hierarchical__Parent.getGuid(), Default_Hierarchical__Parent.getName());
   public static final RelationSide IS_PARENT = RelationSide.SIDE_A;
   public static final RelationSide IS_CHILD = RelationSide.SIDE_B;

   private RelationUtil() {
      // Utility
   }

   public static IRelationType asRelationType(RelationTypeSide typeAndSide) {
      return TokenFactory.createRelationType(typeAndSide.getGuid(), typeAndSide.getName());
   }

   public static RelationTypeSide asTypeSide(IRelationType type, RelationSide side) {
      return RelationTypeSide.create(side, type.getId(), type.getName());
   }

   /**
    * Orders artifacts into a and b relation nodes using relation side parameter as a specification of where art2 is in
    * the set.
    *
    * @param art1 The First artifact
    * @param art2 The Second artifact
    * @param side What side is artifact 2 on
    * @return pair of nodes in a,b order
    * @throws OseeCoreException
    */
   public static Pair<RelationNode, RelationNode> asABNodes(Artifact art1, Artifact art2, RelationSide side) {
      RelationNode aNode;
      RelationNode bNode;
      if (RelationSide.SIDE_A == side) {
         aNode = art2;
         bNode = art1;
      } else {
         aNode = art1;
         bNode = art2;
      }
      return new Pair<RelationNode, RelationNode>(aNode, bNode);
   }

}
