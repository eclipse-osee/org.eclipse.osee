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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumerationValidation implements IOseeValidator {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.validation.IOseeValidator#getQualityOfService()
    */
   @Override
   public int getQualityOfService() {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.validation.IOseeValidator#isApplicable(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.skynet.core.attribute.AttributeType)
    */
   @Override
   public boolean isApplicable(Artifact artifact, AttributeType attributeType) {
      Class<?> baseClass = attributeType.getBaseAttributeClass();
      return EnumeratedAttribute.class.isAssignableFrom(baseClass);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.validation.IOseeValidator#validate(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.Object)
    */
   @Override
   public IStatus validate(Artifact artifact, AttributeType attributeType, Object proposedObject) throws OseeCoreException {
      String text = (String) proposedObject;
      OseeEnumType oseeEnumType = OseeEnumTypeManager.getType(attributeType.getOseeEnumTypeId());
      oseeEnumType.valueOf(text);

      return Status.OK_STATUS;
   }

}
