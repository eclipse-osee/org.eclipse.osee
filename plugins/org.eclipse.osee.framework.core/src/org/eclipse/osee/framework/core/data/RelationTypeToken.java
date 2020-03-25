/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
   RelationTypeToken SENTINEL = create(Id.SENTINEL, Named.SENTINEL);

   public static RelationTypeToken create(long id, String name) {
      return create(Long.valueOf(id), name);
   }

   public static RelationTypeToken create(Long id, String name) {
      final class RelationTypeTokenImpl extends NamedIdBase implements RelationTypeToken {

         public RelationTypeTokenImpl(Long id, String name) {
            super(id, name);
         }

         @Override
         public RelationTypeMultiplicity getMultiplicity() {
            return null;
         }

         @Override
         public RelationSorter getOrder() {
            return null;
         }

         @Override
         public ArtifactTypeToken getArtifactType(RelationSide relationSide) {
            return null;
         }

         @Override
         public String getSideName(RelationSide relationSide) {
            return null;
         }

         @Override
         public boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactTypeToken artifactType) {
            return false;
         }

         @Override
         public boolean isOrdered() {
            return false;
         }

      }
      return new RelationTypeTokenImpl(id, name);
   }

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
            String sideName = null;
            if (relationSide.equals(RelationSide.SIDE_A)) {
               sideName = sideAName;
            } else if (relationSide.equals(RelationSide.SIDE_B)) {
               sideName = sideBName;
            }
            return sideName;
         }

         @Override
         public boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactTypeToken artifactType) {
            boolean isAllowed = false;
            if (relationSide.equals(RelationSide.SIDE_A)) {
               isAllowed = artifactType.inheritsFrom(artifactTypeA);
            } else if (relationSide.equals(RelationSide.SIDE_B)) {
               isAllowed = artifactType.inheritsFrom(artifactTypeB);
            }
            return isAllowed;
         }

         @Override
         public boolean isOrdered() {
            return !RelationSorter.UNORDERED.equals(order);
         }

      }
      return new RelationTypeTokenImpl(id, name, relationTypeMultiplicity, order, artifactTypeA, sideAName,
         artifactTypeB, sideBName);
   }

   public RelationTypeMultiplicity getMultiplicity();

   public RelationSorter getOrder();

   public ArtifactTypeToken getArtifactType(RelationSide relationSide);

   public String getSideName(RelationSide relationSide);

   public boolean isArtifactTypeAllowed(RelationSide relationSide, ArtifactTypeToken artifactType);

   public boolean isOrdered();

}