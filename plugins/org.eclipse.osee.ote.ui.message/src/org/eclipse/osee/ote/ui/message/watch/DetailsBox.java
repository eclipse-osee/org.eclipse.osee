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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.osee.ote.ui.message.tree.ElementNode;
import org.eclipse.osee.ote.ui.message.tree.INodeVisitor;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.RootNode;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.framework.Bundle;

/**
 * @author Ken J. Aguilar
 */
public class DetailsBox implements IRegistryEventListener {
	private static final String EXTENSION_POINT_ID = "org.eclipse.osee.ote.ui.message.detailsProvider";
	private static final String ELEMENT = "TabProvider";
	private static final String PAYLOAD_TXT = "\npayload:\n    0:  ";
	private static final String HEADER_TXT = "header:\n    0:  ";
	private static int HEX_DUMP_BYTES_PER_ROW = 16;
	private static int HEX_DUMP_CHARS_PER_BYTE = 3;

	/**
	 * number of characters that lead each row in the hex dump, these characters represent the byte offset indicator for
	 * each hex row
	 */
	private static int HEX_DUMP_PREFIX_CHARS = 8;
	private static int HEX_DUMP_NON_PREFIX_CHAR = HEX_DUMP_BYTES_PER_ROW * HEX_DUMP_CHARS_PER_BYTE;
	/**
	 * total number of characters per hex dump row including the newline character
	 */
	private static int HEX_DUMP_LINE_WIDTH = HEX_DUMP_PREFIX_CHARS + HEX_DUMP_NON_PREFIX_CHAR + 1;

	private final TabFolder infoFolder;
	private final TabItem hexDumpTab;
	private final StyledText hexDumpTxt;
	private final Font courier;
	private final Image hexImg;
	private final StringBuilder strBuilder = new StringBuilder(8500);
	private TabItem selectedTab;
	private final HashMap<String, TabItem> detailsProviderMap = new HashMap<String, TabItem>();

	private static final String[] hexTbl = new String[256];

	static {
		for (int i = 0; i < 256; i++) {
			hexTbl[i] = String.format("%02X ", i);
		}
	}

	public DetailsBox(Composite parent) {
		hexImg = ImageManager.getImage(OteMessageImage.HEX);
		courier = new Font(parent.getDisplay(), "Courier", 10, SWT.NORMAL);
		/* Create Text box to display values of selected messages */
		infoFolder = new TabFolder(parent, SWT.BORDER);

		hexDumpTab = new TabItem(infoFolder, SWT.NONE);
		hexDumpTab.setText("Hex Dump");
		hexDumpTab.setImage(hexImg);
		hexDumpTab.setToolTipText("displays hex dump of currently selected message");
		hexDumpTxt =
					new StyledText(infoFolder, SWT.DOUBLE_BUFFERED | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		hexDumpTxt.setFont(courier);
		hexDumpTab.setControl(hexDumpTxt);

		installExtensionRegistryListener();

		infoFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedTab = (TabItem) e.item;
			}
		});
		selectedTab = infoFolder.getItem(infoFolder.getSelectionIndex());
	}

	/**
	 * display details about specified node
	 * 
	 * @param node node whose details will be displayed in the detail window of the GUI
	 */
	public void setDetailText(final AbstractTreeNode node) {
		if (selectedTab == null) {
			return;
		}
		if (selectedTab == hexDumpTab) {
			renderHex(node);
		} else {
			DetailsProvider provider = (DetailsProvider) selectedTab.getControl();
			provider.render(node);
		}

	}

	public void dispose() {
		courier.dispose();
	}

	private void renderHex(AbstractTreeNode node) {
		if (!node.isEnabled()) {
			hexDumpTxt.setText(node.getName() + "\nDISABLED: " + node.getDisabledReason());
			return;
		}
		final INodeVisitor<Object> visitor = new INodeVisitor<Object>() {
			public Object elementNode(final ElementNode node) {
				hexDumpTxt.setStyleRange(null);
				WatchedMessageNode msgNode = (WatchedMessageNode) node.getMessageNode();
				if (!msgNode.getSubscription().isResolved()) {
					hexDumpTxt.setText(node.getName() + " not found in library");
					return null;
				}
				final Message<?, ?, ?> msg = msgNode.getSubscription().getMessage();
				if (msg.isDestroyed()) {
					return null;
				}
				hexDumpTxt.setRedraw(false);
				int payloadStart = printByteDump(msg);

				Element e = msg.getElement(node.getElementPath().getElementPath());
				if (e != null) {
					if (!e.isNonMappingElement()) {
						MessageData data = msg.getActiveDataSource();
						int headerSize = data.getMsgHeader() == null ? 0 : data.getMsgHeader().getHeaderSize();
						if (e.getByteOffset() >= data.getCurrentLength() - headerSize) {
							hexDumpTxt.setText("element outside of current message size");
							hexDumpTxt.setRedraw(true);
							return null;
						}
						StyleRange range = new StyleRange();
						range.background = Displays.getSystemColor(SWT.COLOR_GRAY);
						range.foreground = Displays.getSystemColor(SWT.COLOR_BLACK);
						int offset = e.getByteOffset() + e.getMsb() / 8;
						range.length = (e.getLsb() - e.getMsb() + 8) / 8 * HEX_DUMP_CHARS_PER_BYTE - 1;
						int line = offset / HEX_DUMP_BYTES_PER_ROW * HEX_DUMP_LINE_WIDTH;
						int lineIndent = offset % HEX_DUMP_BYTES_PER_ROW * HEX_DUMP_CHARS_PER_BYTE;
						range.start = line + lineIndent + payloadStart;
						if (HEX_DUMP_PREFIX_CHARS + lineIndent + range.length >= HEX_DUMP_LINE_WIDTH) {
							int remaing = range.length - (HEX_DUMP_LINE_WIDTH - lineIndent - 9);
							StyleRange[] ranges = new StyleRange[remaing / HEX_DUMP_NON_PREFIX_CHAR + 2];
							ranges[0] = range;
							range.length -= remaing;
							int c = 1;
							while (remaing > 0) {
								StyleRange newRange = new StyleRange();
								ranges[c] = newRange;
								newRange.background = range.background;
								newRange.foreground = range.foreground;
								newRange.start = line + c * HEX_DUMP_LINE_WIDTH + payloadStart;
								newRange.length = remaing < HEX_DUMP_NON_PREFIX_CHAR ? remaing : HEX_DUMP_NON_PREFIX_CHAR;
								remaing -= newRange.length;
								c++;
							}
							hexDumpTxt.setStyleRanges(ranges);
						} else {
							hexDumpTxt.setStyleRange(range);
						}
						hexDumpTxt.setTopIndex(msg.getHeaderSize() / HEX_DUMP_BYTES_PER_ROW + offset / HEX_DUMP_BYTES_PER_ROW + 2);
						hexDumpTxt.setRedraw(true);
					}
				}

				return node;
			}

			public Object messageNode(final MessageNode node) {
				WatchedMessageNode msgNode = (WatchedMessageNode) node;
				final Message<?, ?, ?> msg = msgNode.getSubscription().getMessage();
				if (msg != null && !msg.isDestroyed()) {
					hexDumpTxt.setRedraw(false);
					printByteDump(msg);
					hexDumpTxt.setStyleRange(null);
					hexDumpTxt.setRedraw(true);
				}
				return node;
			}

			public Object rootNode(RootNode node) {
				return node;
			}

		};
		node.visit(visitor);
	}

	/**
	 * writes message data to a buffer in hex format
	 * 
	 * @param data
	 * @param offset
	 */
	private int printByteDump(Message<?, ?, ?> msg) {
		strBuilder.setLength(0);
		int ypos = hexDumpTxt.getTopIndex();
		int xpos = hexDumpTxt.getHorizontalIndex();
		int cursorPos = hexDumpTxt.getCaretOffset();
		final byte[] data = msg.getData();
		int columnCount = 0;
		strBuilder.append(HEADER_TXT);
		for (int i = 0; i < msg.getHeaderSize(); i++) {
			if (columnCount == HEX_DUMP_BYTES_PER_ROW) {
				strBuilder.append('\n').append(String.format("%5d:  ", i));
				columnCount = 0;
			}
			strBuilder.append(hexTbl[data[i] & 0xFF]);
			columnCount++;
		}
		strBuilder.append(PAYLOAD_TXT);
		int payloadStart = strBuilder.length();
		columnCount = 0;
		for (int i = msg.getHeaderSize(); i < msg.getHeaderSize() + msg.getPayloadSize(); i++) {
			if (columnCount == HEX_DUMP_BYTES_PER_ROW) {
				strBuilder.append('\n').append(String.format("%5d:  ", i - msg.getHeaderSize()));
				columnCount = 0;
			}
			strBuilder.append(hexTbl[data[i] & 0xFF]);
			columnCount++;
		}
		strBuilder.append('\n');
		hexDumpTxt.setText(strBuilder.toString());
		hexDumpTxt.setTopIndex(ypos);
		hexDumpTxt.setHorizontalIndex(xpos);
		hexDumpTxt.setCaretOffset(cursorPos);
		return payloadStart;
	}

	private void installExtensionRegistryListener() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		if (extensionRegistry == null) {
			throw new IllegalStateException("The extension registry is unavailable");
		}

		IExtensionPoint point = extensionRegistry.getExtensionPoint(EXTENSION_POINT_ID);
		if (point == null) {
			return;
		}
		added(point.getExtensions());
		extensionRegistry.addListener(this, EXTENSION_POINT_ID);
	}

	@Override
	public void added(IExtension[] extensions) {
		final List<IConfigurationElement> newElements = new LinkedList<IConfigurationElement>();
		for (IExtension extension : extensions) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				if (element.getName().equals(ELEMENT)) {
					newElements.add(element);
				}
			}
		}
		Displays.ensureInDisplayThread(new Runnable() {
			@Override
			public void run() {
				for (IConfigurationElement element : newElements) {
					String className = element.getAttribute("className");
					String bundleName = element.getContributor().getName();
					Bundle bundle = Platform.getBundle(bundleName);
					if (bundle == null) {
						OseeLog.log(DetailsBox.class, Level.SEVERE, String.format(
									"no bundle found for name %s while handling extension element %s", bundleName,
									element.getName()));
						return;
					}
					try {
						Class<?> clazz = bundle.loadClass(className);
						Class<? extends DetailsProvider> detailsClazz = clazz.asSubclass(DetailsProvider.class);
						Constructor<? extends DetailsProvider> constructor =
									detailsClazz.getConstructor(Composite.class, int.class);
						try {
							DetailsProvider provider = constructor.newInstance(infoFolder, SWT.NONE);
							TabItem newTab = new TabItem(infoFolder, SWT.NONE);
							newTab.setText(provider.getTabText());
							newTab.setToolTipText(provider.getTabToolTipText());
							newTab.setControl(provider);
							detailsProviderMap.put(element.getDeclaringExtension().getUniqueIdentifier(), newTab);
						} catch (Exception ex) {
							OseeLog.log(DetailsBox.class, Level.SEVERE, String.format("failed to install details provider"));
						}
					} catch (ClassCastException ex) {
						OseeLog.log(
									DetailsBox.class,
									Level.SEVERE,
									String.format("the class named %s is not a subclass of %s", className,
												DetailsProvider.class.getName()));
					} catch (ClassNotFoundException ex) {
						OseeLog.log(DetailsBox.class, Level.SEVERE,
									String.format("no class found named %s in bundle %s", className, bundleName));
					} catch (NoSuchMethodException ex) {
						OseeLog.log(DetailsBox.class, Level.SEVERE,
									String.format("can't find appropriate constructor for %s", className));
					}
				}
			}
		});

	}

	@Override
	public void added(IExtensionPoint[] points) {

	}

	@Override
	public void removed(IExtension[] extensions) {
		final List<TabItem> removedElements = new LinkedList<TabItem>();
		for (IExtension extension : extensions) {
			TabItem item = detailsProviderMap.get(extension.getUniqueIdentifier());
			if (item != null) {
				removedElements.add(item);
			}
		}

		Displays.ensureInDisplayThread(new Runnable() {
			@Override
			public void run() {

				for (TabItem item : removedElements) {
					if (selectedTab == item) {
						selectedTab = null;
						infoFolder.setSelection(hexDumpTab);
					}
					item.dispose();
				}
			}
		});
	}

	@Override
	public void removed(IExtensionPoint[] arg0) {
	}
}
