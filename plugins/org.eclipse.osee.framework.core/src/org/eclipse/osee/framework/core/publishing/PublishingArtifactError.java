/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.HasArtifactType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;

/**
 * @author Branden W. Phillips
 */
public class PublishingArtifactError {
   private final List<Object> things;
   private final String errorDescription;

   public <T extends Id> PublishingArtifactError(T thing, String errorDescription) {
      this.things = List.of(thing);
      this.errorDescription = errorDescription;
   }

   public <T extends Id> PublishingArtifactError(List<T> things, String errorDescription) {
      this.things = List.of(things);
      this.errorDescription = errorDescription;
   }

   public void publish(StringBuilder stringBuilder) {

      //@formatter:off
      Objects.requireNonNull
         (
            stringBuilder,
            "PublishingArtifactError::publish, parameter \"stringBuilder\" cannot be null."
         );

      var indent1 = IndentedString.indentString(1);
      var indent2 = IndentedString.indentString(2);

      stringBuilder
         .append( indent1 ).append( "Error: " ).append( "\n" )
         .append( indent2 ).append( this.errorDescription ).append( "\n" );

      for( var thing : this.things ) {

         var identifier       = ((Id) thing).getId().toString();
         var name             = ( thing instanceof NamedId )
                                   ? ((NamedId) thing).getName()
                                   : "(no name information)";
         var artifactTypeName = ( thing instanceof HasArtifactType )
                                   ? ((HasArtifactType) thing).getArtifactType().getName()
                                   : "(no type information)";

         stringBuilder
            .append( indent1 ).append( "Artifact:" ).append( "\n" )
            .append( indent2 ).append( "Identifier: " ).append( identifier       ).append( "\n" )
            .append( indent2 ).append( "Name:       " ).append( name             ).append( "\n" )
            .append( indent2 ).append( "Type:       " ).append( artifactTypeName ).append( "\n" );
      }
      //@formatter:on
   }

   public void publish(PublishingAppender WordMLProducer) {
      //@formatter:off
      Objects.requireNonNull
         (
            WordMLProducer,
            "PublishingArtifactError::publish, parameter \"WordMLProducer\" cannot be null."
         );

      var thingIdentifiers =
         this.things
            .stream()
            .map( ( thing ) -> ((Id) thing).getId().toString() )
            .collect( Collectors.joining("\n") );

      var thingNames =
         this.things
            .stream()
            .map
               (
                  ( thing ) ->
                     ( thing instanceof NamedId )
                        ? ((NamedId) thing).getName()
                        : "(no name information)"
               )
            .collect( Collectors.joining("\n") );

      var thingArtifactTypes =
         this.things
            .stream()
            .map
               (
                  ( thing ) ->
                     ( thing instanceof HasArtifactType )
                        ? ((HasArtifactType) thing).getArtifactType().getName()
                        : "(no type information)"
               )
            .collect( Collectors.joining( "\n" ) );

      WordMLProducer.addErrorRow
         (
            thingIdentifiers,
            thingNames,
            thingArtifactTypes,
            this.errorDescription
         );
      //@formatter:on
   }
}

/* EOF */