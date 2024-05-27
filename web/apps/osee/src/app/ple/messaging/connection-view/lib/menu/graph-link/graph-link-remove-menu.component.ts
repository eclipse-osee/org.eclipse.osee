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
import { OseeNode, connection, nodeData } from '@osee/messaging/shared/types';
import { filter, switchMap, take } from 'rxjs';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { CurrentGraphService } from '../../services/current-graph.service';
import { RemovalDialog } from '../../types/ConfirmRemovalDialog';

@Component({
	selector: 'osee-graph-link-remove-menu',
	standalone: true,
	imports: [MatMenuItem, MatTooltip, MatIcon],
	template: `<button
		mat-menu-item
		[disabled]="!editable()"
		[matTooltip]="removeTooltip()"
		matTooltipPosition="after"
		(click)="openRemoveConnectionDialog()"
		data-cy="delete-connection-btn">
		<mat-icon class="tw-text-osee-red-9">remove_circle_outline</mat-icon
		>Remove connection
		{{ connectionName() }}
	</button>`,
})
export class GraphLinkRemoveMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<connection>();

	private _connection = toObservable(this.data);
	protected connectionName = computed(() => this.data().name.value);

	protected nodes = computed(() =>
		this.data().nodes.map((x) => x.name.value)
	);
	protected nodesString = computed(() => this.nodes().join(' , '));
	protected notDeleted = computed(
		() => this.data().deleted === undefined || this.data().deleted !== true
	);
	protected editable = computed(() => this.editMode() && this.notDeleted());
	protected removeTooltip = computed(() => {
		if (!this.editable()) {
			return 'Cannot remove connections in view only mode.';
		}
		if (!this.notDeleted()) {
			return (
				this.connectionName() +
				' has been deleted. Cannot remove an already deleted connection.'
			);
		}
		return (
			'Removes ' + this.connectionName() + ' from ' + this.nodesString()
		);
	});
	source = input.required<OseeNode<nodeData>>();
	target = input.required<OseeNode<nodeData>>();
	private graphService = inject(CurrentGraphService);
	private dialog = inject(MatDialog);
	private dialogData = computed(() => {
		return {
			id: this.data().id,
			name: this.connectionName(),
			extraNames: [this.source().label, this.target().label],
			type: 'connection',
		};
	});

	private _dialogData = toObservable(this.dialogData);

	private _removeDialog = this._dialogData.pipe(
		take(1),
		switchMap((data) => {
			const dialogRef = this.dialog.open(ConfirmRemovalDialogComponent, {
				data: data,
			});
			return dialogRef.afterClosed();
		}),
		take(1),
		filter(
			(dialogResponse): dialogResponse is RemovalDialog =>
				dialogResponse !== undefined && dialogResponse !== null
		),
		switchMap((_) => this._connection),
		switchMap((connection) =>
			this.graphService.unrelateConnection(
				connection.nodes.map((x) => x.id),
				connection.id
			)
		)
	);
	openRemoveConnectionDialog() {
		this._removeDialog.subscribe();
	}
}
