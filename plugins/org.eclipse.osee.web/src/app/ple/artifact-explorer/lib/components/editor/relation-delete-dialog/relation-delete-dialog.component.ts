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
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
	selector: 'osee-relation-delete-dialog',
	standalone: true,
	imports: [MatDialogModule, MatButtonModule],
	templateUrl: './relation-delete-dialog.component.html',
})
export class RelationDeleteDialogComponent {
	constructor(
		@Inject(MAT_DIALOG_DATA)
		public data: { sideAName: string; sideBName: string }
	) {}
}
