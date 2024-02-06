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
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * A basic implementation of the {@link AttributeSpecificationRecord} interface that will provide the necessary
 * information for the {@link TestDocumentBuilder} to verify or create a test artifact attribute.
 *
 * @author Loren K. Ashley
 */

public class BasicAttributeSpecificationRecord implements AttributeSpecificationRecord {

   /**
    * Saves the test attribute's type descriptor.
    */

   private final @NonNull AttributeTypeGeneric<?> attributeType;

   /**
    * Saves a {@link List} of the test attribute's values.
    */

   private final @NonNull List<Object> attributeValues;

   /**
    * Save a {@link BiConsumer} functional interface implementation for setting the value of the test attribute.
    */

   private final @NonNull BiConsumer<Attribute<?>, Object> attributeSetter;

   /**
    * Creates a new attribute specification record.
    *
    * @param attributeType the attribute's type descriptor.
    * @param attributeValues a {@link List} of the attribute's values.
    * @param attributeSetter a {@link BiConsumer} functional interface implementation for setting the value of the test
    * attribute.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public BasicAttributeSpecificationRecord(@NonNull AttributeTypeGeneric<?> attributeType, @NonNull List<Object> attributeValues, @NonNull BiConsumer<Attribute<?>, Object> attributeSetter) {

      //@formatter:off
      this.attributeType =
         Conditions.requireNonNull
            (
               attributeType,
               "BasicAttributeSpecificationRecord",
               "new",
               "attributeType"
            );
      //@formatter:on

      //@formatter:off
      this.attributeValues =
         Conditions.requireNonNull
            (
               attributeValues,
               "BasicAttributeSpecificationRecord",
               "new",
               "attributeValues"
            );
      //@formatter:on

      //@formatter:off
      this.attributeSetter =
         Conditions.requireNonNull
            (
               attributeSetter,
               "BasicAttributeSpecificationRecord",
               "new",
               "attributeSetter"
            );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull AttributeTypeGeneric<?> getAttributeType() {
      return this.attributeType;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull List<Object> getAttributeValues() {
      return this.attributeValues;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull BiConsumer<Attribute<?>, Object> getAttributeSetter() {
      return this.attributeSetter;
   }

}

/* EOF */
