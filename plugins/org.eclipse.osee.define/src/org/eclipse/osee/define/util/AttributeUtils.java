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

package org.eclipse.osee.define.util;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * A class of static utility methods for working with attributes.
 *
 * @author Loren K. Ashley
 */

public class AttributeUtils {

   /**
    * Gets a {@link List} of the {@link String} values for the attribute specified by <code>attributeTypeToken</code>
    * from the <code>artifactReadable</code>.
    *
    * @param artifactReadable the {@link ArtifactReadable} to get the {@link String} attribute values from.
    * @param attributeTypeToken the {@link AttributeTypeToken} for the {@link String} attribute.
    * @return a {@link List} of the attribute's {@link String} values.
    * @implNote The generic method {@link ArtifactReadable#getAttributeValues} does not have a parameter of the generic
    * return type. In some contexts the compiler is unable to determine the methods return type. This method is a blind
    * cast of the return type from <code>List&lt;Object&gt;</code> to <code>List&lt;String&gt;</code>.
    */

   @SuppressWarnings("unchecked")
   public static List<String> getStringAttributeValues(ArtifactReadable artifactReadable, AttributeTypeToken attributeTypeToken) {
      return (List<String>) (Object) artifactReadable.getAttributeValues(attributeTypeToken);
   }

   /**
    * Gets a {@link List} of the {@link Map.Entry}&lt;String,String&gt; values for the attribute specified by
    * <code>attributeTypeToken</code> from the <code>artifactReadable</code>.
    *
    * @param artifactReadable the {@link ArtifactReadable} to get the {@link Map.Entry} attribute values from.
    * @param attributeTypeToken the {@link AttributeTypeToken} for the {@link String} attribute.
    * @return a {@link List} of the attribute's {@link String} values.
    * @implNote The generic method {@link ArtifactReadable#getAttributeValues} does not have a parameter of the generic
    * return type. In some contexts the compiler is unable to determine the methods return type. This method is a blind
    * cast of the return type from <code>List&lt;Object&gt;</code> to
    * <code>List&lt;Map.Entry&lt;String,String&gt;&gt;</code>.
    */

   @SuppressWarnings("unchecked")
   public static List<Map.Entry<String, String>> getMapEntryAttributeValues(ArtifactReadable artifactReadable, AttributeTypeToken attributeTypeToken) {
      return (List<Map.Entry<String, String>>) (Object) artifactReadable.getAttributeValues(attributeTypeToken);
   }
}
/* EOF */
