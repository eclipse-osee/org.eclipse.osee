/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.api.column;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public interface IAtsColumnProvider {

   /**
    * Use this to automatically create columns base on AtsAttrValCol definition. USE THIS FIRST
    */
   default public Collection<AtsCoreAttrTokColumnToken> getAttrValCols() {
      return Collections.emptyList();
   }

   /**
    * Use this to dynamically create columns based on id. USE getAttrValCols first. Use this only if column must provide
    * code-backed retrieval of data.
    */
   default public Collection<AtsCoreCodeColumnToken> getColumns() {
      return null;
   }

   default public void getLegacyIdToId(Map<String, String> legacyIdToId) {
      // do nothing
   }

}
