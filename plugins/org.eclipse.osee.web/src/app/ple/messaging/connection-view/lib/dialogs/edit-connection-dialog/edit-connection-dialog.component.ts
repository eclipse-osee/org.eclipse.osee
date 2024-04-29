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
import { AsyncPipe } from '@angular/common';
import { Component, Inject, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { CurrentTransportTypeService } from '@osee/messaging/shared/services';
import type { connection, transportType } from '@osee/messaging/shared/types';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import { Subject } from 'rxjs';

@Component({
	selector: 'osee-edit-connection-dialog',
	templateUrl: './edit-connection-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatFormField,
		MatLabel,
		FormsModule,
		MatInput,
		MatSelect,
		MatOptionLoadingComponent,
		MatOption,
		AsyncPipe,
		MatButton,
		ApplicabilitySelectorComponent,
	],
})
export class EditConnectionDialogComponent implements OnDestroy {
	private _done = new Subject();
	title: string = '';
	paginationSize = 5;
	transportTypes = (pageNum: string | number) =>
		this.transportTypeService.getPaginatedTypes(
			pageNum,
			this.paginationSize
		);
	constructor(
		public dialogRef: MatDialogRef<EditConnectionDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: connection,
		private transportTypeService: CurrentTransportTypeService
	) {
		this.title = data.name;
	}
	ngOnDestroy(): void {
		this._done.next(true);
	}

	onNoClick() {
		this.dialogRef.close();
	}

	compareTransportTypes(o1: transportType, o2: transportType) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}
}
