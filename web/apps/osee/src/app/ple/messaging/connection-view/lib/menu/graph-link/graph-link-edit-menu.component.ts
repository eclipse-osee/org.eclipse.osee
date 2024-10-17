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
import { connection } from '@osee/messaging/shared/types';
import { combineLatest, filter, of, switchMap, take } from 'rxjs';
import { EditConnectionDialogComponent } from '../../dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { CurrentGraphService } from '../../services/current-graph.service';

@Component({
	selector: 'osee-graph-link-edit-menu',
	standalone: true,
	imports: [MatMenuItem, MatTooltip, MatIcon],
	template: ` <button
		mat-menu-item
		[disabled]="!editable()"
		[matTooltip]="editTooltip()"
		matTooltipPosition="after"
		(click)="openConnectionEditDialog()">
		<mat-icon class="tw-text-osee-yellow-10 dark:tw-text-osee-amber-9"
			>edit</mat-icon
		>Edit
		{{ connectionName() }}
	</button>`,
})
export class GraphLinkEditMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<connection>();
	protected connectionName = computed(() => this.data().name.value);
	protected notDeleted = computed(
		() => this.data().deleted === undefined || this.data().deleted !== true
	);
	protected editable = computed(() => this.editMode() && this.notDeleted());

	protected editTooltip = computed(() => {
		if (!this.editable()) {
			return 'Cannot edit connections in view only mode.';
		}
		if (!this.notDeleted()) {
			return (
				this.connectionName() +
				' has been deleted. Cannot edit a deleted connection.'
			);
		}
		return 'Change attributes of ' + this.connectionName();
	});
	private graphService = inject(CurrentGraphService);
	private dialog = inject(MatDialog);

	private _connection = toObservable(this.data);
	private _openEditDialog = this._connection.pipe(
		take(1),
		switchMap((connection) => {
			const dialogRef = this.dialog.open(EditConnectionDialogComponent, {
				data: structuredClone(connection),
			});
			return dialogRef.afterClosed();
		}),
		take(1),
		filter(
			(dialogResponse) =>
				dialogResponse !== undefined && dialogResponse !== null
		),
		switchMap((connection: connection) =>
			combineLatest([of(connection), this._connection.pipe(take(1))])
		),
		switchMap(([curr, prev]) =>
			this.graphService.updateConnection(curr, prev)
		)
	);
	openConnectionEditDialog() {
		this._openEditDialog.subscribe();
	}
}
