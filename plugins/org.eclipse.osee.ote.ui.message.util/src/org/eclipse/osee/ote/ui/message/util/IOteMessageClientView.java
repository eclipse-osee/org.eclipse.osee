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
package org.eclipse.osee.ote.ui.message.util;

import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.ui.IViewPart;

/**
 * @author Ken J. Aguilar
 *
 */
public interface IOteMessageClientView extends IViewPart {
	void oteMessageServiceAcquired(IOteMessageService service);
	void oteMessageServiceReleased();	
}