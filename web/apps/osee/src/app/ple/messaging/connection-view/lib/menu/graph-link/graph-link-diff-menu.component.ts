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
import { MatMenuItem } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { connection } from '@osee/messaging/shared/types';
import { applic } from '@osee/applicability/types';
import { difference } from '@osee/shared/types/change-report';
import { CurrentGraphService } from '../../services/current-graph.service';

@Component({
	selector: 'osee-graph-link-diff-menu',
	imports: [MatMenuItem, MatTooltip],
	template: `
		<button
			mat-menu-item
			[disabled]="nameDisabled()"
			[matTooltip]="nameTooltip()"
			(click)="viewDiff(true, $any(name()), 'Name')">
			<!-- TODO: replace with @let binding when that releases -->
			Name
		</button>
		<button
			mat-menu-item
			[disabled]="descriptionDisabled()"
			[matTooltip]="descriptionTooltip()"
			(click)="viewDiff(true, $any(description()), 'Description')">
			<!-- TODO: replace with @let binding when that releases -->
			Description
		</button>
		<!-- TODO: replace with @let binding when that releases -->
		<!-- TODO: update the diff generation function to show the transport type diff for a relation change
		@if (transportType() !== undefined) {
			<button
				mat-menu-item
				(click)="
					viewDiff(true, $any(transportType()), 'Transport Type')
				">
				Transport Type
			</button>
		}
     -->
		<button
			mat-menu-item
			[disabled]="applicabilityDisabled()"
			[matTooltip]="applicabilityTooltip()"
			(click)="viewDiff(true, $any(applicability()), 'Applicability')">
			<!-- TODO: replace with @let binding when that releases -->
			Applicability
		</button>
	`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GraphLinkDiffMenuComponent {
	private graphService = inject(CurrentGraphService);
	data = input.required<connection>();
	protected name = computed(() => this.data().changes?.name);
	protected nameDisabled = computed(() => this.name() === undefined);
	protected nameTooltip = computed(() => {
		if (this.nameDisabled()) {
			return (
				'Make changes to the name of ' +
				this.data().name +
				' to view differences'
			);
		}
		return '';
	});
	protected description = computed(() => this.data().changes?.description);
	protected descriptionDisabled = computed(
		() => this.description() === undefined
	);
	protected descriptionTooltip = computed(() => {
		if (this.descriptionDisabled()) {
			return (
				'Make changes to the description of ' +
				this.data().name +
				' to view differences'
			);
		}
		return '';
	});
	protected applicability = computed(
		() => this.data().changes?.applicability
	);

	protected applicabilityDisabled = computed(
		() => this.applicability() === undefined
	);
	protected applicabilityTooltip = computed(() => {
		if (this.applicabilityDisabled()) {
			return (
				'Make changes to the applicability of ' +
				this.data().name +
				' to view differences'
			);
		}
		return '';
	});

	protected transportType = computed(
		() => this.data().changes?.transportType
	);
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
}
