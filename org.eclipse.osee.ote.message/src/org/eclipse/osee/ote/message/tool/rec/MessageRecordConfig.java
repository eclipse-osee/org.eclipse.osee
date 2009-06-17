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
package org.eclipse.osee.ote.message.tool.rec;

import java.util.Collection;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.enums.MemType;

public class MessageRecordConfig {
	private final Message<?, ? , ?> msg;
	private final Element[] headerElements;
	private final Element[] bodyElements;
	private final MemType type;
	private final boolean headerDump;
	private final boolean bodyDump;
	
	public MessageRecordConfig(final Message<?,?,?> msg, final MemType type, final boolean headerDump, final Element[] headerElements, final boolean bodyDump, final Element[] bodyElements) {
		this.msg = msg;
		this.headerElements = headerElements;
		this.bodyElements = bodyElements;
		this.type = type;
		this.headerDump = headerDump;
		this.bodyDump = bodyDump;
	}
	
	  public MessageRecordConfig(final Message<?,?,?> msg, final MemType type, Element[] hdrElements) {
	      this.msg = msg;
	      this.headerElements = hdrElements;
	      Collection<Element> elements = msg.getElements();
	      this.bodyElements = elements.toArray(new Element[elements.size()]);
	      this.type = type;
	      this.headerDump = true;
	      this.bodyDump = true;
	   }
	
//	public MessageRecordConfig(final Message<?, ? ,?> msg, final MemType type) {
//		super();
//		this.msg = msg;
//		this.headerElements = msg.getActiveDataSource().getMsgHeader().getElements();
//		Collection<Element> vals = msg.getElementMap().values();
//		this.bodyElements = vals.toArray(new Element[vals.size()]);
//		this.type = type;
//	}
//	
//	public MessageRecordConfig(final Message<?, ? ,?> msg, Element[] hdrElements) {
//		super();
//		this.msg = msg;
//		this.headerElements = hdrElements;
//		Collection<Element> vals = msg.getElementMap().values();
//		this.bodyElements = vals.toArray(new Element[vals.size()]);
//		this.type = msg.getMemType();
//	}

	public Element[] getBodyElements() {
		return bodyElements;
	}

	public Element[] getHeaderElements() {
		return headerElements;
	}
	
	public boolean getBodyDump(){
	   return bodyDump;
	}
	
	public boolean getHeaderDump(){
	   return headerDump;
	}

	public Message<?,?,?> getMsg() {
		return msg;
	}

	public MemType getType() {
		return type;
	}
	
}
