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
import { nodeData } from '@osee/messaging/shared/types';
import { CurrentGraphService } from '../../services/current-graph.service';
import { take, switchMap, filter, combineLatest, of } from 'rxjs';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';

@Component({
	selector: 'osee-graph-node-edit-menu',
	imports: [MatMenuItem, MatIcon, MatTooltip],
	template: `<button
		mat-menu-item
		[disabled]="!editable()"
		[matTooltip]="editTooltip()"
		matTooltipPosition="after"
		(click)="openEditNodeDialog()">
		<mat-icon class="tw-text-osee-yellow-10 dark:tw-text-osee-amber-9"
			>edit</mat-icon
		>Edit {{ nodeName() }}
	</button>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraphNodeEditMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<nodeData>();
	protected nodeName = computed(() => this.data().name.value);
	protected notDeleted = computed(
		() => this.data().deleted === undefined || this.data().deleted !== true
	);
	protected editable = computed(
		() => this.editMode() && this.notDeleted() && this.data().id !== '-1'
	);
	protected editTooltip = computed(() => {
		if (!this.editable()) {
			return 'Cannot edit nodes in view only mode.';
		}
		if (!this.notDeleted()) {
			return (
				this.nodeName() +
				' has been deleted. Cannot edit a deleted node.'
			);
		}
		return 'Change attributes of ' + this.nodeName();
	});
	private graphService = inject(CurrentGraphService);
	private dialog = inject(MatDialog);
	private _node = toObservable(this.data);

	private _openDialog = this._node.pipe(
		take(1),
		switchMap((node) => {
			const dialogRef = this.dialog.open(EditNodeDialogComponent, {
				data: structuredClone(node),
			});
			return dialogRef.afterClosed();
		}),
		take(1),
		filter(
			(dialogResponse): dialogResponse is nodeData =>
				dialogResponse !== undefined && dialogResponse !== null
		),
		switchMap((response) => combineLatest([of(response), this._node]))
	);
	private _performTx = this._openDialog.pipe(
		switchMap(([results, previous]) =>
			this.graphService.updateNode(results, previous)
		)
	);

	protected openEditNodeDialog() {
		this._performTx.subscribe();
	}
}
