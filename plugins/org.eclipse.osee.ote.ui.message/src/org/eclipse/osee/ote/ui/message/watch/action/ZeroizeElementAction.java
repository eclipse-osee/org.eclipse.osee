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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.ui.message.tree.ElementNode;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 */
public class ZeroizeElementAction extends Action {

	private final WatchedMessageNode msgNode;
	private final List<Object> path;

	public ZeroizeElementAction(ElementNode node) {
		super("Zeroize Element");
		this.msgNode = (WatchedMessageNode) node.getMessageNode();
		setEnabled(node.isEnabled() && msgNode.getSubscription().getMessageMode() == MessageMode.WRITER);
		path = node.getElementPath().getElementPath();
	}

	@Override
	public void run() {
		try {
			msgNode.getSubscription().zeroize(path);
		} catch (Exception e) {
			String message = "could not zeroize the message " + msgNode.getMessageClassName();
			OseeLog.log(ZeroizeElementAction.class, Level.SEVERE, message, e);
			MessageDialog.openError(Displays.getActiveShell(), "Zeroize Error", message + ". See error log for trace");
		}
	}

}
