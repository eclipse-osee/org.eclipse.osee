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

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;

public class BooleanElement extends DiscreteElement<Boolean> {

	public BooleanElement(Message<?,?,?> msg, String elementName,
			MessageData messageData, int byteOffset, int msb, int lsb,
			int originalMsb, int originalLsb) {
		super(msg, elementName, messageData, byteOffset, msb, lsb, originalMsb,
				originalLsb);
		
	}

	public BooleanElement(Message<?,?,?> msg, String elementName,
			MessageData messageData, int bitOffset, int bitLength) {
		super(msg, elementName, messageData, bitOffset, bitLength);
		
	}

	public BooleanElement(Message<?,?,?> msg, String elementName,
			MessageData messageData, int byteOffset, int msb, int lsb) {
		super(msg, elementName, messageData, byteOffset, msb, lsb);
		
	}

	@Override
	protected Element getNonMappingElement() {
		return null;
	}

	@Override
	public Boolean getValue() {
		return new Boolean(getMsgData().getMem().getBoolean(byteOffset, msb, lsb));
	}
	
	
	@Override
	public void setValue(Boolean obj) {
		getMsgData().getMem().setBoolean((Boolean) obj, byteOffset, msb, lsb);
	}

	@Override
	public String toString(Boolean obj) {
		return obj.toString();
	}

	@Override
	public Boolean valueOf(MemoryResource mem) {
		return new Boolean(mem.getBoolean(byteOffset, msb, lsb));
	}

	@Override
	public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
		set(accessor, Boolean.parseBoolean(value));
	}

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asBooleanElement(this);
   }

@Override
public Boolean elementMask(Boolean value) {
    return value;
}
	
	
}
