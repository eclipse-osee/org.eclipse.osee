/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;

/**
 * @author Ryan D. Brooks
 */
public class ParagraphNumberAttributeValidation implements IOseeValidator {

   @Override
   public int getQualityOfService() {
      return 0;
   }

   @Override
   public boolean isApplicable(Artifact artifact, AttributeTypeId attributeType) {
      return attributeType.equals(CoreAttributeTypes.ParagraphNumber);
   }

   @Override
   public IStatus validate(Artifact artifact, AttributeTypeToken attributeType, Object proposedObject) {
      if (artifact.getSoleAttributeValueAsString(attributeType, "").matches(".*[a-zA-Z].*")) {
         return new Status(IStatus.ERROR, ParagraphNumberAttributeValidation.class.getName(),
            "Invalid Paragraph Number - letters are not allowed");
      }
      return Status.OK_STATUS;
   }

}
