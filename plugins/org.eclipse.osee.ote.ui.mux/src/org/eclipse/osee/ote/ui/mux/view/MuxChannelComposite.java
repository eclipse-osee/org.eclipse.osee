/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.mux.view;

import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.logging.Level;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class MuxChannelComposite extends Composite {

	private TableViewer msgViewer;
	private TableViewer dataViewer;
	private MessageModel chanMsgs;
	private DatawordModel chanDwrds;
	private final int selectedChannel;
	private int selectedRt;
	private int selectedTR;
	private int selectedSubaddr;
	private IOInstrumentation muxProbe;
	private final static Color GRAY = Displays.getSystemColor(SWT.COLOR_GRAY);
	private final static Color WHITE = Displays.getSystemColor(SWT.COLOR_WHITE);

	public MuxChannelComposite(Composite parent, int style, int channel) {
		super(parent, style);
		createPartControl(parent);
		this.selectedChannel = channel;
	}

	public void setMuxProbe(IOInstrumentation muxProbe) {
		this.muxProbe = muxProbe;
	}

	private void createPartControl(Composite parent) {
		GridLayout chanLayout = new GridLayout(1, false);
		this.setLayout(chanLayout);
		
		msgViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		msgViewer.setLabelProvider(new MuxMsgLabelProvider());
		msgViewer.setSorter(new ViewerSorter());
		msgViewer.setContentProvider(new MuxMsgContentProvider());

		createMsgTable(msgViewer);

		Composite bottom1 = new Composite(this, SWT.NONE);
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
		dataViewer = new TableViewer(datawords1, SWT.BORDER
				| SWT.FULL_SELECTION);
		dataViewer.setContentProvider(new DatawordContentProvider());
		dataViewer.setLabelProvider(new DatawordLabelProvider());
		createDwordTable(dataViewer);
		msgViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				MessageNode node = (MessageNode) selection.getFirstElement();
				if (node != null) {
					selectedRt = node.getRt();
					selectedTR = node.getTransmitReceive();
					selectedSubaddr = node.getSubaddress();
					chanDwrds.setCurrentNode(node.getName());
				}
			}
		});
		final Button reset1 = new Button(bottom1, SWT.PUSH);
		reset1.setText("Reset Display");
		reset1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
				| GridData.GRAB_HORIZONTAL));
		reset1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRt = 0;
				selectedTR = 0;
				selectedSubaddr = 0;
				chanMsgs.removeMessages();
				chanDwrds.setCurrentNode(null);
			}
		});

		msgViewer.setInput(chanMsgs = new MessageModel());
		dataViewer.setInput(chanDwrds = new DatawordModel());
		updateColors(false);
	}

	private void createMsgTable(TableViewer parent) {
		final Table table = parent.getTable();
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
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
			@Override
			public void mouseDown(final MouseEvent event) {
				if (event.button == 3) {
					Menu menu = new Menu(table.getShell(), SWT.POP_UP);
					MenuItem enableBoth = new MenuItem(menu, SWT.PUSH);
					enableBoth.setText("Emulate RT (Pri + Sec)");
					enableBoth.addSelectionListener(new SelectionAdapter() {

						@Override
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
									OseeLog.log(MuxToolPlugin.class,
											Level.WARNING,
											"MuxView unable to send RT simulation command");
								}
							}
						}
					});
					MenuItem enableA = new MenuItem(menu, SWT.PUSH);
					enableA.setText("Emulate RT (Pri Only)");
					enableA.addSelectionListener(new SelectionAdapter() {
						@Override
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
									OseeLog.log(MuxToolPlugin.class,
											Level.WARNING,
											"MuxView unable to send RT simulation command");
								}
							}
						}
					});
					MenuItem enableB = new MenuItem(menu, SWT.PUSH);
					enableB.setText("Emulate RT (Sec only)");
					enableB.addSelectionListener(new SelectionAdapter() {
						@Override
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
									OseeLog.log(MuxToolPlugin.class,
											Level.WARNING,
											"MuxView unable to send RT simulation command");
								}
							}
						}
					});
					new MenuItem(menu, SWT.SEPARATOR);
					MenuItem disableBoth = new MenuItem(menu, SWT.PUSH);
					disableBoth.setText("Monitor RT (Pri + Sec)");
					disableBoth.addSelectionListener(new SelectionAdapter() {
						@Override
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
									OseeLog.log(MuxToolPlugin.class,
											Level.WARNING,
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
			@Override
			public void widgetSelected(SelectionEvent e) {

				final TableCursor cursor = new TableCursor(table, SWT.NONE);
				final ControlEditor editor = new ControlEditor(cursor);
				editor.grabHorizontal = true;
				editor.grabVertical = true;

				cursor.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						final Text text = new Text(cursor, SWT.NONE);
						text.setTextLimit(4);
						TableItem row = cursor.getRow();
						int column = cursor.getColumn();
						text.setText(row.getText(column));
						text.addKeyListener(new KeyAdapter() {

							@Override
							public void keyPressed(KeyEvent e) {
								// close the text editor and copy the data over
								// when the user hits "ENTER"
								if (e.character == SWT.CR) {
									TableItem row = cursor.getRow();
									int column = cursor.getColumn();
									int newVal = Integer.parseInt(
											text.getText(), 16);
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
										Object[] o = ((DatawordModel) parent
												.getInput()).getChildren();
										((RowNode) o[table.indexOf(row)])
												.setDataword(newVal, column);
										for (Object r : o) {
											for (int i = 0; i < 16; i++) {
												cmd[index++] = ((RowNode) r)
														.getDatabyte(i);
											}
										}
										try {
											muxProbe.command(cmd);
										} catch (RemoteException ex) {
											OseeLog.log(MuxToolPlugin.class,
													Level.WARNING,
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
								if (e.character >= 0x30 && e.character <= 0x39
										|| e.character >= 0x41
										&& e.character <= 0x46
										|| e.character >= 0x61
										&& e.character <= 0x66
										|| e.character == SWT.BS
										|| (e.character == SWT.DEL && (e.stateMask & SWT.CTRL) == 0)
										|| e.keyCode == SWT.ARROW_LEFT
										|| e.keyCode == SWT.ARROW_RIGHT) {
									e.doit = true;
								} else {
									e.doit = false;
								}
							}
						});
						// close the text editor when the user clicks away
						text.addFocusListener(new FocusAdapter() {
							@Override
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

	void updateColors(boolean connected) {
		msgViewer.getTable().setBackground(connected ? WHITE : GRAY);
		dataViewer.getTable().setBackground(connected ? WHITE : GRAY);
	}

	public void refresh() {
		msgViewer.refresh();
		dataViewer.refresh();
	}

	public void onDataAvailable(ByteBuffer buffer) {
		chanMsgs.onDataAvailable(buffer);
		chanDwrds.onDataAvailable(buffer);
	}
}
