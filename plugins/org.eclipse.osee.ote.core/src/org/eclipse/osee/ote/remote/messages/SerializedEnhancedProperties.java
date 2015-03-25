/*******************************************************************************
ConfigurationAndResponse * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedEnhancedProperties extends SerializedClassMessage<EnhancedProperties> {

	public static final String EVENT = "ote/message/enhancedproperties";
	
	public SerializedEnhancedProperties() {
		super(EVENT);
	}
	
	public SerializedEnhancedProperties(EnhancedProperties commandAdded) throws IOException {
		super(EVENT, commandAdded);
	}
	
	public SerializedEnhancedProperties(byte[] bytes){
		super(bytes);
	}
}
