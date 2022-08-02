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

package org.eclipse.osee.define.rest.synchronization;

import java.util.Objects;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierType;

/**
 * Basic implementation of the abstract class {@link ForeignThingFamily} for providing a foreign thing, its identifier,
 * and it's hierarchical parent identifiers to the {@link SynchronizationArtifact}.
 *
 * @author Loren K. Ashley
 */

public class SimpleForeignThingFamily extends ForeignThingFamily {

   /**
    * The foreign thing.
    */

   final Object child;

   /**
    * An array containing the foreign identifiers as {@link String}s of the foreign things hierarchical parents and the
    * foreign identifier as a {@link String} of the foreign thing. The foreign identifier of the most senior parent is
    * in array index 0. The foreign identifier of the foreign thing is in the highest order element of the array.
    */

   final String[] stringForeignIdentifiers;

   /**
    * An array of the same size as the <code>stringForeignIdentifiers</code> array. Each element in the array contains
    * the {@link IdentifierType} associated with the foreign identifier in the corresponding element of the
    * <code>stringForeignIdentifiers</code> array.
    */

   final IdentifierType[] keyIdentifierTypes;

   /**
    * Creates a new {@link SimpleForeginThingFamily} with the provided foreign thing, foreign thing identifier and
    * parent foreign thing identifiers.
    *
    * @param child the foreign thing.
    * @param stringForeignIdentifiers an array containing the foreign identifiers as {@link String}s of the foreign
    * things hierarchical parents and the foreign identifier as a {@link String} of the foreign thing. The foreign
    * identifier of the most senior parent is in array index 0. The foreign identifier of the foreign thing is in the
    * highest order element of the array.
    * @param keyIdentifierTypes an array of the same size as the <code>stringForeignIdentifiers</code> array. Each
    * element in the array contains the {@link IdentifierType} associated with the foreign identifier in the
    * corresponding element of the <code>stringForeignIdentifiers</code> array.
    */
   public SimpleForeignThingFamily(Object child, String[] stringForeignIdentifiers, IdentifierType[] keyIdentifierTypes) {

      //@formatter:off
      assert
           Objects.nonNull(child)
         : "SimpleForeignThingFamily::new, parameter \"child\" is null.";

      assert
           Objects.nonNull( stringForeignIdentifiers )
         : "SimpleForeignThingFamily::new, parameter \"primaryKeys\" is null.";

      assert
           Objects.nonNull( keyIdentifierTypes )
         : "SimpleForeignThingFamily::new, parameter \"keyIdentifierTypes\" is null.";

      assert
           ( stringForeignIdentifiers.length == keyIdentifierTypes.length )
         : "SimpleForeignThingFamily::new, array parameters \"primaryKeys\" and \"keyIdentifierTypes\" are not the same size.";

      assert
           ( stringForeignIdentifiers.length >= 1 )
         : "SimpleForeignThingFamily::new, array parameters \"primayKeys\" and \"keyIdentifierTypes\" must contain atleast one element.";
      //@formatter:on

      this.child = child;
      this.stringForeignIdentifiers = stringForeignIdentifiers;
      this.keyIdentifierTypes = keyIdentifierTypes;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object getChild() {
      return this.child;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String[] getForeignIdentifiersAsStrings() {
      return this.stringForeignIdentifiers;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType[] getIdentifierTypes() {
      return this.keyIdentifierTypes;
   }

}

/* EOF */