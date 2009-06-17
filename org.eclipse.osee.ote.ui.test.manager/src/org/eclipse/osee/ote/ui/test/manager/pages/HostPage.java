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
package org.eclipse.osee.ote.ui.test.manager.pages;

import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class HostPage extends TestManagerPage {

	private static final String pageName = "Hosts";

	public HostPage(Composite parent, int style,
			TestManagerEditor parentTestManager) {
		super(parent, style, parentTestManager);
		createPage();
		TestManagerPlugin.getInstance().setHelp(this, "tm_hosts_page");
	}

	@Override
	public String getPageName() {
		return pageName;
	}

	protected void createPage() {
		super.createPage();
		Composite parent = (Composite) getContent();

		Group hostGroup = new Group(parent, SWT.NONE);
		hostGroup.setLayout(new GridLayout());
		hostGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		hostGroup.setText("Double click a Host to Connect/Disconnect");

		getTestManager().createHostWidget(hostGroup);
		computeScrollSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#
	 * areSettingsValidForRun()
	 */
	@Override
	public boolean areSettingsValidForRun() {
		return getTestManager().isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#restoreData()
	 */
	@Override
	public void restoreData() {
		// Do Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#saveData()
	 */
	@Override
	public void saveData() {
		// Do Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#getErrorMessage
	 * ()
	 */
	@Override
	public String getErrorMessage() {
		StringBuilder builder = new StringBuilder();
		if (areSettingsValidForRun() != true) {
			builder.append("Connect to a Test Server");
		}
		return builder.toString();
	}

	@Override
	public boolean onConnection(ConnectionEvent event) {
		return false;

	}

	@Override
	public boolean onDisconnect(ConnectionEvent event) {
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#onConnectionLost
	 * (org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment)
	 */
	@Override
	public boolean onConnectionLost(IHostTestEnvironment testHost) {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
	}


}
