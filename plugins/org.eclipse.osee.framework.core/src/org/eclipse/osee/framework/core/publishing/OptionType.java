/*********************************************************************
 * Copyright (c) 2017 Boeing
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

import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactReadableDeserializer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchIdDeserializer;
import org.eclipse.osee.framework.core.renderer.RenderLocation;
import org.eclipse.osee.framework.core.renderer.RenderLocationDeserializer;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.IdDeserializer;

/**
 * Enumeration used with {@link RendererOption} to specify the type of value associated with each
 * {@link RendererOption}.
 *
 * @author Morgan E. Cook
 * @author Loren K. Ashley
 */

public enum OptionType {

//@formatter:off
     AllowedOutlineTypes
        (
           AllowedOutlineTypes.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     Artifact
        (
           ArtifactReadable.class,
           IsCollection.NO,
           CanCopy.NO,
           new ArtifactReadableDeserializer(),
           NO_DEFAULT_VALUE()
        ),

     ArtifactId
        (
           ArtifactId.class,
           IsCollection.NO,
           CanCopy.YES,
           new IdDeserializer<>
                  (
                     org.eclipse.osee.framework.core.data.ArtifactId.class,
                     org.eclipse.osee.framework.core.data.ArtifactId::valueOf
                  ),
           org.eclipse.osee.framework.core.data.ArtifactId.SENTINEL
        ),

     ArtifactTypes
        (
           ArtifactTypeToken.class,
           IsCollection.YES,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     ArtifactTypeToken
        (
           ArtifactTypeToken.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     AttributeTypeToken
        (
           AttributeTypeToken.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     Boolean
        (
           Boolean.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           false
        ),

     BranchId
        (
           BranchId.class,
           IsCollection.NO,
           CanCopy.YES,
           new BranchIdDeserializer(),
           org.eclipse.osee.framework.core.data.BranchId.SENTINEL
        ),

     FilenameFormat
        (
           FilenameFormat.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           org.eclipse.osee.framework.core.publishing.FilenameFormat.PREVIEW
        ),

     FormatIndicator
        (
           FormatIndicator.class,
           IsCollection.NO,
           CanCopy.YES,
           new FormatIndicatorDeserializer(),
           org.eclipse.osee.framework.core.publishing.FormatIndicator.WORD_ML
        ),

     IncludeHeadings
        (
           IncludeHeadings.class,
           IsCollection.NO,
           CanCopy.YES,
           new IncludeHeadingsDeserializer(),
           NO_DEFAULT_VALUE()
        ),

     IncludeMainContentForHeadings
        (
           IncludeMainContentForHeadings.class,
           IsCollection.NO,
           CanCopy.YES,
           new IncludeMainContentForHeadingsDeserializer(),
           NO_DEFAULT_VALUE()
        ),

     IncludeMetadataAttributes
        (
           IncludeMetadataAttributes.class,
           IsCollection.NO,
           CanCopy.YES,
           new IncludeMetadataAttributesDeserializer(),
           NO_DEFAULT_VALUE()
        ),

     Integer
        (
           Integer.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     LinkType
        (
           LinkType.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     OutputStream
        (
           OutputStream.class,
           IsCollection.NO,
           CanCopy.NO,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     Path
        (
           Path.class,
           IsCollection.NO,
           CanCopy.NO,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     ProgressMonitor
        (
           null,
           IsCollection.NO,
           CanCopy.NO,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     RenderLocation
        (
           RenderLocation.class,
           IsCollection.NO,
           CanCopy.YES,
           new RenderLocationDeserializer(),
           NO_DEFAULT_VALUE()
        ),

     String
        (
           String.class,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        ),

     Transaction
        (
           null,
           IsCollection.NO,
           CanCopy.YES,
           NO_DESERIALIZER(),
           NO_DEFAULT_VALUE()
        );
   //@formatter:on

   /**
    * An enumeration to indicate if an option type value can be copied.
    */

   private enum CanCopy {
      NO,
      YES;

      boolean isNo() {
         return this == NO;
      }

      boolean isYes() {
         return this == YES;
      }
   }

   /**
    * An enumeration to indicate if an option type value is a collection.
    */

   private enum IsCollection {
      NO,
      YES;

      boolean isNo() {
         return this == NO;
      }

      boolean isYes() {
         return this == YES;
      }
   }

   /**
    * Provides a named <code>null</code> for enumeration member initialization.
    *
    * @return <code>null</code>.
    */

   private static Object NO_DEFAULT_VALUE() {
      return null;
   }

   /**
    * Provides a named <code>null</code> for enumeration member initialization.
    *
    * @return <code>null</code>.
    */

   private static JsonDeserializer<?> NO_DESERIALIZER() {
      return null;
   }

   /**
    * Flag to indicate that {@link RendererOption}s of the {@link OptionType} and their associated values can be copied
    * from one {@link RendererMap} to another.
    */

   private final boolean canCopy;

   /**
    * Saves the default value for the {@link OptionType}.
    */

   private final Object defaultValue;

   /**
    * Saves the expected implementation class for objects being associated with a {@link RendererOption} of the
    * {@link OptionType}.
    */

   private final Class<?> implementationClass;

   /**
    * Flag to indicate the objects being associated with a {@link RendererOption} of the {@link OptionType} type are
    * {@link Collection}s or scalar values.
    */

   private final boolean isCollection;

   /**
    * If a custom {@link JsonDeserializer} is needed for the option type it is saved in this member.
    */

   private final JsonDeserializer<?> jsonDeserializer;

   /**
    * Creates a new {@link OptionType} member with the specified <code>defaultValue</code>.
    *
    * @param implementationClass the expected {@link Class} of objects being associated with a {@link RendererOption}.
    * @param isCollection flag to indicate the value is expected to be a {@link Collection} of the expected class.
    * @param canCopy flag to indicate the value can be copied from one map to another.
    * @param jsonDeserializer the {@link JsonDeserializer} needed for the option type value or <code>null</code> if a
    * custom deserializer is not needed.
    * @param defaultValue the default value for the {@link OptionType} member.
    */

   private OptionType(Class<?> implementationClass, IsCollection isCollection, CanCopy canCopy, JsonDeserializer<?> jsonDeserializer, Object defaultValue) {
      this.implementationClass = implementationClass;
      this.isCollection = isCollection.isYes();
      this.canCopy = canCopy.isYes();
      this.jsonDeserializer = jsonDeserializer;
      this.defaultValue = defaultValue;
   }

   /**
    * Flag to indicate if {@link RendererOption}s of the {@link OptionType} can be copied from one {@link RendererMap}
    * to another.
    *
    * @return the can copy flag.
    */

   public boolean canCopy() {
      return this.canCopy;
   }

   /**
    * Gets the default value to be used when a map of {@link RendererOption} does not contain an entry for the specified
    * key.
    *
    * @return the default value for the {@link OptionType}.
    */

   public Object getDefaultValue() {
      return this.defaultValue;
   }

   /**
    * Gets the expected implementation class of object associated with {@link RendererOption}s of the
    * {@link OptionType}.
    *
    * @return the expected implementation class.
    */

   public Class<?> getImplementationClass() {
      return this.implementationClass;
   }

   /**
    * Gets the {@link JsonDeserializer} for the {@link OptionType}.
    *
    * @return when a custom deserializer is needed for the {@link OptionType} the {@link JsonDeserializer}; otherwise,
    * <code>null</code>.
    */

   public JsonDeserializer<?> getJsonDeserializer() {
      return this.jsonDeserializer;
   }

   /**
    * Flag to indicate the values of {@link RenderOption}s of the {@link OptionType} are expected to be
    * {@link Collection}s of the expected implementation class or a scalar value.
    *
    * @return <code>true</code>, when the value is expected to be a {@link Collection}; otherwise, <code>false</code>.
    */

   public boolean isCollection() {
      return this.isCollection;
   }
}

/* EOF */
