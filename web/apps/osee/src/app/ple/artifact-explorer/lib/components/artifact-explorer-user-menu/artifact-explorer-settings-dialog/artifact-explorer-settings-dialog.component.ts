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
import { Component, Inject, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MatButtonToggle,
	MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import { ArtifactExplorerPreferencesService } from '../../../services/artifact-explorer-preferences.service';
import { artifactExplorerUserPreferences } from '../../../types/user-preferences';

@Component({
	selector: 'osee-artifact-explorer-settings-dialog',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
		MatButtonToggleGroup,
		MatButtonToggle,
		FormsModule,
		MatLabel,
	],
	templateUrl: './artifact-explorer-settings-dialog.component.html',
})
export class ArtifactExplorerSettingsDialogComponent {
	private preferencesService = inject(ArtifactExplorerPreferencesService);

	preferences = this.preferencesService.artifactExplorerPreferences;

	constructor(
		@Inject(MAT_DIALOG_DATA) public data: artifactExplorerUserPreferences
	) {}
}
