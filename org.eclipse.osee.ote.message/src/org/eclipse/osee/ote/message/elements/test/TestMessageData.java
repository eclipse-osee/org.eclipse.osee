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

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.MemType;

public class TestMessageData extends MessageData {

    public TestMessageData(String typeName, String name, int dataByteSize,
	    int offset, MemType memType) {
	super(typeName, name, dataByteSize, offset, memType);
    }


    @Override
    public IMessageHeader getMsgHeader() {
	return null;
    }

    @Override
    public void initializeDefaultHeaderValues() {

    }

    @Override
    public void visit(IMessageDataVisitor visitor) {

    }

    @Override
    public void zeroize() {

    }

}
