/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.api.importing;

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author David W. Miller
 */
public abstract class ImportBlock implements Named {
   protected ArtifactTypeToken type = ArtifactTypeToken.SENTINEL;
   private Boolean complete = false;
   protected String baseDir;
   protected final LinkedList<BlockField> attrs = new LinkedList<>();
   protected final XResultData results;

   protected ImportBlock(XResultData results) {
      this.results = results;
   }

   public Boolean isParentBlock() {
      if (type.isValid() && type.equals(CoreArtifactTypes.HeadingMsWord)) {
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
      if (attr.getImportTypeName().equals(getCompleteMarker().getName())) {
         complete = true;
      }
   }

   public ArtifactTypeToken getType() {
      return type;
   }

   public abstract BlockFieldToken getCompleteMarker();

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

   public BlockField getImportAttr(BlockFieldToken token) {

      for (BlockField attr : attrs) {
         if (attr.getId().equals(token.getId())) {
            return attr;
         }
      }
      throw new OseeCoreException("Invalid Attribute requested: %s", token.getName());
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

   public void addRelationsToRoughArtifact(RoughArtifact roughArt) {
      if (!isComplete()) {
         throw new OseeCoreException("Imported Block not complete");
      }
      // none of the attributes collected at this level are needed for creating relations
   }

   public void addText(String text) {
      BlockField textAttr = null;
      for (BlockField attr : attrs) {
         if (attr.getId().equals(DoorsImportFieldTokens.blockAttrText.getId())) {
            textAttr = attr;
            break;
         }
      }
      if (textAttr == null) {
         textAttr = DoorsImportFieldTokens.blockAttrText.getNewParser();
         textAttr.appendContent(text, true);
         attrs.add(textAttr);
      } else {
         textAttr.appendContent(text, false);
      }
   }

   public void addAdditionalAttribute(BlockFieldToken token, String text) {
      BlockField addAttr = token.getNewParser();
      addAttr.setData(text);
      attrs.add(addAttr);
   }

   public void replaceReference(String objectID, String reference) {
      BlockField textAttr = null;
      for (BlockField attr : attrs) {
         if (attr.getId().equals(DoorsImportFieldTokens.blockAttrText.getId())) {
            textAttr = attr;
            break;
         }
      }
      if (textAttr == null) {
         System.out.println("textAttribute was null");
      } else {
         String newContent = textAttr.getData().replaceAll("\\[" + objectID + "\\]", reference);
         textAttr.setData(newContent);
      }
   }

   public void addAttribute(BlockFieldToken token, String text) {
      BlockField addAttr = null;
      for (BlockField attr : attrs) {
         if (attr.getId().equals(token.getId())) {
            addAttr = attr;
            break;
         }
      }
      if (addAttr == null) {
         addAttr = token.getNewParser();
         addAttr.setData(text);
         attrs.add(addAttr);
      } else {
         addAttr.setData(text);
      }
   }

   public void setBaseDir(String baseDir) {
      this.baseDir = baseDir;
   }
}
