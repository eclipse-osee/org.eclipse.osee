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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.db.connection.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class XTextDam extends XText implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XTextDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attributeTypeName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeTypeName = attributeTypeName;

      try {
         String value = artifact.getSoleAttributeValue(attributeTypeName);
         super.set(value);
      } catch (AttributeDoesNotExist ex) {
         super.set("");
      }
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      String value = get();
      if (value == null || value.equals("")) {
         artifact.deleteSoleAttribute(attributeTypeName);
      } else if (!value.equals(artifact.getSoleAttributeValue(attributeTypeName, ""))) {
         artifact.setSoleAttributeValue(attributeTypeName, value);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      try {
         String enteredValue = get();
         String storedValue = artifact.getSoleAttributeValue(attributeTypeName);
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (AttributeDoesNotExist ex) {
         if (!get().equals("")) return new Result(true, attributeTypeName + " is dirty");
      }
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws OseeCoreException {
      setArtifact(artifact, attributeTypeName);
   }
}
