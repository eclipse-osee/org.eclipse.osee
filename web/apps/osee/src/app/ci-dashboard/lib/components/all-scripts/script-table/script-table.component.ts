/*********************************************************************
 * Copyright (c) 2023 Boeing
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
	effect,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { scriptDefHeaderDetails } from '../../../table-headers/script-headers';
import { FormsModule } from '@angular/forms';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { HeaderService } from '@osee/shared/services';
import { CiDetailsTableService } from '../../../services/ci-details-table.service';
import type { DefReference, ScriptDefColumnFilters } from '../../../types/tmo';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { Router } from '@angular/router';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { SubsystemSelectorComponent } from '../../subsystem-selector/subsystem-selector.component';
import { TeamSelectorComponent } from '../../team-selector/team-selector.component';
import { TeamSelectorFilterComponent } from '../../team-selector/team-selector-filter.component';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { UserDataAccountService } from '../../../../../auth/user-data-account.service';
import { UserRoles } from '@osee/shared/types/auth';
import { map } from 'rxjs/operators';

const editRoles = new Set<string>([
	UserRoles.CI_TEST_MASTER,
	UserRoles.CI_ADMIN,
]);

@Component({
	selector: 'osee-script-table',
	imports: [
		AsyncPipe,
		SubsystemSelectorComponent,
		TeamSelectorComponent,
		TeamSelectorFilterComponent,
		FormsModule,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatFormField,
		MatInput,
		MatPaginator,
		MatSort,
		MatSortHeader,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<div class="tw-flex tw-h-full tw-w-screen tw-flex-col">
		@if (scriptDefs()) {
			<mat-table
				[dataSource]="datasource"
				class="tw-overflow-auto"
				matSort>
				@for (header of headers; track $index) {
					<ng-container [matColumnDef]="header">
						<th
							mat-header-cell
							*matHeaderCellDef
							mat-sort-header
							class="tw-text-center tw-align-middle tw-font-medium tw-text-primary-600"
							[matTooltip]="
								(getTableHeaderByName(header) | async)
									?.description || ''
							">
							<div class="tw-flex tw-flex-col tw-items-center">
								<div>
									{{
										(getTableHeaderByName(header) | async)
											?.humanReadable || ''
									}}
								</div>
								@if (
									header === 'team' &&
									isFilterableColumn(header)
								) {
									<osee-team-selector-filter />
								} @else if (isFilterableColumn(header)) {
									<mat-form-field
										class="tw-w-full"
										subscriptSizing="dynamic"
										(click)="$event.stopPropagation()">
										<input
											matInput
											[(ngModel)]="
												ciDetailsService.filterSignals[
													header
												]
											" />
									</mat-form-field>
								}
							</div>
						</th>
						<td
							mat-cell
							*matCellDef="let def"
							class="tw-align-middle">
							@if (header === 'name') {
								<button
									mat-list-item
									(click)="resultList(def.id)"
									class="tw-text-primary">
									{{ def[header] }}
								</button>
							} @else if (header === 'subsystem') {
								@if (canEditScripts()) {
									<osee-subsystem-selector [script]="def" />
								} @else {
									{{ def.subsystem || '' }}
								}
							} @else if (header === 'team') {
								@if (canEditScripts()) {
									<osee-team-selector [script]="def" />
								} @else {
									{{ def.team?.name?.value || '' }}
								}
							} @else {
								{{ def[header] }}
							}
						</td>
					</ng-container>
				}
				<tr
					mat-header-row
					*matHeaderRowDef="headers; sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: headers; let i = index"
					class="odd:tw-bg-selected-button even:tw-bg-background-background hover:tw-bg-background-app-bar"
					[attr.data-cy]="'script-def-table-row-' + row.name"></tr>
			</mat-table>

			<mat-paginator
				[pageSizeOptions]="[10, 15, 20, 25, 50, 75, 100, 200, 500]"
				[pageSize]="this.ciDetailsService.currentPageSize()"
				[pageIndex]="this.ciDetailsService.currentPage()"
				(page)="setPage($event)"
				[length]="scriptDefCount()"
				[disabled]="false"></mat-paginator>
		}
	</div>`,
})
export class ScriptTableComponent {
	ciDetailsService = inject(CiDetailsTableService);
	ciDashboardService = inject(CiDashboardUiService);
	headerService = inject(HeaderService);
	userData = inject(UserDataAccountService);
	router = inject(Router);

	headerFilters = signal<(keyof ScriptDefColumnFilters)[]>([
		'name',
		'team',
		'subsystem',
		'safety',
		'statusBy',
		'machineName',
		'fullScriptName',
		'notes',
	]);

	isFilterableColumn(header: string): header is keyof ScriptDefColumnFilters {
		return this.headerFilters().includes(
			header as keyof ScriptDefColumnFilters
		);
	}

	private matSort = viewChild(MatSort);

	scriptDefCount = toSignal(this.ciDetailsService.scriptDefCount, {
		initialValue: 0,
	});

	scriptDefs = toSignal(
		this.ciDetailsService.scriptDefs.pipe(takeUntilDestroyed())
	);
	ciSetId = this.ciDashboardService.ciSetId;

	canEditScripts = toSignal(
		this.userData.user.pipe(
			map((u) => (u?.roles ?? []).some((r) => editRoles.has(r.id)))
		),
		{ initialValue: false }
	);

	datasource = new MatTableDataSource<DefReference>();

	resultList(defId: string) {
		let url = this.router.url;
		url = url.replace('allScripts', 'results');
		const tree = this.router.parseUrl(url);
		tree.queryParams['script'] = defId;
		this.router.navigateByUrl(tree);
	}



	private _sortEffect = effect(() => {
		const sort = this.matSort();
		if (sort) {
			this.datasource.sort = sort;
		}
	});

	private _scriptsEffect = effect(() => {
		const scripts = this.scriptDefs();
		if (scripts) {
			this.datasource.data = scripts;
		} else {
			this.datasource.data = [];
		}
	});

	getTableHeaderByName(header: keyof DefReference) {
		return this.headerService.getHeaderByName(
			scriptDefHeaderDetails,
			header
		);
	}

	headers: (keyof DefReference)[] = [
		'name',
		'team',
		'subsystem',
		'safety',
		'statusBy',
		'latestExecutionDate',
		'latestResult',
		'latestPassedCount',
		'latestFailedCount',
		'latestScriptAborted',
		'machineName',
		'latestElapsedTime',
		'fullScriptName',
		'notes',
	];

	setPage(event: PageEvent) {
		this.ciDetailsService.pageSize = event.pageSize;
		this.ciDetailsService.page = event.pageIndex;
	}
}
