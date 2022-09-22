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
import java.util.Objects;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * A basic implementation of the {@link AttributeSpecificationRecord} interface for test attribute specifications.
 *
 * @author Loren K. Ashley
 */

public class BasicAttributeSpecification implements AttributeSpecificationRecord {

   /**
    * Saves the test attribute's type descriptor.
    */

   private final AttributeTypeGeneric<?> attributeType;

   /**
    * Saves a {@link List} of the test attribute's values.
    */

   private final List<Object> attributeValues;

   /**
    * Save a {@link BiConsumer} functional interface implementation for setting the value of the test attribute.
    */

   private final BiConsumer<Attribute<?>, Object> attributeSetter;

   /**
    * Creates a new attribute specification record.
    *
    * @param attributeType the attribute's type descriptor.
    * @param attributeValues a {@link List} of the attribute's values.
    * @param attributeSetter a {@link BiConsumer} functional interface implementation for setting the value of the test
    * attribute.
    */

   public BasicAttributeSpecification(AttributeTypeGeneric<?> attributeType, List<Object> attributeValues, BiConsumer<Attribute<?>, Object> attributeSetter) {
      this.attributeType = Objects.requireNonNull(attributeType);
      this.attributeValues = Objects.requireNonNull(attributeValues);
      this.attributeSetter = Objects.requireNonNull(attributeSetter);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public AttributeTypeGeneric<?> getAttributeType() {
      return this.attributeType;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<Object> getAttributeValues() {
      return this.attributeValues;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public BiConsumer<Attribute<?>, Object> getAttributeSetter() {
      return this.attributeSetter;
   }

}

/* EOF */
