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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataType;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Class to represent an Attribute Definition in the Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public final class AttributeDefinitionGroveThing extends AbstractGroveThing {

   /**
    * Saves a reference to the {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} that this attribute
    * definition is for. Attribute definitions are unique to SpecificationGroveThing Types and Spec Object Types.
    */

   /**
    * Saves a reference to the {@link DataTypeDefinitionGroveThing} for the type of data attributes of this type
    * contain. The {@link DataTypeDefinitionGroveThing} objects are shared by all {@link AttributeDefinitionGroveThing}
    * objects for attributes with the same type of data.
    */

   DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing;

   /**
    * Creates a new {@link AttributeDefinitionGroveThing} object with an unique identifier.
    */

   AttributeDefinitionGroveThing(GroveThing parent) {
      super(IdentifierType.ATTRIBUTE_DEFINITION.createIdentifier(), 3, parent);
   }

   /**
    * Gets a reference to the {@link DataTypeDefinitionGroveThing} for the type of data this
    * {@link AttributeDefinitionGroveThing} is for. When assertions are enabled, an assertion error is thrown if the
    * {@link DataTypeDefinitionGroveThing} has not yet been set.
    *
    * @return a reference to the {@link DataTypeDefinitionGroveThing} for the type of data this
    * {@link AttributeDefinitionGroveThing} is for.
    */

   public DataTypeDefinitionGroveThing getDataTypeDefinition() {
      assert Objects.nonNull(this.dataTypeDefinitionGroveThing);

      return this.dataTypeDefinitionGroveThing;
   }

   /**
    * Gets the {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} object that this attribute definition is
    * for.
    *
    * @return the {@link CommonObjectTypeGroveThing} this attribute definition is for.
    */

   CommonObjectTypeGroveThing getParent() {
      return (CommonObjectTypeGroveThing) this.nativeThings[0];
   }

   /**
    * Saves the {@link DataTypeDefinitionGroveThing} representing the type of data this attribute definition is for.
    * When assertions are enabled, an assertion error is thrown if the data type definition has already been set.
    *
    * @param dataTypeDefinitionGroveThing {@link DataTypeDefinitionGroveThing} representing the type of data this
    * attribute definition is for.
    * @throws NullPointerException when the provided {@link DataTypeDefinitionGroveThing} is <code>null</code>.
    */

   public void setDataTypeDefinition(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing) {
      assert Objects.isNull(this.dataTypeDefinitionGroveThing);
      this.dataTypeDefinitionGroveThing = Objects.requireNonNull(dataTypeDefinitionGroveThing);
   }

   /**
    * {@inheritDoc}
    * <p>
    * When assertions are enabled an assertion error will be thrown when the <code>nativeThings</code> array does not
    * contain:
    * <dl>
    * <dt>index 0:</dt>
    * <dd>An instance of {@link NativeDataType}</dd>
    * <dt>index 1:</dt>
    * <dd>An instance of {@link ArtifactTypeToken}</dd>
    * <dt>index 2:</dt>
    * <dd>An instance of {@link AttributeTypeToken}</dd>
    * </dl>
    *
    * @param nativeThings The array of native objects to be associated with the {@link AttributeDefinitionGroveThing}.
    * @return <code>true</code> when the contents of the <code>nativeThings</code> array meets the requirements;
    * otherwise; <code>false</code>.
    */

   @Override
   public boolean validateNativeThings(Object... nativeThings) {
      //@formatter:off
      return
            ParameterArray.validateNonNullAndSize(nativeThings, 3, 3)
         && (nativeThings[0] instanceof IdentifierType )
         && (nativeThings[1] instanceof ArtifactTypeToken )
         && (nativeThings[2] instanceof AttributeTypeToken);
      //@formatter:on
   }

   /**
    * Save the {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} this attribute definition is for. When
    * assertions are enabled an assertion error is thrown if the <code>parent</code> parameter is <code>null</code>.
    *
    * @param parent the {@link CommonObjectTypeGroveThing} object this attribute definition is for.
    */

   //   void setParent(CommonObjectTypeGroveThing parent) {
   //      this.parent = parent;
   //   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * The generated message is for debugging, there is no contract for the message contents or structure.
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = super.toMessage(indent, message);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off

      outMessage
         .append( indent1 ).append( "DataTypeDefinitionGroveThing Key: " ).append( this.dataTypeDefinitionGroveThing != null ? this.dataTypeDefinitionGroveThing.getIdentifier() : "(null)" ).append( "\n" )
         ;
      //@formatter:on

      return outMessage;
   }

}

/* EOF */
