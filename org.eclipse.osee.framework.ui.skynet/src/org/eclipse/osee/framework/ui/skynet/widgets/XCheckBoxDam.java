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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxDam extends XCheckBox implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XCheckBoxDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeTypeName = attrName;
      super.set(artifact.getSoleAttributeValue(attributeTypeName, Boolean.FALSE));
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      artifact.setSoleAttributeValue(attributeTypeName, checkButton.getSelection());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException {
      if (checkButton != null && !checkButton.isDisposed()) {
         Boolean enteredValue = checkButton.getSelection();
         Boolean storedValue = artifact.getSoleAttributeValue(attributeTypeName, false);
         if (enteredValue.booleanValue() != storedValue.booleanValue()) {
            return new Result(true, attributeTypeName + " is dirty");
         }
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#isValid()
    */
   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, artifact, attributeTypeName, get());
      }
      return status;
   }
}
