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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ote.ui.message.tree.WatchList;

/**
 * @author Ken J. Aguilar
 *
 */
public class ClearUpdatesAction extends Action {


	private final IStructuredSelection selection;
	private final WatchList watchList;

	public ClearUpdatesAction(WatchList watchList, IStructuredSelection selection) {
		super("Clear Update Counter");
		this.selection = selection;
		this.watchList = watchList;
	}
	
	@Override
	public void run() {
		watchList.clearUpdateCounters(selection);
	}
	
	
}
