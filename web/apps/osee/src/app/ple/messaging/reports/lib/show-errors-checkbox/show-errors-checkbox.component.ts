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
import {
	Component,
	effect,
	inject,
	input,
	output,
	signal,
	viewChild,
} from '@angular/core';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import { ReportsHideErrorColoringDialogComponent } from '../reports-hide-error-coloring-dialog/reports-hide-error-coloring-dialog.component';
import { ConnectionValidationResult } from '@osee/messaging/shared/types';
import { first, tap } from 'rxjs';

@Component({
	selector: 'osee-show-errors-checkbox',
	standalone: true,
	imports: [MatCheckbox, MatLabel],
	template: `
		<mat-checkbox
			class="primary-checkbox tw-block"
			#checkbox
			[checked]="showErrors()"
			(change)="toggleErrorColoring($event)">
			<mat-label>Show error coloring</mat-label>
		</mat-checkbox>
	`,
})
export class ShowErrorsCheckboxComponent {
	validationResults = input.required<ConnectionValidationResult>();
	showErrorColoring = output<boolean>();

	private dialog = inject(MatDialog);

	private checkbox = viewChild.required(MatCheckbox);

	showErrors = signal<boolean>(true, { equal: () => false });
	private _showErrorsEffect = effect(() => {
		this.checkbox().checked = this.showErrors();
		this.showErrorColoring.emit(this.showErrors());
	});

	toggleErrorColoring(event: MatCheckboxChange) {
		if (event.checked) {
			this.showErrors.set(event.checked);
			return;
		}
		const dialogRef = this.dialog.open(
			ReportsHideErrorColoringDialogComponent,
			{ data: this.validationResults(), minWidth: '60vw' }
		);
		dialogRef
			.afterClosed()
			.pipe(
				first(),
				tap((res) => {
					this.showErrors.set(!res.value);
				})
			)
			.subscribe();
	}
}
