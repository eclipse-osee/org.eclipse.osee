/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.HasOrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitor;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.tuple.TupleVisitor;

/**
 * Collect all the dirty OrcsData's into a changeSet;
 *
 * @author Roberto E. Escobar
 */
public class ChangeSetBuilder implements ArtifactVisitor, RelationVisitor, TupleVisitor, HasOrcsChangeSet {

   private final OrcsChangeSetImpl changeSet;

   public ChangeSetBuilder() {
      this.changeSet = new OrcsChangeSetImpl();
   }

   @Override
   public void visit(Artifact artifact) {
      if (artifact.isDirty()) {
         changeSet.arts.add(artifact.getOrcsData());
      }
   }

   @Override
   public void visit(Attribute<?> attribute) {
      if (attribute.isDirty()) {
         changeSet.attrs.add(attribute.getOrcsData());
      }
   }

   @Override
   public void visit(Relation relation) {
      if (relation.isDirty()) {
         changeSet.rels.add(relation.getOrcsData());
      }
   }

   @Override
   public void visit(TupleData tuple)  {
      changeSet.tuples.add(tuple);
   }

   @Override
   public OrcsChangeSet getChangeSet() {
      return changeSet;
   }

   private static final class OrcsChangeSetImpl implements OrcsChangeSet {

      private final Set<ArtifactData> arts = Sets.newLinkedHashSet();
      private final Set<AttributeData> attrs = Sets.newLinkedHashSet();
      private final Set<RelationData> rels = Sets.newLinkedHashSet();
      private final List<TupleData> tuples = new ArrayList<>();

      @Override
      public void accept(OrcsVisitor visitor)  {
         for (ArtifactData data : getArtifactData()) {
            visitor.visit(data);
         }
         for (AttributeData data : getAttributeData()) {
            visitor.visit(data);
         }
         for (RelationData data : getRelationData()) {
            visitor.visit(data);
         }
         for (TupleData data : getTupleData()) {
            visitor.visit(data);
         }
      }

      @Override
      public Iterable<ArtifactData> getArtifactData() {
         return arts;
      }

      @Override
      public Iterable<AttributeData> getAttributeData() {
         return attrs;
      }

      @Override
      public Iterable<RelationData> getRelationData() {
         return rels;
      }

      @Override
      public Iterable<TupleData> getTupleData() {
         return tuples;
      }

      @Override
      public boolean isEmpty() {
         return arts.isEmpty() && attrs.isEmpty() && rels.isEmpty() && tuples.isEmpty();
      }

      @Override
      public int size() {
         return arts.size() + attrs.size() + rels.size();
      }

      @Override
      public String toString() {
         return "OrcsChangeSetImpl [arts=" + arts + ", attrs=" + attrs + ", rels=" + rels + "]";
      }

   }
}