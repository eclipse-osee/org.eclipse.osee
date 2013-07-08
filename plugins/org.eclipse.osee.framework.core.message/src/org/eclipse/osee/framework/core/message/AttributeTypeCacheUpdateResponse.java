/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.message;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.model.type.AttributeType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeCacheUpdateResponse {

   private final List<AttributeType> rows;
   private final Map<Integer, Integer> attrToEnum;

   public AttributeTypeCacheUpdateResponse(List<AttributeType> rows, Map<Integer, Integer> attrToEnum) {
      this.rows = rows;
      this.attrToEnum = attrToEnum;
   }

   public List<AttributeType> getAttrTypeRows() {
      return rows;
   }

   public Map<Integer, Integer> getAttrToEnums() {
      return attrToEnum;
   }

}
