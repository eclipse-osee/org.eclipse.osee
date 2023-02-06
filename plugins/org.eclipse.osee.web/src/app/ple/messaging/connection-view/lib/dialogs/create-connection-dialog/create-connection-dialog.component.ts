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
import { from, of, Subject } from 'rxjs';
import { concatMap, filter, reduce, switchMap, take } from 'rxjs/operators';
import { CurrentGraphService } from '../../services/current-graph.service';
import {
	CurrentTransportTypeService,
	newConnection,
	node,
	transportType,
} from '@osee/messaging/shared';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionLoadingComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-create-connection-dialog',
	templateUrl: './create-connection-dialog.component.html',
	styleUrls: ['./create-connection-dialog.component.sass'],
	standalone: true,
	imports: [
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
	nodes = (pageNum: string | number) =>
		this.graphService.getPaginatedNodes(pageNum, 3).pipe(
			switchMap((nodes) =>
				of(nodes).pipe(
					concatMap((n) =>
						from(n).pipe(filter((node) => node.id !== this.data.id))
					),

					take(nodes.length),
					reduce((acc, curr) => [...acc, curr], [] as node[])
				)
			)
		);
	title: string = '';
	newConnection: newConnection = {
		nodeId: '',
		connection: {
			name: '',
			description: '',
		},
	};

	transportTypes = (pageNum: string | number) =>
		this.transportTypeService.getPaginatedTypes(pageNum, 3);
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
