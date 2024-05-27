/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { MatDialogRef } from '@angular/material/dialog';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import { PlatformType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-new-type-dialog',
	templateUrl: './new-type-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [NewTypeFormComponent],
})
export class NewTypeDialogComponent {
	private dialog = inject(MatDialogRef<NewTypeDialogComponent>);
	closeDialog(result: PlatformType) {
		this.dialog.close(result);
	}
}
