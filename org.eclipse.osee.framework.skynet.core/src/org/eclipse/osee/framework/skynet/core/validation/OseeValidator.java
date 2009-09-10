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
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
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
            new ExtensionDefinedObjects<IOseeValidator>(EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE);
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

   public IStatus validate(int requiredQualityOfService, Artifact artifact, AttributeType attributeType, Object proposedValue) {
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
               OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
            }
         }
      }
      return Status.OK_STATUS;
   }

   public IStatus validate(int requiredQualityOfService, Artifact artifact) {
      try {
         for (AttributeType attributeType : artifact.getAttributeTypes()) {
            String attributeTypeName = attributeType.getName();
            for (Attribute<?> attribute : artifact.getAttributes(attributeTypeName)) {
               IStatus status = validate(requiredQualityOfService, artifact, attributeType, attribute.getValue());
               if (!status.isOK()) {
                  return status;
                  //                  String messageToUse =
                  //                        String.format("%s:[%s] - %s", artifact.getArtifactTypeName(), artifact.getDescriptiveName(),
                  //                              status.getMessage());
                  //                  if (status.isMultiStatus()) {
                  //                     MultiStatus mStatus =
                  //                           new MultiStatus(status.getPlugin(), status.getCode(), messageToUse, status.getException());
                  //                     mStatus.merge(status);
                  //                     return mStatus;
                  //                  } else {
                  //                     return new Status(status.getSeverity(), status.getPlugin(), status.getCode(), messageToUse,
                  //                           status.getException());
                  //                  }
               }
            }
         }
      } catch (Exception ex) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      }
      return Status.OK_STATUS;
   }
   //   private void checkExtensionsLoaded() {
   //      if (loadedObjects.isEmpty()) {
   //         List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(EXTENSION_ID, EXTENSION_ELEMENT);
   //         for (IConfigurationElement element : elements) {
   //            // TODO Implement dynamic attribute type validation chain definition  
   //            //            IExtension extension = ((IExtension) element.getParent());
   //            //            String identifier = extension.getUniqueIdentifier();
   //            String attributeTypeName = element.getAttribute(ATTRIBUTE_TYPENAME);
   //            String className = element.getAttribute(CLASS_NAME_ATTRIBUTE);
   //            String bundleName = element.getContributor().getName();
   //
   //            if (Strings.isValid(bundleName) && Strings.isValid(className)) {
   //               try {
   //                  Bundle bundle = Platform.getBundle(bundleName);
   //                  Class<?> taskClass = bundle.loadClass(className);
   //                  IOseeValidator object = null;
   //                  try {
   //                     Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
   //                     object = (IOseeValidator) getInstance.invoke(null, new Object[] {});
   //                  } catch (Exception ex) {
   //                     object = (IOseeValidator) taskClass.newInstance();
   //                  }
   //                  if (object != null) {
   //                     AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
   //                     loadedObjects.put(attributeType, object);
   //                  }
   //               } catch (Exception ex) {
   //                  OseeLog.log(OseeActivator.class, Level.SEVERE, String.format("Unable to Load: [%s - %s]", bundleName,
   //                        className), ex);
   //               }
   //            }
   //         }
   //      }
   //   }

   // TODO Implement dynamic attribute type validation chain definition  
   //   public List<IValidator> createValidateChain(String xml) {
   //      List<IValidator> validators = new ArrayList<IValidator>();
   //      return validators;
   //   }
}
