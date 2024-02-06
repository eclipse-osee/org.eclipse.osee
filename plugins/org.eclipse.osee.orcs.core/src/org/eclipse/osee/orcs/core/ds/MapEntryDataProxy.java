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

package org.eclipse.osee.orcs.core.ds;

import java.util.Map;

/**
 * A {@link DataProxy} interface for {@link MapEntryAttribute}s.
 *
 * @author Loren K. Ashley
 */

public interface MapEntryDataProxy extends DataProxy<Map.Entry<String, String>> {

   /**
    * Sets the data proxy value from a {@link Map.Entry}&lt;String,String&gt;.
    *
    * @param value the value to set.
    * @return <code>true</code> when the proxied value has been changed; otherwise, <code>false</code>.
    */

   boolean setValue(Map.Entry<String, String> value);
}

/* EOF */
