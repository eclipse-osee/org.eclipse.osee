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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import type {
	OseeNode,
	__connectionChanges,
	connection,
	nodeData,
} from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { GraphLinkAddMenuComponent } from './graph-link-add-menu.component';
import { GraphLinkDiffMenuComponent } from './graph-link-diff-menu.component';
import { GraphLinkEditMenuComponent } from './graph-link-edit-menu.component';
import { GraphLinkLinkMenuComponent } from './graph-link-link-menu.component';
import { GraphLinkRemoveMenuComponent } from './graph-link-remove-menu.component';

@Component({
	selector: 'osee-messaging-graph-link-menu',
	template: `<osee-graph-link-link-menu
			[data]="data()"></osee-graph-link-link-menu>
		<mat-divider></mat-divider>
		<osee-graph-link-add-menu
			[data]="data()"
			[editMode]="editMode()"></osee-graph-link-add-menu>
		<osee-graph-link-edit-menu
			[data]="data()"
			[editMode]="editMode()"></osee-graph-link-edit-menu>
		<osee-graph-link-remove-menu
			[data]="data()"
			[editMode]="editMode()"
			[source]="source()"
			[target]="target()"></osee-graph-link-remove-menu>
		<mat-divider></mat-divider>
		<button
			mat-menu-item
			[disabled]="isNotDiffable()"
			[matTooltip]="diffTooltip()"
			[matTooltipPosition]="diffTooltipPosition()"
			[matMenuTriggerFor]="linkDiffMenu"
			[matMenuTriggerData]="{ diffData: data() }">
			<mat-icon class="tw-text-osee-yellow-10 dark:tw-text-osee-amber-9"
				>pageview</mat-icon
			><span>View Diff for</span>
		</button>
		<mat-menu #linkDiffMenu="matMenu">
			<ng-template
				matMenuContent
				let-diffData="diffData">
				<osee-graph-link-diff-menu
					[data]="diffData"></osee-graph-link-diff-menu>
			</ng-template>
		</mat-menu>`,
	imports: [
		MatIcon,
		MatTooltip,
		MatDivider,
		MatMenuContent,
		MatMenuTrigger,
		MatMenuItem,
		MatMenu,
		GraphLinkDiffMenuComponent,
		GraphLinkLinkMenuComponent,
		GraphLinkAddMenuComponent,
		GraphLinkEditMenuComponent,
		GraphLinkRemoveMenuComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraphLinkMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<connection>();
	protected connectionName = computed(() => this.data().name);

	private _uiService = inject(UiService);
	protected diffEnabled = toSignal(this._uiService.isInDiff, {
		initialValue: false,
	});
	protected branchType = toSignal(this._uiService.type, {
		initialValue: 'baseline',
	});
	protected isOnWorkingBranch = computed(
		() => this.branchType() === 'working'
	);

	protected isNotDiffable = computed(() => {
		return !(
			this.isOnWorkingBranch() &&
			this.diffEnabled() &&
			!(
				this.data().changes === undefined ||
				(typeof this.data().changes === 'object' &&
					this.data().changes !== null &&
					Object.keys(this.data().changes as __connectionChanges)
						.length === 0)
			)
		);
	});
	protected diffTooltip = computed(() => {
		if (!this.isOnWorkingBranch()) {
			return 'Switch to a working branch to be able to view diffs';
		}
		if (!this.diffEnabled()) {
			return 'Show deltas to enable diff viewing';
		}
		if (
			this.data().changes === undefined ||
			(typeof this.data().changes === 'object' &&
				this.data().changes !== null &&
				Object.keys(this.data().changes as __connectionChanges)
					.length === 0)
		) {
			return (
				'Make changes to ' +
				this.connectionName() +
				' in order to view diffs'
			);
		}
		return 'View differences in attributes of ' + this.connectionName();
	});

	protected diffTooltipPosition = computed(() => {
		if (
			this.isOnWorkingBranch() &&
			this.diffEnabled() &&
			!(
				this.data().changes === undefined ||
				(typeof this.data().changes === 'object' &&
					this.data().changes !== null &&
					Object.keys(this.data().changes as __connectionChanges)
						.length === 0)
			)
		) {
			return 'before';
		}
		return 'after';
	});
	source = input.required<OseeNode<nodeData>>();
	target = input.required<OseeNode<nodeData>>();
}
