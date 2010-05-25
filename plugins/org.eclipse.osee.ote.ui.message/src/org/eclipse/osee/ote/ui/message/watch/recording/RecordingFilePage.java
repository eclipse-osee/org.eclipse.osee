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

import java.io.File;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RecordingFilePage extends WizardPage {
	private static final String SECTION = "ote.ui.message.settings.section";
	private static final String PATH_KEY = "ote.ui.message.settings.rec_file_path";
	
	private String selectedFile;
	private Text filePathTxt;

	public RecordingFilePage() {
		super("filePage");
		setTitle("Select A File");
		setDescription("This is the file that will contain the recorded data.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		Widgets.setFormLayout(composite);
		Label fileLbl = new Label(composite, SWT.RIGHT);
		fileLbl.setText("Path:");
		filePathTxt = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		Button filePickBtn = new Button(composite, SWT.PUSH);

		Widgets.attachToParent(filePickBtn, SWT.RIGHT, 100, -10);
		Widgets.attachToParent(filePickBtn, SWT.TOP, 50, 0);

		Widgets.attachToParent(fileLbl, SWT.LEFT, 0, 10);

		Widgets.attachToControl(filePathTxt, filePickBtn, SWT.RIGHT, SWT.LEFT,
				-5);
		Widgets.attachToControl(filePathTxt, filePickBtn, SWT.TOP, SWT.CENTER,
				0);
		Widgets.attachToControl(filePathTxt, fileLbl, SWT.LEFT, SWT.RIGHT, 5);

		Widgets.attachToControl(fileLbl, filePathTxt, SWT.TOP, SWT.CENTER, 0);

		filePickBtn.setText("Browse...");
		filePickBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IDialogSettings settings = Activator.getDefault().getDialogSettings();
				IDialogSettings section = settings.getSection(SECTION);
				if (section == null) {
					section = settings.addNewSection(SECTION);
				}
				
				FileDialog dialog = new FileDialog(Display.getDefault()
						.getActiveShell(), SWT.SAVE);
				String path = section.get(PATH_KEY);
				if (path != null) {
					File file = new File(path);
					if (file.exists() && file.isDirectory()) {
						dialog.setFilterPath(path);
					}
				}
				dialog.setFilterExtensions(new String[]{"*.csv"});
				String result = dialog.open();
				if (result != null) {
					File file = new File(result);
					section.put(PATH_KEY, file.getParent());
					
					int filterIndex = dialog.getFilterIndex();
					String[] extensions = dialog.getFilterExtensions();
					if(filterIndex >= 0 && filterIndex < extensions.length){
						if(!result.endsWith(extensions[filterIndex].substring(1))){
							result = result + extensions[filterIndex].substring(1);
						}
					}
					selectedFile = result;
					selectSource();
				}
			}

		});
		if (selectedFile != null) {
			selectSource();
		} else {
			setPageComplete(false);
		}
		setControl(composite);
	}

	private void selectSource() {
		filePathTxt.setText(selectedFile);
		setPageComplete(true);
	}

	public String getFileName(){
		return filePathTxt.getText();
	}

}
