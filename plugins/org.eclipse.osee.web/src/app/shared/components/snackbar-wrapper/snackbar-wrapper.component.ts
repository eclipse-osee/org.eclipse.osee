/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	MatSnackBar,
	MatSnackBarModule,
	MatSnackBarRef,
} from '@angular/material/snack-bar';
import { filter, tap } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-snackbar-wrapper',
	standalone: true,
	imports: [CommonModule, MatSnackBarModule],
	templateUrl: './snackbar-wrapper.component.html',
})
export class SnackbarWrapperComponent {
	constructor(
		private snackBar: MatSnackBar,
		private uiService: UiService
	) {}

	errors = this.uiService.errorText.pipe(
		filter((text) => text !== ''),
		tap((_) => this.openSnackBar())
	);

	openSnackBar() {
		this.snackBar.openFromComponent(SnackbarWrapperInternalComponent, {
			horizontalPosition: 'center',
			verticalPosition: 'top',
			panelClass: 'snackbar-styles',
		});
	}
}

@Component({
	selector: 'osee-snackbar-internal',
	standalone: true,
	imports: [CommonModule, MatSnackBarModule, MatButtonModule],
	templateUrl: './snackbar-wrapper-internal.component.html',
})
export class SnackbarWrapperInternalComponent {
	constructor(
		private uiService: UiService,
		public snackbarRef: MatSnackBarRef<SnackbarWrapperInternalComponent>
	) {}

	errorText = this.uiService.errorText;
	errorDetails = this.uiService.errorDetails;

	showDetails: boolean = false;

	toggleDetails() {
		this.showDetails = !this.showDetails;
	}

	closeSnackbar() {
		this.snackbarRef.dismiss();
	}
}
