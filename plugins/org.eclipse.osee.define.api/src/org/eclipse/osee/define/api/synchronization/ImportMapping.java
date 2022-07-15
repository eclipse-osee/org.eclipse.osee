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

package org.eclipse.osee.define.api.synchronization;

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class to encapsulate the association between a Synchronization Artifact Specification and an OSEE artifact.
 *
 * @author Loren K. Ashley
 */

public class ImportMapping implements ToMessage {

   /**
    * Identifier of the OSEE artifact for the root of the import.
    */

   private Root root;

   /**
    * Identifier of the Synchronization Artifact Specification.
    */

   private String specification;

   /**
    * Creates a new empty {@link ImportMapping} object for JSON deserialization.
    */

   public ImportMapping() {
      this.root = null;
      this.specification = null;
   }

   /**
    * Creates a new {@link ImportMapping} object with data for serialization (client) or for making a Synchronization
    * Artifact service call (server).
    *
    * @param root a {@link Root} object describing the OSEE artifact to be imported to.
    * @param specification the identifier of the Synchronization Artifact Specification to be imported to the OSEE
    * artifact specified by the parameter <code>root</code>.
    * @throws NullPointerException when either of the parameters <code>root</code> or <code>specification</code> are
    * <code>null</code>.
    */

   public ImportMapping(Root root, String specification) {
      this.root = Objects.requireNonNull(root, "ImportMapping::new, parameter \"root\" is null.");
      this.specification =
         Objects.requireNonNull(specification, "ImportMapping::new parameter \"specification\" is null.");
   }

   /**
    * Gets the {@link Root} specification for OSEE root artifact.
    *
    * @return the OSEE root artifact specification.
    * @throws IllegalStateException when an attempt is made to get the {@link #root} for an {@link ImportMapping} where
    * the OSEE root artifact has not been set.
    */

   public Root getRoot() {
      if (Objects.isNull(this.root)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportMapping::getRoot, the member \"root\" has not been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(0, message);
         throw new IllegalStateException(message.toString());
      }
      return this.root;
   }

   /**
    * Gets the identifier of the Synchronization Artifact Specification.
    *
    * @return the Synchronization Artifact Specification identifier.
    * @throws IllegalStateException when an attempt is made to get the {@link #specification} for an
    * {@link ImportMapping} where the Synchronization Artifact Specification identifier has not been set.
    */

   public String getSpecification() {
      if (Objects.isNull(this.specification)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportMapping::getSpecification, the member \"specification\" has not been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(0, message);
         throw new IllegalStateException(message.toString());
      }
      return specification;
   }

   /**
    * Predicate to test the validity of the {@link ImportMapping} object.
    *
    * @return <code>true</code>, when member {@link #specification} is non-<code>null</code>, member {@link #root} is
    * non-<code>null</code>, and the member {@link #root} is valid; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.specification )
         && Objects.nonNull( this.root )
         && this.root.isValid();
      //@formatter:on
   }

   /**
    * Set the OSEE root artifact. This method is provided for the deserialization of a JSON message into an
    * {@link ImportMapping} object.
    *
    * @param root the OSEE root artifact specification.
    * @throws NullPointerException when the parameter <code>root</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link root} of an {@link ImportMapping} object
    * that already has a non-<code>null</code> value.
    */

   public void setRoot(Root root) {
      if (Objects.nonNull(this.root)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportMapping::setRoot, the member \"root\" has alreday been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(0, message);
         throw new IllegalStateException(message.toString());
      }
      this.root = Objects.requireNonNull(root, "ImportMapping::new, parameter \"root\" is null.");
   }

   /**
    * Set the Synchronization Artifact Specification identifier. This method is provided for the deserialization of a
    * JSON message into an {@link ImportMapping} object.
    *
    * @param specification the Synchronization Artifact Specification identifier.
    * @throws NullPointerException when the parameter <code>specification</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link specification} of an {@link ImportMapping}
    * object that already has a non-<code>null</code> value.
    */

   public void setSpecification(String specification) {
      if (Objects.nonNull(this.specification)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportMapping::setSpecification, the member \"specification\" has already been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(0, message);
         throw new IllegalStateException(message.toString());
      }
      this.specification =
         Objects.requireNonNull(specification, "ImportMapping::new parameter \"specification\" is null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off
      outMessage
         .append( indent0 ).append( "ImportMapping:" ).append( "\n" )
         .append( indent1 ).append( "specification: " ).append( Objects.nonNull( this.specification ) ? this.specification : "(null)" ).append( "\n" )
         ;
      //@formatter:on

      if (Objects.nonNull(this.root)) {
         this.root.toMessage(2, outMessage);
      } else {
         //@formatter:off
         outMessage
            .append( indent1 ).append( "root:          ").append( "(null)" ).append( "\n" )
            ;
         //@formatter:on
      }

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
