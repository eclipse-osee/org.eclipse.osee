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
package org.eclipse.osee.framework.ui.skynet.search.page.data;

import java.util.Collection;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.ui.skynet.search.page.SkynetArtifactAdapter;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeNode extends TreeParent implements Comparable<RelationTypeNode> {

   private IRelationLinkDescriptor relationLinkDescriptor;
   private ArtifactTypeNode parentArtifactTypeNode;
   private Collection<ArtifactTypeNode> childArtifactTypeNodes;
   int branchId;
   int revision;

   public RelationTypeNode(IRelationLinkDescriptor relationLinkDescriptor, ArtifactTypeNode parentArtifactTypeNode, int branchId, int revision) {
      super(relationLinkDescriptor.getName());
      this.relationLinkDescriptor = relationLinkDescriptor;
      this.parentArtifactTypeNode = parentArtifactTypeNode;
      this.childArtifactTypeNodes = new TreeSet<ArtifactTypeNode>();
      this.branchId = branchId;
      this.revision = revision;
   }

   public String getRelationTypeName() {
      return getName();
   }

   public void setRelationTypeName(String RelationTypeName) {
      setName(RelationTypeName);
   }

   public void setChecked(boolean isChecked) {
      super.setChecked(isChecked);
      if (childArtifactTypeNodes.size() == 0) {
         populateChildArtifactTypeNodes();
      }
      manageRelationDisplay(isChecked);
   }

   public Collection<ArtifactTypeNode> getChildArtifactTypeNodes() {
      return childArtifactTypeNodes;
   }

   public void populateChildArtifactTypeNodes() {
      Collection<ArtifactSubtypeDescriptor> descriptors =
            SkynetArtifactAdapter.getInstance().getValidArtifactTypesForRelationLink(relationLinkDescriptor, branchId,
                  revision);
      if (descriptors != null && (descriptors.size() > 0)) {
         childArtifactTypeNodes.clear();
         for (ArtifactSubtypeDescriptor descriptor : descriptors) {
            if (!descriptor.getName().equals(parentArtifactTypeNode.getArtifactTypeName())) {
               childArtifactTypeNodes.add(SkynetArtifactAdapter.getInstance().createArtifactTypeNode(descriptor,
                     getRelationTypeName(), branchId, revision));
            }
         }
      }
   }

   public void manageRelationDisplay(boolean isChecked) {
      if (isChecked()) {
         for (ArtifactTypeNode node : childArtifactTypeNodes) {
            addChild(node);
         }
      } else {
         for (ArtifactTypeNode node : childArtifactTypeNodes) {
            removeChild(node);
         }
      }
   }

   public ArtifactTypeNode getParentArtifactTypeNode() {
      return parentArtifactTypeNode;
   }

   public IRelationLinkDescriptor getRelationLinkDescriptor() {
      return relationLinkDescriptor;
   }

   public int compareTo(RelationTypeNode other) {
      return this.getRelationTypeName().compareTo(other.getRelationTypeName());
   }
}
