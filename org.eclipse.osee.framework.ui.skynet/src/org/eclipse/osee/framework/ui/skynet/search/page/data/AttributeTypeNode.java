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
package org.eclipse.osee.framework.ui.skynet.search.page.data;

import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeNode extends TreeObject implements Comparable<AttributeTypeNode> {

   private DynamicAttributeDescriptor attributeType;

   public AttributeTypeNode(DynamicAttributeDescriptor attributeType) {
      super(attributeType.getName());
      this.attributeType = attributeType;
   }

   public DynamicAttributeDescriptor getAttributeType() {
      return attributeType;
   }

   public String getAttributeTypeName() {
      return getName();
   }

   public void setAttributeTypeNamee(String attributeTypeName) {
      setName(attributeTypeName);
   }

   public int compareTo(AttributeTypeNode other) {
      return this.getAttributeTypeName().compareTo(other.getAttributeTypeName());
   }
}
