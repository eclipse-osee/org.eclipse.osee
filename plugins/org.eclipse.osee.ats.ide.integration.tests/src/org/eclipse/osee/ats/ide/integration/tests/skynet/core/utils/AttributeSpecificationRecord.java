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

import java.util.List;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * Lists of instances of this interface are used to define the attributes to be set for test attributes.
 *
 * @author Loren K. Ashley
 */

public interface AttributeSpecificationRecord {

   /**
    * Gets the type descriptor for the test attribute.
    *
    * @return the test attribute type descriptor.
    */

   AttributeTypeGeneric<?> getAttributeType();

   /**
    * Gets the attribute's values.
    *
    * @return a {@link List} of {@link Object}s to be assigned as the attribute's values.
    */

   List<Object> getAttributeValues();

   /**
    * Gets a {@link BiConsumer} functional interface implementation for setting the value of an attribute. The
    * {@link BiConsumer} first parameter is the {@link Attribute} to be set. The {@link BiConsumer} second parameter is
    * the attribute value to be set.
    *
    * @return the attribute setter functional interface implementation.
    */

   BiConsumer<Attribute<?>, Object> getAttributeSetter();
}

/* EOF */