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

package org.eclipse.osee.define.api.publishing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.define.api.publishing.templatemanager.RendererOptions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class used to specify attribute metadata publishing options in a publishing template {@link RendererOptions}. The
 * following JSON schema specifies the metadata section of the renderer options in a publishing template:
 *
 * <pre>
      "MetadataOptions":
      {
         "$id":     "#root/MetadataOptions",
         "title":   "Metadataoptions",
         "type":    "array",
         "default": [],
         "items":
         {
            "$id":        "#root/MetadataOptions/items",
            "title":      "Items",
            "type":       "object",
            "required":   [
                            "Type",
                            "Format",
                            "Label"
                          ],
            "properties":
            {
               "Type":
               {
                  "$id":     "#root/MetadataOptions/items/Type",
                  "title":   "Type",
                  "type":    "string",
                  "enum":    [ "Artifact Type", "Artifact Id", "Applicability" ]
               },
               "Format":
               {
                  "$id":     "#root/MetadataOptions/items/Format",
                  "title":   "Format",
                  "type":    "string",
                  "default": "",
                  "pattern": "^.*$"
               },
               "Label":
               {
                  "$id":     "#root/MetadataOptions/items/Label",
                  "title":   "Label",
                  "type":    "string",
                  "default": "",
                  "pattern": "^.*$"
               }
            }
         }
      }
 * </pre>
 *
 * The "Type" enumeration has the following supported functions:
 * <dl>
 * <dt>Artifact Type:</dt>
 * <dd>Adds a pseudo attribute to the Artifact's output with the name of the Artifact's type.</dd>
 * <dt>Artifact Id:</dt>
 * <dd>Adds a pseudo attribute to the Artifact's output with the OSEE identifier of the Artifact.</dd>
 * <dt>Applicability:</dt>
 * <dd>Adds a pseudo attribute to the Artifact's output with the views the Artifact is applicable to.</dd>
 * </dl>
 * The "Label" is a Word ML string used to wrap the pseudo attribute's name. If specified as a non-empty string, the
 * &quot;x&quot; in the label string character sequence &quot;&gt;x&lt;&quot; is replaced with the pseudo attribute's
 * name appended with &quot;: &quot;.
 * <p>
 * The "Format" is a Word ML string used to wrap the pseudo attribute's value. If specified as a non-empty string, the
 * &quot;x&quot; in the format string character sequence &quot;&gt;x&lt;&quot; is replaced with the pseudo attribute's
 * value.
 * <p>
 *
 * @author Dominic A. Guss
 * @author Loren K. Ashley
 */

public class MetadataOptions implements ToMessage {

   /**
    * The allowable values for the member {@link #type}.
    */

   //@formatter:off
   private static Set<String> types =
      Set.of
         (
            "Artifact Type",
            "Artifact Id",
            "Applicability"
         );
   //@formatter:on

   /**
    * Saves the Word ML format string.
    */

   @JsonProperty("Format")
   private String format;

   /**
    * Saves the Word ML label string.
    */

   @JsonProperty("Label")
   private String label;

   /**
    * Save the "Type" JSON enumeration specifying the pseudo attribute.
    */

   @JsonProperty("Type")
   private String type;

   /**
    * Creates a new empty {@link MetadataOptions} for JSON deserialization.
    */

   public MetadataOptions() {
      this.format = null;
      this.label = null;
      this.type = null;
   }

   /**
    * Creates a new {@link MetadataOptions} with data for JSON serialization.
    *
    * @param type the pseudo attribute type.
    * @param format the pseudo attribute value Word ML format string.
    * @param label the pseudo attribute name Word ML format string.
    * @throws NullPointerException when any of the parameters <code>type</code>, <code>format</code>, or
    * <code>label</code> are <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>type</code> is not one of the following strings:
    * <ul>
    * <li>"Artifact Type",</li>
    * <li>"Artifact Id", or</li>
    * <li>"Applicability"</li>
    * </ul>
    */

   public MetadataOptions(String type, String format, String label) {
      this.format = Objects.requireNonNull(format, "MetadataOptions::new, parameter \"format\" cannot be null.");
      this.label = Objects.requireNonNull(label, "MetadataOptions::new, parameter \"label\" cannot be null.");
      this.type = Objects.requireNonNull(type, "MetadataOptions::new, parameter \"type\" cannot be null.");

      if (!MetadataOptions.types.contains(this.type)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "MetadataOptions::new, parameter \"type\" has an invalid value." )
                             .indentInc()
                             .segment( "Specified type", this.type )
                             .segmentIndexedArray( "Allowed Values", MetadataOptions.types.toArray() )
                             .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Sets the format and label Word ML string to empty string, if they were not specified.
    */

   public void defaults() {
      if (Objects.isNull(this.format)) {
         this.format = "";
      }
      if (Objects.isNull(this.label)) {
         this.label = "";
      }
   }

   /**
    * Gets the pseudo attribute's Word ML format string.
    *
    * @return the Word ML format string.
    * @throws IllegalStateException when the member {@link #format} has not been set.
    */

   public String getFormat() {
      if (Objects.isNull(this.format)) {
         throw new IllegalStateException("MetadataOptions::getFormat, member \"format\" has not been set.");
      }
      return this.format;
   }

   /**
    * Gets the pseudo attribute's Word ML label string.
    *
    * @return the Word ML label string.
    * @throws IllegalStateException when the member {@link #label} has not been set.
    */

   public String getLabel() {
      if (Objects.isNull(this.label)) {
         throw new IllegalStateException("MetadataOptions::getLabel, member \"label\" has not been set.");
      }
      return this.label;
   }

   /**
    * Gets the pseudo attribute's type.
    *
    * @return the pseudo attribute type.
    * @throws IllegalStateException when the member {@link #type} has not been set.
    */

   public String getType() {
      if (Objects.isNull(this.type)) {
         throw new IllegalStateException("MetadataOptions::getType, member \"type\" has not been set.");
      }
      return this.type;
   }

   /**
    * Predicate to test the validity of the {@link MetadataOptions} object.
    *
    * @return <code>true</code>, when all members are non-<code>null</code>, and the member {@link #type} is a valid
    * pseudo attribute type name; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.format )
         && Objects.nonNull( this.label  )
         && Objects.nonNull( this.type   ) && MetadataOptions.types.contains( this.type );
      //@formatter:on
   }

   /**
    * Sets the Word ML format string. Used for deserialization.
    *
    * @param format the Word ML format string.
    * @throws NullPointerException when the parameter <code>format</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #format} has already been set.
    */

   public void setFormat(String format) {
      if (Objects.nonNull(this.format)) {
         throw new IllegalStateException("MetadataOptions::setFormat, member \"format\" has already been set.");
      }
      this.format = Objects.requireNonNull(format, "MetadataOptions::setFormat, parameter \"format\" cannot be null.");
   }

   /**
    * Sets the Word ML label string. Used for deserialization.
    *
    * @param label the Word ML label string.
    * @throws NullPointerException when the parameter <code>label</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #label} has already been set.
    */

   public void setLabel(String label) {
      if (Objects.nonNull(this.label)) {
         throw new IllegalStateException("MetadataOptions::setLabel, member \"label\" has already been set.");
      }
      this.label = Objects.requireNonNull(label, "MetadataOptions::setLabel, parameter \"label\" cannot be null.");
   }

   /**
    * Sets the pseudo attribute type. Used for deserialization.
    *
    * @param type the pseudo attribute type.
    * @throws NullPointerException when the parameter <code>label</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #label} has already been set.
    * @throws IllegalArgumentException when the parameter <code>type</code> is not one of the following strings:
    * <ul>
    * <li>"Artifact Type",</li>
    * <li>"Artifact Id", or</li>
    * <li>"Applicability"</li>
    * </ul>
    */

   public void setType(String type) {
      if (Objects.nonNull(this.type)) {
         throw new IllegalStateException("MetadataOptions::setType, member \"type\" has already been set.");
      }

      Objects.requireNonNull(type, "MetadataOptions::setType, parameter \"type\" cannot be null.");

      if (!MetadataOptions.types.contains(type)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "MetadataOptions::new, parameter \"type\" has an invalid value." )
                             .indentInc()
                             .segment( "Specified type", this.type )
                             .segmentIndexedArray( "Allowed Values", MetadataOptions.types.toArray() )
                             .toString()
                   );
         //@formatter:on
      }

      this.type = type;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "MetadataOptions" )
         .indentInc()
         .segment( "Type",   this.type   )
         .segment( "Format", this.format )
         .segment( "Label",  this.label  )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }
}

/* EOF */
