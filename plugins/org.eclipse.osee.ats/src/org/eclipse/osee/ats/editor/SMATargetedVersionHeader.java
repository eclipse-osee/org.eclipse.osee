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
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMATargetedVersionHeader extends Composite {

	private static String TARGET_VERSION = "Target Version:";
	Label valueLabel;

	public SMATargetedVersionHeader(Composite parent, int style, final StateMachineArtifact sma, XFormToolkit toolkit) {
		super(parent, style);
		setLayoutData(new GridData());
		setLayout(ALayout.getZeroMarginLayout(2, false));
		toolkit.adapt(this);

		try {
			if (!sma.isCancelled() && !sma.isCompleted()) {
				Hyperlink link = toolkit.createHyperlink(this, TARGET_VERSION, SWT.NONE);
				link.addHyperlinkListener(new IHyperlinkListener() {

					public void linkEntered(HyperlinkEvent e) {
					}

					public void linkExited(HyperlinkEvent e) {
					}

					public void linkActivated(HyperlinkEvent e) {
						try {
							if (PromptChangeUtil.promptChangeVersion(sma,
										AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, false)) {
								updateLabel(sma);
								sma.getEditor().onDirtied();
							}
						} catch (Exception ex) {
							OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
						}
					}
				});
			} else {
				Label origLabel = toolkit.createLabel(this, TARGET_VERSION);
				origLabel.setLayoutData(new GridData());
			}

			valueLabel = toolkit.createLabel(this, "Not Set");
			valueLabel.setLayoutData(new GridData());
			updateLabel(sma);

		} catch (OseeCoreException ex) {
			Label errorLabel = toolkit.createLabel(this, "Error: " + ex.getLocalizedMessage());
			errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
			OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
		}

	}

	private void updateLabel(StateMachineArtifact sma) throws OseeCoreException {
		String value = "Not Set";
		if (sma.getTargetedForVersion() != null) {
			value = sma.getTargetedForVersion().getName();
		}
		valueLabel.setText(value);
		valueLabel.getParent().layout();
	}

}
