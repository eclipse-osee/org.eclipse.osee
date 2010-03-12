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

import org.eclipse.osee.ote.message.elements.StringElement;

/**
 * this condition checks to see if a {@link StringElement} is empty. Empty is defined as having the first byte/character
 * of the element equal to zero.
 * 
 * @author Ken J. Aguilar
 */
public class EmptyStringCondition extends AbstractCondition implements IDiscreteElementCondition<Character> {

   public final StringElement element;
   private char lastValue;
   private final int offset;

   public EmptyStringCondition(StringElement element) {
      this.element = element;
      offset = element.getMsgData().getMem().getOffset() + element.getByteOffset();
   }

   public Character getLastCheckValue() {
      return lastValue;
   }

   public boolean check() {
      lastValue = (char) element.getMsgData().getMem().getData()[offset];
      return lastValue == (char) 0;
   }

}
