/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.crossbranch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * This class provides the ability for an artifact to link to an artifact on another branch. It is not a full featured
 * link and is also omni-directional, so care must be taken when using this.<br>
 * The information for this link is stored as attributes on the sideA artifact. User needs to be aware that the relation
 * type, branch and artifact may not exist when the link is accessed, so care must be taken when using this.<br>
 * <br>
 * Link uses the Cross Branch Link attribute which must be added as a valid attribute on the SIDE_A artifact type. <br>
 * <br>
 * TODO Remove this class and replace with framework cross branch links when available.
 * 
 * @author Donald G. Dunne
 */
public class CrossBranchLinkManager {

   public static void addRelation(Artifact artifact, IRelationEnumeration relationSide, Artifact otherArt) throws OseeCoreException {
      CrossBranchLink newLink = new CrossBranchLink(relationSide, otherArt);
      if (!getLinks(artifact).contains(newLink)) {
         newLink.store(artifact);
      }
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (CrossBranchLink link : getLinks(artifact)) {
         if (link.getRelationType().equals(relationEnum) && link.aSide == relationEnum.getSide().isSideA()) {
            artifacts.add(link.getArtifact());
         }
      }
      return artifacts;
   }

   public static int getRelatedArtifactCount(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      return getRelatedArtifacts(artifact, relationEnum).size();
   }

   public static void deleteRelation(Artifact artifact, IRelationEnumeration relationSide, Artifact otherArt) throws OseeCoreException {
      CrossBranchLink newLink = new CrossBranchLink(relationSide, otherArt);
      for (CrossBranchLink link : getLinks(artifact)) {
         if (link.equals(newLink)) {
            link.getMatchingAttribute().delete();
         }
      }
   }

   public static void deleteRelations(Artifact artifact, IRelationEnumeration relationSide) throws OseeCoreException {
      for (CrossBranchLink link : getLinks(artifact)) {
         if (link.getRelationEnum().equals(relationSide)) {
            link.getMatchingAttribute().delete();
         }
      }
   }

   public static Collection<CrossBranchLink> getLinks(Artifact artifact) throws OseeCoreException {
      List<CrossBranchLink> links = new ArrayList<CrossBranchLink>();
      for (Attribute<?> attr : artifact.getAttributes(CoreAttributeTypes.CrossBranchLink)) {
         links.add(new CrossBranchLink(attr));
      }
      return links;
   }
}
