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
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 *
 */
public class SetDataSourceMenu extends XSubMenuManager implements IMenuListener {
	private final static String NAME = "Set Data Source";
	private final WatchedMessageNode node;

	public static IContributionItem createMenu(WatchedMessageNode node) {
		if (!node.getSubscription().getAvailableTypes().isEmpty()) {
			return new SetDataSourceMenu(node);
		}
		return new ActionContributionItem(new DisabledAction(NAME));
	}
	
	protected SetDataSourceMenu(WatchedMessageNode node) {
		super(NAME);
		this.node = node;
		setRemoveAllWhenShown(true);
		setEnabled(!node.getSubscription().getAvailableTypes().isEmpty());
		addMenuListener(this);
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		for (MemType type : node.getSubscription().getAvailableTypes()) {
			add(new SetDataSourceAction(node, type));
		}
		
	}

}
