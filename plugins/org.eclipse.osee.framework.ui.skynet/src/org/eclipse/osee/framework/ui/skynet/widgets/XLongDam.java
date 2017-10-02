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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XLongDam extends XLong implements IAttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XLongDam(String displayLabel) {
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
   public void setAttributeType(Artifact artifact, AttributeTypeToken attrName)  {
      this.artifact = artifact;
      this.attributeType = attrName;
      try {
         Long value = artifact.getSoleAttributeValue(getAttributeType());
         super.set(value.toString());
      } catch (AttributeDoesNotExist ex) {
         super.set("");
      }
   }

   @Override
   public void saveToArtifact() {
      try {
         if (!Strings.isValid(text)) {
            getArtifact().deleteSoleAttribute(getAttributeType());
         } else {
            Long enteredValue = getLong();
            getArtifact().setSoleAttributeValue(getAttributeType(), enteredValue);
         }
      } catch (NumberFormatException ex) {
         // do nothing
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty()  {
      if (isEditable()) {
         try {
            Long enteredValue = getLong();
            Long storedValue = getArtifact().getSoleAttributeValue(getAttributeType());
            if (enteredValue.doubleValue() != storedValue.doubleValue()) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (AttributeDoesNotExist ex) {
            if (!get().equals("")) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (NumberFormatException ex) {
            // do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert()  {
      setAttributeType(getArtifact(), getAttributeType());
   }

}
