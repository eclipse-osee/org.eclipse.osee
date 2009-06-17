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

import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingFloat32Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andrew M. Finkbeiner
 */
public class Float32Element extends RealElement {

	public Float32Element(Message<?,?,?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
		this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
	}

	public Float32Element(Message<?,?,?> message, String elementName, MessageData messageData, int byteOffset, int msb,
			int lsb, int originalLsb, int originalMsb) {
		super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
	}
	public Float32Element(Message<?,?,?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
		super(message, elementName, messageData, bitOffset, bitLength);
	}
	/**
	 * Checks that this element correctly forwards a message sent from cause with the value passed.
	 * 
	 * @param accessor
	 * @param cause The originator of the signal
	 * @param value The value sent by cause and being forwarded by this element
	 * @throws InterruptedException
	 */
	public void checkForwarding(ITestAccessor accessor, Float32Element cause, double value) throws InterruptedException {
		/* check for 0 to begine */
		check(accessor, 0d, 0);

		/* Set the DP1 Mux Signal */
		cause.set(accessor, (float) value);

		/* Chk Value on DP2 */
		check(accessor, value, 1000);

		/* Set DP1 to 0 */
		cause.set(accessor, 0);

		/* Init DP2 Mux to 0 */
		set(accessor, 0);

		/* Chk Value on DP2 is still set */
		check(accessor, value, 500);

		/* Chk DP2 is 0 for two-pulse signals and high for four-oulse signal */
		check(accessor, 0d, 500);

	}

	/**
	 * Sets the element to the "value" passed.
	 * 
	 * @param accessor
	 * @param value The value to set.
	 */
	public void set(ITestEnvironmentAccessor accessor, double value) {
		if (accessor != null) {
			accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
					this.getMessage());
		}
		setValue(value);
		if (accessor != null) {
			accessor.getLogger().methodEnded(accessor);
		}
	}

	/**
	 * Sets the element to the "value" passed and immediately sends the message that contains it..
	 * 
	 * @param accessor
	 * @param value The value to set.
	 */
	public void setAndSend(ITestEnvironmentAccessor accessor, double value) {
		this.set(accessor, value);
		super.sendMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osee.ote.message.elements.RealElement#getValue()
	 */
	@Override
	public Double getValue() {
		return new Double(Float.intBitsToFloat(getMsgData().getMem().getInt(byteOffset, msb, lsb)));

	}



	@Override
	public Double valueOf(MemoryResource mem) {
		return new Double(Float.intBitsToFloat(mem.getInt(byteOffset, msb, lsb)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osee.ote.message.elements.Element#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Double obj) {
		setValue(obj.floatValue());
	}

	public void setValue(Float obj) {
		getMsgData().getMem().setInt(Float.floatToIntBits(obj), byteOffset, msb, lsb);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see osee.test.core.message.RealElement#toDouble(long)
	 */
	protected double toDouble(long value) {
		return Float.intBitsToFloat((int) value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see osee.test.core.message.RealElement#toLong(double)
	 */
	protected long toLong(double value) {
		return Double.doubleToLongBits(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.message.elements.Element#getNonMappingElement()
	 */
	@Override
	protected NonMappingFloat32Element getNonMappingElement() {
		return new NonMappingFloat32Element(this);
	}
	@Override
	public void setHex(long hex) {
	    getMsgData().getMem().setLong(hex, byteOffset, msb, lsb);
	}

}
