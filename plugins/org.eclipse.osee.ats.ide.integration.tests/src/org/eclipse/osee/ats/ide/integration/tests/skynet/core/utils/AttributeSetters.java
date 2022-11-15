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

import java.util.function.BiConsumer;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
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
    * {@link BiConsumer} to set the value of a {@link StringAttribute}.
    *
    * @param attribute the {@link StringAttribute} to be set.
    * @param value the {@link String} vale to be set.
    */

   public static BiConsumer<Attribute<?>, Object> stringAttributeSetter =
      (attribute, value) -> ((StringAttribute) attribute).setValue((String) value);

}

/* EOF */
