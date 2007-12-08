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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Jeff C. Phillips
 */
public class DynamicRelationLink extends RelationLinkBase {

   /**
    * @param descriptor
    */
   protected DynamicRelationLink(IRelationLinkDescriptor descriptor) {
      super(descriptor);
   }

   protected DynamicRelationLink(Artifact artA, Artifact artB, IRelationLinkDescriptor descriptor, LinkPersistenceMemo memo, String rationale, int aOrder, int bOrder, boolean dirty) {
      super(artA, artB, descriptor, memo, rationale, aOrder, bOrder);

      this.dirty = dirty;
   }

   public void setArtifact(String sideName, Artifact artifact) {
      if (artifact == null) throw new IllegalArgumentException("artifact can not be null");
      if (sideName == null) throw new IllegalArgumentException("sideName can not be null");

      if (getASideName().equals(sideName)) {
         setArtifactA(artifact);
      } else if (getBSideName().equals(sideName)) {
         setArtifactB(artifact);
      } else {
         throw new IllegalArgumentException(
               "sideName '" + sideName + "' does not match '" + getASideName() + "' or '" + getBSideName() + "' for link type " + getName());
      }
   }

   public boolean isExplorable() {
      return true;
   }

   public void setNotDirty() {
      dirty = false;
   }

   public void setDirty() {
      dirty = true;
   }

   public boolean isVersionControlled() {
      return true;
   }

   public Branch getBranch() {
      return getArtifactA().getBranch();
   }

   public void setDirty(boolean isDirty) {
      if (isDirty) {
         setDirty();
      } else {
         setNotDirty();
      }
   }
}
