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
package org.eclipse.osee.ote.message.elements;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

public abstract class NumericElement<T extends Number & Comparable<T>> extends DiscreteElement<T>{

    public NumericElement(Message<?,?,?> msg, String elementName,
	    MessageData messageData, int byteOffset, int msb, int lsb,
	    int originalMsb, int originalLsb) {
	super(msg, elementName, messageData, byteOffset, msb, lsb, originalMsb,
		originalLsb);
    }

    public NumericElement(Message<?,?,?> msg, String elementName,
	    MessageData messageData, int byteOffset, int msb, int lsb) {
	super(msg, elementName, messageData, byteOffset, msb, lsb);
    }

    public NumericElement(Message<?,?,?> msg, String elementName,
	    MessageData messageData, int bitOffset, int bitLength) {
	super(msg, elementName, messageData, bitOffset, bitLength);
    }

    public abstract long getNumericBitValue();
}
