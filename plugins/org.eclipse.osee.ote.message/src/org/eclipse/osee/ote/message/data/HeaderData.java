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
package org.eclipse.osee.ote.message.data;

import org.eclipse.osee.ote.message.IMessageHeader;

public class HeaderData extends MessageData {


    public HeaderData(String name, MemoryResource memoryResource) {
        super(name, memoryResource);
    }
    
	public HeaderData(MemoryResource memoryResource) {
		this("", memoryResource);
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
