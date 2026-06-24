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
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { artifactExplorerUserPreferences } from '../../../types/user-preferences';

@Component({
	selector: 'osee-artifact-explorer-settings-dialog',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
	],
	templateUrl: './artifact-explorer-settings-dialog.component.html',
})
export class ArtifactExplorerSettingsDialogComponent {
	data = inject<artifactExplorerUserPreferences>(MAT_DIALOG_DATA);
}
