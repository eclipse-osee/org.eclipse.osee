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
import {
	BehaviorSubject,
	debounceTime,
	filter,
	map,
	of,
	Subject,
	switchMap,
} from 'rxjs';
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
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { CommonModule } from '@angular/common';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ConnectionNodesCountDirective } from '@osee/messaging/shared/directives';

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
		MatAutocompleteModule,
		ConnectionNodesCountDirective,
	],
})
export class CreateConnectionDialogComponent implements OnDestroy {
	constructor(
		private graphService: CurrentGraphService,
		private transportTypeService: CurrentTransportTypeService,
		public dialogRef: MatDialogRef<CreateConnectionDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: node | undefined
	) {
		this.title = data?.name || '';
		this.toNode = data?.id || '';
		if (this.toNode !== '') {
			this.newConnection.nodeIds.push(this.toNode);
		}
	}

	private _done = new Subject();
	paginationSize = 50;

	paginatedNodes = (pageNum: string | number) =>
		this.graphService.getPaginatedNodes(pageNum, this.paginationSize);

	nodes = this.graphService.getPaginatedNodes(0, 0);

	title: string = '';
	fromNode: string = '';
	toNode: string = '';
	nodeSearch = new BehaviorSubject<string>('');

	newConnection: newConnection = {
		connection: {
			name: '',
			description: '',
		},
		nodeIds: [],
	};

	availableNodes = this.nodeSearch.pipe(
		debounceTime(250),
		map(
			(search) => (pageNum: number | string) =>
				this.graphService.getPaginatedNodesByName(
					search,
					pageNum,
					this.paginationSize
				)
		)
	);

	availableNodesCount = this.nodeSearch.pipe(
		debounceTime(250),
		switchMap((search) => this.graphService.getNodesByNameCount(search))
	);

	minNodes = of(this.newConnection).pipe(
		switchMap((newConnection) =>
			of(newConnection.connection).pipe(
				filter((connection) => connection.transportType !== undefined),
				switchMap((connection) => {
					const minPub =
						connection.transportType!.minimumPublisherMultiplicity;
					const minSub =
						connection.transportType!.minimumSubscriberMultiplicity;
					const min = Math.min(minPub, minSub);
					return of(min);
				})
			)
		)
	);

	maxNodes = of(this.newConnection.connection.transportType).pipe(
		filter((transportType) => transportType !== undefined),
		switchMap((transportType) => {
			const maxPub = transportType!.maximumPublisherMultiplicity;
			const maxSub = transportType!.maximumSubscriberMultiplicity;
			const max = maxPub + maxSub;
			return of(max);
		})
	);

	transportTypes = (pageNum: string | number) =>
		this.transportTypeService.getPaginatedTypes(
			pageNum,
			this.paginationSize
		);

	ngOnDestroy(): void {
		this._done.next(true);
	}

	onNoClick() {
		this.dialogRef.close();
	}

	selectFromNode(change: MatSelectChange) {
		this.fromNode = change.value;
		if (this.data?.id) {
			this.newConnection.nodeIds = [this.fromNode, this.data.id];
		}
	}

	selectToNode(change: MatSelectChange) {
		this.toNode = change.value;
		this.newConnection.nodeIds = [this.fromNode, this.toNode];
	}

	applySearchTerm(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.nodeSearch.next(value);
	}

	compareTransportTypes(o1: transportType, o2: transportType) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	compareNodes(node1: string, node2: string) {
		return node1 && node2 ? node1 === node2 : false;
	}

	updateNodes(val: string[]) {
		this.newConnection.nodeIds = val;
	}
}
