package org.eclipse.osee.ote.ui.builder;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class OTEBuilderPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String DO_JAR_PACKAGING = "DO_JAR_PACKAGING";
	
	private Button oteBuilder;
	
	public OTEBuilderPreferencePage() {
	}

	public OTEBuilderPreferencePage(String title) {
		super(title);
	}

	public OTEBuilderPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		oteBuilder = new Button(parent, SWT.CHECK);
		oteBuilder.setText("Enable the OTE Jar Builder.  This will enable client disconnect and not disrupt a batch.");
		Preferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		oteBuilder.setSelection(prefs.getBoolean(OTEBuilderPreferencePage.DO_JAR_PACKAGING, false));
		oteBuilder.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
			   Preferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			   prefs.putBoolean(OTEBuilderPreferencePage.DO_JAR_PACKAGING, oteBuilder.getSelection());
			   try {
               prefs.flush();
            } catch (BackingStoreException e1) {
               e1.printStackTrace();
            }
			}
		});
		return parent;
	}
	
}
