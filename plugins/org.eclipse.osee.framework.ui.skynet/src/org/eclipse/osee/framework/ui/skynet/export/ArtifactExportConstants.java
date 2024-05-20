/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.export;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * Constants for the Artifact Export Wizard.
 *
 * @author Loren K. Ashley
 */

@SuppressWarnings("restriction")
class ArtifactExportConstants {

   static final String ARTIFACT_EXPORT_OPERATION_DEFAULT_STATUS_MESSAGE =
      "Failed to export one or more of the selected artifacts.";

   static final String ARTIFACT_EXPORT_OPERATION_EXPORT_ARTIFACT_PREFIX = "Export Artifact: ";

   static final String ARTIFACT_EXPORT_OPERATION_GET_CHILDREN_ERROR_TITLE = "Failed to get children of artifact.";

   static final String ARTIFACT_EXPORT_OPERATION_MAKE_PATH_ERROR_ARTIFACT_SEGMENT_TITLE = "Output Folder";

   static final String ARTIFACT_EXPORT_OPERATION_MAKE_PATH_ERROR_TITLE = "Failed to make output folder.";

   static final String ARTIFACT_EXPORT_OPERATION_MAKE_PATH_PREFIX = "Make Folder: ";

   static final String ARTIFACT_EXPORT_OPERATION_MONITOR_NAME = "Exporting Artifacts";

   static final String ARTIFACT_EXPORT_OPERATION_NAME = "OSEE Artifact Export";

   static final String ARTIFACT_EXPORT_OPERATION_NOT_RUN_ERROR_TITLE = "Operation has not been run.";

   static final String ARTIFACT_EXPORT_OPERATION_RENDER_ERROR_ARTIFACT_SEGMENT_TITLE = "Artifact Name";

   static final String ARTIFACT_EXPORT_OPERATION_RENDER_ERROR_TITLE = "Failed to render artifact.";

   //naughty access
   static final IDialogSettings DEFAULT_DIALOG_SETTINGS = WorkbenchPlugin.getDefault().getDialogSettings();

   static final String WINDOW_TITLE = "OSEE Artifact Export";

   static final String WIZARD_EXPORT_ERROR_SHORT_MESSAGE = "Failed to export one or more of the selected artifacts.";

   static final String WIZARD_EXPORT_ERROR_TITLE = "OSEE Artifact Export Error";

   static final String WIZARD_NAME = "ArtifactExportWizard";

   static final String WIZARD_PAGE_1_BROWSE_BUTTON_TEXT = "Browse...";

   static final String WIZARD_PAGE_1_BROWSE_TITLE = ArtifactExportConstants.WINDOW_TITLE + " - Select Export Folder";

   static final boolean WIZARD_PAGE_1_CANCEL_ON_ERROR_DEFAULT = true;

   static final String WIZARD_PAGE_1_DESCRIPTION =
      "Select the output directory and click \"Finish\" to export the selected artifacts.";

   static final boolean WIZARD_PAGE_1_DISABLE_ERROR_POP_UPS_DEFAULT = true;

   static final String WIZARD_PAGE_1_ERROR_DIALOG_TITLE_DEFAULT = ArtifactExportConstants.WIZARD_EXPORT_ERROR_TITLE;

   static final String WIZARD_PAGE_1_ERROR_DIALOG_TITLE_INVALID_SELECTION =
      ArtifactExportConstants.WIZARD_EXPORT_ERROR_TITLE + " - Invalid Selection";

   //naughty access
   static final ImageDescriptor WIZARD_PAGE_1_ICON =
      WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_EXPORT_WIZ);

   static final String WIZARD_PAGE_1_INVALID_SELECTION_ERROR_SEGMENT_TITLE = "Invalid Selections";

   static final String WIZARD_PAGE_1_INVALID_SELECTION_ERROR_TITLE = "Selections are expected to be of type Artifact.";

   static final String WIZARD_PAGE_1_NAME = "ArtifactExportWizardPage1";

   static final String WIZARD_PAGE_1_PATH_SELECTION_DEFAULT_PATH = System.getProperty("user.dir");

   static final String WIZARD_PAGE_1_PATH_SELECTION_LABEL = "Export To Directory:";

   static final String WIZARD_PAGE_1_STORE_CACNEL_ON_ERROR_ID =
      ArtifactExportConstants.WIZARD_PAGE_1_NAME + ".CANCEL_ON_ERROR_ID"; //$NON-NLS-1$

   static final String WIZARD_PAGE_1_STORE_DESTINATION_NAMES_ID =
      ArtifactExportConstants.WIZARD_PAGE_1_NAME + ".STORE_DESTINATION_NAMES_ID"; //$NON-NLS-1$

   static final String WIZARD_PAGE_1_STORE_DISABLE_ERROR_POP_UPS_ID =
      ArtifactExportConstants.WIZARD_PAGE_1_NAME + ".DISABLE_ERROR_POP_UPS_ID"; //$NON-NLS-1$

   static final String WIZARD_PAGE_1_TITLE = "OSEE Artifact Preview Export";
}

/* EOF */
