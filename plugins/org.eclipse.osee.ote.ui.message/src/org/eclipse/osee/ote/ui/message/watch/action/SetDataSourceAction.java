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
package org.eclipse.osee.ote.ui.message.watch.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 *
 */
public class SetDataSourceAction extends Action {


	private final WatchedMessageNode node;
	private final DataType type;
	
	public SetDataSourceAction(WatchedMessageNode node, DataType type) {
		super(type.name(), Action.AS_RADIO_BUTTON);
		this.node = node;
		this.type = type;
		setChecked(node.getSubscription().getMemType() == type);
	}
	
	@Override
	public void run() {
		node.getSubscription().changeMemType(type);
	}

}
