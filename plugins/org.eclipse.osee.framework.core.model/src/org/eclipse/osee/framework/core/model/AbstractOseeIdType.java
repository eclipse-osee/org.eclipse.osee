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
package org.eclipse.osee.framework.core.model;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeIdType extends AbstractOseeType {

   protected AbstractOseeIdType(Long guid, String key) {
      super(guid, key);
   }

   public Long getGuid() {
      return getId();
   }
}
