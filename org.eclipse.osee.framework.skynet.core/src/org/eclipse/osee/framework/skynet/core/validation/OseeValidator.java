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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class OseeValidator {
   private static final String EXTENSION_ELEMENT = "OseeValidator";
   private static final String EXTENSION_ID = SkynetActivator.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";
   private static final String ATTRIBUTE_TYPENAME = "AttributeTypeName";
   private final static OseeValidator instance = new OseeValidator();

   private final HashCollection<AttributeType, IOseeValidator> loadedObjects =
         new HashCollection<AttributeType, IOseeValidator>();

   private OseeValidator() {
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
         status = new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      }
      return status;
   }

   public IStatus validate(int requiredQualityOfService, Artifact artifact, AttributeType attributeType, Object proposedValue) {
      checkExtensionsLoaded();
      MultiStatus result = new MultiStatus(SkynetActivator.PLUGIN_ID, IStatus.OK, "ok", null);

      Collection<IOseeValidator> validators = loadedObjects.getValues(attributeType);
      if (validators != null) {
         for (IOseeValidator validator : validators) {
            try {
               if (requiredQualityOfService >= validator.getQualityOfService()) {
                  result.add(validator.validate(artifact, proposedValue));
               }
            } catch (Exception ex) {
               result.add(new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, ex.getLocalizedMessage(), ex));
            }
         }
      }
      return result;
   }

   public IStatus validate(int requiredQualityOfService, Artifact artifact, Object proposedValue) {
      checkExtensionsLoaded();
      MultiStatus result = new MultiStatus(SkynetActivator.PLUGIN_ID, IStatus.OK, "ok", null);
      try {
         for (AttributeType attributeType : artifact.getAttributeTypes()) {
            String attributeTypeName = attributeType.getName();
            for (Attribute<?> attribute : artifact.getAttributes(attributeTypeName)) {
               result.add(validate(requiredQualityOfService, artifact, attributeType, (Object) attribute.getValue()));
            }
         }
      } catch (Exception ex) {
         result.add(new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, ex.getLocalizedMessage(), ex));
      }
      return result;
   }

   private void checkExtensionsLoaded() {
      if (loadedObjects.isEmpty()) {
         List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(EXTENSION_ID, EXTENSION_ELEMENT);
         for (IConfigurationElement element : elements) {
            // TODO Implement dynamic attribute type validation chain definition  
            //            IExtension extension = ((IExtension) element.getParent());
            //            String identifier = extension.getUniqueIdentifier();
            String attributeTypeName = element.getAttribute(ATTRIBUTE_TYPENAME);
            String className = element.getAttribute(CLASS_NAME_ATTRIBUTE);
            String bundleName = element.getContributor().getName();

            if (Strings.isValid(bundleName) && Strings.isValid(className)) {
               try {
                  Bundle bundle = Platform.getBundle(bundleName);
                  Class<?> taskClass = bundle.loadClass(className);
                  IOseeValidator object = null;
                  try {
                     Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
                     object = (IOseeValidator) getInstance.invoke(null, new Object[] {});
                  } catch (Exception ex) {
                     object = (IOseeValidator) taskClass.newInstance();
                  }
                  if (object != null) {
                     AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
                     loadedObjects.put(attributeType, object);
                  }
               } catch (Exception ex) {
                  OseeLog.log(OseeActivator.class, Level.SEVERE, String.format("Unable to Load: [%s - %s]", bundleName,
                        className), ex);
               }
            }
         }
      }
   }

   // TODO Implement dynamic attribute type validation chain definition  
   //   public List<IValidator> createValidateChain(String xml) {
   //      List<IValidator> validators = new ArrayList<IValidator>();
   //      return validators;
   //   }
}
