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
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { applic } from 'src/app/types/applicability/applic';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { connection } from '../../../../shared/types/connection';
import { CurrentTransportTypeService } from '../../../../shared/services/ui/current-transport-type.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { transportType } from '../../../../shared/types/transportType';

@Component({
	selector: 'osee-edit-connection-dialog',
	templateUrl: './edit-connection-dialog.component.html',
	styleUrls: ['./edit-connection-dialog.component.sass'],
})
export class EditConnectionDialogComponent implements OnDestroy {
	private _done = new Subject();
	title: string = '';
	applics = this.graphService.applic;
	transportTypes = (pageNum: string | number) =>
		this.transportTypeService.getPaginatedTypes(pageNum, 3);
	constructor(
		public dialogRef: MatDialogRef<EditConnectionDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: connection,
		private graphService: CurrentGraphService,
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

	compareApplics(o1: applic, o2: applic) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	compareTransportTypes(o1: transportType, o2: transportType) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}
}
