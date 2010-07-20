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
package org.eclipse.osee.ote.ui.message.watch;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.WatchList;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Andrew M. Finkbeiner
 */
public class ViewRefresher implements Runnable {
	private final ArrayList<AbstractTreeNode> deltas = new ArrayList<AbstractTreeNode>(256);
	private final Benchmark benchMark = new Benchmark("Message Watch Update Time");
	private final WatchView view;
	private volatile AbstractTreeNode selectedNode;
	private final int period;
	private ScheduledFuture<?> handle = null;
	private final WatchList list;

	public ViewRefresher(WatchList list, final WatchView view, int period) {
		this.period = period;
		this.view = view;
		this.list = list;
		view.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) view.getTreeViewer().getSelection();
				selectedNode = (AbstractTreeNode) selection.getFirstElement();
			}
		});
	}

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private final Runnable task = new Runnable() {

		@Override
		public void run() {
			if (view != null && !view.getTreeViewer().getTree().isDisposed()) {
				if (selectedNode != null) {
					view.setDetailText(selectedNode);
				}
				for (AbstractTreeNode node : deltas) {
					view.getTreeViewer().update(node, null);
				}
			}
		}

	};

	public void start() {
		handle = scheduler.scheduleWithFixedDelay(this, period, period, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		if (handle != null) {
			handle.cancel(false);
			handle = null;
		}
	}

	public void dispose() {
		scheduler.shutdown();
	}

	@Override
	public void run() {
		try {
			benchMark.startSample();
			deltas.clear();
			for (MessageNode node : list.getMessages()) {
				((WatchedMessageNode) node).determineDeltas(deltas);
			}
			Displays.pendInDisplayThread(task);
			benchMark.endSample();

		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

}
