/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class NamedIdDescription extends NamedIdBase implements HasDescription {
   private final String description;

   public NamedIdDescription(Long id, String name, String description) {
      super(id, name);
      this.description = description;
   }

   @Override
   public String getDescription() {
      return description;
   }
}