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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { UiService } from '@osee/shared/services';
import { filter, tap } from 'rxjs';

@Component({
	selector: 'osee-snackbar-wrapper',
	imports: [AsyncPipe],
	templateUrl: './snackbar-wrapper.component.html',
})
export class SnackbarWrapperComponent {
	private snackBar = inject(MatSnackBar);
	private uiService = inject(UiService);

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
	imports: [AsyncPipe, MatButton],
	templateUrl: './snackbar-wrapper-internal.component.html',
})
export class SnackbarWrapperInternalComponent {
	private uiService = inject(UiService);
	snackbarRef =
		inject<MatSnackBarRef<SnackbarWrapperInternalComponent>>(
			MatSnackBarRef
		);

	errorText = this.uiService.errorText;
	errorDetails = this.uiService.errorDetails;

	showDetails = false;

	toggleDetails() {
		this.showDetails = !this.showDetails;
	}

	closeSnackbar() {
		this.snackbarRef.dismiss();
	}
}
