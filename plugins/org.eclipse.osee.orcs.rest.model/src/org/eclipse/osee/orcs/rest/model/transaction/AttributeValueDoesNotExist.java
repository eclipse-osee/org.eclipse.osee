/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.orcs.rest.model.transaction;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public class AttributeValueDoesNotExist extends OseeCoreException {

   private static final long serialVersionUID = 1L;

   public AttributeValueDoesNotExist(String message, Object... args) {
      super(message, args);
   }
}