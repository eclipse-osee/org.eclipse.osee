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

package org.eclipse.osee.framework.skynet.core.validation;

import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
      loadedObjects = new ExtensionDefinedObjects<>(EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE);
   }

   public static OseeValidator getInstance() {
      return instance;
   }

   public XResultData validate(int requiredQualityOfService, Artifact artifact, String attributeTypeName,
      Object proposedValue) {
      XResultData status = XResultData.OK_STATUS;
      try {
         AttributeTypeToken attributeType = AttributeTypeManager.getType(attributeTypeName);
         status = validate(requiredQualityOfService, artifact, attributeType, proposedValue);
      } catch (Exception ex) {
         status = XResultData.valueOf(XResultData.Type.Severe, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      }
      return status;
   }

   public XResultData validate(int requiredQualityOfService, Artifact artifact, AttributeTypeToken attributeType,
      Object proposedValue) {
      return validate(requiredQualityOfService, artifact, attributeType, proposedValue, new XResultData());
   }

   public XResultData validate(int requiredQualityOfService, Artifact artifact, AttributeTypeToken attributeType,
      Object proposedValue, XResultData rd) {
      if (artifact != null) {
         List<IOseeValidator> objects = loadedObjects.getObjects();
         for (IOseeValidator validator : objects) {
            if (requiredQualityOfService >= validator.getQualityOfService()) {
               try {
                  if (validator.isApplicable(artifact, attributeType)) {
                     try {
                        XResultData status = validator.validate(artifact, attributeType, proposedValue);
                        if (!status.isOK()) {
                           return status;
                        }
                     } catch (Exception ex) {
                        return XResultData.valueOf(XResultData.Type.Severe, Activator.PLUGIN_ID,
                           ex.getLocalizedMessage(), ex);
                     }
                  }
               } catch (Exception ex) {
                  return XResultData.valueOf(XResultData.Type.Severe, Activator.PLUGIN_ID, ex.getLocalizedMessage(),
                     ex);
               }
            }
         }
      }
      return XResultData.OK_STATUS;
   }

   public XResultData validate(int requiredQualityOfService, Artifact artifact, XResultData rd) {
      try {
         for (AttributeTypeToken attributeType : artifact.getAttributeTypes()) {
            for (Object value : artifact.getAttributeValues(attributeType)) {
               XResultData status = validate(requiredQualityOfService, artifact, attributeType, value, rd);
               if (!status.isOK()) {
                  return status;
               }
            }
         }
      } catch (Exception ex) {
         return XResultData.valueOf(XResultData.Type.Severe, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      }
      return XResultData.OK_STATUS;
   }

   public void clearCaches() {
      // for subclass implementation
   }
}