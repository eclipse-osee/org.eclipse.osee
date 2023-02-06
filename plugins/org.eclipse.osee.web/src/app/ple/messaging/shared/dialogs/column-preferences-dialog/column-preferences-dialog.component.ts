/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	defaultEditStructureProfile,
	defaultEditElementProfile,
	defaultViewStructureProfile,
	defaultViewElementProfile,
} from '@osee/messaging/shared/constants';
import {
	EditAuthService,
	HeaderService,
} from '@osee/messaging/shared/services';
import {
	settingsDialogData,
	structure,
	element,
} from '@osee/messaging/shared/types';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-column-preferences-dialog',
	templateUrl: './column-preferences-dialog.component.html',
	styleUrls: ['./column-preferences-dialog.component.sass'],
	standalone: true,
	imports: [
		MatDialogModule,
		MatCheckboxModule,
		FormsModule,
		MatFormFieldModule,
		MatTooltipModule,
		NgIf,
		MatButtonModule,
		MatListModule,
		NgFor,
		AsyncPipe,
	],
})
export class ColumnPreferencesDialogComponent {
	editability: Observable<boolean> =
		this.editAuthService.branchEditability.pipe(map((x) => x?.editable));

	constructor(
		public dialogRef: MatDialogRef<ColumnPreferencesDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: settingsDialogData,
		private editAuthService: EditAuthService,
		private _headerService: HeaderService
	) {
		this.editAuthService.BranchIdString = data.branchId;
	}

	onNoClick() {
		this.dialogRef.close();
	}

	getHeaderByName(
		value: keyof structure | keyof element,
		type: 'structure' | 'element'
	) {
		return this._headerService.getHeaderByName(value, type);
	}

	resetToDefaultHeaders(event: MouseEvent) {
		if (this.data.editable) {
			this.data.allowedHeaders1 = defaultEditStructureProfile;
			this.data.allowedHeaders2 = defaultEditElementProfile;
			return;
		}
		this.data.allowedHeaders1 = defaultViewStructureProfile;
		this.data.allowedHeaders2 = defaultViewElementProfile;
	}

	/**
	 * solely for generating test attributes for integration tests, do not use elsewhere
	 */
	/* istanbul ignore next */
	isChecked(
		columnNumber: 0 | 1,
		preference: keyof structure | keyof element
	) {
		const headerList = this.getHeaderList(columnNumber);
		return headerList.includes(preference);
	}
	/**
	 * solely for generating test attributes for integration tests, do not use elsewhere
	 */
	/* istanbul ignore next */
	getHeaderList(
		columnNumber: 0 | 1
	): (keyof element)[] | (keyof structure)[] {
		if (columnNumber) {
			return this.data.allowedHeaders2;
		}
		return this.data.allowedHeaders1;
	}
}
