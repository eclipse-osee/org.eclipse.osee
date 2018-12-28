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
package org.eclipse.osee.framework.skynet.core.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeValidator {
   private static final String EXTENSION_ELEMENT = "OseeValidator";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";
   private final static OseeValidator instance = new OseeValidator();

   private final ExtensionDefinedObjects<IOseeValidator> loadedObjects;

   private OseeValidator() {
      loadedObjects =
         new ExtensionDefinedObjects<>(EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE);
   }

   public static OseeValidator getInstance() {
      return instance;
   }

   public IStatus validate(int requiredQualityOfService, Artifact artifact, String attributeTypeName, Object proposedValue) {
      IStatus status = Status.OK_STATUS;
      try {
         AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
         status = validate(requiredQualityOfService, artifact, attributeType, proposedValue);
      } catch (Exception ex) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      }
      return status;
   }

   public IStatus validate(int requiredQualityOfService, Artifact artifact, AttributeTypeToken attributeType, Object proposedValue) {
      if (artifact != null) {
         for (IOseeValidator validator : loadedObjects.getObjects()) {
            if (requiredQualityOfService >= validator.getQualityOfService()) {
               try {
                  if (validator.isApplicable(artifact, attributeType)) {
                     try {
                        IStatus status = validator.validate(artifact, attributeType, proposedValue);
                        if (!status.isOK()) {
                           return status;
                        }
                     } catch (Exception ex) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
                     }
                  }
               } catch (Exception ex) {
                  return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
               }
            }
         }
      }
      return Status.OK_STATUS;
   }

   public IStatus validate(int requiredQualityOfService, Artifact artifact) {
      try {
         for (AttributeTypeToken attributeType : artifact.getAttributeTypes()) {
            for (Object value : artifact.getAttributeValues(attributeType)) {
               IStatus status = validate(requiredQualityOfService, artifact, attributeType, value);
               if (!status.isOK()) {
                  return status;
               }
            }
         }
      } catch (Exception ex) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      }
      return Status.OK_STATUS;
   }
}