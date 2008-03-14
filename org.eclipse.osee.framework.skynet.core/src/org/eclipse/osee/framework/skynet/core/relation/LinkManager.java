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
import java.util.TreeSet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;

/**
 * @author Jeff C. Phillips
 */
public class LinkManager {
   private static int count = 0;
   public final int aaaSerialId = count++;
   private Artifact artifact;
   private final Set<IRelationLink> links;
   public final Set<IRelationLink> deletedLinks;
   private final Set<IRelationLinkDescriptor> descriptors;
   private final Map<IRelationLinkDescriptor, RelationLinkGroup> sideALinks;
   private final Map<IRelationLinkDescriptor, RelationLinkGroup> sideBLinks;
   private boolean inTrace;
   private static final RelationPersistenceManager relationManager = RelationPersistenceManager.getInstance();
   private static final RelationLinkGroup[] dummyRelationLinkGroups = new RelationLinkGroup[0];
   private static final IRelationLink[] dummyRelationLinks = new IRelationLink[0];
   private boolean released;
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();

   public LinkManager(Artifact artifact) throws SQLException {
      this.artifact = artifact;
      this.links = Collections.synchronizedSet(new HashSet<IRelationLink>());
      this.deletedLinks = Collections.synchronizedSet(new HashSet<IRelationLink>());
      this.descriptors = Collections.synchronizedSet(new HashSet<IRelationLinkDescriptor>());
      this.sideALinks = Collections.synchronizedMap(new HashMap<IRelationLinkDescriptor, RelationLinkGroup>());
      this.sideBLinks = Collections.synchronizedMap(new HashMap<IRelationLinkDescriptor, RelationLinkGroup>());

      this.inTrace = false;
      this.released = false;
   }

   private void checkReleased() {
      if (released) throw new IllegalStateException(
            "This link manager has been released, and is thus no longer supporting an Artifact.");
   }

   public Collection<IRelationLink> getLinks() {
      checkReleased();
      return links;
   }

   public Set<IRelationLink> getLinks(IRelationEnumeration side) throws SQLException {
      return getGroup(side).getGroupSide();
   }

   public void fixOrderingOf(IRelationLink link, boolean sideA) {
      checkReleased();
      (!sideA ? sideALinks : sideBLinks).get(link.getLinkDescriptor()).fixOrder();
   }

   /**
    * @param descriptor
    * @param sideName
    */
   public RelationLinkGroup ensureRelationGroupExists(IRelationLinkDescriptor descriptor, String sideName) {
      return ensureRelationGroupExists(descriptor, descriptor.isSideAName(sideName));
   }

   public RelationLinkGroup ensureRelationGroupExists(IRelationEnumeration relationSide) throws SQLException {
      return ensureRelationGroupExists(relationSide.getDescriptor(getOwningArtifact().getBranch()),
            relationSide.isSideA());
   }

   public RelationLinkGroup ensureRelationGroupExists(IRelationLinkDescriptor descriptor, boolean sideA) {
      checkReleased();

      Map<IRelationLinkDescriptor, RelationLinkGroup> hash = getHash(sideA);
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
   private Map<IRelationLinkDescriptor, RelationLinkGroup> getHash(boolean sideA) {
      return sideA ? sideALinks : sideBLinks;
   }

   public void addLink(IRelationLink link) {
      checkReleased();

      if (link.getArtifactA() == null || link.getArtifactB() == null) throw new IllegalArgumentException(
            "Can not add links that have a null artifact reference");

      descriptors.add(link.getLinkDescriptor());
      hashLink((artifact == link.getArtifactA()) ? sideBLinks : sideALinks, link);
      if (link.isDeleted())
         deletedLinks.add(link);
      else
         links.add(link);
   }

   private boolean hashLink(Map<IRelationLinkDescriptor, RelationLinkGroup> hash, IRelationLink link) {
      RelationLinkGroup group = hash.get(link.getLinkDescriptor());
      Artifact artA = link.getArtifactA();
      Artifact artB = link.getArtifactB();

      if (artifact != artA && artifact != artB) throw new IllegalArgumentException(
            "Link does not pertain to this linkmanger's artifact");

      if (group == null) {
         group = new RelationLinkGroup(this, link.getLinkDescriptor(), artifact != artA);
         hash.put(link.getLinkDescriptor(), group);
      }

      return group.getGroupSide().add(link);
   }

   protected void deleteLink(IRelationLink link) throws SQLException {
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
    */
   protected void removeLink(IRelationLink link) throws SQLException {
      checkReleased();
      boolean useSideB = (link.getArtifactA().isLinkManagerLoaded() && this == link.getArtifactA().getLinkManager());

      if (unhashLink((useSideB) ? sideBLinks : sideALinks, link) && !((useSideB) ? sideALinks : sideBLinks).containsKey(link.getLinkDescriptor())) {

         descriptors.remove(link.getLinkDescriptor());
      }
      links.remove(link);
   }

   public void removeDeleted(IRelationLink link) {
      checkReleased();
      deletedLinks.remove(link);
   }

   private boolean unhashLink(Map<IRelationLinkDescriptor, RelationLinkGroup> hash, IRelationLink link) {
      RelationLinkGroup group = hash.get(link.getLinkDescriptor());
      if (group == null) {
         return false;
         // throw new IllegalStateException("link does not exist on this artifact");
      }

      if (!group.getGroupSide().remove(link)) throw new IllegalStateException("link does not exist on this artifact");

      // System.out.println("---a:" + Integer.toHexString(link.getArtifactA().hashCode()) + "@" +
      // link.getArtifactA().aaaSerialId + " b:" +
      // Integer.toHexString(link.getArtifactB().hashCode()) + "@" + link.getArtifactB().aaaSerialId
      // + " r:" + Integer.toHexString(link.hashCode()) + "@" +
      // ((DynamicRelationLink)link).aaaSerialId + " type:" + link.getLinkDescriptor().getName() + "
      // from:" + artifact.aaaSerialId + "-" + aaaSerialId);
      if (group.getGroupSide().isEmpty()) {
         hash.remove(link.getLinkDescriptor());
         return true;
      }

      return false;
   }

   public boolean isDirty() {
      checkReleased();
      boolean dirty = !deletedLinks.isEmpty();

      for (IRelationLink link : links) {
         dirty |= link.isDirty();

         if (dirty) break;
      }
      return dirty;
   }

   public void persistLinks() throws SQLException {
      checkReleased();
      for (IRelationLink link : links) {
         link.persist(false);
      }
      relationManager.deleteRelationLinks(deletedLinks, artifact.getBranch());

      for (IRelationLink link : deletedLinks.toArray(dummyRelationLinks)) {
         link.getArtifactA().getLinkManager().deletedLinks.remove(link);
         link.getArtifactB().getLinkManager().deletedLinks.remove(link);
      }
   }

   public void traceLinks(boolean recurse, SkynetTransactionBuilder builder) throws SQLException {
      checkReleased();
      if (!inTrace) {
         inTrace = true;
         for (IRelationLink link : links) {
            relationManager.trace(link, recurse, builder);
         }
         builder.addLinks(deletedLinks);
         inTrace = false;
      }
   }

   public Artifact getSoleArtifact(RelationSide side) throws SQLException {
      checkReleased();
      Collection<Artifact> artifacts = getArtifacts(side);
      int size = artifacts.size();
      if (size > 1) throw new IllegalStateException(
            "More than one Artifact is relation through " + side.getTypeName() + " as " + side.getSideName(artifact.getBranch()));

      if (size == 1)
         return artifacts.iterator().next();
      else
         return null;
   }

   public String getSide(IRelationLink currentLink) {
      checkReleased();
      for (IRelationLink link : links) {
         if (currentLink == link) {
            if (artifact == link.getArtifactA())
               return link.getLinkDescriptor().getSideAName();
            else if (artifact == link.getArtifactB()) return link.getLinkDescriptor().getSideBName();
         }
      }
      return "empty";
   }

   public RelationLinkGroup getGroup(IRelationEnumeration side) throws SQLException {
      IRelationLinkDescriptor descriptor = side.getDescriptor(getOwningArtifact().getBranch());
      RelationLinkGroup toReturn = null;
      if (descriptor != null) {
         toReturn = getSideGroup(descriptor, side.isSideA());
      } else {
         throw new IllegalStateException(String.format("Descriptor was null for %s and %s on branch %s",
               side.getTypeName(), side.getSideName(artifact.getBranch()), getOwningArtifact().getBranch()));
      }
      return toReturn;
   }

   public Set<RelationLinkGroup> getGroups(IRelationLinkDescriptor descriptor) {
      checkReleased();
      Set<RelationLinkGroup> groups = new HashSet<RelationLinkGroup>(2);
      RelationLinkGroup group1 = sideALinks.get(descriptor);
      RelationLinkGroup group2 = sideBLinks.get(descriptor);

      if (group1 != null) groups.add(group1);
      if (group2 != null) groups.add(group2);

      return groups;
   }

   public Set<RelationLinkGroup> getGroups() {
      checkReleased();
      Set<RelationLinkGroup> groups = new HashSet<RelationLinkGroup>(sideALinks.size() + sideBLinks.size());
      groups.addAll(sideALinks.values());
      groups.addAll(sideBLinks.values());

      return groups;
   }

   public Set<Artifact> getOtherSideArtifacts() {
      checkReleased();
      Set<Artifact> arts = new HashSet<Artifact>(links.size());

      for (IRelationLink link : links)
         arts.add(getOtherSideAritfact(link));

      return arts;
   }

   public Artifact getOtherSideAritfact(IRelationLink link) {
      checkReleased();
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
      checkReleased();
      if (side == null) throw new IllegalArgumentException("side can not be null");

      if (side.isSideA()) {
         RelationLinkGroup group = sideALinks.get(side.getDescriptor(getOwningArtifact().getBranch()));
         if (group != null) return hasArtifacts(group);
      } else {
         RelationLinkGroup group = sideBLinks.get(side.getDescriptor(getOwningArtifact().getBranch()));
         if (group != null) return hasArtifacts(group);
      }
      return false;
   }

   public Set<Artifact> getArtifacts(IRelationEnumeration side) throws SQLException {
      checkReleased();
      if (side == null) throw new IllegalArgumentException("side can not be null");

      if (side.isSideA()) {
         RelationLinkGroup group = sideALinks.get(side.getDescriptor(getOwningArtifact().getBranch()));
         if (group != null) return getArtifacts(group);
      } else {
         RelationLinkGroup group = sideBLinks.get(side.getDescriptor(getOwningArtifact().getBranch()));
         if (group != null) return getArtifacts(group);
      }
      return new TreeSet<Artifact>();
   }

   private boolean hasArtifacts(RelationLinkGroup group) {
      checkReleased();
      return group.hasArtifacts();
   }

   @SuppressWarnings("unchecked")
   private Set<Artifact> getArtifacts(RelationLinkGroup group) {
      checkReleased();
      if (group == null) {
         return Collections.EMPTY_SET;
      }
      return group.getArtifacts();
   }

   /**
    * Returns the descriptors used from all the stored links of this manager.
    * 
    * @return return link descriptors reference
    */
   public Set<IRelationLinkDescriptor> getLinkDescriptors() {
      checkReleased();
      return descriptors;
   }

   public RelationLinkGroup getSideAGroup(IRelationLinkDescriptor descriptor) {
      checkReleased();
      if (descriptor == null) throw new IllegalArgumentException("descriptor can not be null");
      return sideALinks.get(descriptor);
   }

   public RelationLinkGroup getSideBGroup(IRelationLinkDescriptor descriptor) {
      checkReleased();
      if (descriptor == null) throw new IllegalArgumentException("descriptor can not be null");
      return sideBLinks.get(descriptor);
   }

   public RelationLinkGroup getSideGroup(IRelationLinkDescriptor descriptor, boolean sideA) {
      checkReleased();
      if (sideA)
         return getSideAGroup(descriptor);
      else
         return getSideBGroup(descriptor);
   }

   protected Artifact getOwningArtifact() {
      checkReleased();
      return artifact;
   }

   /**
    * Populates the linkManager with all of an artifacts links. It will first check with the relationManager to see if
    * they are cached before creating them from the database.
    * 
    * @throws SQLException
    */
   public synchronized void populateLinks() throws SQLException {
      checkReleased();
      if (artifact.getPersistenceMemo() != null) {
         relationManager.populateArtifactRelations(artifact);
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
   public void releaseManager() throws SQLException {
      checkReleased();

      deleteLinks(deletedLinks, true);
      deleteLinks(links, false);

      for (IRelationLink link : deletedLinks) {
         ((RelationLinkBase) link).setNotDeleted();
      }

      released = true;
   }

   /**
    * Sets release to true. Therefore, this link manager is no longer being supported.
    */
   public void setReleased() {
      released = true;
   }

   /**
    * Removes the links from the other artifact
    * 
    * @param links
    * @throws SQLException
    */
   private void deleteLinks(Collection<IRelationLink> links, boolean isDeletedLinks) throws SQLException {
      checkReleased();
      LinkManager otherLinkManager = null;

      for (IRelationLink link : links) {
         otherLinkManager = link.getArtifactA().getLinkManager();

         if (this == otherLinkManager) otherLinkManager = link.getArtifactB().getLinkManager();

         if (isDeletedLinks)
            otherLinkManager.removeDeleted(link);
         else
            otherLinkManager.removeLink(link);

         eventManager.kick(new CacheRelationModifiedEvent(link, link.getLinkDescriptor().getName(),
               link.getASideName(), ModType.Deleted.name(), this, link.getBranch()));
      }
   }

   /**
    * Remove the RelationLinkGroup
    * 
    * @param descriptor
    * @throws SQLException
    */
   public void deleteGroups(IRelationLinkDescriptor descriptor) throws SQLException {
      checkReleased();

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
      checkReleased();
      for (IRelationLink link : group.getGroupSide().toArray(dummyRelationLinks)) {
         link.delete();
      }

      // to remove a group when it is empty
      if (group.isSideA())
         sideALinks.remove(group.getDescriptor());
      else
         sideBLinks.remove(group.getDescriptor());

   }

   public void deleteAllLinks() throws SQLException {
      checkReleased();
      for (IRelationLink link : links.toArray(dummyRelationLinks)) {
         link.delete();
      }
   }

   public void clearEmptyRelationGroups() {
      checkReleased();
      cleanUpEmptyGroups(sideALinks);
      cleanUpEmptyGroups(sideBLinks);
   }

   private void cleanUpEmptyGroups(Map<IRelationLinkDescriptor, RelationLinkGroup> side) {

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
    */
   public void ensureLinkValidity(IRelationLinkDescriptor descriptor, boolean sideA, Artifact otherArtifact) {
      checkReleased();
      if (getOwningArtifact() == otherArtifact) throw new IllegalArgumentException(
            "An artifact can not be related to itself: " + otherArtifact.getDescriptiveName() + " - " + otherArtifact.getGuid());

      // and validate adding argument artifact to this group
      descriptor.ensureSideWillSupportArtifact(sideA, otherArtifact, 1);
      descriptor.ensureSideWillSupportArtifact(!sideA, getOwningArtifact(), 1);
   }

   /**
    * check validity of creating artifactCount number of links of type descriptor with the owning artifact for this link
    * manager on on the specified side
    */
   public void ensureHalfLinksValidity(IRelationLinkDescriptor descriptor, boolean sideA, int artifactCount) {
      checkReleased();
      descriptor.ensureSideWillSupportArtifact(sideA, getOwningArtifact(), artifactCount);
   }

   /**
    * Purges all links from runtime.
    * 
    * @throws SQLException
    */
   public void purge() throws SQLException {
      Artifact otherSideArtifact;
      for (IRelationLink link : getLinks()) {
         otherSideArtifact = getOtherSideAritfact(link);

         if (otherSideArtifact.isLinkManagerLoaded()) {
            otherSideArtifact.getLinkManager().removeLink(link);
         }
      }
      links.clear();
   }
}
