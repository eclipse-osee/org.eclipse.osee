/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
