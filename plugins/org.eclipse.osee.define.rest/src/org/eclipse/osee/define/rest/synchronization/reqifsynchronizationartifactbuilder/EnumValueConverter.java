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

package org.eclipse.osee.define.rest.synchronization.reqifsynchronizationartifactbuilder;

import java.math.BigInteger;
import java.util.Objects;
import org.eclipse.osee.define.rest.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.rmf.reqif10.EmbeddedValue;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.ReqIF10Factory;

/**
 * Class contains the converter method to create the ReqIF {@link EnumValue} things from the native OSEE
 * {@link EnumToken} things. The {@link EnumValueGroveThing}s represent the allowable or defined values of an
 * enumeration.
 *
 * @author Loren K. Ashley
 */

class EnumValueConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private EnumValueConverter() {
   }

   /**
    * Converter method for {@link EnumValueGroveThing}s. This method creates the foreign ReqIF {@link EnumValue} with
    * it's {@link EmbeddedValue} from the native {@link EnumValueGroveThing}.
    *
    * @param groveThing the {@link EnumValueGroveThing} to be converted.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType( IdentifierType.ENUM_VALUE )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.ENUM_VALUE );
      //@formatter:on

      var nativeEnumToken = (EnumToken) groveThing.getNativeThing();

      var reqifEmbeddedValue = ReqIF10Factory.eINSTANCE.createEmbeddedValue();

      reqifEmbeddedValue.setOtherContent(nativeEnumToken.getName());
      reqifEmbeddedValue.setKey(BigInteger.valueOf(nativeEnumToken.getId()));

      var reqifEnumValue = ReqIF10Factory.eINSTANCE.createEnumValue();

      reqifEnumValue.setDesc("OSEE Enumeration Member Value");
      reqifEnumValue.setLongName(nativeEnumToken.getName());
      reqifEnumValue.setIdentifier(groveThing.getIdentifier().toString());
      reqifEnumValue.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifEnumValue.setProperties(reqifEmbeddedValue);

      groveThing.setForeignThing(reqifEnumValue);
   }

}

/* EOF */
