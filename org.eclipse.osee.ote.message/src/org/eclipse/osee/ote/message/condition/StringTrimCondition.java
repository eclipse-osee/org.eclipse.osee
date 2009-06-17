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
package org.eclipse.osee.ote.message.condition;

import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class StringTrimCondition extends AbstractCondition implements IDiscreteElementCondition<String> {

   private final DiscreteElement<String> element;
   private final String value;
   private String actualValue;

   public StringTrimCondition(DiscreteElement<String> element, String value) {
      this.element = element;
      this.value = value;
   }

   public boolean check() {
      actualValue = element.getValue().trim();
      return actualValue.equals(value);
   }

   public String getLastCheckValue() {
      return actualValue;
   }
}
