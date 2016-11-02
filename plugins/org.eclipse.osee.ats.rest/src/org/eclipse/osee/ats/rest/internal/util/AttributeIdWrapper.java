/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.AttributeId;

/**
 * @author Donald G Dunne
 */
public class AttributeIdWrapper implements AttributeId {

   private final IAttribute<?> attribute;

   public AttributeIdWrapper(IAttribute<?> attribute) {
      this.attribute = attribute;
   }

   @Override
   public Long getId() {
      return attribute.getId();
   }
}
