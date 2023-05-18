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

package org.eclipse.osee.define.api.publishing.templatemanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class used to specify sub-document publishing options in a publishing template {@link RendererOptions}. The following
 * JSON schema specifies the nested templates section of the renderer options in a publishing template:
 *
 * <pre>
    "NestedTemplates":
    {
       "$id":     "#root/NestedTemplates",
       "title":   "NestedTemplates",
       "type":    "array",
       "default": [],
       "items":
       {
          "$id":      "#root/NestedTemplates/items"
          "title":    "Items",
          "type":     "object",
          "required": [
                        "OutlineType",
                        "SectionNumber",
                        "SubDocName",
                        "Key",
                        "Value"
                      ],
          "properties":
          {
            "OutlineType":
            {
               "$id":     "#root/NestedTemplates/items/OutlineType",
               "title":   "OutlineType",
               "type":    "string",
               "enum":    [ "", "APPENDIX" ]
               "default": ""
            },
            "SectionNumber":
            {
               "$id":     "#root/NestedTemplates/items/SectionNumber",
               "title":   "SectionNumber",
               "type":    "string",
               "pattern": "^.*$"
            },
            "SubDocName":
            {
               "$id":     "#root/NestedTemplates/items/SubDocName",
               "title":   "SubDocName",
               "type":    "string",
               "pattern": "^.*$"
            },
            "Key":
            {
               "$id":   "#root/NestedTemplates/items/Key",
               "title": "Key",
               "type":  "string",
               "enum":  [ "Id", "Name" ]
            },
            "Value":
            {
               "$id":     "#root/NestedTemplates/items/Value",
               "title":   "Value",
               "type":    "string",
               "pattern": "^.*$"
            },
          }
       }
    }
 * </pre>
 *
 * @author Loren K. Ashley
 */

public class NestedTemplates implements ToMessage {

   /**
    * The allowable values for the member {@link #key}.
    */

   //@formatter:off
   private final static Set<String> keys =
      Set.of
         (
            "Id",
            "Name"
         );
   //@formatter:on

   /**
    * The allowable values for the member {@link #outlineType}
    */

   //@formatter:off
   private final static Set<String> outlineTypes =
      Set.of
         (
            "",
            "APPENDIX"
         );
   //@formatter:on

   /**
    * Validates the provided value for the member {@link #key}.
    *
    * @param key the value to check.
    * @throws IllegalArgumentException when the parameter <code>key</code> is not one of the following strings:
    * <ul>
    * <li>"Id"</li>
    * <li>"Name"</li>
    * </ul>
    */

   private static void validateKey(String key) {
      if (!NestedTemplates.keys.contains(key)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "NestedTemplates::validateKey, parameter \"key\" has an invalid value." )
                             .indentInc()
                             .segment( "Specified Key", key )
                             .segmentIndexedArray( "Allowed Values", NestedTemplates.keys.toArray() )
                             .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Validates the provided value for the member {@link #outlineType}.
    *
    * @param outlineType the value to check.
    * @throws IllegalArgumentException when the parameter <code>outlineType</code> is not one of the following strings:
    * <ul>
    * <li>""</li>
    * <li>"APPENDIX"</li>
    * </ul>
    */

   private static void validateOutlineType(String outlineType) {
      if (!NestedTemplates.outlineTypes.contains(outlineType)) {
         //@formatter:off
         throw
         new IllegalArgumentException
                (
                   new Message()
                          .title( "NestedTemplates::validateOutlineType, parameter \"outlineType\" has an invalid value." )
                          .indentInc()
                          .segment( "Specified Outline Type", outlineType )
                          .segmentIndexedArray( "Allowed Values", NestedTemplates.outlineTypes.toArray() )
                          .toString()
                );
         //@formatter:on
      }
   }

   /**
    * Indicates if the nested template is to be looked up by identifier or name.
    */

   @JsonProperty("Key")
   private String key;

   /**
    * Saves the outline type.
    */

   @JsonProperty("OutlineType")
   private String outlineType;

   /**
    * Saves the section number for the nested template.
    */

   @JsonProperty("SectionNumber")
   private String sectionNumber;

   /**
    * Saves the nested or sub-document name.
    */

   @JsonProperty("SubDocName")
   private String subDocName;

   /**
    * Saves the nested template's identifier or name.
    */

   @JsonProperty("Value")
   private String value;

   /**
    * Creates a new empty {@link NestedTemplates} for JSON deserialization.
    */

   public NestedTemplates() {
      this.outlineType = null;
      this.sectionNumber = null;
      this.subDocName = null;
      this.key = null;
      this.value = null;
   }

   /**
    * Creates a new {@link NestedTemplates} with data for JSON serialization.
    *
    * @param outlineType the type of outline.
    * @param sectionNumber the section number for the nested template.
    * @param subDocName the nested document name.
    * @param key indicates if the nested template is to be looked up by identifier or name.
    * @param value the nested template's identifier or name.
    * @throws NullPointerException when any of the following parameters are <code>null</code>:
    * <ul>
    * <li><code>outlineType</code></li>
    * <li><code>sectionNumber</code></li>
    * <li><code>subDocName</code></li>
    * <li><code>key</code></li>
    * <li><code>value</code></li>
    * </ul>
    * @throws IllegalArgumentException when the parameter <code>key</code> is not one of the following strings:
    * <ul>
    * <li>"Id"</li>
    * <li>"Name"</li>
    * </ul>
    */

   public NestedTemplates(String outlineType, String sectionNumber, String subDocName, String key, String value) {

      Objects.requireNonNull(outlineType, "NestedTemplates::new, parameter \"outlineType\" cannot be null.");
      NestedTemplates.validateOutlineType(outlineType);
      this.outlineType = outlineType;

      this.sectionNumber =
         Objects.requireNonNull(sectionNumber, "NestedTemplates::new, parameter \"sectionNumber\" cannot be null.");

      this.subDocName =
         Objects.requireNonNull(subDocName, "NestedTemplates::new, parameter \"subDocName\" cannot be null.");

      Objects.requireNonNull(key, "NestedTemplates::new, parameter \"key\" cannot be null.");
      NestedTemplates.validateKey(key);
      this.key = key;

      this.value = Objects.requireNonNull(value, "NestedTemplates::new, parameter \"value\" cannot be null.");
   }

   /**
    * Sets the outlineType to an empty string, if it was not specified.
    */

   public void defaults() {
      if (Objects.isNull(this.outlineType)) {
         this.outlineType = "";
      }
   }

   /**
    * Gets the key for the nested template.
    *
    * @return "Id" when the nested publishing template is specified by identifier and "Name" when the nested publishing
    * template is specified by name.
    * @throws IllegalStateException when the member {@link #key} has not been set.
    */

   public String getKey() {
      if (Objects.isNull(this.key)) {
         throw new IllegalStateException("NestedTemplates::getKey, member \"key\" has not been set.");
      }
      return this.key;
   }

   /**
    * Gets the outline type for the nested template.
    *
    * @return the outline type.
    * @throws IllegalStateException when the member {@link #outlineType} has not been set.
    */

   public String getOutlineType() {
      if (Objects.isNull(this.outlineType)) {
         throw new IllegalStateException("NestedTemplates::getOutlineType, member \"outlineType\" has not been set.");
      }
      return this.outlineType;
   }

   /**
    * Gets the sectionNumber for the nested template.
    *
    * @return the section number.
    * @throws IllegalStateException when the member {@link #sectionNumber} has not been set.
    */

   public String getSectionNumber() {
      if (Objects.isNull(this.sectionNumber)) {
         throw new IllegalStateException(
            "NestedTemplates::getSectionNumber, member \"sectionNumber\" has not been set.");
      }
      return this.sectionNumber;
   }

   /**
    * Gets the sub-document name for the nested template.
    *
    * @return the sub-document name.
    * @throws IllegalStateException when the member {@link #subDocName} has not been set.
    */

   public String getSubDocName() {
      if (Objects.isNull(this.subDocName)) {
         throw new IllegalStateException("NestedTemplates::getSubDocName, member \"subDocName\" has not been set.");
      }
      return this.subDocName;
   }

   /**
    * Gets the nested publishing templates identifier or name. The member {@link #key} specifies whether the member
    * contains an identifier or name.
    *
    * @return the nested publishing template's name or identifier.
    * @throws IllegalStateException when the member {@link #value} has not been set.
    */

   public String getValue() {
      if (Objects.isNull(this.outlineType)) {
         throw new IllegalStateException("NestedTemplates::getValue, member \"value\" has not been set.");
      }
      return this.value;
   }

   /**
    * Predicate to test the validity of the {@link NestedTemplates} object.
    *
    * @return <code>true</code>, when all members are non-<code>null</code> and the members
    * {@link NestedTemplates#outlineType} and {@link #key} are valid values; otherwise <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.outlineType   ) && NestedTemplates.outlineTypes.contains( this.outlineType )
         && Objects.nonNull( this.sectionNumber )
         && Objects.nonNull( this.subDocName    )
         && Objects.nonNull( this.key           ) && NestedTemplates.keys.contains( this.key )
         && Objects.nonNull( this.value         );
      //@formatter:on
   }

   /**
    * Sets the key. The key specifies if the value contains an identifier or a name. Used for deserialization.
    *
    * @param key the string "Id" or "Name".
    * @throws NullPointerException when the parameter <code>key</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #key} has already been set.
    * @throws IllegalArgumentException when the parameter <code>key</code> is not one of the following strings:
    * <ul>
    * <li>"Id"</li>
    * <li>"Name"</li>
    * </ul>
    */

   public void setkey(String key) {
      if (Objects.nonNull(this.key)) {
         throw new IllegalStateException("NestedTemplates::setkey, member \"key\" has already been set.");
      }

      Objects.requireNonNull(key, "NestedTemplates::setkey, parameter \"key\" cannot be null.");

      NestedTemplates.validateKey(key);

      this.key = key;
   }

   /**
    * Sets the outline type. Used for deserialization.
    *
    * @param outlineType the outline type.
    * @throws NullPointerException when the parameter <code>outlineType</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #outlineType} has already been set.
    * @throws IllegalArgumentException when the parameter <code>outlineType</code> is not one of the following strings:
    * <ul>
    * <li>""</li>
    * <li>"APPENDIX"</li>
    * </ul>
    */

   public void setOutlineType(String outlineType) {
      if (Objects.nonNull(this.outlineType)) {
         throw new IllegalStateException(
            "NestedTemplates::setOutlineType, member \"outlineType\" has already been set.");
      }

      Objects.requireNonNull(outlineType, "NestedTemplates::setOutlineType, parameter \"outlineType\" cannot be null.");

      NestedTemplates.validateOutlineType(outlineType);

      this.outlineType = outlineType;
   }

   /**
    * Sets the section number for the nested publishing template. Used for deserialization.
    *
    * @param sectionNumber the section number for the nested publishing template.
    * @throws NullPointerException when the parameter <code>sectionNumber</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #sectionNumber} has already been set.
    */

   public void setSectionNumber(String sectionNumber) {
      if (Objects.nonNull(this.sectionNumber)) {
         throw new IllegalStateException(
            "NestedTemplates::setSectionNumber, member \"sectionNumber\" has already been set.");
      }
      this.sectionNumber = Objects.requireNonNull(sectionNumber,
         "NestedTemplates::setSectionNumber, parameter \"sectionNumber\" cannot be null.");
   }

   /**
    * Sets the sub-document name. Used for deserialization.
    *
    * @param subDocName the sub-document name for the nested publishing template.
    * @throws NullPointerException when the parameter <code>subDocName</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #subDocName} has already been set.
    * @throws IllegalArgumentException when the parameter <code>type</code> is not one of the following strings:
    * <ul>
    * <li>"Id"</li>
    * <li>"Name"</li>
    * </ul>
    */

   public void setSubDocName(String subDocName) {
      if (Objects.nonNull(this.subDocName)) {
         throw new IllegalStateException("NestedTemplates::setSubDocName, member \"subDocName\" has already been set.");
      }
      this.subDocName =
         Objects.requireNonNull(subDocName, "NestedTemplates::setSubDocName, parameter \"subDocName\" cannot be null.");
   }

   /**
    * Sets the identifier or name of the nested publishing template. Used for deserialization. When specifying an
    * identifier use the method {@link #setKey} with the value "Id". When specifying a name use the method
    * {@link #setKey} with the value "Name".
    *
    * @param value publishing template identifier or name.
    * @throws NullPointerException when the parameter <code>value</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #value} has already been set.
    */

   public void setValue(String value) {
      if (Objects.nonNull(this.value)) {
         throw new IllegalStateException("NestedTemplates::setvalue, member \"value\" has already been set.");
      }
      this.value = Objects.requireNonNull(value, "NestedTemplates::setvalue, parameter \"value\" cannot be null.");
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
         .title( "NestedTemplates" )
         .indentInc()
         .segment( "Outline Type",      this.outlineType   )
         .segment( "Section Number",    this.sectionNumber )
         .segment( "Sub-document Name", this.subDocName    )
         .segment( "Key",               this.key           )
         .segment( "Value",             this.value         )
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
