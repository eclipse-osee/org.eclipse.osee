/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AttributeColumn extends AbstractServicesColumn {

   private final AttributeTypeId attrType;

   public AttributeColumn(IAtsServices services, AttributeTypeId attrType) {
      super(services);
      this.attrType = attrType;
   }

   @Override
   public String getText(IAtsObject atsObject) {
      return Collections.toString("; ", services.getAttributeResolver().getAttributesToStringList(atsObject, attrType));
   }

   @Override
   public String toString() {
      return "AttributeColumn [attrType=" + attrType + "]";
   }

}
