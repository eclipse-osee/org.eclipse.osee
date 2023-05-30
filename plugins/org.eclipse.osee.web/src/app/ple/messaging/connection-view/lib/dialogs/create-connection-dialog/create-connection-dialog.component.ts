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
import { Component, Inject, OnDestroy } from '@angular/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { CurrentGraphService } from '../../services/current-graph.service';
import type {
	newConnection,
	node,
	transportType,
} from '@osee/messaging/shared/types';
import { CurrentTransportTypeService } from '@osee/messaging/shared/services';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'osee-create-connection-dialog',
	templateUrl: './create-connection-dialog.component.html',
	standalone: true,
	imports: [
		CommonModule,
		MatDialogModule,
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		MatSelectModule,
		MatOptionLoadingComponent,
		MatOptionModule,
		MatButtonModule,
	],
})
export class CreateConnectionDialogComponent implements OnDestroy {
	private _done = new Subject();
	paginationSize = 5;

	nodes = (pageNum: string | number) =>
		this.graphService.getPaginatedNodes(pageNum, this.paginationSize);
	title: string = '';
	newConnection: newConnection = {
		nodeId: '',
		connection: {
			name: '',
			description: '',
		},
	};

	transportTypes = (pageNum: string | number) =>
		this.transportTypeService.getPaginatedTypes(
			pageNum,
			this.paginationSize
		);
	constructor(
		private graphService: CurrentGraphService,
		private transportTypeService: CurrentTransportTypeService,
		public dialogRef: MatDialogRef<CreateConnectionDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: node
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
