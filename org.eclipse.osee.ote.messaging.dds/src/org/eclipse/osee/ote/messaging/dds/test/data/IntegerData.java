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
package org.eclipse.osee.ote.messaging.dds.test.data;
import java.nio.ByteBuffer;

import org.eclipse.osee.ote.messaging.dds.Data;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class IntegerData implements Data {
	private int theInt;
	/**
	 * 
	 */
	public IntegerData(int theInt) {
		super();
		this.theInt = theInt;
	}



	/**
	 * @return Returns the theInt.
	 */
	public int getTheInt() {
		return theInt;
	}


	/**
	 * @param theInt The theInt to set.
	 */
	public void setTheInt(int theInt) {
		this.theInt = theInt;
	}

	public byte[] toByteArray() {
		int x = theInt;

		return new byte[]{byteOf(x,3), byteOf(x,2), byteOf(x,1), byteOf(x,0)};
	}

	private byte byteOf(int x, int index) {
		return (byte)(x>>(index*4) & 0xff);
	}
	public void setFromByteArray(byte[] input) {

		int value=0;

		if (input.length > 4) {
			value = Integer.MAX_VALUE;
		} else {
			for (int x = input.length-1;x>-1;x--) {
				value += input[x]<<((input.length-(x+1))*4);
			}
		}
		theInt = value;
	}

	public Object getKeyValue() {
		return new Integer(theInt);
	}



	@Override
	public void copyFrom(Data buffer) {
		// TODO Auto-generated method stub

	}



	@Override
	public void setFromByteBuffer(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}



	@Override
	public ByteBuffer toByteBuffer() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}


}
