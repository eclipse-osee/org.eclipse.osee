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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, computed, effect, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatMenu,
	MatMenuContent,
	MatMenuTrigger,
} from '@angular/material/menu';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
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
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { OperatorFunction } from 'rxjs';
import { filter } from 'rxjs/operators';
import { ConfigGroupMenuComponent } from '../../menus/config-group-menu/config-group-menu.component';
import { ConfigMenuComponent } from '../../menus/config-menu/config-menu.component';
import { FeatureMenuComponent } from '../../menus/feature-menu/feature-menu.component';
import { ValueMenuComponent } from '../../menus/value-menu/value-menu.component';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { view } from '../../types/pl-config-applicui-branch-mapping';
import { configGroup } from '../../types/pl-config-configurations';
import {
	configurationValue,
	plconfigTableEntry,
} from '../../types/pl-config-table';
import { PlconfigCellComponent } from '../plconfig-cell/plconfig-cell.component';
import { PlconfigFeatureCellComponent } from '../plconfig-feature-cell/plconfig-feature-cell.component';
import { PLConfigFilterComponent } from '../plconfig-filter/plconfig-filter.component';

@Component({
	selector: 'osee-plconfig-applicability-table',
	template: `<osee-plconfig-filter></osee-plconfig-filter>
		<div
			class="tw-h-[72vh] tw-w-screen tw-overflow-auto [&::-webkit-scrollbar-corner]:tw-bg-background-app-bar [&::-webkit-scrollbar-thumb]:tw-bg-primary [&::-webkit-scrollbar-track]:tw-border-r-[10px] [&::-webkit-scrollbar-track]:tw-bg-background-app-bar [&::-webkit-scrollbar]:tw-bg-background-app-bar">
			<table
				mat-table
				[dataSource]="dataSource"
				matSort
				matSortActive="feature"
				matSortDirection="asc"
				class="mat-elevation-z8 tw-w-full tw-min-w-[100vw] tw-max-w-[100vw] tw-border-separate tw-overflow-auto">
				@for (column of topHeaders(); track $index; let idx = $index) {
					<ng-container [matColumnDef]="column">
						<th
							mat-header-cell
							*matHeaderCellDef
							[attr.colspan]="topHeaderLengths()[idx]"
							class="tw-border-b-2 tw-border-r-2 tw-border-solid tw-border-foreground-divider tw-text-center tw-text-sm tw-font-bold tw-tracking-tighter tw-text-primary-600">
							{{ column }}
						</th>
					</ng-container>
				}
				@for (
					column of groupHeaders();
					track $index;
					let idx = $index
				) {
					<ng-container [matColumnDef]="column">
						<th
							mat-header-cell
							*matHeaderCellDef
							[attr.colspan]="groupHeaderLengths()[idx]"
							class="tw-border-b-2 tw-border-r-2 tw-border-solid tw-border-foreground-divider tw-text-center tw-text-sm tw-font-bold tw-tracking-tighter tw-text-primary-600">
							{{ column.trim() }}
						</th>
					</ng-container>
				}
				@for (column of viewHeaders(); track $index) {
					<ng-container
						[matColumnDef]="column.headerId"
						[sticky]="column.id === '-1'">
						<th
							mat-header-cell
							*matHeaderCellDef
							[ngClass]="{
								'tw-text-primary-darker': column.typeId === '6',
								'tw-text-primary':
									column.typeId !== '6' && !column.added,
								'tw-bg-success-300 tw-text-success-300-contrast':
									column.added,
								'tw-bg-warning-100': column.deleted,
							}"
							class="tw-cursor-pointer tw-border-b-2 tw-border-r-2 tw-border-solid tw-border-foreground-divider tw-text-center tw-font-bold"
							(click)="openConfigMenu(column)"
							(contextmenu)="
								openContextMenu(
									$event,
									column.typeId === '6' ? 'GROUP' : 'CONFIG',
									column.id
								)
							">
							{{ column.name }}
						</th>
						@if (column.typeId !== '-1' && column.id !== '-1') {
							<!-- Configuration/Group -->
							<td
								mat-cell
								*matCellDef="let element"
								class="tw-border-b-0 tw-border-r-2 tw-border-solid tw-border-foreground-divider tw-px-1">
								<osee-plconfig-cell
									[feature]="element"
									[configId]="column.id"
									[editMode]="editable()"
									[allowEdits]="
										column.typeId !== '6' &&
										element.id !== '-1'
									" />
							</td>
						} @else {
							<!-- Feature -->
							<td
								*matCellDef="let element"
								mat-cell
								class="tw-border-b-0 tw-border-r-2 tw-border-solid tw-border-foreground-divider tw-px-1 tw-text-inherit"
								(contextmenu)="
									openContextMenu($event, 'FEATURE', element)
								">
								<osee-plconfig-feature-cell
									[feature]="element" />
							</td>
						}
					</ng-container>
				}
				<tr
					mat-header-row
					*matHeaderRowDef="topHeaders(); sticky: true"></tr>
				<tr
					mat-header-row
					*matHeaderRowDef="groupHeaders(); sticky: true"></tr>
				<tr
					mat-header-row
					*matHeaderRowDef="viewHeaderIds(); sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: viewHeaderIds()"
					class="tw-h-12"
					[class]="
						row.added
							? 'odd:tw-text-success-300-constrast tw-font-bold odd:tw-bg-success-300 even:tw-bg-success-100 even:tw-text-success-100-contrast'
							: row.deleted
								? 'tw-font-bold odd:tw-bg-warning-100 odd:tw-text-warning-100-contrast even:tw-bg-warning-100 even:tw-text-warning-100-contrast'
								: row.changes
									? 'odd:tw-bg-accent-100 odd:tw-text-accent-100-contrast even:tw-bg-accent-200 even:tw-text-accent-200-contrast'
									: 'odd:tw-bg-background-background even:tw-bg-background-card'
					"></tr>
			</table>
		</div>
		<mat-paginator
			[pageSizeOptions]="pageSizeOptions()"
			[length]="tableCount()"
			[pageIndex]="pageIndex()"
			[pageSize]="pageSize()"
			(page)="setPage($event)"></mat-paginator>
		<mat-menu #featureMenu="matMenu">
			<ng-template
				matMenuContent
				let-feature="feature">
				<osee-plconfig-feature-menu
					[feature]="feature"></osee-plconfig-feature-menu>
			</ng-template>
		</mat-menu>
		<mat-menu #configMenu="matMenu">
			<ng-template
				matMenuContent
				let-config="config">
				<osee-plconfig-config-menu
					[config]="
						(config | async) || {
							name: '',
							description: '',
							hasFeatureApplicabilities: false,
							id: '-1',
						}
					"></osee-plconfig-config-menu>
			</ng-template>
		</mat-menu>
		<mat-menu #configGroupMenu="matMenu">
			<ng-template
				matMenuContent
				let-group="group">
				<osee-plconfig-config-group-menu
					[group]="
						(group | async) || {
							name: '',
							description: '',
							id: '-1',
							configurations: [],
						}
					"></osee-plconfig-config-group-menu>
			</ng-template>
		</mat-menu>
		<mat-menu #valueMenu="matMenu">
			<ng-template
				matMenuContent
				let-value="value">
				<osee-plconfig-value-menu
					[value]="value"></osee-plconfig-value-menu>
			</ng-template>
		</mat-menu>
		<div
			#featureMenuTrigger="matMenuTrigger"
			style="visibility: hidden; position: fixed"
			[style.left]="menuPosition.x"
			[style.top]="menuPosition.y"
			[matMenuTriggerFor]="featureMenu"
			class="featureMenu">
			LinkMenu
		</div>
		<div
			#configMenuTrigger="matMenuTrigger"
			style="visibility: hidden; position: fixed"
			[style.left]="menuPosition.x"
			[style.top]="menuPosition.y"
			[matMenuTriggerFor]="configMenu"
			class="configMenuTrigger">
			NodeMenu
		</div>
		<div
			#configGroupMenuTrigger="matMenuTrigger"
			style="visibility: hidden; position: fixed"
			[style.left]="menuPosition.x"
			[style.top]="menuPosition.y"
			[matMenuTriggerFor]="configGroupMenu"
			class="configGroupMenuTrigger">
			GraphMenu
		</div>
		<div
			#valueMenuTrigger="matMenuTrigger"
			style="visibility: hidden; position: fixed"
			[style.left]="menuPosition.x"
			[style.top]="menuPosition.y"
			[matMenuTriggerFor]="valueMenu"
			class="valueMenuTrigger">
			GraphMenu
		</div>`,
	imports: [
		FormsModule,
		AsyncPipe,
		NgClass,
		MatTable,
		MatSort,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
		MatMenu,
		MatMenuContent,
		MatMenuTrigger,
		FeatureMenuComponent,
		ConfigMenuComponent,
		ConfigGroupMenuComponent,
		ValueMenuComponent,
		PLConfigFilterComponent,
		PlconfigCellComponent,
		PlconfigFeatureCellComponent,
	],
})
export class ApplicabilityTableComponent {
	private uiStateService = inject(PlConfigUIStateService);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	private dialogService = inject(DialogService);
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');
	private _completeTable =
		this.currentBranchService.applicabilityTableData.pipe(
			takeUntilDestroyed()
		);
	protected completeTable = toSignal(this._completeTable, {
		initialValue: { table: [], headers: [], headerLengths: [] },
	});

	private _tableCount =
		this.currentBranchService.applicabilityTableDataCount.pipe(
			takeUntilDestroyed()
		);
	protected tableCount = toSignal(this._tableCount, { initialValue: 0 });
	protected pageIndex = this.uiStateService.currentPage;
	protected pageSize = this.uiStateService.currentPageSize;

	protected pageSizeOptions = computed(() => {
		const startingOptions = [this.pageSize()];
		//ensure there are atleast 5 entries between the current page and 0
		const pageOptions =
			this.pageSize() >= 5
				? Array.from(
						{ length: (this.pageSize() - 0) / 5 + 1 },
						(_, index) => 0 + index * 5
					)
				: [];
		//ensure there is atleast 5 entries between the current page index and max table count
		const countOptions =
			this.tableCount() >= 5 &&
			this.pageSize() < this.tableCount() &&
			this.tableCount() - this.pageSize() > 5
				? Array.from(
						{
							length:
								(this.tableCount() - this.pageSize()) / 5 + 1,
						},
						(_, index) => this.pageSize() + index * 5
					)
				: [];
		const combined = pageOptions
			.concat(startingOptions)
			.concat([this.tableCount()])
			.concat(countOptions);
		const set = new Set(combined);
		return Array.from(set);
	});
	topHeaders = computed(() => {
		const index = this.completeTable().headers.findIndex(
			(x) => x.id === '-1'
		);
		if (index !== -1) {
			if (
				Math.max(
					this.completeTable().headerLengths.reduce(
						(acc, curr) => acc + curr,
						0
						//@ts-expect-error headerLengths will always be populated
					) - this.completeTable().headerLengths.at(-1),
					0
				) > 0
			) {
				return ['       ', 'Configuration Groups', 'Configurations'];
			}
			return ['       ', 'Configurations'];
		}
		return ['       ', 'Configuration Groups'];
	});
	topHeaderLengths = computed(() => {
		const index = this.completeTable().headers.findIndex(
			(x) => x.id === '-1'
		);
		if (index !== -1) {
			return [
				1,
				Math.max(
					this.completeTable()
						.headerLengths.slice(
							0,
							this.completeTable().headerLengths.length - 1
						)
						.reduce((acc, curr) => acc + curr, 0),
					0
				),
				this.completeTable().headerLengths.at(-1),
			];
		}
		return [
			1,
			this.completeTable().headerLengths[0],
			Math.max(
				this.completeTable().headerLengths.reduce(
					(acc, curr) => acc + curr,
					0
				) - this.completeTable().headerLengths[0],
				0
			),
		];
	});
	groups = computed(() =>
		this.completeTable().headers.filter(
			(x) => x.typeId === ARTIFACTTYPEIDENUM.CONFIGURATION_GROUP
		)
	);
	numOfGroups = computed(() => this.groups().length);
	groupHeaders = computed(() => [
		'    ',
		...this.groups().map((x) => x.name + ' '),
	]);
	groupHeaderLengths = computed(() => {
		const index = this.completeTable().headers.findIndex(
			(x) => x.id === '-1'
		);
		if (index !== -1) {
			return [
				this.completeTable().headerLengths.at(-1) !== 0 ? 1 : 0,
				...this.completeTable().headerLengths,
			];
		}
		return [
			this.completeTable().headerLengths[0] !== 0 ? 1 : 0,
			...this.completeTable().headerLengths,
		];
	});

	viewHeaders = computed<(configurationValue & { headerId: string })[]>(
		() => [
			{
				id: '-1',
				name: 'Feature',
				gammaId: '-1',
				typeId: '-1',
				applicability: { id: '-1', gammaId: '-1', name: '' },
				headerId: '-1' + crypto.randomUUID(),
			},
			...this.completeTable()
				.headers.filter((x) => x.id !== '-1')
				.map((x) => ({
					...x,
					headerId: x.id + crypto.randomUUID(),
				})),
		]
	);
	viewHeaderIds = computed(() => this.viewHeaders().map((x) => x.headerId));
	hasNoGroup = computed(
		() => this.numOfGroups() !== this.completeTable().headerLengths.length
	);
	private sort = viewChild.required(MatSort);
	private paginator = viewChild.required(MatPaginator);
	private _filter = this.uiStateService.filter;
	protected dataSource = new MatTableDataSource<plconfigTableEntry>();
	private _updateDataSourceWithData = effect(
		() => {
			this.dataSource.data = this.completeTable().table;
		},
		{ allowSignalWrites: true }
	);
	private _updateDataSourceWithSorter = effect(
		() => {
			if (this.dataSource.sort === null) {
				this.dataSource.sort = this.sort();
			}
		},
		{ allowSignalWrites: true }
	);
	private _updateDataSourceWithPaginator = effect(
		() => {
			if (this.dataSource.paginator === null) {
				this.dataSource.paginator = this.paginator();
			}
		},
		{ allowSignalWrites: true }
	);
	private _updateDataSourceWithFilter = effect(
		() => {
			if (this.dataSource.paginator && this._filter()) {
				this.dataSource.paginator.firstPage();
			}
		},
		{ allowSignalWrites: true }
	);

	errors = this.uiStateService.errors;

	menuPosition = {
		x: '0',
		y: '0',
	};
	featureTrigger = viewChild.required('featureMenuTrigger', {
		read: MatMenuTrigger,
	});
	configTrigger = viewChild.required('configMenuTrigger', {
		read: MatMenuTrigger,
	});
	configGroupTrigger = viewChild.required('configGroupMenuTrigger', {
		read: MatMenuTrigger,
	});
	valueTrigger = viewChild.required('valueMenuTrigger', {
		read: MatMenuTrigger,
	});

	displayFeatureMenu(feature: string) {
		//do not display feature menu for compound applicabilities
		if (feature !== '' && feature !== '-1' && feature !== '0') {
			this.dialogService.displayFeatureDialog(feature).subscribe();
		}
	}

	openConfigMenu(config: configurationValue) {
		if (config.typeId !== '6') {
			this.dialogService
				.openEditConfigDialog(config.id, this.editable())
				.subscribe();
			return;
		}
		this.dialogService
			.openEditConfigGroupDialog(config.id, this.editable())
			.subscribe();
	}

	openContextMenu<T>(
		event: MouseEvent,
		type: 'FEATURE' | 'GROUP' | 'CONFIG' | 'VALUE',
		data: T
	) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		switch (type) {
			case 'FEATURE':
				this.featureTrigger().menuData = {
					feature: data,
				};
				this.configTrigger().closeMenu();
				this.configGroupTrigger().closeMenu();
				this.valueTrigger().closeMenu();
				this.featureTrigger().openMenu();
				break;
			case 'CONFIG':
				this.configTrigger().menuData = {
					config: this.currentBranchService
						.getView(data as unknown as string, true)
						.pipe(
							filter(
								(val) => val !== undefined
							) as OperatorFunction<view, view>
						),
				};
				this.configTrigger().openMenu();
				this.configGroupTrigger().closeMenu();
				this.valueTrigger().closeMenu();
				this.featureTrigger().closeMenu();
				break;
			case 'GROUP':
				this.configGroupTrigger().menuData = {
					group: this.currentBranchService
						.getCfgGroupDetail(data as unknown as string, true)
						.pipe(
							filter(
								(val) => val !== undefined
							) as OperatorFunction<
								configGroup | undefined,
								configGroup
							>
						),
				};
				this.configGroupTrigger().openMenu();
				this.configTrigger().closeMenu();
				this.valueTrigger().closeMenu();
				this.featureTrigger().closeMenu();
				break;
			case 'VALUE':
				this.valueTrigger().menuData = {
					value: data,
				};
				this.configGroupTrigger().closeMenu();
				this.configTrigger().closeMenu();
				this.valueTrigger().openMenu();
				this.featureTrigger().closeMenu();
				break;

			default:
				break;
		}
	}
	setPage(event: PageEvent) {
		this.uiStateService.currentPageSize.set(event.pageSize);
		this.uiStateService.currentPage.set(event.pageIndex);
	}
}
