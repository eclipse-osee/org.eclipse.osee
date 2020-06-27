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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public interface RelationTypeToken extends NamedId {
   RelationTypeToken SENTINEL = create(Id.SENTINEL, Named.SENTINEL, null, null, null, null, null, null);

   public static @NonNull RelationTypeToken create(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity, RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB, String sideBName) {
      final class RelationTypeTokenImpl extends NamedIdBase implements RelationTypeToken {
         private final RelationTypeMultiplicity relationTypeMultiplicity;
         private final RelationSorter order;
         private final ArtifactTypeToken artifactTypeA;
         private final ArtifactTypeToken artifactTypeB;
         private final String sideAName;
         private final String sideBName;

         public RelationTypeTokenImpl(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity, RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB, String sideBName) {
            super(id, name);
            this.relationTypeMultiplicity = relationTypeMultiplicity;
            this.order = order;
            this.artifactTypeA = artifactTypeA;
            this.artifactTypeB = artifactTypeB;
            this.sideAName = sideAName;
            this.sideBName = sideBName;
         }

         @Override
         public RelationTypeMultiplicity getMultiplicity() {
            return relationTypeMultiplicity;
         }

         @Override
         public RelationSorter getOrder() {
            return order;
         }

         @Override
         public ArtifactTypeToken getArtifactType(RelationSide relationSide) {
            ArtifactTypeToken artifactType = null;
            if (relationSide.equals(RelationSide.SIDE_A)) {
               artifactType = artifactTypeA;
            } else if (relationSide.equals(RelationSide.SIDE_B)) {
               artifactType = artifactTypeB;
            }
            return artifactType;
         }

         @Override
         public String getSideName(RelationSide relationSide) {
            return relationSide.equals(RelationSide.SIDE_A) ? sideAName : sideBName;
         }

         @Override
         public boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactTypeToken artifactType) {
            return artifactType.inheritsFrom(getArtifactType(relationSide));
         }

         @Override
         public boolean isOrdered() {
            return !RelationSorter.UNORDERED.equals(order);
         }
      }
      return new RelationTypeTokenImpl(id, name, relationTypeMultiplicity, order, artifactTypeA, sideAName,
         artifactTypeB, sideBName);
   }

   RelationTypeMultiplicity getMultiplicity();

   RelationSorter getOrder();

   ArtifactTypeToken getArtifactType(RelationSide relationSide);

   String getSideName(RelationSide relationSide);

   boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactTypeToken artifactType);

   boolean isOrdered();

   default int getRelationSideMax(ArtifactTypeToken artifactType, RelationSide relationSide) {
      return isArtifactTypeAllowed(relationSide, artifactType) ? getMultiplicity().getLimit(relationSide) : 0;
   }
}