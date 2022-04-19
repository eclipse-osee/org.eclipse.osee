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

package org.eclipse.osee.synchronization.rest.forest;

import java.util.Objects;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Class to represent an Attribute Value of a SpecificationGroveThing or Spec Object in the Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public final class AttributeValueGroveThing extends AbstractGroveThing {

   /**
    * Saves a reference to the {@link SpecificationGroveThing} or {@link SpecObjectGroveThing} that this attribute value
    * is for. Attribute values are unique to SpecificationGroveThing and Spec Objects.
    */

   CommonObjectGroveThing parent;

   /**
    * Saves a reference to the {@link AttributeDefinitionGroveThing} for the attribute that this type contains.
    */

   AttributeDefinitionGroveThing attributeDefinitionGroveThing;

   /**
    * Creates a new {@link AttributeValueGroveThing} object with a unique identifier.
    */

   AttributeValueGroveThing(GroveThing parent) {
      super(IdentifierType.ATTRIBUTE_VALUE.createIdentifier(), 1);
   }

   /**
    * Gets a reference to the {@link AttributeDefinitionGroveThing} for the attribute type of this
    * {@link AttributeValueGroveThing}. When assertions are enabled, an assertion error is thrown if the
    * {@link AttributeDefinitionGroveThing} has not yet been set.
    *
    * @return a reference to the {@link AttributeDefinitionGroveThing} for the attribute type of this
    * {@link AttributeValueGroveThing}.
    */

   public AttributeDefinitionGroveThing getAttributeDefinition() {
      assert Objects.nonNull(this.attributeDefinitionGroveThing);

      return this.attributeDefinitionGroveThing;
   }

   /**
    * Gets the {@link SpecificationGroveThing} or {@link SpecObjectGroveThing} thing that this attribute value is for.
    *
    * @return the {@link CommonObjectGroveThing} this attribute value is for.
    */

   public CommonObjectGroveThing getParent() {
      return this.parent;
   }

   /**
    * Saves the {@link AttributeDefinitionGroveThing} representing the attribute type this attribute value if for.
    *
    * @param attributeDefinitionGroveThing {@link AttributeDefinitionGroveThing} representing the attribute type this
    * attribute value is for.
    * @throws NullPointerException when the provided {@link AttributeDefinitionGroveThing} is <code>null</code>.
    */

   public void setAttributeDefinition(AttributeDefinitionGroveThing attributeDefinitionGroveThing) {
      this.attributeDefinitionGroveThing = Objects.requireNonNull(attributeDefinitionGroveThing);
   }

   /**
    * Saves a reference to the Synchronization Artifact SpecificationGroveThing or Spec Object this attribute value is
    * for. When assertions are enabled, an assertion error is thrown when the parent has already been set.
    *
    * @param parent the {@link SpecificationGroveThing} or {@link SpecObjectGroveThing} this attribute value is for.
    * @throws NullPointerException when the provided <code>parent</code> is <code>null</code>.
    */

   public void setParent(CommonObjectGroveThing parent) {
      assert Objects.isNull(this.parent);
      this.parent = Objects.requireNonNull(parent);
   }

   @Override
   public boolean validateNativeThings(Object... nativeThings) {
      //@formatter:off
      return
            ParameterArray.validateNonNullAndSize(nativeThings, 1, 1)
         ;//&& (nativeThings[0] instanceof ArtifactTypeToken);
      //@formatter:on
   }
}

/* EOF */
