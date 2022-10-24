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

package org.eclipse.osee.define.operations.synchronization;

import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.Specification;

/**
 * @imlNote To create a {@link Stream} of {@link ForeignThingFamily} objects for a {@link Specification} and all of its
 * children {@link SpecObject}s, the following code can be used:
 *
 * <pre>
 * var specificationAndSpecObjectStream =
 *    Stream.concat(Stream.of(new SpecObjectSliderForeignThingFamily(specification)),
 *       SpecObjectSliderForeignThingFamily.start(specification).flatMap(SpecObjectSliderForeignThingFamily::flatten));
 * </pre>
 *
 * @author Loren K. Ashley
 */

public class SpecObjectSliderForeignThingFamily extends ForeignThingFamily {

   /**
    * The foreign thing.
    */

   final SpecElementWithAttributes child;

   /**
    * The foreign {@link SpecHierarchy} object of the encapsulated <code>child</code>.
    */

   final SpecHierarchy specHierarchy;

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

   final IdentifierType[] identifierTypes;

   /**
    * Creates a slider for a foreign {@link Specification}.
    *
    * @param specification the {@link Specification}.
    */

   public SpecObjectSliderForeignThingFamily(Specification specification) {

      this.specHierarchy = null;
      this.child = specification;

      //@formatter:off
      this.stringForeignIdentifiers =
         new String[]
         {
            specification.getIdentifier()
         };

      this.identifierTypes =
         new IdentifierType[]
         {
            IdentifierType.SPECIFICATION
         };
      //@formatter:on
   }

   /**
    * Constructor for sliders made for the foreign {@link SpecHierarchy} things that are immediate children of the
    * foreign {@link Specification}.
    *
    * @param specHierarchy a foreign child {@link SpecHierarchy} thing of the foreign {@link Specification}.
    * @param specification the {@link Specification}.
    */

   private SpecObjectSliderForeignThingFamily(SpecHierarchy specHierarchy, Specification specification) {

      this.specHierarchy = specHierarchy;
      this.child = specHierarchy.getObject();

      //@formatter:off
      this.stringForeignIdentifiers =
         new String[]
         {
            specification.getIdentifier(),
            specification.getIdentifier(),
            specHierarchy.getObject().getIdentifier()
         };

      this.identifierTypes =
         new IdentifierType[]
         {
            IdentifierType.SPECIFICATION,
            IdentifierType.SPECIFICATION,
            IdentifierType.SPEC_OBJECT
         };
      //@formatter:on
   }

   /**
    * Constructor for sliders made for the foreign {@link SpecHierarchy} things that are immediate children of the
    * foreign {@link SpecHierarchy} thing encapsulated in the provided <code>parentSlider</code>.
    *
    * @param specHierarchy a foreign child {@link SpecHierarchy} thing of the foreign {@link SpecHierarchy} encapsulated
    * in the provided <code>parentSlider</code>.
    * @param parentSlider the {@link SpecObjectSliderForeignThingFamily} that encapsulates the foreign
    * {@link SpecHierarchy} thing that is the hierarchical parent.
    */

   private SpecObjectSliderForeignThingFamily(SpecHierarchy specHierarchy, SpecObjectSliderForeignThingFamily parentSlider) {

      this.specHierarchy = specHierarchy;
      this.child = specHierarchy.getObject();

      //@formatter:off
      this.stringForeignIdentifiers =
         new String[]
         {
            parentSlider.getSpecificationIdentifier(),
            parentSlider.getChildIdentifier(),
            specHierarchy.getObject().getIdentifier()
         };

      this.identifierTypes =
         new IdentifierType[]
         {
            IdentifierType.SPECIFICATION,
            IdentifierType.SPEC_OBJECT,
            IdentifierType.SPEC_OBJECT
         };
      //@formatter:on
   }

   /**
    * Creates a stream of {@link SpecObjectSliderForeignThingFamily} objects created from the specified foreign
    * Specification's hierarchical children.
    *
    * @param specification the foreign {@link Specification}.
    * @return a stream of {@link SpecObjectSliderForeignThingFamily} objects created from the specified foreign
    * Specification's hierarchical children.
    */

   public static Stream<SpecObjectSliderForeignThingFamily> start(Specification specification) {
      //@formatter:off
      return
         specification.isSetChildren()
            ? specification.getChildren().stream()
                 .map( (specHierarchy) -> new SpecObjectSliderForeignThingFamily( specHierarchy, specification) )
            : Stream.empty();
      //@formatter:on
   }

   /**
    * Creates a stream of {@link SpecObjectSliderForeignThingFamily} objects created from the children of the
    * encapsulated {@link SpecHierarchy} and recursively calls {@link #flatten} for each created
    * {@link SpecObjectSliderForeignThingFamily}.
    *
    * @return a {@link Stream} of {@link SpecObjectSliderForeignThingFamily} objects created for the hierarchical
    * children of the encapsulated {@link SpecHierarchy} object.
    */

   public Stream<SpecObjectSliderForeignThingFamily> flatten() {
      //@formatter:off
      return
         Stream.concat
            (
               Stream.of(this),
               this.specHierarchy.getChildren().stream()
                  .map
                     (
                       (specHierarchy) ->

                          new SpecObjectSliderForeignThingFamily( specHierarchy, this )
                     )
                  .flatMap( SpecObjectSliderForeignThingFamily::flatten )
            );
      //@formatter:on
   }

   /**
    * Gets the string representation of the foreign identifier of the foreign Specification that contains the foreign
    * Spec Object that is the child. If this {@link ForeignThingFamily} represents a foreign Specification, this method
    * gets the string representation of the foreign Specification's identifier.
    *
    * @return the string foreign identifier of the containing foreign Specification.
    */

   public String getSpecificationIdentifier() {
      return this.stringForeignIdentifiers[0];
   }

   /**
    * Gets the string representation of the child foreign thing's identifier.
    *
    * @return the child foreign thing's identifier.
    */

   public String getChildIdentifier() {
      return this.stringForeignIdentifiers[2];
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
      return this.identifierTypes;
   }

}

/* EOF */