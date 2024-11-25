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
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { nodeData, OseeEdge, connection } from '@osee/messaging/shared/types';
import { CurrentGraphService } from '../../services/current-graph.service';
import { MatDialog } from '@angular/material/dialog';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, switchMap, take } from 'rxjs';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { RemovalDialog } from '../../types/ConfirmRemovalDialog';

@Component({
	selector: 'osee-graph-node-remove-menu',
	imports: [MatMenuItem, MatTooltip, MatIcon],
	template: `<button
		mat-menu-item
		[disabled]="!editable()"
		[matTooltip]="removeTooltip()"
		matTooltipPosition="after"
		(click)="removeNodeAndConnection()">
		<mat-icon
			class="tw-text-osee-red-9"
			data-cy="delete-node-btn"
			>remove_circle_outline</mat-icon
		>Remove {{ nodeName() }} & Connections
	</button>`,
})
export class GraphNodeRemoveMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<nodeData>();
	public sources = input.required<OseeEdge<connection>[]>();
	public targets = input.required<OseeEdge<connection>[]>();
	protected nodeName = computed(() => this.data().name.value);
	protected notDeleted = computed(
		() => this.data().deleted === undefined || this.data().deleted !== true
	);
	protected editable = computed(() => this.editMode() && this.notDeleted());
	protected removeTooltip = computed(() => {
		if (!this.editable()) {
			return 'Cannot remove nodes in view only mode.';
		}
		if (!this.notDeleted()) {
			return (
				this.nodeName() +
				' has been deleted. Cannot remove an already deleted node.'
			);
		}
		return 'Remove ' + this.nodeName();
	});
	private graphService = inject(CurrentGraphService);
	private dialog = inject(MatDialog);
	private dialogData = computed(() => {
		return {
			id: this.data().id,
			name: this.nodeName(),
			extraNames: [
				...this.sources().map((x) => x.label),
				...this.targets().map((x) => x.label),
			],
			type: 'node',
		};
	});
	private _dialogData = toObservable(this.dialogData);
	private _dialogRef = this._dialogData.pipe(
		take(1),
		switchMap((d) => {
			const ref = this.dialog.open(ConfirmRemovalDialogComponent, {
				data: structuredClone(d),
			});
			return ref.afterClosed();
		}),
		take(1)
	);
	private _performTx = this._dialogRef.pipe(
		filter(
			(dialogResponse: RemovalDialog) =>
				dialogResponse !== undefined && dialogResponse !== null
		),
		filter((result) => result.name.length > 0 && result.id.length > 0),
		switchMap((results) =>
			this.graphService.deleteNodeAndUnrelate(results.id)
		)
	);
	protected removeNodeAndConnection() {
		this._performTx.subscribe();
	}
}
