package org.eclipse.osee.framework.ui.wizard;

import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.xtext.ui.core.wizard.IProjectInfo;
import org.eclipse.xtext.ui.core.wizard.XtextNewProjectWizard;

public class OseeTypesNewProjectWizard extends XtextNewProjectWizard {

	private WizardNewProjectCreationPage mainPage;

	public OseeTypesNewProjectWizard() {
		super();
		setWindowTitle("New OseeTypes Project");
	}

	/**
	 * Use this method to add pages to the wizard.
	 * The one-time generated version of this class will add a default new project page to the wizard.
	 */
	public void addPages() {
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
		mainPage.setTitle("OseeTypes Project");
		mainPage.setDescription("Create a new OseeTypes project.");
		addPage(mainPage);
	}

	/**
	 * Use this method to read the project settings from the wizard pages and feed them into the project info class.
	 */
	@Override
	protected IProjectInfo getProjectInfo() {
		org.eclipse.osee.framework.ui.wizard.OseeTypesProjectInfo projectInfo = new org.eclipse.osee.framework.ui.wizard.OseeTypesProjectInfo();
		projectInfo.setProjectName(mainPage.getProjectName());
		return projectInfo;
	}

}
