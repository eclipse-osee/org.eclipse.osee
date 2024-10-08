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
import { AsyncPipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import type {
	OseeEdge,
	connection,
	nodeData,
} from '@osee/messaging/shared/types';
import { applic } from '@osee/applicability/types';
import { difference } from '@osee/shared/types/change-report';
import { CurrentGraphService } from '../../services/current-graph.service';
import { GraphNodeAddMenuComponent } from './graph-node-add-menu.component';
import { GraphNodeEditMenuComponent } from './graph-node-edit-menu.component';
import { GraphNodeRemoveMenuComponent } from './graph-node-remove-menu.component';

@Component({
	selector: 'osee-messaging-graph-node-menu',
	template: `@if (hasChanges(data())) {
			<button
				mat-menu-item
				[matMenuTriggerFor]="nodeDiffMenu"
				[matMenuTriggerData]="{ data: data() }">
				<mat-icon
					class="tw-text-osee-yellow-10 dark:tw-text-osee-amber-9"
					>pageview</mat-icon
				>View Diff for
			</button>
		}
		<osee-graph-node-edit-menu
			[editMode]="editMode()"
			[data]="data()" />
		<osee-graph-node-add-menu
			[editMode]="editMode()"
			[data]="data()" />
		<osee-graph-node-remove-menu
			[editMode]="editMode()"
			[data]="data()"
			[sources]="sources()"
			[targets]="targets()" />
		<mat-menu #nodeDiffMenu="matMenu">
			<ng-template
				matMenuContent
				let-data="data">
				@if (data?.changes?.name !== undefined) {
					<button
						mat-menu-item
						(click)="viewDiff(true, data.changes.name, 'Name')">
						Name
					</button>
				}
				@if (data?.changes?.description !== undefined) {
					<button
						mat-menu-item
						(click)="
							viewDiff(
								true,
								data.changes.description,
								'Description'
							)
						">
						Description
					</button>
				}
				@if (data?.changes?.interfaceNodeAddress !== undefined) {
					<button
						mat-menu-item
						(click)="
							viewDiff(
								true,
								data.changes.interfaceNodeAddress,
								'Address/Port'
							)
						">
						Address/Port
					</button>
				}
				@if (
					data?.changes?.interfaceNodeBackgroundColor !== undefined
				) {
					<button
						mat-menu-item
						(click)="
							viewDiff(
								true,
								data.changes.interfaceNodeBackgroundColor,
								'Background Color'
							)
						">
						Background Color
					</button>
				}
				@if (data?.changes?.applicability !== undefined) {
					<button
						mat-menu-item
						(click)="
							viewDiff(
								true,
								data.changes.applicability,
								'Applicability'
							)
						">
						Applicability
					</button>
				}
			</ng-template>
		</mat-menu>`,
	standalone: true,
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenu,
		MatIcon,
		AsyncPipe,
		GraphNodeEditMenuComponent,
		GraphNodeAddMenuComponent,
		GraphNodeRemoveMenuComponent,
	],
})
export class GraphNodeMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<nodeData>();
	public sources = input.required<OseeEdge<connection>[]>();
	public targets = input.required<OseeEdge<connection>[]>();

	private graphService = inject(CurrentGraphService);

	protected viewDiff(open: boolean, value: difference, header: string) {
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

	protected hasChanges(value: nodeData): value is nodeData {
		return (value as nodeData).changes !== undefined;
	}
}
