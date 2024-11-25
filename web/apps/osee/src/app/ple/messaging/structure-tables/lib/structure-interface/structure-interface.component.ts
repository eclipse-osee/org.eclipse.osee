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
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { CurrentViewSelectorComponent } from '@osee/shared/components';
import { switchMap, iif, of } from 'rxjs';
import { StructureTableComponent } from '../tables/structure-table/structure-table.component';
import { StructureFilterComponent } from '../structure-filter/structure-filter.component';
import { StructureTableToolbarComponent } from '../structure-table-toolbar/structure-table-toolbar.component';

@Component({
	selector: 'osee-structure-interface',
	imports: [
		StructureTableToolbarComponent,
		StructureTableComponent,
		StructureFilterComponent,
		MessagingControlsComponent,
		CurrentViewSelectorComponent,
		AsyncPipe,
	],
	template: `
		<osee-messaging-controls
			[branchControls]="false"
			[actionControls]="true"
			[diff]="true"
			[diffRouteLink]="
				(inDiffMode | async) === 'false'
					? [
							{
								outlets: {
									primary: 'diff',
									rightSideNav: null,
								},
							},
						]
					: '../'
			">
			<osee-current-view-selector />
		</osee-messaging-controls>
		<!-- TODO: think of a good way to show this and hide the top level search for smaller screen sizes -->
		<!-- <osee-structure-filter /> -->
		<div
			class="tw-inline-block tw-max-h-[78vh] tw-w-full tw-max-w-[100vw] tw-overflow-auto tw-bg-background-background max-sm:tw-block">
			<osee-structure-table
				[previousLink]="previousLink()"
				[breadCrumb]="breadCrumb()" />
		</div>
		<osee-structure-table-toolbar
			[breadCrumb]="breadCrumb()"
			[structuresCount]="structuresCount()" />
	`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StructureInterfaceComponent {
	previousLink = input('../../../../');
	breadCrumb = input('');
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	private _structuresCount = this.structureService.structuresCount;
	protected structuresCount = toSignal(this._structuresCount, {
		initialValue: 0,
	});
	protected inDiffMode = this.structureService.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);
}
