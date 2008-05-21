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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;

/**
 * @author Jeff C. Phillips
 */
public class LinkManager {
   private Artifact artifact;
   private final Set<RelationLink> links;
   public final Set<RelationLink> deletedLinks;
   private final Set<RelationType> descriptors;
   private final Map<RelationType, RelationLinkGroup> sideALinks;
   private final Map<RelationType, RelationLinkGroup> sideBLinks;
   private boolean inTrace;
   private static final RelationLinkGroup[] dummyRelationLinkGroups = new RelationLinkGroup[0];
   private static final RelationLink[] dummyRelationLinks = new RelationLink[0];

   public LinkManager(Artifact artifact) {
      this.artifact = artifact;
      this.links = Collections.synchronizedSet(new HashSet<RelationLink>());
      this.deletedLinks = Collections.synchronizedSet(new HashSet<RelationLink>());
      this.descriptors = Collections.synchronizedSet(new HashSet<RelationType>());
      this.sideALinks = Collections.synchronizedMap(new HashMap<RelationType, RelationLinkGroup>());
      this.sideBLinks = Collections.synchronizedMap(new HashMap<RelationType, RelationLinkGroup>());
      this.inTrace = false;
   }

   public Collection<RelationLink> getLinks() {
      return links;
   }

   public Set<RelationLink> getLinks(IRelationEnumeration side) throws SQLException {
      return getGroup(side).getGroupSide();
   }

   public void fixOrderingOf(RelationLink link, boolean sideA) {
      (!sideA ? sideALinks : sideBLinks).get(link.getRelationType()).fixOrder();
   }

   /**
    * @param descriptor
    * @param sideName
    */
   public RelationLinkGroup ensureRelationGroupExists(RelationType descriptor, String sideName) {
      return ensureRelationGroupExists(descriptor, descriptor.isSideAName(sideName));
   }

   public RelationLinkGroup ensureRelationGroupExists(IRelationEnumeration relationSide) throws SQLException {
      return ensureRelationGroupExists(relationSide.getRelationType(), relationSide.isSideA());
   }

   public RelationLinkGroup ensureRelationGroupExists(RelationType descriptor, boolean sideA) {
      Map<RelationType, RelationLinkGroup> hash = getHash(sideA);
      RelationLinkGroup group = hash.get(descriptor);

      if (group != null) return group;

      group = new RelationLinkGroup(this, descriptor, sideA);
      descriptors.add(descriptor);
      hash.put(descriptor, group);
      return group;
   }

   /**
    * @param sideA
    * @return Return side link map referrence
    */
   private Map<RelationType, RelationLinkGroup> getHash(boolean sideA) {
      return sideA ? sideALinks : sideBLinks;
   }

   public void addLink(RelationLink link) throws SQLException {
      descriptors.add(link.getRelationType());
      addLinkToGroup(link);
      if (link.isDeleted())
         deletedLinks.add(link);
      else
         links.add(link);
   }

   private void addLinkToGroup(RelationLink link) throws SQLException {
      Map<RelationType, RelationLinkGroup> hash = artifact == link.getArtifactA() ? sideBLinks : sideALinks;
      Artifact artA = link.getArtifactA();
      Artifact artB = link.getArtifactB();

      if (artifact != artA && artifact != artB) throw new IllegalArgumentException(
            "Link does not pertain to this linkmanger's artifact");

      RelationLinkGroup group = hash.get(link.getRelationType());
      if (group == null) {
         group = new RelationLinkGroup(this, link.getRelationType(), artifact != artA);
         hash.put(link.getRelationType(), group);
      }

      group.getGroupSide().add(link);
   }

   public void traceLinks(boolean recurse, SkynetTransactionBuilder builder) throws Exception {
      if (!inTrace) {
         inTrace = true;
         for (RelationLink link : links) {
            RelationPersistenceManager.getInstance().trace(link, recurse, builder);
         }
         builder.addLinks(deletedLinks);
         inTrace = false;
      }
   }

   public RelationLinkGroup getGroup(IRelationEnumeration side) throws SQLException {
      RelationType relationType = side.getRelationType();
      if (relationType == null) {
         throw new IllegalStateException(String.format("Relation Type was null for %s and %s on branch %s",
               side.getTypeName(), side.getSideName(artifact.getBranch()), getOwningArtifact().getBranch()));
      } else {
         return getSideGroup(relationType, side.isSideA());
      }
   }

   public Set<RelationLinkGroup> getGroups(RelationType descriptor) {
      Set<RelationLinkGroup> groups = new HashSet<RelationLinkGroup>(2);
      RelationLinkGroup group1 = sideALinks.get(descriptor);
      RelationLinkGroup group2 = sideBLinks.get(descriptor);

      if (group1 != null) groups.add(group1);
      if (group2 != null) groups.add(group2);

      return groups;
   }

   /**
    * This method should not be called by application code
    * 
    * @return
    */
   public Set<RelationLinkGroup> getGroups() {
      Set<RelationLinkGroup> groups = new HashSet<RelationLinkGroup>(sideALinks.size() + sideBLinks.size());
      groups.addAll(sideALinks.values());
      groups.addAll(sideBLinks.values());

      return groups;
   }

   public Set<Artifact> getOtherSideArtifacts() throws SQLException {
      Set<Artifact> arts = new HashSet<Artifact>(links.size());

      for (RelationLink link : links)
         arts.add(getOtherSideAritfact(link));

      return arts;
   }

   public Artifact getOtherSideAritfact(RelationLink link) throws SQLException {
      if (link == null) throw new IllegalArgumentException("link can not be null");

      if (artifact == link.getArtifactA())
         return link.getArtifactB();
      else if (artifact == link.getArtifactB()) return link.getArtifactA();
      return null;
   }

   /**
    * Returns the descriptors used from all the stored links of this manager.
    * 
    * @return return link descriptors reference
    */
   public Set<RelationType> getLinkDescriptors() {
      return descriptors;
   }

   public RelationLinkGroup getSideAGroup(RelationType descriptor) {
      if (descriptor == null) throw new IllegalArgumentException("descriptor can not be null");
      return sideALinks.get(descriptor);
   }

   public RelationLinkGroup getSideBGroup(RelationType descriptor) {
      if (descriptor == null) throw new IllegalArgumentException("descriptor can not be null");
      return sideBLinks.get(descriptor);
   }

   public RelationLinkGroup getSideGroup(RelationType descriptor, boolean sideA) {
      if (sideA)
         return getSideAGroup(descriptor);
      else
         return getSideBGroup(descriptor);
   }

   public Artifact getOwningArtifact() {
      return artifact;
   }

   public void deleteAllLinks() throws SQLException {
      for (RelationLink link : links.toArray(dummyRelationLinks)) {
         link.delete();
      }
   }
}