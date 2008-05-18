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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;

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

   protected void deleteLink(RelationLink link) throws SQLException {
      // This removes the link from the cache of each artifact's link manager (if loaded)
      removeLink(link);
      // This marks the link for deletion form the DB upon persist and end transaction
      deletedLinks.add(link);
   }

   /**
    * caller is responsible for invoking kickDeleteLinkEvent
    * 
    * @param link
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   protected void removeLink(RelationLink link) throws SQLException {
      boolean useSideB = (link.getArtifactA().isLinksLoaded() && this == link.getArtifactA().getLinkManager());

      if (unhashLink((useSideB) ? sideBLinks : sideALinks, link) && !((useSideB) ? sideALinks : sideBLinks).containsKey(link.getRelationType())) {

         descriptors.remove(link.getRelationType());
      }
      links.remove(link);
   }

   public void removeDeleted(RelationLink link) {
      deletedLinks.remove(link);
   }

   private boolean unhashLink(Map<RelationType, RelationLinkGroup> hash, RelationLink link) {
      RelationLinkGroup group = hash.get(link.getRelationType());
      if (group == null) {
         return false;
      }

      if (!group.getGroupSide().remove(link)) throw new IllegalStateException("link does not exist on this artifact");

      if (group.getGroupSide().isEmpty()) {
         hash.remove(link.getRelationType());
         return true;
      }

      return false;
   }

   public boolean isDirty() {
      boolean dirty = !deletedLinks.isEmpty();

      for (RelationLink link : links) {
         dirty |= link.isDirty();

         if (dirty) break;
      }
      return dirty;
   }

   public void persistLinks() throws SQLException {
      for (RelationLink link : links) {
         link.persist();
      }
      RelationPersistenceManager.getInstance().deleteRelationLinks(deletedLinks, artifact.getBranch());

      for (RelationLink link : deletedLinks.toArray(dummyRelationLinks)) {
         link.getArtifactA().getLinkManager().deletedLinks.remove(link);
         link.getArtifactB().getLinkManager().deletedLinks.remove(link);
      }
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

   public Artifact getSoleArtifact(RelationSide side) throws SQLException {
      Collection<Artifact> artifacts = getArtifacts(side);
      int size = artifacts.size();
      if (size > 1) throw new IllegalStateException(
            "More than one Artifact is relation through " + side.getTypeName() + " as " + side.getSideName(artifact.getBranch()));

      if (size == 1)
         return artifacts.iterator().next();
      else
         return null;
   }

   public String getSide(RelationLink currentLink) throws SQLException {
      for (RelationLink link : links) {
         if (currentLink == link) {
            if (artifact == link.getArtifactA())
               return link.getRelationType().getSideAName();
            else if (artifact == link.getArtifactB()) return link.getRelationType().getSideBName();
         }
      }
      return "empty";
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
      else if (artifact == link.getArtifactB())
         return link.getArtifactA();
      else
         throw new IllegalArgumentException(
               "The link " + link.getPersistenceMemo() + " does not pertain to this link manager for artifact " + artifact.getGuid() + ". Artifact a: " + (link.getArtifactA() == null ? null : link.getArtifactA().getGuid()) + " Artifact b: " + (link.getArtifactB() == null ? null : link.getArtifactB().getGuid()));
   }

   public boolean hasArtifacts(IRelationEnumeration side) throws SQLException {
      if (side == null) throw new IllegalArgumentException("side can not be null");

      if (side.isSideA()) {
         RelationLinkGroup group = sideALinks.get(side.getRelationType());
         if (group != null) return hasArtifacts(group);
      } else {
         RelationLinkGroup group = sideBLinks.get(side.getRelationType());
         if (group != null) return hasArtifacts(group);
      }
      return false;
   }

   public Set<Artifact> getArtifacts(IRelationEnumeration side) throws SQLException {
      if (side == null) throw new IllegalArgumentException("side can not be null");

      RelationLinkGroup group = null;
      if (side.isSideA()) {
         group = sideALinks.get(side.getRelationType());
      } else {
         group = sideBLinks.get(side.getRelationType());
      }

      if (group == null) {
         return Collections.emptySet();
      }
      return group.getArtifacts();
   }

   public int getRelationCount(IRelationEnumeration side) throws SQLException {
      RelationLinkGroup group = null;
      if (side.isSideA()) {
         group = sideALinks.get(side.getRelationType());
      } else {
         group = sideBLinks.get(side.getRelationType());
      }

      if (group == null) {
         return 0;
      }
      return group.getLinkCount();
   }

   private boolean hasArtifacts(RelationLinkGroup group) {
      return group.hasArtifacts();
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

   protected Artifact getOwningArtifact() {
      return artifact;
   }

   /**
    * Populates the linkManager with all of an artifacts links. It will first check with the relationManager to see if
    * they are cached before creating them from the database.
    * 
    * @throws SQLException
    */
   public synchronized void ensurePopulated() throws SQLException {
      if (!artifact.isLinksLoaded() && artifact.isInDb()) {
         ArtifactLoader.loadArtifactData(artifact, ArtifactLoad.FULL_FULL);
      }
   }

   /**
    * Releases the LinkManager from supporting its Artifact. The LinkManager will clean up any state data it contains,
    * and will no longer be usable. All references to this LinkManager should be updated to the new supporting
    * LinkManager for the Artifact.<br/><br/> This method call is only intended to be used from within the Skynet
    * system, and should not be called from application code.
    * 
    * @throws SQLException
    */
   public void revert() throws SQLException {
      links.clear();
      deletedLinks.clear();
      descriptors.clear();
      sideALinks.clear();
      sideBLinks.clear();
      ArtifactLoader.loadArtifactData(artifact, ArtifactLoad.RELATION);
   }

   /**
    * Remove the RelationLinkGroup
    * 
    * @param descriptor
    * @throws SQLException
    */
   public void deleteGroups(RelationType descriptor) throws SQLException {
      if (descriptor == null) return;

      for (RelationLinkGroup group : getGroups(descriptor).toArray(dummyRelationLinkGroups)) {
         deleteGroupSide(group);
      }

      // to remove descriptors when groups are empty
      descriptors.remove(descriptor);
   }

   /**
    * Removes all the links of one side of a group
    * 
    * @param group
    * @throws SQLException
    */
   public void deleteGroupSide(RelationLinkGroup group) throws SQLException {
      for (RelationLink link : group.getGroupSide().toArray(dummyRelationLinks)) {
         link.delete();
      }

      // to remove a group when it is empty
      if (group.isSideA())
         sideALinks.remove(group.getDescriptor());
      else
         sideBLinks.remove(group.getDescriptor());

   }

   public void deleteAllLinks() throws SQLException {
      for (RelationLink link : links.toArray(dummyRelationLinks)) {
         link.delete();
      }
   }

   public void clearEmptyRelationGroups() {
      cleanUpEmptyGroups(sideALinks);
      cleanUpEmptyGroups(sideBLinks);
   }

   private void cleanUpEmptyGroups(Map<RelationType, RelationLinkGroup> side) {

      Iterator<RelationLinkGroup> iterator = side.values().iterator();
      RelationLinkGroup group;

      while (iterator.hasNext()) {
         group = iterator.next();

         if (group.getGroupSide().isEmpty()) {
            iterator.remove();

            if (!side.containsKey(group.getDescriptor())) {
               descriptors.remove(group.getDescriptor());
            }
         }
      }
   }

   /**
    * check validity of creating a link of type descriptor with otherArtifact on the specified side and the owning
    * artifact for this link manager on the opposite side
    * 
    * @throws SQLException
    */
   public void ensureLinkValidity(RelationType relationType, boolean sideA, Artifact otherArtifact) throws SQLException {
      if (getOwningArtifact() == otherArtifact) throw new IllegalArgumentException(
            "An artifact can not be related to itself: " + otherArtifact.getDescriptiveName() + " - " + otherArtifact.getGuid());

      // and validate adding argument artifact to this group

      RelationTypeManager.ensureSideWillSupportArtifact(relationType, sideA, otherArtifact, 1);
      RelationTypeManager.ensureSideWillSupportArtifact(relationType, !sideA, getOwningArtifact(), 1);
   }

   /**
    * check validity of creating artifactCount number of links of type descriptor with the owning artifact for this link
    * manager on on the specified side
    * 
    * @throws SQLException
    */
   public void ensureHalfLinksValidity(RelationType relationType, boolean sideA, int artifactCount) throws SQLException {
      RelationTypeManager.ensureSideWillSupportArtifact(relationType, sideA, getOwningArtifact(), artifactCount);
   }

   /**
    * Purges all links from runtime.
    * 
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   public void purge() throws SQLException {
      for (RelationLink link : getLinks()) {
         Artifact otherSideArtifact = link.getOtherSideAritfactIfAvailable(artifact);

         if (otherSideArtifact != null && otherSideArtifact.isLinksLoaded()) {
            otherSideArtifact.getLinkManager().removeLink(link);
         }
      }
      links.clear();
   }

   public RelationLink getRelation(int relationId) {
      for (RelationLink relation : links) {
         if (relation.getPersistenceMemo().getLinkId() == relationId) {
            return relation;
         }
      }
      return null;
   }
}