/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api.data;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author John R. Misinco
 */
public class ViewId extends NamedIdentity<String> {

   Map<String, String> attributes = new HashMap<String, String>();

   public ViewId(String guid, String name) {
      super(guid, name);
   }

   public void setAttribute(String key, String value) {
      attributes.put(key, value);
   }

   public String getAttribute(String key) {
      return attributes.containsKey(key) ? attributes.get(key) : null;
   }
}
