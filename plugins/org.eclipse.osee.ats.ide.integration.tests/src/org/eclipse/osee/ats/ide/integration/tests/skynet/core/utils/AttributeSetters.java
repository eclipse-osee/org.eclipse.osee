/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.Date;
import java.util.Map;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.MapEntryAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;

/**
 * A collection of {@link BiConsumer}s for setting attribute values.
 *
 * @author Loren K. Ashley
 */

public class AttributeSetters {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private AttributeSetters() {
   }

   /**
    * {@link BiConsumer} to set the value of a {@link DateAttribute}.
    *
    * @param attribute the {@link DateAttribute} to be set.
    * @param value the {@link Date} value to be set.
    */

   public static BiConsumer<Attribute<?>, Object> dateAttributeSetter =
      (attribute, value) -> ((DateAttribute) attribute).setValue((Date) value);

   /**
    * {@link BiConsumer} to set the value of a {@link StringAttribute}.
    *
    * @param attribute the {@link StringAttribute} to be set.
    * @param value the {@link String} value to be set.
    */

   public static BiConsumer<Attribute<?>, Object> stringAttributeSetter =
      (attribute, value) -> ((StringAttribute) attribute).setValue((String) value);

   /**
    * {@link BiConsumer} to set the value of a {@link MapEntryAttribute}.
    *
    * @param attribute the {@link MapEntryAttribute} to be set.
    * @param value the {@link Map.Entry} value to be set.
    */

   @SuppressWarnings("unchecked")
   public static BiConsumer<Attribute<?>, Object> mapEntryAttributeSetter =
      (attribute, value) -> ((MapEntryAttribute) attribute).setValue((Map.Entry<String, String>) value);
}

/* EOF */
