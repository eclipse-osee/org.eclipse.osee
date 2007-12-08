/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;

/**
 * @author Robert A. Fisher
 */
public class FromArtifactsSearchAttribute extends StringAttribute implements ISearchAttribute<FromArtifactsSearch> {
   /**
    * @param name
    */
   public FromArtifactsSearchAttribute(String name) {
      super(name);
   }

   /**
    * @param name
    * @param value
    */
   public FromArtifactsSearchAttribute(String name, String value) {
      super(name);
      setStringData(value);
   }

   /**
    * @param value
    */
   public void setSearchPrimitive(FromArtifactsSearch value) {
      setStringData(value.getStorageString());
   }

   public FromArtifactsSearch getSearchPrimitive() {
      return FromArtifactsSearch.getPrimitive(getStringData());
   }
}
