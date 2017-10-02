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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumerationValidation implements IOseeValidator {

   @Override
   public int getQualityOfService() {
      return SHORT;
   }

   @Override
   public boolean isApplicable(Artifact artifact, AttributeTypeId attributeType)  {
      return AttributeTypeManager.getType(attributeType).isEnumerated();
   }

   @Override
   public IStatus validate(Artifact artifact, AttributeTypeToken attributeType, Object proposedObject)  {
      String text = (String) proposedObject;
      AttributeType type = AttributeTypeManager.getType(attributeType);
      OseeEnumType enumType = type.getOseeEnumType();
      enumType.valueOf(text);
      return Status.OK_STATUS;
   }

}
