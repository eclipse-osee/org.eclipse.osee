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

/**
 * Class to represent a Data Type Definition in the Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public class DataTypeDefinitionGroveThing extends AbstractGroveThing {

   /**
    * Creates a new {@link DataTypeDefinitionGroveThing} object with a unique identifier.
    */

   DataTypeDefinitionGroveThing() {
      super(IdentifierType.DATA_TYPE_DEFINITION.createIdentifier());
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * When assertions are enabled an assertion error will be thrown when the <code>nativeThing</code> is not an instance
    * of {@link NativeDataType}.
    */

   @Override
   public GroveThing setNativeThing(Object nativeThing) {
      assert nativeThing instanceof NativeDataType;
      return super.setNativeThing(nativeThing);
   }

}

/* EOF */
