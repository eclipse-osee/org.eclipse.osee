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

package org.eclipse.osee.define.rest.synchronization.reqifsynchronizationartifactbuilder;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueEnumeration;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;

/**
 * This class contains various methods for {@link AttributeValue} foreign things.
 *
 * @author Loren K. Ashley
 */

public class AttributeValueUtils {

   /**
    * Gets the identifier of the {@link AttributeDefinition} referenced by the {@link AttributeValue} sub-class.
    *
    * @param attributeValue the foreign thing extending the {@link AttributeValue} class.
    * @return when the <code>attributeValue</code> implements a known sub-class of {@link AttributeValue}, an
    * {@link Optional} containing the identifier of the referenced {@link AttributeDefinition}; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getAttributeDefinitionIdentifier(AttributeValue attributeValue) {
      //@formatter:off
      var attributeDefinition =
         (attributeValue instanceof AttributeValueBoolean)
            ? ((AttributeValueBoolean) attributeValue).getDefinition()
            : (attributeValue instanceof AttributeValueDate)
                 ? ((AttributeValueDate) attributeValue).getDefinition()
                 : (attributeValue instanceof AttributeValueEnumeration)
                      ? ((AttributeValueEnumeration) attributeValue).getDefinition()
                      : (attributeValue instanceof AttributeValueInteger)
                           ? ((AttributeValueInteger) attributeValue).getDefinition()
                           : (attributeValue instanceof AttributeValueReal)
                                ? ((AttributeValueReal) attributeValue).getDefinition()
                                : (attributeValue instanceof AttributeValueString)
                                     ? ((AttributeValueString) attributeValue).getDefinition()
                                     : (attributeValue instanceof AttributeValueXHTML)
                                          ? ((AttributeValueXHTML) attributeValue).getDefinition()
                                          : null;

      return
         Objects.nonNull( attributeDefinition )
            ? Optional.ofNullable( attributeDefinition.getIdentifier() )
            : Optional.empty();
      //@formatter:on
   }
}

/* EOF */
