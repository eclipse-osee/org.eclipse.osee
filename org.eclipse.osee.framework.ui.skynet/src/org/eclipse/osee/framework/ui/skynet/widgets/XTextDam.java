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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
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
      super.set(artifact.getSoleAttributeValue(attributeTypeName, ""));
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
      String enteredValue = get();
      String storedValue = artifact.getSoleAttributeValue(attributeTypeName, "");
      if (!enteredValue.equals(storedValue)) {
         return new Result(true, attributeTypeName + " is dirty");
      }
      return Result.FalseResult;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, artifact, attributeTypeName, get());
      }
      return status;
   }

   @Override
   public void revert() throws OseeCoreException {
      setArtifact(artifact, attributeTypeName);
   }
}
