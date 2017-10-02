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

import java.lang.ref.WeakReference;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XTextDam extends XText implements IAttributeWidget {

   private Artifact artifactStrongRef;
   private AttributeTypeToken attributeType;
   private final boolean isWeakReference;
   private WeakReference<Artifact> artifactRef;

   public XTextDam(String displayLabel) {
      this(displayLabel, false);
   }

   public XTextDam(String displayLabel, boolean isWeakReference) {
      super(displayLabel);
      this.isWeakReference = isWeakReference;
   }

   @Override
   public Artifact getArtifact()  {
      Artifact toReturn = null;
      if (isWeakReference) {
         if (artifactRef.get() == null) {
            throw new OseeStateException("Artifact has been garbage collected");
         }
         toReturn = artifactRef.get();
      } else {
         toReturn = artifactStrongRef;
      }
      return toReturn;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   private void setArtifact(Artifact artifact) {
      if (isWeakReference) {
         this.artifactRef = new WeakReference<>(artifact);
      } else {
         artifactStrongRef = artifact;
      }
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType)  {
      this.attributeType = attributeType;
      setArtifact(artifact);
      onAttributeTypeSet();
   }

   public void onAttributeTypeSet()  {
      super.set(getArtifact().getSoleAttributeValue(getAttributeType(), ""));
   }

   @Override
   public void saveToArtifact()  {
      String value = get();
      if (!Strings.isValid(value)) {
         getArtifact().deleteSoleAttribute(getAttributeType());
      } else if (!value.equals(getArtifact().getSoleAttributeValue(getAttributeType(), ""))) {
         getArtifact().setSoleAttributeValue(getAttributeType(), value);
      }
   }

   @Override
   public Result isDirty()  {
      if (isEditable()) {
         String enteredValue = get();
         String storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), "");
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeType + " is dirty");
         }
      }
      return Result.FalseResult;
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
      setAttributeType(getArtifact(), getAttributeType());
   }
}
