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
package org.eclipse.osee.orcs.db.internal.search.util;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.RelationData;

/**
 * @author Roberto E. Escobar
 */
public class LoadDataBuffer {

   private final Map<Integer, ArtifactData> artifacts;
   private final Multimap<Integer, AttributeData<?>> attributes;
   private final Multimap<Integer, RelationData> relations;

   public LoadDataBuffer(int initialSize) {
      artifacts = new LinkedHashMap<>(initialSize);
      attributes = newLinkedHashListMultimap(initialSize);
      relations = newLinkedHashListMultimap(initialSize);
   }

   public synchronized void clear() {
      artifacts.clear();
      attributes.clear();
      relations.clear();
   }

   public void addData(ArtifactData data) {
      synchronized (artifacts) {
         artifacts.put(data.getLocalId(), data);
      }
   }

   public void addData(AttributeData<?> data) {
      synchronized (attributes) {
         attributes.put(data.getArtifactId().getIdIntValue(), data);
      }
   }

   public void addData(RelationData data) {
      synchronized (relations) {
         relations.put(data.getArtIdA(), data);
         relations.put(data.getArtIdB(), data);
      }
   }

   public ArtifactData removeArtifactByArtId(int artifactId) {
      ArtifactData art = null;
      synchronized (artifacts) {
         art = artifacts.remove(artifactId);
      }
      return art;
   }

   public Iterable<AttributeData<?>> removeAttributesByArtId(int artifactId) {
      Collection<AttributeData<?>> data = null;
      synchronized (attributes) {
         data = attributes.removeAll(artifactId);
      }
      return data;
   }

   public Iterable<RelationData> removeRelationsByArtId(int artifactId) {
      Collection<RelationData> rels = null;
      synchronized (relations) {
         rels = relations.removeAll(artifactId);
      }
      return rels;
   }

   private static <K, V> ListMultimap<K, V> newLinkedHashListMultimap(int fetchSize) {
      Map<K, Collection<V>> map = new LinkedHashMap<>(fetchSize);
      return Multimaps.newListMultimap(map, new Supplier<List<V>>() {
         @Override
         public List<V> get() {
            return Lists.newArrayList();
         }
      });
   }
}