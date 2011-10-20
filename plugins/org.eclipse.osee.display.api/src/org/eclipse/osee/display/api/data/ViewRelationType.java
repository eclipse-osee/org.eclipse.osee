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

/*
 * @author Shawn Cook
 */
public enum ViewRelationType {
   PARENT("Default Hierarchical - parent");

   private final String stringValue;

   private ViewRelationType(String stringValue) {
      this.stringValue = stringValue;
   }

   public String getStringValue() {
      return stringValue;
   }
}
