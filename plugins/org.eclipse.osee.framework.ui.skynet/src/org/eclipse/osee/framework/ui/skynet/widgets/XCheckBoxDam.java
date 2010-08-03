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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxDam extends XCheckBox implements IAttributeWidget {

   private Artifact artifact;
   private IAttributeType attributeType;

   public XCheckBoxDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeType = attributeType;
      super.set(artifact.getSoleAttributeValue(this.attributeType, Boolean.FALSE));
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      artifact.setSoleAttributeValue(attributeType, checkButton.getSelection());
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      if (checkButton != null && !checkButton.isDisposed()) {
         Boolean enteredValue = checkButton.getSelection();
         Boolean storedValue = artifact.getSoleAttributeValue(attributeType, false);
         if (enteredValue.booleanValue() != storedValue.booleanValue()) {
            return new Result(true, attributeType + " is dirty");
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(artifact, attributeType);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, artifact, attributeType, get());
      }
      return status;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }
}
