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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.io.InputStream;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Ryan D. Brooks
 */
@OseeAttribute("StringAttribute")
public class StringAttribute extends CharacterBackedAttribute<String> {
   public static final String NAME = StringAttribute.class.getSimpleName();

   @Override
   public String getValue() throws OseeCoreException {
      return getDataProxy().getValueAsString();
   }

   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      return getDataProxy().setValue(value);
   }

   @Override
   protected String convertStringToValue(String value) {
      return value;
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      String toReturn = null;
      InputStream inputStream = null;
      try {
         if (getAttributeType().equals(CoreAttributeTypes.WordTemplateContent)) {
            inputStream = new XmlTextInputStream(getValue());
            toReturn = Lib.inputStreamToString(inputStream);
         } else {
            toReturn = getValue();
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
      return toReturn;
   }
}
