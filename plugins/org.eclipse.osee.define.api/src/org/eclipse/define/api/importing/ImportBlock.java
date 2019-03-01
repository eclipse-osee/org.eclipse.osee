/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.define.api.importing;

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author David W. Miller
 */
public abstract class ImportBlock implements Named {
   protected IArtifactType type = IArtifactType.SENTINEL;
   private Boolean complete = false;
   protected final LinkedList<BlockField> attrs = new LinkedList<>();

   public Boolean isParentBlock() {
      if (type.isValid() && type.equals(CoreArtifactTypes.HeadingMSWord)) {
         return true;
      } else {
         return false;
      }
   }

   public Boolean isComplete() {
      return complete;
   }

   public void addAttr(BlockField attr) {
      attrs.add(attr);
      if (attr.getImportTypeName().equals("Type")) {
         setType(attr);
      }
      if (attr.getMarksComplete()) {
         complete = true;
      }
   }

   public IArtifactType getType() {
      return type;
   }

   public abstract void addContent(String content);

   public abstract void setType(BlockField attr);

   public BlockField getImportAttrFromName(String attrName) {
      Conditions.assertNotNull(attrName, "null attribute given");

      for (BlockField attr : attrs) {
         String localAttrName = attr.getImportTypeName();
         if (attrName.equals(localAttrName)) {
            return attr;
         }
      }
      throw new OseeCoreException("Invalid Attribute Name requested: %s", attrName);
   }

   public RoughArtifact addAttributesToRoughArtifact(RoughArtifact roughArt) {
      if (!isComplete()) {
         throw new OseeCoreException("Imported Block not complete");
      }
      for (BlockField attr : attrs) {
         AttributeTypeToken attrType = attr.getOseeType();
         if (attrType.isValid()) {
            roughArt.addAttribute(attrType, attr.getData());
         }
      }
      return roughArt;
   }
}
