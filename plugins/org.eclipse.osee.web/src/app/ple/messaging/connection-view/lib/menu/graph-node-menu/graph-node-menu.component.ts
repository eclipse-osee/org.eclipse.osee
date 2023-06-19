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
import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { from, of } from 'rxjs';
import { take, filter, mergeMap, reduce, switchMap } from 'rxjs/operators';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { CurrentGraphService } from '../../services/current-graph.service';
import { RemovalDialog } from '../../types/ConfirmRemovalDialog';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';
import { MatMenuModule } from '@angular/material/menu';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import type {
	connection,
	connectionWithChanges,
	newConnection,
	node,
	nodeData,
	nodeDataWithChanges,
	OseeEdge,
	_newConnection,
} from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-graph-node-menu',
	templateUrl: './graph-node-menu.component.html',
	standalone: true,
	imports: [NgIf, MatMenuModule, MatIconModule, MatButtonModule, AsyncPipe],
})
export class GraphNodeMenuComponent {
	@Input() editMode: boolean = false;
	@Input() data: nodeData | nodeDataWithChanges = {
		id: '',
		name: '',
		interfaceNodeNumber: '',
		interfaceNodeGroupId: '',
		interfaceNodeAddress: '',
		interfaceNodeBackgroundColor: '',
		interfaceNodeBuildCodeGen: false,
		interfaceNodeCodeGen: false,
		interfaceNodeCodeGenName: '',
		interfaceNodeNameAbbrev: '',
		interfaceNodeToolUse: false,
		interfaceNodeType: '',
		notes: '',
	};
	@Input() sources: OseeEdge<connection | connectionWithChanges>[] = [];
	@Input() targets: OseeEdge<connection | connectionWithChanges>[] = [];
	constructor(
		private graphService: CurrentGraphService,
		public dialog: MatDialog
	) {}
	openEditNodeDialog(value: nodeData | nodeDataWithChanges) {
		let dialogRef = this.dialog.open(EditNodeDialogComponent, {
			data: Object.assign({}, value),
		});
		dialogRef
			.afterClosed()
			.pipe(
				//only take first response
				take(1),
				//filter out non-valid responses
				filter(
					(dialogResponse) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				//convert object to key-value pair emissions emitted sequentially instead of all at once
				mergeMap((arrayDialogResponse: node) =>
					from(Object.entries(arrayDialogResponse)).pipe(
						//filter out key-value pairs that are unchanged on value, and maintain id property
						filter(
							(filteredProperties) =>
								value[filteredProperties[0] as keyof node] !==
									filteredProperties[1] ||
								filteredProperties[0] === 'id'
						),
						//accumulate into an array of properties that are changed
						reduce(
							(acc, curr) => [...acc, curr],
							[] as [string, any][]
						)
					)
				),
				//transform array of properties into Partial<node> using Object.fromEntries()(ES2019)
				switchMap((arrayOfProperties) =>
					of(
						Object.fromEntries(arrayOfProperties) as Partial<node>
					).pipe(
						//HTTP PATCH call to update value
						switchMap((results) =>
							this.graphService.updateNode(results)
						)
					)
				)
			)
			.subscribe();
	}

	removeNodeAndConnection(
		value: nodeData | nodeDataWithChanges,
		sources: OseeEdge<connection | connectionWithChanges>[],
		targets: OseeEdge<connection | connectionWithChanges>[]
	) {
		let dialogRef = this.dialog.open(ConfirmRemovalDialogComponent, {
			data: {
				id: value.id,
				name: value.name,
				extraNames: [
					...sources.map((x) => x.label),
					...targets.map((x) => x.label),
				],
				type: 'node',
			},
		});
		dialogRef
			.afterClosed()
			.pipe(
				//only take first response
				take(1),
				//filter out non-valid responses
				filter(
					(dialogResponse: RemovalDialog) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				//make sure there is a name and id
				filter(
					(result) => result.name.length > 0 && result.id.length > 0
				),
				//delete Node api call, then unrelate connection from other node(s) using unrelate api call
				switchMap((results) =>
					this.graphService.deleteNodeAndUnrelate(results.id, [
						...sources,
						...targets,
					])
				) //needs testing
			)
			.subscribe();
	}
	private _newConnectionIsConnection(
		value: _newConnection | connection
	): value is connection {
		return value?.transportType !== undefined;
	}

	createConnectionToNode(value: nodeData | nodeDataWithChanges) {
		//todo open dialog to select node to connect to this node
		let dialogRef = this.dialog.open(CreateConnectionDialogComponent, {
			minWidth: '40%',
			data: value,
		});
		dialogRef
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(dialogResponse: newConnection) =>
						dialogResponse !== undefined && dialogResponse !== null
				),
				//HTTP Rest call to create connection(branch/nodes/nodeId/connections/type), then rest call to relate it to the associated nodes(branch/nodes/nodeId/connections/id/type)
				filter((val) =>
					this._newConnectionIsConnection(val.connection)
				),
				switchMap((results) =>
					this.graphService.createNewConnection(
						results.connection as connection, //typescript bug relating to type narrowing not being inferred by filter
						results.nodeIds
					)
				)
			)
			.subscribe();
	}
	viewDiff(open: boolean, value: difference, header: string) {
		let current = value.currentValue as string | number | applic;
		let prev = value.previousValue as string | number | applic;
		if (prev === null) {
			prev = '';
		}
		if (current === null) {
			current = '';
		}
		this.graphService.sideNav = {
			opened: open,
			field: header,
			currentValue: current,
			previousValue: prev,
			transaction: value.transactionToken,
		};
	}

	hasChanges(
		value: nodeData | nodeDataWithChanges
	): value is nodeDataWithChanges {
		return (value as nodeDataWithChanges).changes !== undefined;
	}
}
