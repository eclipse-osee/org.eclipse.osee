/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxDam extends XCheckBox implements AttributeWidget {

   public static String WIDGET_ID = XCheckBoxDam.class.getSimpleName();
   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XCheckBoxDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void refresh() {
      super.set(artifact.getSoleAttributeValue(this.attributeType, Boolean.FALSE));
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      refresh();
   }

   @Override
   public void saveToArtifact() {
      artifact.setSoleAttributeValue(attributeType, checkButton.getSelection());
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         if (checkButton != null && !checkButton.isDisposed()) {
            Boolean enteredValue = checkButton.getSelection();
            Boolean storedValue = artifact.getSoleAttributeValue(attributeType, false);
            if (enteredValue.booleanValue() != storedValue.booleanValue()) {
               return new Result(true, attributeType + " is dirty");
            }
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      setAttributeType(artifact, attributeType);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         XResultData rd =
            OseeValidator.getInstance().validate(IOseeValidator.SHORT, artifact, attributeType, isChecked());
         if (rd.isErrors()) {
            status = new Status(IStatus.ERROR, getClass().getSimpleName(), rd.toString());
         }
      }
      return status;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }
}
