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

package org.eclipse.osee.client.integration.tests.integration.skynet.core.utils;

import java.util.List;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * Instances of this interface are used to define test artifacts and the hierarchical structure of a test document.
 */

public interface BuilderRecord {

   /**
    * Gets the {@link ArtifactTypeToken} that specifies the test artifact's type.
    *
    * @return the test artifact's {@link ArtifactTypeToken}.
    */

   ArtifactTypeToken getArtifactTypeToken();

   /**
    * Gets a {@link BiConsumer} used to assign the attribute value to the test attribute. The first parameter is the
    * attribute as an {@link Attribute} and the second parameter is the value as an {@link Object}.
    *
    * @return {@link BiConsumer} for setting the test attribute value or values.
    */

   BiConsumer<Attribute<?>, Object> getAttributeSetter();

   /**
    * Gets the {@link BuilderRecord} identifier of this {@link BuilderRecord}'s test artifact's hierarchical parent.
    *
    * @return the {@link Integer} identifier of the hierarchical parent.
    */

   Integer getHierarchicalParentIdentifier();

   /**
    * Gets the {@link BuilderRecord} identifier for the test artifact.
    *
    * @return the assigned {@link Integer} identifier.
    */

   Integer getIdentifier();

   /**
    * Gets the test artifact's name.
    *
    * @return the test artifact name.
    */

   String getName();

   /**
    * Gets the test attribute's definition.
    *
    * @return the {@link AttributeTypeGeneric} that defines the test attribute.
    */

   AttributeTypeGeneric<?> getTestAttributeType();

   /**
    * Gets the list of values for the test attribute. Most attribute types have a single value. Enumerated attribute
    * types may have multiple values.
    */

   List<Object> getTestAttributeValues();

}

/* EOF */
