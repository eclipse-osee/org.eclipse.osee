/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.jms.internal;

import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.ote.jms.OteServerJmsNodeProvider;


public final class ClientSideConnectionNodeProviderImpl implements OteServerJmsNodeProvider{

	private final ConnectionNode node;

	ClientSideConnectionNodeProviderImpl(ConnectionNode node) {
		this.node = node;
	}

	@Override
	public ConnectionNode getConnectionNode() {
		return node;
	}
}
