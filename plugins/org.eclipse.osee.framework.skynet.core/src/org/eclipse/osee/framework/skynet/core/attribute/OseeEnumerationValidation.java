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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
   public boolean isApplicable(Artifact artifact, AttributeTypeToken attributeType) {
      return attributeType.isEnumerated();
   }

   @Override
   public XResultData validate(Artifact artifact, AttributeTypeToken attributeType, Object proposedObject) {
      AttributeTypeEnum<?> enumType = (AttributeTypeEnum<?>) attributeType;

      if (proposedObject != null && enumType.isValidEnum(proposedObject.toString())) {
         return XResultData.OK_STATUS;
      }
      XResultData rd = new XResultData();
      rd.logStr(XResultData.Type.Severe, "The enumerated value [%s] is not valid for the attribute type [%s]",
         proposedObject.toString(), enumType);
      return rd;
   }
}