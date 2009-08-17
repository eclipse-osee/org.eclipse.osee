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
package org.eclipse.osee.ote.message.elements.test;

import java.util.Collection;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.Float32Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.enums.MemType;

public class TestMessage extends Message<UnitTestAccessor, TestMessageData, TestMessage> {

   public final IntegerElement INT_ELEMENT_1;
   public final StringElement STRING_ELEMENT_1;
   public final EnumeratedElement<TestEnum> ENUM_ELEMENT_1;
   public final Float32Element FLOAT32_ELEMENT_1;
   private final int defaultByteSize = 100;
   private final int defaultOffset = 0;

   public TestMessage() {
      super("TEST_MSG", true, 0, 50.0);
      TestMessageData ethData =
            new TestMessageData(this.getClass().getName(), getName(), getDefaultByteSize(), getDefaultOffset(),
                  MemType.ETHERNET);
      TestMessageData serialData =
            new TestMessageData(this.getClass().getName(), getName(), getDefaultByteSize(), getDefaultOffset(),
                  MemType.SERIAL);
      setDefaultMessageData(ethData);
      INT_ELEMENT_1 = new IntegerElement(this, "INT_ELEMENT_1", ethData, 0, 0, 15);
      STRING_ELEMENT_1 = new StringElement(this, "STRING_ELEMENT_1", ethData, 2, 0, 159);
      ENUM_ELEMENT_1 = new EnumeratedElement<TestEnum>(this, "ENUM_ELEMENT_1", TestEnum.class, ethData, 22, 0, 7);
      FLOAT32_ELEMENT_1 = new Float32Element(this, "FLOAT32_ELEMENT_1", ethData, 23, 0, 31);
      addElements(INT_ELEMENT_1, STRING_ELEMENT_1, ENUM_ELEMENT_1, FLOAT32_ELEMENT_1);
      setMemSource(MemType.ETHERNET);
   }

   @Override
   public void switchElementAssociation(Collection<TestMessage> messages) {

   }

   public int getDefaultByteSize() {
      return defaultByteSize;
   }

   public int getDefaultOffset() {
      return defaultOffset;
   }

}
