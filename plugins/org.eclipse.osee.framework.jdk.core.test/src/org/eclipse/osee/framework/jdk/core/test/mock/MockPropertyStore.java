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
package org.eclipse.osee.framework.jdk.core.test.mock;

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class MockPropertyStore extends PropertyStore {

   private static final long serialVersionUID = 750608597542081776L;

   public MockPropertyStore() {
      super();
   }

   public MockPropertyStore(String id) {
      super(id);
   }

   public MockPropertyStore(Map<String, Object> properties) {
      super(properties);
   }

   @Override
   public void setId(String name) {
      super.setId(name);
   }

   @Override
   public Map<String, Object> getItems() {
      return super.getItems();
   }

   @Override
   public Map<String, Object> getArrays() {
      return super.getArrays();
   }

   @Override
   public Map<String, Object> getPropertyStores() {
      return super.getPropertyStores();
   }
}