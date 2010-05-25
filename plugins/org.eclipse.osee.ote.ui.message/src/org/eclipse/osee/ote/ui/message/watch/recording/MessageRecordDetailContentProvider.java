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
package org.eclipse.osee.ote.ui.message.watch.recording;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.osee.ote.ui.message.tree.HeaderElementNode;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.WatchList;
import org.eclipse.osee.ote.ui.message.tree.WatchedElementNode;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageRecordDetailContentProvider implements ITreeContentProvider {

	private WatchList root;
	// we can do this because the tree doesn't change
	private Map<Object, Object> childToParent = new HashMap<Object, Object>(1024);
	private Map<Object, Object[]> parentToChildren = new HashMap<Object, Object[]>();

	/**
	 * @param msgToolClient
	 */
	public MessageRecordDetailContentProvider() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		childToParent.clear();
		parentToChildren.clear();
		if (newInput != null) {
			root = (WatchList) newInput;
			recursivePreLoad(newInput);
		}
	}

	/*
	 * We have do to this so that we can restore the recording state. Since we
	 * are utilizing the node structure of the MW tree but modifying it on the
	 * fly we have to rely on this content provider to find the nodes that need
	 * checks. So we need our lookup maps populated so that
	 * viewer.setSubtreeSelected can find the widget that it needs to update.
	 */
	private void recursivePreLoad(Object... input) {
		for (Object obj : input) {
			Object[] children = getChildren(obj);
			if (children.length > 0) {
				recursivePreLoad(children);
			}
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] returnVals = parentToChildren.get(parentElement);
		if (returnVals != null) {
			return returnVals;
		}
		if (parentElement instanceof WatchList) {
			HashSet<MessageNode> list = new HashSet<MessageNode>();
			for (MessageNode node : ((WatchList) parentElement).getMessages()) {
				if (node.isEnabled()) {
					list.add(node);
				}
			}
			return list.toArray();
		}
		if (parentElement instanceof MessageNode) {
			MessageNode messageNode = (MessageNode) parentElement;
			Object headerDump = new HeaderDump(messageNode);
			Object headerElements = new HeaderElements(messageNode);
			Object bodyDump = new BodyDump(messageNode);
			Object bodyElements = new BodyElements(messageNode);

			childToParent.put(headerDump, parentElement);
			childToParent.put(headerElements, parentElement);
			childToParent.put(bodyDump, parentElement);
			childToParent.put(bodyElements, parentElement);

			returnVals = new Object[] { headerDump, headerElements, bodyDump,
					bodyElements };
		} else if (parentElement instanceof BodyElements) {
			Object[] children = ((BodyElements) parentElement).getMessageNode()
			.getChildren().toArray();
			Arrays.sort(children, new ElementByteComparator());

			for (Object obj : children) {
				childToParent.put(obj, parentElement);
			}

			returnVals = children;
		} else if (parentElement instanceof HeaderElements) {
			String msgName = ((HeaderElements) parentElement).getMessageNode()
			.getMessageClassName();
			WatchedMessageNode node = (WatchedMessageNode) root.getMessageNode(msgName);
			if (node != null) {
				IMessageHeader header = node.getSubscription().getMessage().getActiveDataSource(node.getMemType())
				.getMsgHeader();

				Element[] headerElements = header != null ? header
						.getElements() : new Element[0];
				Object[] objs = new Object[headerElements.length];
				for (int i = 0; i < headerElements.length; i++) {
					HeaderElementNode elementNode = new HeaderElementNode(headerElements[i]);
					objs[i] = elementNode;
					childToParent.put(objs[i], parentElement);
				}
				returnVals = objs;
			}
		} else if (parentElement instanceof AbstractTreeNode) {
			Object[] children = ((AbstractTreeNode) parentElement)
			.getChildren().toArray();
			Arrays.sort(children, new ElementByteComparator());
			for (Object obj : children) {
				childToParent.put(obj, parentElement);
			}
			returnVals = children;
		}
		if (returnVals != null) {
			parentToChildren.put(parentElement, returnVals);
			return returnVals;
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return childToParent.get(element);
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	private static final class ElementByteComparator implements Comparator<Object>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7434564953521614526L;

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof WatchedElementNode && o2 instanceof WatchedElementNode) {
				Integer int1 = (((WatchedElementNode) o1).getByteOffset());
				Integer int2 = (((WatchedElementNode) o2).getByteOffset());
				if (int1 == null || int2 == null) {
					return 0;
				} else {
					int byte1 = int1.intValue();
					int byte2 = int2.intValue();
					if (byte1 == byte2) {
						Integer msb1 = (((WatchedElementNode) o1).getMsb());
						Integer msb2 = (((WatchedElementNode) o2).getMsb());
						if (msb1 == null || msb2 == null) {
							return byte1 - byte2;
						} else {
							return msb1.intValue() - msb2.intValue();
						}
					} else {
						return byte1 - byte2;
					}
				}
			} else if (o1 instanceof HeaderElementNode && o2 instanceof HeaderElementNode) {
				Integer int1 = (((HeaderElementNode) o1).getByteOffset());
				Integer int2 = (((HeaderElementNode) o2).getByteOffset());
				if (int1 == null || int2 == null) {
					return 0;
				} else {
					int byte1 = int1.intValue();
					int byte2 = int2.intValue();
					if (byte1 == byte2) {
						Integer msb1 = (((HeaderElementNode) o1).getMsb());
						Integer msb2 = (((HeaderElementNode) o2).getMsb());
						if (msb1 == null || msb2 == null) {
							return byte1 - byte2;
						} else {
							return msb1.intValue() - msb2.intValue();
						}
					} else {
						return byte1 - byte2;
					}
				}
			} else {
				return 0;
			}
		}
	}

}
