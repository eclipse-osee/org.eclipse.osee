/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { connection } from '@osee/messaging/shared/types';
import { OperatorFunction, filter, switchMap, take } from 'rxjs';
import { AddNodeDialog } from '../../dialogs/add-node-dialog/add-node-dialog';
import { AddNodeDialogComponent } from '../../dialogs/add-node-dialog/add-node-dialog.component';
import { DefaultAddNodeDialog } from '../../dialogs/add-node-dialog/add-node-dialog.default';
import { CurrentGraphService } from '../../services/current-graph.service';

@Component({
	selector: 'osee-graph-link-add-menu',
	standalone: true,
	imports: [MatMenuItem, MatTooltip, MatIcon],
	template: ` <button
		mat-menu-item
		[disabled]="!addable()"
		[matTooltip]="addTooltip()"
		matTooltipPosition="after"
		(click)="openAddNodeDialog()">
		<mat-icon class="tw-text-osee-green-9">add</mat-icon>Add a node
	</button>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraphLinkAddMenuComponent {
	private graphService = inject(CurrentGraphService);
	private dialog = inject(MatDialog);
	public editMode = input.required<boolean>();
	public data = input.required<connection>();
	protected connectionName = computed(() => this.data().name.value);

	protected nodes = computed(() =>
		this.data().nodes.map((x) => x.name.value)
	);
	protected nodesString = computed(() => this.nodes().join(' , '));
	protected notDeleted = computed(
		() => this.data().deleted === undefined || this.data().deleted !== true
	);
	protected editable = computed(() => this.editMode() && this.notDeleted());
	protected notDirectConnection = computed(
		() => !this.data().transportType.directConnection
	);

	protected nodesNotLongerThanDc = computed(
		() => this.data().nodes.length < 2
	);
	protected canAddNode = computed(
		() => this.notDirectConnection() || this.nodesNotLongerThanDc()
	);
	protected addable = computed(() => this.canAddNode() && this.editable());

	protected addTooltip = computed(() => {
		if (!this.editable()) {
			return 'Cannot add to connections in view only mode.';
		}
		if (!this.notDeleted()) {
			return (
				this.connectionName() +
				' has been deleted. Cannot add to a deleted connection.'
			);
		}
		if (
			!this.addable() &&
			!this.notDirectConnection() &&
			!this.nodesNotLongerThanDc()
		) {
			return 'Cannot add more than 2 nodes to a direct (point to point) connection.';
		}
		return (
			'Add a new node to ' +
			this.connectionName() +
			'. Nodes already related to ' +
			this.connectionName() +
			' : ' +
			this.nodesString()
		);
	});
	private _connection = toObservable(this.data);
	private _addNode = this._connection.pipe(
		take(1),
		switchMap((connection) => {
			const dialogRef = this.dialog.open(AddNodeDialogComponent, {
				minWidth: '80%',
				data: new DefaultAddNodeDialog(connection),
			});
			return dialogRef.afterClosed();
		}),
		take(1),
		filter((res) => res !== undefined && res !== null) as OperatorFunction<
			//This is a type defined by @angular/material...not much we can do here
			//eslint-disable-next-line @typescript-eslint/no-explicit-any
			any,
			AddNodeDialog
		>,
		switchMap((res) =>
			res.node.id === '-1'
				? this.graphService
						.createNodeWithRelation(res.node, res.connection.id!)
						.pipe()
				: this.graphService.relateNode(res.connection.id!, res.node.id!)
		)
	);

	openAddNodeDialog() {
		this._addNode.subscribe();
	}
}
