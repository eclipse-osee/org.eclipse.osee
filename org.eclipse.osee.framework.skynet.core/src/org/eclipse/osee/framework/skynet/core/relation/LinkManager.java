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
package org.eclipse.osee.framework.skynet.core.relation;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class LinkManager {
   private Artifact artifact;
   private final Map<RelationType, RelationLinkGroup> sideALinks;
   private final Map<RelationType, RelationLinkGroup> sideBLinks;

   public LinkManager(Artifact artifact) {
      this.artifact = artifact;
      this.sideALinks = Collections.synchronizedMap(new HashMap<RelationType, RelationLinkGroup>());
      this.sideBLinks = Collections.synchronizedMap(new HashMap<RelationType, RelationLinkGroup>());
   }

   public void fixOrderingOf(RelationLink link, boolean sideA) {
      (!sideA ? sideALinks : sideBLinks).get(link.getRelationType()).fixOrder();
   }

   public RelationLinkGroup ensureRelationGroupExists(RelationType descriptor, String sideName) {
      return ensureRelationGroupExists(descriptor, descriptor.isSideAName(sideName));
   }

   public RelationLinkGroup ensureRelationGroupExists(RelationType descriptor, boolean sideA) {
      Map<RelationType, RelationLinkGroup> hash = getHash(sideA);
      RelationLinkGroup group = hash.get(descriptor);

      if (group != null) return group;

      group = new RelationLinkGroup(this, descriptor, sideA);
      hash.put(descriptor, group);
      return group;
   }

   private Map<RelationType, RelationLinkGroup> getHash(boolean sideA) {
      return sideA ? sideALinks : sideBLinks;
   }

   public Set<RelationLinkGroup> getGroups() {
      Set<RelationLinkGroup> groups = new HashSet<RelationLinkGroup>(sideALinks.size() + sideBLinks.size());
      groups.addAll(sideALinks.values());
      groups.addAll(sideBLinks.values());

      return groups;
   }

   public Set<Artifact> getOtherSideArtifacts() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public RelationLinkGroup getSideGroup(RelationType descriptor, boolean sideA) {
      if (sideA)
         return sideALinks.get(descriptor);
      else
         return sideBLinks.get(descriptor);
   }

   public Artifact getOwningArtifact() {
      return artifact;
   }
}