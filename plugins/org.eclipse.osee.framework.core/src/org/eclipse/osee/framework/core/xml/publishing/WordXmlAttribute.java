/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.xml.publishing;

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;

/**
 * Enumeration of Word ML attribute names. The attributes are assigned to Word ML tags by the enumeration
 * {@link WordXmlTag}.
 *
 * @author Loren K. Ashley
 */

public enum WordXmlAttribute {

   /**
    * Destination attribute name.
    * <dl>
    * <dt>Namespace:</dt>
    * <dd>w</dd>
    * <dt>Name:</dt>
    * <dd>dest</dd>
    * </dl>
    */

   DESTINATION("w:dest"),

   /**
    * Field Character Type attribute name.
    * <dl>
    * <dt>Namespace:</dt>
    * <dd>w</dd>
    * <dt>Name:</dt>
    * <dd>fldCharType</dd>
    * </dl>
    */

   FIELD_CHARACTER_TYPE("w:fldCharType"),

   /**
    * Value attribute name.
    * <dl>
    * <dt>Namespace:</dt>
    * <dd>w</dd>
    * <dt>Name:</dt>
    * <dd>val</dd>
    * </dl>
    */

   VALUE("w:val"),

   /**
    * Value attribute name.
    * <dl>
    * <dt>Namespace:</dt>
    * <dd>xml</dd>
    * <dt>Name:</dt>
    * <dd>space</dd>
    * </dl>
    */

   XML_SPACE("xml:space");

   /**
    * Saves the Word ML attribute name.
    */

   private final @NonNull String attributeName;

   /**
    * Creates a new member with the specified <code>attributeName</code>.
    *
    * @param attributeName the Word ML attribute name with namespace.
    * @throws NullPointerException when <code>attributeName</code> is <code>null</code>.
    */

   private WordXmlAttribute(@NonNull String attributeName) {
      this.attributeName = Objects.requireNonNull(attributeName);
   }

   /**
    * Gets the Word ML attribute name.
    *
    * @return the attribute name.
    */

   public @NonNull String getName() {
      return this.attributeName;
   }

   /**
    * Gets the attribute's name with namespace followed by an assignment to the <code>value</code>.
    *
    * @param value the value to be assigned to the attribute.
    * @return a {@link CharSequence} representing the attribute being assigned the <code>value</code>.
    * @throws NullPointerException when <code>value</code> is <code>null</code>.
    */

   public @NonNull CharSequence getNameWithValue(@NonNull CharSequence value) {

      Objects.requireNonNull(value);

      var encodedValue = XmlEncoderDecoder.textToXml(value, XmlEncoderDecoder.DONT_DOUBLE_QUOTE);
      //@formatter:off
      return
         new StringBuilder( this.attributeName.length() + encodedValue.length() + 5 )
            .append( " " )
            .append( this.attributeName )
            .append( "=\"" )
            .append( encodedValue )
            .append( "\" " );
      //@formatter:on
   }
}

/* EOF */