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
import { Component, computed, inject, input } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { connection, nodeData } from '@osee/messaging/shared/types';
import { filter, switchMap, take } from 'rxjs';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CurrentGraphService } from '../../services/current-graph.service';

@Component({
	selector: 'osee-graph-node-add-menu',
	standalone: true,
	imports: [MatMenuItem, MatIcon, MatTooltip],
	template: `<button
		mat-menu-item
		[disabled]="!editable()"
		[matTooltip]="addTooltip()"
		matTooltipPosition="after"
		(click)="createConnectionToNode()">
		<mat-icon
			class="tw-text-osee-green-9"
			data-cy="create-connection-btn"
			>show_chart</mat-icon
		><span>Create Connection To {{ nodeName() }}</span>
	</button>`,
})
export class GraphNodeAddMenuComponent {
	private graphService = inject(CurrentGraphService);
	private dialog = inject(MatDialog);
	public editMode = input.required<boolean>();
	public data = input.required<nodeData>();
	protected nodeName = computed(() => this.data().name.value);
	protected notDeleted = computed(
		() => this.data().deleted === undefined || this.data().deleted !== true
	);
	protected editable = computed(() => this.editMode() && this.notDeleted());
	protected addTooltip = computed(() => {
		if (!this.editable()) {
			return 'Cannot add connections to nodes in view only mode.';
		}
		if (!this.notDeleted()) {
			return (
				this.nodeName() +
				' has been deleted. Cannot add to a deleted node.'
			);
		}
		return 'Add a connection to ' + this.nodeName();
	});

	private _node = toObservable(this.data);
	private _openDialog = this._node.pipe(
		take(1),
		switchMap((node) => {
			const dialogRef = this.dialog.open(
				CreateConnectionDialogComponent,
				{
					minWidth: '40%',
					data: structuredClone(node),
				}
			);
			return dialogRef.afterClosed();
		}),
		take(1),
		filter(
			(dialogResponse: connection) =>
				dialogResponse !== undefined && dialogResponse !== null
		),
		filter((val) => val?.transportType !== undefined)
	);
	private _performTx = this._openDialog.pipe(
		switchMap((results) => this.graphService.createNewConnection(results))
	);
	protected createConnectionToNode() {
		this._performTx.subscribe();
	}
}
