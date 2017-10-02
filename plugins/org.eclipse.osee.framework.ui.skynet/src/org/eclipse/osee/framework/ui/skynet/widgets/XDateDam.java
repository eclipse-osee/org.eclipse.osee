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

import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XDateDam extends XDate implements IAttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XDateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken AttributeTypeId) {
      this.artifact = artifact;
      this.attributeType = AttributeTypeId;
      Date value = artifact.getSoleAttributeValue(getAttributeType(), null);
      if (value != null) {
         super.setDate(value);
      }
   }

   @Override
   public void saveToArtifact() {
      try {
         if (date == null) {
            getArtifact().deleteSoleAttribute(getAttributeType());
         } else {
            Date enteredValue = getDate();
            getArtifact().setSoleAttributeValue(getAttributeType(), enteredValue);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         Date enteredValue = getDate();
         Date storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), null);
         Result dirty = new Result(true, getAttributeType() + " is dirty");

         if (enteredValue == null) {
            return storedValue == null ? Result.FalseResult : dirty;
         } else {
            if (storedValue == null) {
               return dirty;
            }
            if (enteredValue.getTime() != storedValue.getTime()) {
               return dirty;
            }
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
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
}
