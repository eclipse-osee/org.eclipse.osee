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

package org.eclipse.osee.synchronization.rest;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.synchronization.util.IndentedString;

/**
 * Class to represent an Attribute Definition in the Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public class AttributeDefinitionGroveThing extends AbstractGroveThing {

   /**
    * Saves a reference to the {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} that this attribute definition is for.
    * Attribute definitions are unique to SpecificationGroveThing Types and Spec Object Types.
    */

   CommonObjectTypeGroveThing parent;

   /**
    * Saves a reference to the {@link DataTypeDefinitionGroveThing} for the type of data attributes of this type contain. The
    * {@link DataTypeDefinitionGroveThing} objects are shared by all {@link AttributeDefinitionGroveThing} objects for attributes
    * with the same type of data.
    */

   DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing;

   /**
    * Creates a new {@link AttributeDefinitionGroveThing} object with an unique identifier.
    */

   AttributeDefinitionGroveThing() {
      super(IdentifierType.ATTRIBUTE_DEFINITION.createIdentifier());
   }

   /**
    * Gets a reference to the {@link DataTypeDefinitionGroveThing} for the type of data this {@link AttributeDefinitionGroveThing}
    * is for. When assertions are enabled, an assertion error is thrown if the {@link DataTypeDefinitionGroveThing} has not yet
    * been set.
    *
    * @return a reference to the {@link DataTypeDefinitionGroveThing} for the type of data this
    * {@link AttributeDefinitionGroveThing} is for.
    */

   public DataTypeDefinitionGroveThing getDataTypeDefinition() {
      assert Objects.nonNull(this.dataTypeDefinitionGroveThing);

      return this.dataTypeDefinitionGroveThing;
   }

   /**
    * Gets the {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} object that this attribute definition is for.
    *
    * @return the {@link CommonObjectTypeGroveThing} this attribute definition is for.
    */

   CommonObjectTypeGroveThing getParent() {
      return this.parent;
   }

   /**
    * Saves the {@link DataTypeDefinitionGroveThing} representing the type of data this attribute definition is for. When
    * assertions are enabled, an assertion error is thrown if the data type definition has already been set.
    *
    * @param dataTypeDefinitionGroveThing {@link DataTypeDefinitionGroveThing} representing the type of data this attribute definition is
    * for.
    * @throws NullPointerException when the provided {@link DataTypeDefinitionGroveThing} is <code>null</code>.
    */

   void setDataTypeDefinition(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing) {
      assert Objects.isNull(this.dataTypeDefinitionGroveThing);
      this.dataTypeDefinitionGroveThing = Objects.requireNonNull(dataTypeDefinitionGroveThing);
   }

   /**
    * {@inheritDoc}
    * <p>
    * When assertions are enabled an assertion error will be thrown when the <code>nativeThing</code> is not an instance
    * of {@link AttributeTypeToken}.
    */

   @Override
   public GroveThing setNativeThing(Object nativeThing) {
      assert nativeThing instanceof AttributeTypeToken;
      return super.setNativeThing(nativeThing);
   }

   /**
    * Save the {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} this attribute definition is for. When assertions are enabled
    * an assertion error is thrown if the <code>parent</code> parameter is <code>null</code>.
    *
    * @param parent the {@link CommonObjectTypeGroveThing} object this attribute definition is for.
    */

   void setParent(CommonObjectTypeGroveThing parent) {
      this.parent = parent;
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * The generated message is for debugging, there is no contract for the message contents or structure.
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .append( indent0 ).append( name ).append( "\n" )
         .append( indent1 ).append( "Parent Type:            " ).append( this.parent.getClass().getName() ).append( "\n" )
         .append( indent1 ).append( "Parent Key:             " ).append( this.parent.getGroveThingKey() ).append( "\n" )
         .append( indent1 ).append( "DataTypeDefinitionGroveThing Key: " ).append( this.dataTypeDefinitionGroveThing != null ? this.dataTypeDefinitionGroveThing.getGroveThingKey() : "(null)" ).append( "\n" )
         ;
      //@formatter:on

      return outMessage;
   }

}

/* EOF */
