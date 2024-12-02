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
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { StructureTablePaginatorComponent } from '../structure-table-paginator/structure-table-paginator.component';
import { StructureTableToolbarAddActionsComponent } from '../structure-table-toolbar-add-actions/structure-table-toolbar-add-actions.component';

@Component({
	selector: 'osee-structure-table-toolbar',
	imports: [
		MatToolbar,
		StructureTablePaginatorComponent,
		StructureTableToolbarAddActionsComponent,
	],
	template: `<mat-toolbar
		><span class="tw-flex-auto"></span
		><osee-structure-table-paginator
			[structuresCount]="
				structuresCount()
			" /><osee-structure-table-toolbar-add-actions
			[breadCrumb]="breadCrumb()"
	/></mat-toolbar>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StructureTableToolbarComponent {
	structuresCount = input.required<number>();
	breadCrumb = input('');
}
