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
import { CdkTrapFocus } from '@angular/cdk/a11y';
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { AdvancedSearchCriteria } from '../../../../../types/artifact-search';
import { AdvancedSearchFormComponent } from '../advanced-search-form/advanced-search-form.component';

@Component({
	selector: 'osee-advanced-search-dialog',
	standalone: true,
	imports: [
		MatDialogContent,
		MatDialogTitle,
		MatDialogActions,
		MatButton,
		CdkTrapFocus,
		MatDialogClose,
		AdvancedSearchFormComponent,
	],
	templateUrl: './advanced-search-dialog.component.html',
})
export class AdvancedSearchDialogComponent {
	data = inject<AdvancedSearchCriteria>(MAT_DIALOG_DATA);
}
