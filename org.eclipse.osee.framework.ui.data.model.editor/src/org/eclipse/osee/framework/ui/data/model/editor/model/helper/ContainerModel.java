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
package org.eclipse.osee.framework.ui.data.model.editor.model.helper;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;

/**
 * @author Roberto E. Escobar
 */
public class ContainerModel {
   private final ArtifactDataType artifactDataType;
   private final ContainerType internals;

   public static enum ContainerType {
      INHERITED_ATTRIBUTES, INHERITED_RELATIONS, LOCAL_RELATIONS, LOCAL_ATTRIBUTES;
   }

   public ContainerModel(ArtifactDataType theClass, ContainerType internals) {
      this.artifactDataType = theClass;
      this.internals = internals;
   }

   public List<? extends DataType> getChildren() {
      List<? extends DataType> children = new ArrayList<DataType>();
      switch (internals) {
         case INHERITED_ATTRIBUTES:
            children = artifactDataType.getInheritedAttributes();
            break;
         case LOCAL_ATTRIBUTES:
            children = artifactDataType.getLocalAttributes();
            break;
         case INHERITED_RELATIONS:
            children = artifactDataType.getInheritedRelations();
            break;
         case LOCAL_RELATIONS:
            children = artifactDataType.getLocalRelations();
            break;
         default:
            break;
      }
      return children;
   }

   public ArtifactDataType getArtifact() {
      return artifactDataType;
   }

   public ContainerType getContainerType() {
      return internals;
   }
}
