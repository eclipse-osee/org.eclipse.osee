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
package org.eclipse.osee.framework.skynet.core.attribute.utils;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.BlobWordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.CompressedContentAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.JavaObjectAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;

/**
 * @author Theron Virgin
 */
public class AttributeObjectConverter {

   @SuppressWarnings( {"unchecked"})
   public static Object stringToObject(Attribute attribute, String value) {
      return stringToObject(attribute.getAttributeType().getBaseAttributeClass(), value);
   }

   @SuppressWarnings( {"unchecked"})
   public static Object stringToObject(Class clas, String value) {

      if (clas.equals(BooleanAttribute.class)) {
         return new Boolean(value.equals(BooleanAttribute.booleanChoices[0]));
      }
      if (clas.equals(IntegerAttribute.class)) {
         if (value.equals("")) return new Integer(0);
         return new Integer(value);
      }
      if (clas.equals(DateAttribute.class)) {
         if (value.equals("")) return new Date(1);
         return new Date(Long.parseLong(value));
      }
      if (clas.equals(FloatingPointAttribute.class)) {
         if (value.equals("")) return new Double(0);
         return new Double(value);
      }
      if (clas.equals(EnumeratedAttribute.class)) {
         return value;
      }
      if (clas.equals(StringAttribute.class)) {
         return value;
      }
      if (clas.equals(BlobWordAttribute.class)) {
         return value;
      }
      if (clas.equals(JavaObjectAttribute.class)) {
         return value;
      }
      if (clas.equals(CompressedContentAttribute.class)) {
         return value;
      }

      SkynetActivator.getLogger().log(Level.SEVERE,
            "The Attribute Object Creator for the " + clas + " is not implemented yet");
      return value;
   }
}
