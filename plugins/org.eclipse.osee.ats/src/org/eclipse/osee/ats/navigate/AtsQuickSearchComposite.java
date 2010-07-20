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
package org.eclipse.osee.ats.navigate;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorOperationProvider;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class AtsQuickSearchComposite extends Composite {

	Text searchArea;
	XCheckBox completeCancelledCheck;

	public AtsQuickSearchComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(ALayout.getZeroMarginLayout(4, false));
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button searchButton = new Button(this, SWT.PUSH);
		searchButton.setText("Search:");
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSearch();
			}
		});
		searchButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mouseEvent) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent mouseEvent) {
				if (mouseEvent.button == 3) {
					try {
						File file = AtsPlugin.getInstance().getPluginFile("support/OSEEDay.wav");
						Program.launch(file.getAbsolutePath());
					} catch (Exception ex) {
						OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
			}
		});

		GridData gridData = new GridData(SWT.RIGHT, SWT.NONE, false, false);
		gridData.heightHint = 15;
		searchArea = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		searchArea.setFont(parent.getFont());
		searchArea.setLayoutData(gd);
		searchArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.character == '\r') {
					handleSearch();
				}
			}
		});
		searchArea.setToolTipText("ATS Quick Search - Type in a search string.");
		completeCancelledCheck = new XCheckBox("IC");
		completeCancelledCheck.createWidgets(this, 2);
		completeCancelledCheck.setToolTip("Include completed/cancelled ATS Artifacts");

	}

	private void handleSearch() {
		if (!Strings.isValid(searchArea.getText())) {
			AWorkbench.popup("Please enter search string");
			return;
		}
		AtsQuickSearchData data =
					new AtsQuickSearchData("ATS Quick Search", searchArea.getText(), completeCancelledCheck.get());
		WorldEditor.open(new WorldEditorOperationProvider(new AtsQuickSearchOperation(data)));
	}
}
