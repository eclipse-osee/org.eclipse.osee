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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataType;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataTypeKey;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Class to represent a Data Type Definition in the Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public final class DataTypeDefinitionGroveThing extends AbstractGroveThing {

   private final List<EnumValueGroveThing> enumValueGroveThings;

   /**
    * Creates a new {@link DataTypeDefinitionGroveThing} object with a unique identifier.
    */

   DataTypeDefinitionGroveThing(GroveThing parent) {
      super(IdentifierType.DATA_TYPE_DEFINITION.createIdentifier(), 1);

      this.enumValueGroveThings = new ArrayList<>();
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * When assertions are enabled an assertion error will be thrown when the <code>nativeThing</code> is not an instance
    * of {@link NativeDataType}.
    */

   @Override
   public boolean validateNativeThings(Object... nativeThings) {
      //@formatter:off
      return
            ParameterArray.validateNonNullAndSize(nativeThings, 1, 1)
         && (nativeThings[0] instanceof NativeDataTypeKey);
      //@formatter:on
   }

   @Override
   public Optional<Object[]> getNativeKeys() {
      return Optional.of(new Object[] {this.nativeThings[0]});
   }

   @Override
   public Object getNativeThing() {
      return this.nativeThings[0];
   }

   public Optional<AttributeTypeToken> getNativeAttributeTypeToken() {
      //@formatter:off
      return
         ( this.getNativeThing().equals(NativeDataType.ENUMERATED) )
            ? ((NativeDataTypeKey) this.nativeThings[0]).getNativeAttributeTypeToken()
            : Optional.empty();
      //@formatter:on
   }

   public GroveThing addEnumerationMember(GroveThing groveThing) {
      assert (groveThing instanceof EnumValueGroveThing);

      this.enumValueGroveThings.add((EnumValueGroveThing) groveThing);

      return groveThing;
   }

   public Stream<EnumValueGroveThing> streamEnumValueGroveThings() {
      return this.enumValueGroveThings.stream();
   }
}

/* EOF */
