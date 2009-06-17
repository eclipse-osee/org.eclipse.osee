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

import org.eclipse.osee.ote.message.elements.IEnumValue;

public enum TestEnum implements IEnumValue<TestEnum> {
    VAL_0,
    VAL_1,
    VAL_2,
    VAL_3,
    VAL_4,
    VAL_5,
    VAL_6,
    VAL_7,
    VAL_8,
    VAL_9,
    VAL_10;

    public TestEnum getEnum(int value) {
	if (value < 0 || value >= values().length) {
	    throw new IllegalArgumentException("no enum matching value of " + value);
	}
	return values()[value];
    }

    public int getIntValue() {
	return ordinal();
    }
    
    
}
