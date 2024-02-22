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
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { AdvancedSearchCriteria } from '../../../../../types/artifact-search';
import { MatButtonModule } from '@angular/material/button';
import { AdvancedSearchFormComponent } from '../advanced-search-form/advanced-search-form.component';

@Component({
	selector: 'osee-advanced-search-dialog',
	standalone: true,
	imports: [MatButtonModule, MatDialogModule, AdvancedSearchFormComponent],
	templateUrl: './advanced-search-dialog.component.html',
})
export class AdvancedSearchDialogComponent {
	constructor(@Inject(MAT_DIALOG_DATA) public data: AdvancedSearchCriteria) {}
}
