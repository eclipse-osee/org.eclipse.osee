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
import java.util.Optional;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.IndentedString;

/**
 * This class provides a basic implementation of the {@link GroveThing} interface, to minimize the effort required to
 * implement this interface for specific Synchronization Artifact things.
 *
 * @author Loren K. Ashley
 */

public class AbstractGroveThing implements GroveThing {

   /**
    * Saves the unique identifier for the thing.
    */

   protected Identifier groveThingKey;

   /**
    * Saves a reference to the native thing associated with this {@link GroveThing}.
    */

   private Object nativeThing;

   /**
    * Saves a reference to the foreign thing associated with this {@link GroveThing}.
    */

   private Object foreignThing;

   /**
    * Creates a new {@link GroveThing} with the assigned {@link Identifier}.
    *
    * @param groveThingKey a unique {@link Identifier} for the {@link GroveThing}.
    */

   public AbstractGroveThing(Identifier groveThingKey) {

      assert Objects.nonNull(groveThingKey);

      this.groveThingKey = groveThingKey;
      this.foreignThing = null;
      this.nativeThing = null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object getForeignThing() {
      return this.foreignThing;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Identifier getGroveThingKey() {
      return this.groveThingKey;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Long> getNativeKey() {
      //@formatter:off
      return ( this.nativeThing instanceof Id)
                ? Optional.of( ((Id) this.nativeThing).getId() )
                : Optional.empty();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object getNativeThing() {
      return this.nativeThing;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setForeignThing(Object foreignThing) {
      assert Objects.nonNull(foreignThing);
      assert Objects.isNull(this.foreignThing);

      this.foreignThing = foreignThing;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public GroveThing setNativeThing(Object nativeThing) {
      assert Objects.nonNull(nativeThing);
      assert Objects.isNull(this.nativeThing);

      this.nativeThing = nativeThing;

      return this;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .append( indent0 ).append( name ).append( "\n" );
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
