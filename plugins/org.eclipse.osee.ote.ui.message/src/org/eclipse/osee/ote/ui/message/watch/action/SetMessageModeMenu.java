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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.nebula.widgets.xviewer.XSubMenuManager;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 *
 */
public class SetMessageModeMenu extends XSubMenuManager implements IMenuListener {
	private final static String NAME = "Set Reader/Writer Buffer";
	private final WatchedMessageNode node;

	public static IContributionItem createMenu(WatchedMessageNode node) {
		if (node.isEnabled() && node.getSubscription().isResolved()) {
			return new SetMessageModeMenu(node);
		}
		return new ActionContributionItem(new DisabledAction(NAME));
	}
	
	protected SetMessageModeMenu(WatchedMessageNode node) {
		super(NAME);
		this.node = node;
		setRemoveAllWhenShown(true);
		addMenuListener(this);
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		for (MessageMode mode : MessageMode.values()) {
			add(new SetMessageModeAction(node, mode));
		}
		
	}

}
