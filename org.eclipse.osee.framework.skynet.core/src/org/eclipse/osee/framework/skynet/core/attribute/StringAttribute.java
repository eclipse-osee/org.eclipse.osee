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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

/**
 * @author Ryan D. Brooks
 */
public class StringAttribute extends CharacterBackedAttribute<String> {

   public StringAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public String getValue() throws OseeDataStoreException {
      return getAttributeDataProvider().getValueAsString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#subClassSetValue(java.lang.Object)
    */
   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      return getAttributeDataProvider().setValue(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#convertStringToValue(java.lang.String)
    */
   @Override
   protected String convertStringToValue(String value) throws OseeCoreException {
      return value;
   }
}
