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

package org.eclipse.osee.framework.skynet.core.attribute.providers;

import java.util.Map;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * A {@link ICharacterAttributeDataProvider} implementation for attributes with {@link Map.Entry}&lt;String,String&gt;
 * values.
 *
 * @author Loren K. Ashley
 */

public class MapEntryAttributeDataProvider extends DefaultAttributeDataProvider<Map.Entry<String, String>> {

   /**
    * Creates a new {@link MapEntryAttributeDataProvider} for an {@link Attribute}.
    *
    * @param attribute the {@link Attribute} to create the data provider for.
    */

   @SuppressWarnings("unchecked")
   public MapEntryAttributeDataProvider(Attribute<?> attribute) {
      super((Attribute<Map.Entry<String, String>>) attribute);
   }

   /**
    * Serializes a {@link Map.Entry}&lt;String,String&gt; implementation into a JSON string.
    *
    * @param mapEntry the {@link Map.Entry} implementation to be serialized.
    * @return a JSON string representing the provided <code>mapEntry</code>.
    * @throws OseeCoreException when the JSON encoding fails.
    */

   @Override
   protected String rawValueToString(Map.Entry<String, String> mapEntry) {
      return MapEntryAttributeUtil.jsonEncode(mapEntry);
   }

}

/* EOF */
