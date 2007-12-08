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

import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeNode extends TreeParent implements Comparable<ArtifactTypeNode> {
   private ArtifactSubtypeDescriptor subTypeDescriptor;

   public ArtifactTypeNode(ArtifactSubtypeDescriptor subTypeDescriptor) {
      super(subTypeDescriptor.getName());
      this.subTypeDescriptor = subTypeDescriptor;
   }

   public String getArtifactTypeName() {
      return getName();
   }

   public void setSchemaName(String artifactTypeName) {
      setName(artifactTypeName);
   }

   public ArtifactSubtypeDescriptor getSubTypeDescriptor() {
      return subTypeDescriptor;
   }

   public int compareTo(ArtifactTypeNode other) {
      return this.getArtifactTypeName().compareTo(other.getArtifactTypeName());
   }
}
