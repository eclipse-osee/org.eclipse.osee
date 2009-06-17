/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.mux.view;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.PeriodicDisplayTask;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.ui.mux.MuxToolPlugin;
import org.eclipse.osee.ote.ui.mux.datatable.DatawordContentProvider;
import org.eclipse.osee.ote.ui.mux.datatable.DatawordLabelProvider;
import org.eclipse.osee.ote.ui.mux.datatable.RowNode;
import org.eclipse.osee.ote.ui.mux.model.DatawordModel;
import org.eclipse.osee.ote.ui.mux.model.MessageModel;
import org.eclipse.osee.ote.ui.mux.msgtable.MessageNode;
import org.eclipse.osee.ote.ui.mux.msgtable.MuxMsgContentProvider;
import org.eclipse.osee.ote.ui.mux.msgtable.MuxMsgLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class MuxView extends ViewPart implements ITestConnectionListener, IInstrumentationRegistrationListener, Remote {
	private TableViewer msgViewer1, msgViewer2, msgViewer3, msgViewer5;
	private TableViewer dataViewer1, dataViewer2, dataViewer3, dataViewer5;
	private MessageModel chan1Msgs, chan2Msgs, chan3Msgs, chan5Msgs;
	private DatawordModel chan1Dwrds, chan2Dwrds, chan3Dwrds, chan5Dwrds;
	private IOInstrumentation muxProbe;
	private PeriodicDisplayTask task;
	private ListenerThread thread;
	private int port;
	private int selectedChannel;
	private int selectedRt;
	private int selectedTR;
	private int selectedSubaddr;
	private final static Color GRAY = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	private final static Color WHITE = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

	private IInstrumentationRegistrationListener exportedThis;

	public static final String VIEW_ID = "osee.test.muxTool.views.MuxView";
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public MuxView() {
		super();
	}

	/**
	 * This is a callback that will allow us to create the viewers and initialize them.
	 */
	public void createPartControl(Composite parent) {
		final TabFolder tabFolder = new TabFolder(parent, SWT.WRAP);
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				switch (tabFolder.getSelectionIndex()) {
				case 0:
					selectedChannel = 1;
					break;
				case 1:
					selectedChannel = 2;
					break;
				case 2:
					selectedChannel = 3;
					break;
				case 3:
					selectedChannel = 5;
					break;
				default:
					selectedChannel = 1;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Setup Channel 1 display
		TabItem chan1Tab = new TabItem(tabFolder, SWT.NONE);
		chan1Tab.setText("Channel 1");
		Composite chan1TabComp = new Composite(tabFolder, SWT.NONE);
		chan1Tab.setControl(chan1TabComp);
		GridLayout chan1Layout = new GridLayout(1, false);
		chan1TabComp.setLayout(chan1Layout);
		msgViewer1 = new TableViewer(chan1TabComp, SWT.BORDER | SWT.FULL_SELECTION);
		msgViewer1.setContentProvider(new MuxMsgContentProvider());
		msgViewer1.setLabelProvider(new MuxMsgLabelProvider());
		msgViewer1.setSorter(new NameSorter());
		createMsgTable(msgViewer1);
		Composite bottom1 = new Composite(chan1TabComp, SWT.NONE);
		bottom1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		bottom1.setLayout(new GridLayout(2, false));

		Composite data1 = new Composite(bottom1, SWT.NONE);
		RowLayout dataLayout1 = new RowLayout(SWT.HORIZONTAL);
		data1.setLayout(dataLayout1);

		Composite labels1 = new Composite(data1, SWT.NONE);
		RowLayout labelLayout1 = new RowLayout(SWT.VERTICAL);
		labelLayout1.marginTop = 5;
		labelLayout1.spacing = 1;
		labels1.setLayout(labelLayout1);
		Label label1_1 = new Label(labels1, SWT.NONE);
		label1_1.setText("Datawords 1-8");
		Label label2_1 = new Label(labels1, SWT.NONE);
		label2_1.setText("Datawords 9-16");
		Label label3_1 = new Label(labels1, SWT.NONE);
		label3_1.setText("Datawords 17-24");
		Label label4_1 = new Label(labels1, SWT.NONE);
		label4_1.setText("Datawords 25-32");

		Composite datawords1 = new Composite(data1, SWT.NONE);
		datawords1.setLayout(new GridLayout(1, false));
		dataViewer1 = new TableViewer(datawords1, SWT.BORDER | SWT.FULL_SELECTION);
		dataViewer1.setContentProvider(new DatawordContentProvider());
		dataViewer1.setLabelProvider(new DatawordLabelProvider());
		createDwordTable(dataViewer1);
		msgViewer1.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				MessageNode node = (MessageNode) selection.getFirstElement();
				if (node != null) {
					selectedRt = node.getRt();
					selectedTR = node.getTransmitReceive();
					selectedSubaddr = node.getSubaddress();
					chan1Dwrds.setCurrentNode(node.getName());
				}
			}
		});
		final Button reset1 = new Button(bottom1, SWT.PUSH);
		reset1.setText("Reset Display");
		reset1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
		reset1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedRt = 0;
				selectedTR = 0;
				selectedSubaddr = 0;
				chan1Msgs.removeMessages();
				chan1Dwrds.setCurrentNode(null);
			}
		});

		// Setup Channel 2 display
		TabItem chan2Tab = new TabItem(tabFolder, SWT.NONE);
		chan2Tab.setText("Channel 2");
		Composite chan2TabComp = new Composite(tabFolder, SWT.NONE);
		chan2Tab.setControl(chan2TabComp);
		GridLayout chan2Layout = new GridLayout(1, false);
		chan2TabComp.setLayout(chan2Layout);
		msgViewer2 = new TableViewer(chan2TabComp, SWT.BORDER | SWT.FULL_SELECTION);
		msgViewer2.setContentProvider(new MuxMsgContentProvider());
		msgViewer2.setLabelProvider(new MuxMsgLabelProvider());
		msgViewer2.setSorter(new NameSorter());
		createMsgTable(msgViewer2);
		Composite bottom2 = new Composite(chan2TabComp, SWT.NONE);
		bottom2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		bottom2.setLayout(new GridLayout(2, false));

		Composite data2 = new Composite(bottom2, SWT.NONE);
		RowLayout dataLayout2 = new RowLayout(SWT.HORIZONTAL);
		data2.setLayout(dataLayout2);

		Composite labels2 = new Composite(data2, SWT.NONE);
		RowLayout lableLayout2 = new RowLayout(SWT.VERTICAL);
		lableLayout2.marginTop = 5;
		lableLayout2.spacing = 1;
		labels2.setLayout(lableLayout2);
		Label label1_2 = new Label(labels2, SWT.NONE);
		label1_2.setText("Datawords 1-8");
		Label label2_2 = new Label(labels2, SWT.NONE);
		label2_2.setText("Datawords 9-16");
		Label label3_2 = new Label(labels2, SWT.NONE);
		label3_2.setText("Datawords 17-24");
		Label label4_2 = new Label(labels2, SWT.NONE);
		label4_2.setText("Datawords 25-32");

		Composite datawords2 = new Composite(data2, SWT.NONE);
		datawords2.setLayout(new GridLayout(1, false));
		dataViewer2 = new TableViewer(datawords2, SWT.BORDER | SWT.FULL_SELECTION);
		dataViewer2.setContentProvider(new DatawordContentProvider());
		dataViewer2.setLabelProvider(new DatawordLabelProvider());
		createDwordTable(dataViewer2);
		msgViewer2.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				MessageNode node = (MessageNode) selection.getFirstElement();
				if (node != null) {
					selectedRt = node.getRt();
					selectedTR = node.getTransmitReceive();
					selectedSubaddr = node.getSubaddress();
					chan2Dwrds.setCurrentNode(node.getName());
				}
			}
		});
		final Button reset2 = new Button(bottom2, SWT.PUSH);
		reset2.setText("Reset Display");
		reset2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
		reset2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedRt = 0;
				selectedTR = 0;
				selectedSubaddr = 0;
				chan2Msgs.removeMessages();
				chan2Dwrds.setCurrentNode(null);
			}
		});

		// Setup Channel 3 display
		TabItem chan3Tab = new TabItem(tabFolder, SWT.NONE);
		chan3Tab.setText("Channel 3");
		Composite chan3TabComp = new Composite(tabFolder, SWT.NONE);
		chan3Tab.setControl(chan3TabComp);
		GridLayout chan3Layout = new GridLayout(1, false);
		chan3TabComp.setLayout(chan3Layout);
		msgViewer3 = new TableViewer(chan3TabComp, SWT.BORDER | SWT.FULL_SELECTION);
		msgViewer3.setContentProvider(new MuxMsgContentProvider());
		msgViewer3.setLabelProvider(new MuxMsgLabelProvider());
		msgViewer3.setSorter(new NameSorter());
		createMsgTable(msgViewer3);
		Composite bottom3 = new Composite(chan3TabComp, SWT.NONE);
		bottom3.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		bottom3.setLayout(new GridLayout(2, false));

		Composite data3 = new Composite(bottom3, SWT.NONE);
		RowLayout dataLayout3 = new RowLayout(SWT.HORIZONTAL);
		data3.setLayout(dataLayout3);

		Composite labels3 = new Composite(data3, SWT.NONE);
		RowLayout lableLayout3 = new RowLayout(SWT.VERTICAL);
		lableLayout3.marginTop = 5;
		lableLayout3.spacing = 1;
		labels3.setLayout(lableLayout3);
		Label label1_3 = new Label(labels3, SWT.NONE);
		label1_3.setText("Datawords 1-8");
		Label label2_3 = new Label(labels3, SWT.NONE);
		label2_3.setText("Datawords 9-16");
		Label label3_3 = new Label(labels3, SWT.NONE);
		label3_3.setText("Datawords 17-24");
		Label label4_3 = new Label(labels3, SWT.NONE);
		label4_3.setText("Datawords 25-32");

		Composite datawords3 = new Composite(data3, SWT.NONE);
		datawords3.setLayout(new GridLayout(1, false));
		dataViewer3 = new TableViewer(datawords3, SWT.BORDER | SWT.FULL_SELECTION);
		dataViewer3.setContentProvider(new DatawordContentProvider());
		dataViewer3.setLabelProvider(new DatawordLabelProvider());
		createDwordTable(dataViewer3);
		msgViewer3.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				MessageNode node = (MessageNode) selection.getFirstElement();
				if (node != null) {
					selectedRt = node.getRt();
					selectedTR = node.getTransmitReceive();
					selectedSubaddr = node.getSubaddress();
					chan3Dwrds.setCurrentNode(node.getName());
				}
			}
		});
		final Button reset3 = new Button(bottom3, SWT.PUSH);
		reset3.setText("Reset Display");
		reset3.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
		reset3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedRt = 0;
				selectedTR = 0;
				selectedSubaddr = 0;
				chan3Msgs.removeMessages();
				chan3Dwrds.setCurrentNode(null);
			}
		});

		// Setup Channel 5 display
		TabItem chan5Tab = new TabItem(tabFolder, SWT.NONE);
		chan5Tab.setText("Channel 5");
		Composite chan5TabComp = new Composite(tabFolder, SWT.NONE);
		chan5Tab.setControl(chan5TabComp);
		GridLayout chan5Layout = new GridLayout(1, false);
		chan5TabComp.setLayout(chan5Layout);
		msgViewer5 = new TableViewer(chan5TabComp, SWT.BORDER | SWT.FULL_SELECTION);
		msgViewer5.setContentProvider(new MuxMsgContentProvider());
		msgViewer5.setLabelProvider(new MuxMsgLabelProvider());
		msgViewer5.setSorter(new NameSorter());
		createMsgTable(msgViewer5);
		Composite bottom5 = new Composite(chan5TabComp, SWT.NONE);
		bottom5.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		bottom5.setLayout(new GridLayout(2, false));

		Composite data5 = new Composite(bottom5, SWT.NONE);
		RowLayout dataLayout5 = new RowLayout(SWT.HORIZONTAL);
		data5.setLayout(dataLayout5);

		Composite labels5 = new Composite(data5, SWT.NONE);
		RowLayout lableLayout5 = new RowLayout(SWT.VERTICAL);
		lableLayout5.marginTop = 5;
		lableLayout5.spacing = 1;
		labels5.setLayout(lableLayout5);
		Label label1_5 = new Label(labels5, SWT.NONE);
		label1_5.setText("Datawords 1-8");
		Label label2_5 = new Label(labels5, SWT.NONE);
		label2_5.setText("Datawords 9-16");
		Label label3_5 = new Label(labels5, SWT.NONE);
		label3_5.setText("Datawords 17-24");
		Label label4_5 = new Label(labels5, SWT.NONE);
		label4_5.setText("Datawords 25-32");

		Composite datawords5 = new Composite(data5, SWT.NONE);
		datawords5.setLayout(new GridLayout(1, false));
		dataViewer5 = new TableViewer(datawords5, SWT.BORDER | SWT.FULL_SELECTION);
		dataViewer5.setContentProvider(new DatawordContentProvider());
		dataViewer5.setLabelProvider(new DatawordLabelProvider());
		createDwordTable(dataViewer5);
		msgViewer5.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				MessageNode node = (MessageNode) selection.getFirstElement();
				if (node != null) {
					selectedRt = node.getRt();
					selectedTR = node.getTransmitReceive();
					selectedSubaddr = node.getSubaddress();
					chan5Dwrds.setCurrentNode(node.getName());
				}
			}
		});
		final Button reset5 = new Button(bottom5, SWT.PUSH);
		reset5.setText("Reset Display");
		reset5.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
		reset5.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedRt = 0;
				selectedTR = 0;
				selectedSubaddr = 0;
				chan5Msgs.removeMessages();
				chan5Dwrds.setCurrentNode(null);
			}
		});
		msgViewer1.setInput(chan1Msgs = new MessageModel());
		dataViewer1.setInput(chan1Dwrds = new DatawordModel());
		msgViewer2.setInput(chan2Msgs = new MessageModel());
		dataViewer2.setInput(chan2Dwrds = new DatawordModel());
		msgViewer3.setInput(chan3Msgs = new MessageModel());
		dataViewer3.setInput(chan3Dwrds = new DatawordModel());
		msgViewer5.setInput(chan5Msgs = new MessageModel());
		dataViewer5.setInput(chan5Dwrds = new DatawordModel());
		updateColors(false);
		try {
			thread = new ListenerThread();
		} catch (Exception e) {
			OseeLog.log(MuxView.class, Level.SEVERE, "Mux View could not start listening thread", e);
			MessageDialog.openError(parent.getShell(), "Error", "Mux View could not initialize. See Error Log for details");
			return;
		}
		thread.start();

		task = new PeriodicDisplayTask(Display.getDefault(), 333) {
			@Override
			protected void update() {
				try {
					msgViewer1.refresh();
					dataViewer1.refresh();
					msgViewer2.refresh();
					dataViewer2.refresh();
					msgViewer3.refresh();
					dataViewer3.refresh();
					msgViewer5.refresh();
					dataViewer5.refresh();
				} catch (Throwable t) {
					OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "problems refreshing viewer", t);
					stop();
				}
			}
		};
		task.start();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.eclipse.osee.ote.ui.mux.muxView");
		MuxToolPlugin.getDefault().getOteClientService().addConnectionListener(this);
	}

	/**
	 * Create the Mux Message Tree
	 */
	private void createMsgTable(TableViewer parent) {
		final Table table = parent.getTable();
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		int height = table.getItemHeight() * 20;
		Rectangle trim = table.computeTrim(0, 0, 0, height);
		gridData.heightHint = trim.height;
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);
		column.setText("Message");
		column.setWidth(70);
		column = new TableColumn(table, SWT.CENTER, 1);
		column.setText("RT-RT");
		column.setWidth(60);
		column = new TableColumn(table, SWT.CENTER, 2);
		column.setText("Word Cnt");
		column.setWidth(70);
		column = new TableColumn(table, SWT.CENTER, 3);
		column.setText("StatusWd");
		column.setWidth(70);
		column = new TableColumn(table, SWT.CENTER, 4);
		column.setText("Emulation");
		column.setWidth(70);
		column = new TableColumn(table, SWT.CENTER, 5);
		column.setText("Bus");
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 6);
		column.setText("Activity");
		column.setWidth(60);
		column = new TableColumn(table, SWT.CENTER, 7);
		column.setText("Error Cnt");
		column.setWidth(70);
		column = new TableColumn(table, SWT.CENTER, 8);
		column.setText("Error Type");
		column.setWidth(150);

		table.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent event) {
				if (event.button == 3) {
					Menu menu = new Menu(table.getShell(), SWT.POP_UP);
					MenuItem enableBoth = new MenuItem(menu, SWT.PUSH);
					enableBoth.setText("Emulate RT (Pri + Sec)");
					enableBoth.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (muxProbe != null) {
								byte[] cmd = new byte[4];
								cmd[0] = 'R'; // RT simulation cmd
								cmd[1] = (byte) selectedChannel;
								cmd[2] = (byte) selectedRt;
								cmd[3] = (byte) 3;
								try {
									muxProbe.command(cmd);
								} catch (RemoteException ex) {
									OseeLog.log(MuxToolPlugin.class, Level.WARNING,
									"MuxView unable to send RT simulation command");
								}
							}
						}
					});
					MenuItem enableA = new MenuItem(menu, SWT.PUSH);
					enableA.setText("Emulate RT (Pri Only)");
					enableA.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (muxProbe != null) {
								byte[] cmd = new byte[4];
								cmd[0] = 'R'; // RT simulation cmd
								cmd[1] = (byte) selectedChannel;
								cmd[2] = (byte) selectedRt;
								cmd[3] = (byte) 1;
								try {
									muxProbe.command(cmd);
								} catch (RemoteException ex) {
									OseeLog.log(MuxToolPlugin.class, Level.WARNING,
									"MuxView unable to send RT simulation command");
								}
							}
						}
					});
					MenuItem enableB = new MenuItem(menu, SWT.PUSH);
					enableB.setText("Emulate RT (Sec only)");
					enableB.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (muxProbe != null) {
								byte[] cmd = new byte[4];
								cmd[0] = 'R'; // RT simulation cmd
								cmd[1] = (byte) selectedChannel;
								cmd[2] = (byte) selectedRt;
								cmd[3] = (byte) 2;
								try {
									muxProbe.command(cmd);
								} catch (RemoteException ex) {
									OseeLog.log(MuxToolPlugin.class, Level.WARNING,
									"MuxView unable to send RT simulation command");
								}
							}
						}
					});
					new MenuItem(menu, SWT.SEPARATOR);
					MenuItem disableBoth = new MenuItem(menu, SWT.PUSH);
					disableBoth.setText("Monitor RT (Pri + Sec)");
					disableBoth.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (muxProbe != null) {
								byte[] cmd = new byte[4];
								cmd[0] = 'R'; // RT simulation cmd
								cmd[1] = (byte) selectedChannel;
								cmd[2] = (byte) selectedRt;
								cmd[3] = (byte) 0;
								try {
									muxProbe.command(cmd);
								} catch (RemoteException ex) {
									OseeLog.log(MuxToolPlugin.class, Level.WARNING,
									"MuxView unable to send RT simulation command");
								}
							}
						}
					});

					// draws pop up menu:
					Point pt = new Point(event.x, event.y);
					pt = table.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}
		});

	}

	/**
	 * Create the Datawords Tree
	 */
	private void createDwordTable(final TableViewer parent) {
		final Table table = parent.getTable();
		GridData gridData = new GridData();
		int height = table.getItemHeight() * 2;
		Rectangle trim = table.computeTrim(0, 0, 0, height);
		gridData.heightHint = trim.height;
		table.setLayoutData(gridData);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		TableColumn column = new TableColumn(table, SWT.CENTER, 0);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 1);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 2);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 3);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 4);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 5);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 6);
		column.setWidth(50);
		column = new TableColumn(table, SWT.CENTER, 7);
		column.setWidth(50);

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				final TableCursor cursor = new TableCursor(table, SWT.NONE);
				final ControlEditor editor = new ControlEditor(cursor);
				editor.grabHorizontal = true;
				editor.grabVertical = true;

				cursor.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						final Text text = new Text(cursor, SWT.NONE);
						text.setTextLimit(4);
						TableItem row = cursor.getRow();
						int column = cursor.getColumn();
						text.setText(row.getText(column));
						text.addKeyListener(new KeyAdapter() {
							public void keyPressed(KeyEvent e) {
								// close the text editor and copy the data over
								// when the user hits "ENTER"
								if (e.character == SWT.CR) {
									TableItem row = cursor.getRow();
									int column = cursor.getColumn();
									int newVal = Integer.parseInt(text.getText(), 16);
									row.setText(column, text.getText());
									text.dispose();
									cursor.dispose();
									if (muxProbe != null) {
										byte[] cmd = new byte[100];
										int index = 0;
										cmd[index++] = 'S'; // set 1553 data cmd
										cmd[index++] = (byte) selectedChannel;
										cmd[index++] = (byte) selectedRt;
										cmd[index++] = (byte) selectedTR;
										cmd[index++] = (byte) selectedSubaddr;
										Object[] o = ((DatawordModel) parent.getInput()).getChildren();
										((RowNode) o[table.indexOf(row)]).setDataword(newVal, column);
										for (Object r : o) {
											for (int i = 0; i < 16; i++) {
												cmd[index++] = ((RowNode) r).getDatabyte(i);
											}
										}
										try {
											muxProbe.command(cmd);
										} catch (RemoteException ex) {
											OseeLog.log(MuxToolPlugin.class, Level.WARNING,
											"MuxView unable to send RT simulation command");
										}
									}
								}
								// close the text editor when the user hits
								// "ESC"
								if (e.character == SWT.ESC) {
									text.dispose();
									cursor.dispose();
								}
								// allow only hexadecimal characters, backspace,
								// delete,
								// left and right arrow keys
								if (e.character >= 0x30 && e.character <= 0x39 || e.character >= 0x41 && e.character <= 0x46 || e.character >= 0x61 && e.character <= 0x66 || e.character == SWT.BS || e.character == SWT.DEL || e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT)
									e.doit = true;
								else
									e.doit = false;
							}
						});
						// close the text editor when the user clicks away
						text.addFocusListener(new FocusAdapter() {
							public void focusLost(FocusEvent e) {
								text.dispose();
								cursor.dispose();
							}
						});
						editor.setEditor(text);
						text.setFocus();
					}
				});
				table.deselectAll();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// msgViewer1.getControl().setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.ui.test.manager.data.plugin.TestToolViewPart#dispose
	 * ()
	 */
	@Override
	public void dispose() {
		MuxToolPlugin.getDefault().getOteClientService().removeConnectionListener(this);
		ITestEnvironment env = MuxToolPlugin.getDefault().getOteClientService().getConnectedEnvironment();
		if (env != null) {
			try {
				((ITestEnvironmentMessageSystem) env).removeInstrumentationRegistrationListener(exportedThis);
			} catch (RemoteException ex) {
				OseeLog.log(MuxView.class, Level.WARNING, "could not deregister instrumentation registration listener", ex);
			}
			IServiceConnector connector = MuxToolPlugin.getDefault().getOteClientService().getConnector();
			try {
				connector.unexport(this);
			} catch (Exception ex) {
				OseeLog.log(MuxView.class, Level.WARNING, "could not unexport this", ex);
			}
		}
		if (muxProbe != null) {
			try {
				muxProbe.unregister(thread.address);
			} catch (RemoteException ex) {
				OseeLog.log(MuxView.class, Level.WARNING, "could not disconnect from mux probe", ex);
			}
			muxProbe = null;
		}
		if (task != null) {
			task.stop();
		}
		thread.shutdown();

		super.dispose();
	}

	class ListenerThread extends Thread {
		private volatile boolean done = false;
		private final DatagramChannel channel;
		private final InetSocketAddress address;
		
		public ListenerThread()throws IOException {
			super("Mux View Listener Thread");
			channel = DatagramChannel.open();
			port = PortUtil.getInstance().getValidPort();
			address = new InetSocketAddress(InetAddress.getLocalHost(), port);
			channel.socket().bind(address);
			OseeLog.log(MuxToolPlugin.class, Level.INFO,
					"MuxView connection - host: " + address.getHostName() + "    port: " + address.getPort());
		}

		public void run() {
			final ByteBuffer buffer = ByteBuffer.wrap(new byte[256]);
			try {
				while (!done) {
					buffer.clear();
					channel.receive(buffer);
					buffer.flip();
					switch (buffer.array()[0]) {
					case 1:
						chan1Msgs.onDataAvailable(buffer);
						chan1Dwrds.onDataAvailable(buffer);
						break;
					case 2:
						chan2Msgs.onDataAvailable(buffer);
						chan2Dwrds.onDataAvailable(buffer);
						break;
					case 3:
						chan3Msgs.onDataAvailable(buffer);
						chan3Dwrds.onDataAvailable(buffer);
						break;
					case 5:
						chan5Msgs.onDataAvailable(buffer);
						chan5Dwrds.onDataAvailable(buffer);
					}
				}
			} catch (InterruptedIOException e) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				if (!isInterrupted()) {
					OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "Interrupted", e);
				}
			} finally {
				try {
					channel.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		public void shutdown() {
			done = true;
			interrupt();
			try {
				thread.join(5000);
				assert !channel.isOpen();
			} catch (InterruptedException e) {
				OseeLog.log(MuxView.class, Level.SEVERE, "could not join wiht listener thread", e);
			}
		}
	}

	@Override
	public void onConnectionLost(IServiceConnector connector, IHostTestEnvironment testHost) {
		handleConnectionLostStatus();
	}

	@Override
	public void onPostConnect(final ConnectionEvent event) {
		final ITestEnvironmentMessageSystem environment = (ITestEnvironmentMessageSystem) event.getEnvironment();
		if (environment != null) {
			// we are connected
			try {
				exportedThis = (IInstrumentationRegistrationListener) event.getConnector().findExport(MuxView.this);
				if (exportedThis == null) {
					exportedThis = (IInstrumentationRegistrationListener) event.getConnector().export(MuxView.this);
				}
				environment.addInstrumentationRegistrationListener(exportedThis);
			} catch (Exception ex) {
				OseeLog.log(MuxView.class, Level.SEVERE, "could not register for instrumentation events", ex);
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Connect Error", "Could not register for instrumentation events. See Error Log for details");
					}

				});

			}
		}

	}

	private void detach() {

	}

	@Override
	public void onPreDisconnect(ConnectionEvent event) {
		final ITestEnvironmentMessageSystem environment = (ITestEnvironmentMessageSystem) event.getEnvironment();
		try {
			environment.removeInstrumentationRegistrationListener(exportedThis);
		} catch (RemoteException ex1) {
			OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "Problem unregistering instrumentation registration listener", ex1);
		}

		if (muxProbe != null) {
			try {
				muxProbe.unregister(thread.address);
			} catch (RemoteException ex) {
				OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "Problem unregistering socket address", ex);
			} finally {
				muxProbe = null;
			}
		}
		handleConnectionLostStatus();
	}

	private void handleConnectionLostStatus() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				updateColors(false);
				// we are not connected
				if (task != null) {
					task.stop();
				}
			}
		});
	}

	private void updateColors(boolean connected) {
		msgViewer1.getTable().setBackground(connected ? WHITE : GRAY);
		msgViewer2.getTable().setBackground(connected ? WHITE : GRAY);
		msgViewer3.getTable().setBackground(connected ? WHITE : GRAY);
		msgViewer5.getTable().setBackground(connected ? WHITE : GRAY);
		dataViewer1.getTable().setBackground(connected ? WHITE : GRAY);
		dataViewer2.getTable().setBackground(connected ? WHITE : GRAY);
		dataViewer3.getTable().setBackground(connected ? WHITE : GRAY);
		dataViewer5.getTable().setBackground(connected ? WHITE : GRAY);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.message.IInstrumentationRegistrationListener#onDeregistered(java.lang.String)
	 */
	@Override
	public void onDeregistered(String name) throws RemoteException{
		if (muxProbe != null && name.equals("MUXIO")) {
			muxProbe = null;
			handleConnectionLostStatus();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.message.IInstrumentationRegistrationListener#onRegistered(java.lang.String, org.eclipse.osee.ote.message.instrumentation.IOInstrumentation)
	 */
	@Override
	public void onRegistered(String name, IOInstrumentation instrumentation) throws RemoteException{
		try {
			if (muxProbe == null && name.equals("MUXIO")) {
				muxProbe = instrumentation;
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (task != null) task.start();
						updateColors(true);
					}

				});
				muxProbe.register(thread.address);


			}
		} catch (RemoteException ex) {
			OseeLog.log(MuxToolPlugin.class, Level.SEVERE,
					"Problem registering socket address with remote instrumentation service", ex);
		}

	}
}
