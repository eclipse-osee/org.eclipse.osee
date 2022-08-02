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
import org.eclipse.rmf.reqif10.AttributeDefinitionBoolean;
import org.eclipse.rmf.reqif10.AttributeDefinitionDate;
import org.eclipse.rmf.reqif10.AttributeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.AttributeDefinitionInteger;
import org.eclipse.rmf.reqif10.AttributeDefinitionReal;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeDefinitionXHTML;
import org.eclipse.rmf.reqif10.DatatypeDefinition;

/**
 * This class contains various methods for {@link AttributeDefinition} foreign things.
 *
 * @author Loren K. Ashley
 */

public class AttributeDefinitionUtils {

   /**
    * Gets the identifier of the {@link DatatypeDefinition} referenced by the {@link AttributeDefinition} sub-class.
    *
    * @param attributeDefinition the foreign thing extending the {@link AttributeDefinition} class.
    * @return when the <code>attributeDefinition</code> implements a known sub-class of {@link AttributeDefinition}, an
    * {@link Optional} containing the identifier of the referenced {@link DatatypeDefinition}; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getDatatypeDefinitionIdentifier(AttributeDefinition attributeDefinition) {
      //@formatter:off
      var datatypeDefinition =
         (attributeDefinition instanceof AttributeDefinitionBoolean)
            ? ((AttributeDefinitionBoolean) attributeDefinition).getType()
            : (attributeDefinition instanceof AttributeDefinitionDate)
                 ? ((AttributeDefinitionDate) attributeDefinition).getType()
                 : (attributeDefinition instanceof AttributeDefinitionEnumeration)
                      ? ((AttributeDefinitionEnumeration) attributeDefinition).getType()
                      : (attributeDefinition instanceof AttributeDefinitionInteger)
                           ? ((AttributeDefinitionInteger) attributeDefinition).getType()
                           : (attributeDefinition instanceof AttributeDefinitionReal)
                                ? ((AttributeDefinitionReal) attributeDefinition).getType()
                                : (attributeDefinition instanceof AttributeDefinitionString)
                                     ? ((AttributeDefinitionString) attributeDefinition).getType()
                                     : (attributeDefinition instanceof AttributeDefinitionXHTML)
                                          ? ((AttributeDefinitionXHTML) attributeDefinition).getType()
                                          : null;

      return
         Objects.nonNull( datatypeDefinition )
            ? Optional.ofNullable( datatypeDefinition.getIdentifier() )
            : Optional.empty();
      //@formatter:on
   }
}

/* EOF */
