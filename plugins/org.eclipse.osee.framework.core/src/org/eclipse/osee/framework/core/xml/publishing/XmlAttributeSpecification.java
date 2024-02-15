/*********************************************************************
 * Copyright (c) 2024 Boeing
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

public interface XmlAttributeSpecification extends XmlNameSpecification {

   /**
    * Gets the attribute's name with namespace followed by an assignment to the <code>value</code>.
    *
    * @param value the value to be assigned to the attribute.
    * @return a {@link CharSequence} representing the attribute being assigned the <code>value</code>.
    * @throws NullPointerException when <code>value</code> is <code>null</code>.
    */

   public default @NonNull CharSequence getNameWithValue(@NonNull CharSequence value) {

      Objects.requireNonNull(value);

      var fullName = this.getFullname();
      var encodedValue = XmlEncoderDecoder.textToXml(value, XmlEncoderDecoder.DONT_DOUBLE_QUOTE);
      var size = fullName.length() + encodedValue.length() + 7;
      size = size + (size >> 2);
      //@formatter:off
      return
         new StringBuilder( size )
             .append( " " )
             .append( fullName )
             .append( "=\"" )
             .append( encodedValue )
             .append( "\"" );
      //@formatter:on
   }

}
