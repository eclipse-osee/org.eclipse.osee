/*********************************************************************
 * Copyright (c) 2019 Boeing
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