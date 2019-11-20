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

import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public interface RelationTypeToken extends IRelationType, NamedId {
   RelationTypeToken SENTINEL = create(Id.SENTINEL, Named.SENTINEL);

   public static RelationTypeToken create(long id, String name) {
      return create(Long.valueOf(id), name);
   }

   public static RelationTypeToken create(Long id, String name) {
      final class RelationTypeTokenImpl extends NamedIdBase implements RelationTypeToken {

         public RelationTypeTokenImpl(Long id, String name) {
            super(id, name);
         }
      }
      return new RelationTypeTokenImpl(id, name);
   }

   public static RelationTypeToken create(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity, RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB, String sideBName) {
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
      }
      return new RelationTypeTokenImpl(id, name, relationTypeMultiplicity, order, artifactTypeA, sideAName,
         artifactTypeB, sideBName);
   }
}