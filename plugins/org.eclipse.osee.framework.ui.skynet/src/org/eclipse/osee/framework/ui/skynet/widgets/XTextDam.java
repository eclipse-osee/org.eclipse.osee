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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class XTextDam extends XText implements IAttributeWidget {

   private Artifact artifactStrongRef;
   private IAttributeType attributeType;
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
   public Artifact getArtifact() throws OseeCoreException {
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
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   private void setArtifact(Artifact artifact) {
      if (isWeakReference) {
         this.artifactRef = new WeakReference<Artifact>(artifact);
      } else {
         artifactStrongRef = artifact;
      }
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      this.attributeType = attributeType;
      setArtifact(artifact);
      onAttributeTypeSet();
   }

   public void onAttributeTypeSet() throws OseeCoreException {
      super.set(getArtifact().getSoleAttributeValue(getAttributeType(), ""));
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      String value = get();
      if (value == null || value.equals("")) {
         getArtifact().deleteSoleAttribute(getAttributeType());
      } else if (!value.equals(getArtifact().getSoleAttributeValue(getAttributeType(), ""))) {
         getArtifact().setSoleAttributeValue(getAttributeType(), value);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      String enteredValue = get();
      String storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), "");
      if (!enteredValue.equals(storedValue)) {
         return new Result(true, attributeType + " is dirty");
      }
      return Result.FalseResult;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            status =
               OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(), get());
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(getArtifact(), getAttributeType());
   }
}
