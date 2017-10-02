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
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XComboDam extends XCombo implements IAttributeWidget {

   protected Artifact artifact;
   protected AttributeTypeToken attributeType;

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   public XComboDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType)  {
      this.artifact = artifact;
      this.attributeType = attributeType;
      try {
         String value = artifact.getSoleAttributeValue(this.attributeType);
         super.set(value.toString());
      } catch (AttributeDoesNotExist ex) {
         super.set("");
      }
   }

   @Override
   public void saveToArtifact() {
      try {
         if (!Strings.isValid(data)) {
            artifact.deleteSoleAttribute(attributeType);
         } else {
            String enteredValue = get();
            artifact.setSoleAttributeValue(attributeType, enteredValue);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty()  {
      Result dirty = Result.FalseResult;
      if (isEditable()) {
         try {
            String enteredValue = get();
            String storedValue = artifact.getSoleAttributeValue(attributeType);
            if (!enteredValue.equals(storedValue)) {
               dirty = new Result(true, attributeType + " is dirty");
            }
         } catch (AttributeDoesNotExist ex) {
            if (!get().equals("")) {
               dirty = new Result(true, attributeType + " is dirty");
            }
         }
      }
      return dirty;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               status =
                  OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(), get());
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   @Override
   public void revert()  {
      setAttributeType(artifact, attributeType);
   }

}
