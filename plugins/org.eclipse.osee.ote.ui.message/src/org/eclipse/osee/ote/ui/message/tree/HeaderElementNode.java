/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;

/**
 * @author Ken J. Aguilar
 *
 */
public class HeaderElementNode extends ElementNode {

	private final Element headerElement;
	
	public HeaderElementNode(Element headerElement) {
		super(new ElementPath(true, headerElement.getElementPath()));
		this.headerElement = headerElement;
	}

	public int getByteOffset() {
		return headerElement.getByteOffset();
	}

	public int getMsb() {
		return headerElement.getMsb();
	}

	public int getLsb() {
		return headerElement.getLsb();
	}
}
