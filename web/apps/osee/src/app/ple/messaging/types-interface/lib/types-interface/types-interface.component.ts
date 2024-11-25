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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { PlatformTypeActionsComponent } from '@osee/messaging/shared/main-content';
import { PlatformType } from '@osee/messaging/shared/types';
import { PlatformTypesFabComponent } from '../platform-types-fab/platform-types-fab.component';
import { PlatformTypesFilterComponent } from '../platform-types-filter/platform-types-filter.component';
import { PlatformTypesToolbarComponent } from '../platform-types-toolbar/platform-types-toolbar.component';
import { CurrentTypesService } from '../services/current-types.service';
import { TypeGridComponent } from '../type-grid/type-grid.component';
import { TypesTableComponent } from '../types-table/types-table.component';

@Component({
	selector: 'osee-types-interface',
	imports: [
		PlatformTypesFilterComponent,
		PlatformTypesFabComponent,
		PlatformTypeActionsComponent,
		TypeGridComponent,
		TypesTableComponent,
		PlatformTypesToolbarComponent,
	],
	template: `<osee-platform-types-filter></osee-platform-types-filter>
		<div
			class="tw-inline-block tw-max-h-[72vh] tw-w-full tw-overflow-auto tw-bg-background-background max-sm:tw-block">
			<!-- TODO: once we have a switcher/preference in place to detect preferred mode, this will be uncommented. -->
			<!-- <osee-messaging-types-type-grid
			[platformTypes]="filteredData()"></osee-messaging-types-type-grid> -->
			<osee-types-table
				[platformTypes]="filteredData()"></osee-types-table>
		</div>
		<osee-platform-types-toolbar></osee-platform-types-toolbar>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TypesInterfaceComponent {
	private typesService = inject(CurrentTypesService);
	filteredDataObs = this.typesService.typeData.pipe(takeUntilDestroyed());
	filteredData = toSignal(this.filteredDataObs, {
		initialValue: [] as PlatformType[],
	});
}
