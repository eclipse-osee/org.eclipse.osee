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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 */
public class ZeroizeMessageAction extends Action {

	private final WatchedMessageNode msgNode;

	public ZeroizeMessageAction(WatchedMessageNode msgNode) {
		super("Zeroize " + msgNode.getName());
		this.msgNode = msgNode;
		setEnabled(msgNode.isEnabled() && msgNode.getSubscription().getMessageMode() == MessageMode.WRITER && msgNode.getSubscription().isActive());
	}

	@Override
	public void run() {
		try {
			msgNode.getSubscription().zeroize(null);
		} catch (Exception e) {
			String message = "could not zeroize the message " + msgNode.getMessageClassName();
			OseeLog.log(ZeroizeMessageAction.class, Level.SEVERE, message, e);
			MessageDialog.openError(Displays.getActiveShell(), "Zeroize Error", message + ". See error log for trace");
		}
	}

}
