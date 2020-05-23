/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class ViewModel {

   private final String viewId;
   private final Map<String, Object> model = new LinkedHashMap<>();

   public ViewModel(String viewId) {
      this.viewId = viewId;
   }

   public String getViewId() {
      return viewId;
   }

   public Map<String, Object> asMap() {
      return model;
   }

   public ViewModel param(String key, Object value) {
      model.put(key, value);
      return this;
   }

}
