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
import { AsyncPipe, CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import {
	AfterViewInit,
	Component,
	OnChanges,
	SimpleChanges,
	ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	BehaviorSubject,
	combineLatest,
	from,
	iif,
	of,
	OperatorFunction,
	throwError,
} from 'rxjs';
import {
	distinct,
	filter,
	map,
	mergeMap,
	reduce,
	share,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { ExtendedNameValuePairWithChanges } from '../../types/base-types/ExtendedNameValuePair';
import { extendedFeature, trackableFeature } from '../../types/features/base';
import {
	view,
	viewWithChanges,
} from '../../types/pl-config-applicui-branch-mapping';
import {
	configGroup,
	configGroupWithChanges,
} from '../../types/pl-config-configurations';
import { FeatureMenuComponent } from '../../menus/feature-menu/feature-menu.component';
import { ConfigMenuComponent } from '../../menus/config-menu/config-menu.component';
import { ConfigGroupMenuComponent } from '../../menus/config-group-menu/config-group-menu.component';
import { ValueMenuComponent } from '../../menus/value-menu/value-menu.component';
import { MatListModule } from '@angular/material/list';

@Component({
	selector: 'osee-plconfig-applicability-table',
	templateUrl: './applicability-table.component.html',
	styleUrls: ['./applicability-table.component.sass'],
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		NgFor,
		NgIf,
		NgClass,
		MatFormFieldModule,
		MatInputModule,
		MatDialogModule,
		MatIconModule,
		MatTableModule,
		MatSortModule,
		MatTooltipModule,
		MatSelectModule,
		MatOptionModule,
		MatListModule,
		MatPaginatorModule,
		MatMenuModule,
		FeatureMenuComponent,
		ConfigMenuComponent,
		ConfigGroupMenuComponent,
		ValueMenuComponent,
	],
})
export class ApplicabilityTableComponent implements AfterViewInit, OnChanges {
	branchApplicability = this.currentBranchService.branchApplicability.pipe(
		share(),
		shareReplay({ refCount: true, bufferSize: 1 })
	);
	private _filter = new BehaviorSubject<string>('');
	dataSource = combineLatest([this.branchApplicability, this._filter]).pipe(
		tap((value) => {
			this.uiStateService.editableValue = value[0].editable;
			this.uiStateService.groupsString = value[0].groups.map((a) => a.id);
		}),
		switchMap(([branchApplicability, filter]) =>
			iif(
				() => filter.length === 0,
				of(new MatTableDataSource(branchApplicability.features)).pipe(
					map((ds) => {
						if (this.paginator) {
							ds.paginator = this.paginator;
						}
						return ds;
					})
				),
				of(new MatTableDataSource(branchApplicability.features)).pipe(
					map((data) => {
						if (this.paginator) {
							data.paginator = this.paginator;
						}
						data.filter = filter;
						if (data.paginator) {
							data.paginator.firstPage();
						}
						return data;
					})
				)
			)
		)
	);

	topLevelHeaders = this.currentBranchService.topLevelHeaders;
	secondaryHeaders = this.currentBranchService.secondaryHeaders;
	secondaryHeaderLength = this.currentBranchService.secondaryHeaderLength;
	headers = this.currentBranchService.headers;
	columnIds = this.headers.pipe(map((a) => a.map((b) => b.columnId)));
	errors = this.uiStateService.errors;
	viewCount = this.currentBranchService.viewCount;
	groupCount = this.currentBranchService.groupCount;
	groupList = this.currentBranchService.groupList;
	_editable = this.uiStateService.editable;
	applicsWithFeatureConstraint$ =
		this.currentBranchService.applicsWithFeatureConstraints;
	feature$ = this.currentBranchService.branchApplicFeatures;
	@ViewChild(MatSort, { static: false }) sort: MatSort;
	menuPosition = {
		x: '0',
		y: '0',
	};
	@ViewChild('featureMenuTrigger', { static: true })
	featureTrigger!: MatMenuTrigger;
	@ViewChild('configMenuTrigger', { static: true })
	configTrigger!: MatMenuTrigger;
	@ViewChild('configGroupMenuTrigger', { static: true })
	configGroupTrigger!: MatMenuTrigger;
	@ViewChild('valueMenuTrigger', { static: true })
	valueTrigger!: MatMenuTrigger;
	constructor(
		private uiStateService: PlConfigUIStateService,
		private currentBranchService: PlConfigCurrentBranchService,
		public dialog: MatDialog,
		private dialogService: DialogService
	) {
		this.sort = new MatSort();
	}
	ngOnChanges(changes: SimpleChanges): void {
		this.uiStateService.editableValue = false;
	}

	ngAfterViewInit() {
		this.dataSource
			.pipe(
				take(1),
				map((val) => {
					val.sort = this.sort;
					val.paginator = this.paginator;
				})
			)
			.subscribe();
	}
	@ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;
	applyFilter(event: Event) {
		this._filter.next(
			(event.target as HTMLInputElement).value.trim().toLowerCase()
		);
	}
	log(value: any) {
		console.log(value);
	}
	valueTracker(index: any, item: any) {
		return index;
	}
	applicIsNotPermitted(
		columnName: string,
		feature: trackableFeature,
		featureValue: string
	) {
		// 2 cases to check for:
		//
		// #1: if the input applic matches a childApplic in a feature constraint, and the parentApplic of the same
		// constraint shares a feature but not value with an applic on the current config (column), disable the input applicability option
		//
		// #2: if the input applic matches the feature but not value of parentApplic in a feature constraint, and the childApplic of the same
		//constraint matches an applic on the current config (column), disable the input applicablity option
		let isChildApplicMatch = false;
		let isParentApplicWrongValueMatch = false;
		return combineLatest([
			this.applicsWithFeatureConstraint$,
			this.feature$,
		]).pipe(
			switchMap(([childApplics, features]) =>
				of(childApplics).pipe(
					map((childApplics) => {
						// find a parentApplic that shares the feature but NOT the value of the input applic
						const foundParent = childApplics.find(
							(childApplic) =>
								childApplic.constraints[0].name
									.toLowerCase()
									.includes(feature.name.toLowerCase()) &&
								!childApplic.constraints[0].name
									.toLowerCase()
									.includes(featureValue.toLowerCase())
						);
						// find a childApplic that matches the applic of the input applic
						const foundChild = childApplics.find(
							(childApplic) =>
								childApplic.name
									.toLowerCase()
									.includes(feature.name.toLowerCase()) &&
								childApplic.name
									.toLowerCase()
									.includes(featureValue.toLowerCase())
						);
						if (foundParent) {
							isParentApplicWrongValueMatch = true;
							return foundParent;
						} else if (foundChild) {
							isChildApplicMatch = true;
							return foundChild;
						} else {
							return undefined;
						}
					}),
					filter((childApplic) => childApplic !== undefined),
					switchMap((childApplic) =>
						of(
							features
								.map((feature) => {
									return (
										(isParentApplicWrongValueMatch &&
											childApplic &&
											feature.configurations.some(
												(config) => {
													// if the childApplic within the same feature constraint as the found parentApplic matches an existing applicability
													// within the current config, do not allow the input option to be selectable
													return (
														config.name
															.toLowerCase()
															.includes(
																columnName.toLowerCase()
															) &&
														childApplic.name
															.toLowerCase()
															.includes(
																config.value.toLowerCase()
															) &&
														childApplic.name
															.toLowerCase()
															.includes(
																feature.name.toLowerCase()
															)
													);
												}
											)) ||
										(isChildApplicMatch &&
											childApplic &&
											feature.configurations.some(
												(config) => {
													// if the parentApplic matches the feature but not value of an applic on the current configuration, mark the current value as unselectable
													return (
														config.name
															.toLowerCase()
															.includes(
																columnName.toLowerCase()
															) &&
														!childApplic.constraints[0].name
															.toLowerCase()
															.includes(
																config.value.toLowerCase()
															) &&
														childApplic.constraints[0].name
															.toLowerCase()
															.includes(
																feature.name.toLowerCase()
															)
													);
												}
											))
									);
								})
								.reduce(
									(acc, curr) => (acc = acc || curr),
									false
								)
						)
					)
				)
			)
		);
	}
	modifyProduct(
		configuration: string,
		feature: trackableFeature,
		event: MatSelectChange
	) {
		this.requestConfigurationChange(configuration, feature, event);
	}
	modifyConfiguration(
		configuration: string,
		feature: trackableFeature,
		event: MatSelectChange
	) {
		this.requestConfigurationChange(configuration, feature, event);
	}
	requestConfigurationChange(
		configuration: string,
		feature: trackableFeature,
		event: HTMLInputElement | MatSelectChange
	) {
		combineLatest([
			this.branchApplicability.pipe(
				take(1),
				switchMap((app) =>
					of(app.features).pipe(
						map((features) =>
							features.find((value) => value.id === feature.id)
						),
						filter(
							(feature) => feature !== undefined
						) as OperatorFunction<
							extendedFeature | undefined,
							extendedFeature
						>,
						map((featureValue) => featureValue.values)
					)
				)
			),
			this.branchApplicability.pipe(
				take(1),
				switchMap((app) =>
					of(app.views).pipe(
						map((views) =>
							views.find(
								(value) =>
									value.name.toLowerCase() ===
									configuration.toLowerCase()
							)
						),
						filter(
							(feature) => feature !== undefined
						) as OperatorFunction<view | undefined, view>,
						map((view) => view.id)
					)
				)
			),
			this.groupList,
		])
			.pipe(
				take(1),
				switchMap(([latestFeatures, latestViews, groupList]) =>
					of(latestFeatures, latestViews, groupList).pipe(
						switchMap((latest) =>
							iif(
								() => feature.multiValued,
								of(event.value as string[]),
								of([event.value as string])
							).pipe(
								switchMap((values) =>
									of(values).pipe(
										mergeMap((values) =>
											from(values).pipe(
												switchMap((value) =>
													iif(
														() =>
															latestFeatures.findIndex(
																(v) =>
																	v.toLowerCase() ===
																	value.toLowerCase()
															) === -1,
														throwError(() => {
															this.uiStateService.error =
																'Error: ' +
																value +
																' is not a valid value.';
														}),
														of(value)
													)
												)
											)
										)
									)
								)
							)
						),
						distinct(),
						reduce((acc, curr) => [...acc, curr], [] as string[]),
						switchMap((v) =>
							this.currentBranchService
								.modifyConfiguration(
									latestViews,
									feature.name + ' = ' + v,
									groupList
								)
								.pipe(take(1))
						)
					)
				)
			)
			.subscribe();
	}
	isSticky(header: string) {
		return header === 'feature';
	}
	isCorrectConfiguration(
		configName: { name: string; value: string },
		column: string
	) {
		return configName.name === column;
	}
	isAddedCfg(configName: string) {
		return this.currentBranchService.findViewByName(configName).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				view | viewWithChanges | undefined,
				view | viewWithChanges
			>,
			take(1),
			filter(
				(val) => (val as viewWithChanges)?.changes !== undefined
			) as OperatorFunction<view | viewWithChanges, viewWithChanges>,
			map((val) => val.added)
		);
	}

	isDeletedCfg(configName: string) {
		return this.currentBranchService.findViewByName(configName).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				view | viewWithChanges | undefined,
				view | viewWithChanges
			>,
			take(1),
			filter(
				(val) => (val as viewWithChanges)?.changes !== undefined
			) as OperatorFunction<view | viewWithChanges, viewWithChanges>,
			map((val) => val.deleted)
		);
	}

	hasChangesCfg(configName: string) {
		return this.currentBranchService.findViewByName(configName).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				view | viewWithChanges | undefined,
				view | viewWithChanges
			>,
			take(1),
			map((val) => (val as viewWithChanges).changes !== undefined)
		);
	}

	isDeletedCfgGroup(configName: string) {
		return this.currentBranchService.findGroup(configName).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				configGroup | configGroupWithChanges | undefined,
				configGroup | configGroupWithChanges
			>,
			take(1),
			filter(
				(val) => (val as configGroupWithChanges)?.changes !== undefined
			) as OperatorFunction<
				configGroup | configGroupWithChanges,
				configGroupWithChanges
			>,
			map((val) => val.deleted)
		);
	}

	isAddedCfgGroup(configName: string) {
		return this.currentBranchService.findGroup(configName).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				configGroup | configGroupWithChanges | undefined,
				configGroup | configGroupWithChanges
			>,
			take(1),
			filter(
				(val) => (val as configGroupWithChanges)?.changes !== undefined
			) as OperatorFunction<
				configGroup | configGroupWithChanges,
				configGroupWithChanges
			>,
			map((val) => val.added)
		);
	}

	hasChangesCfgGroup(configName: string) {
		return this.currentBranchService.findGroup(configName).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				configGroup | configGroupWithChanges | undefined,
				configGroup | configGroupWithChanges
			>,
			take(1),
			map((val) => (val as configGroupWithChanges).changes !== undefined)
		);
	}
	isACfgGroup(name: string) {
		return this.currentBranchService.isACfgGroup(name);
	}
	isCompoundApplic(name: string) {
		return name.includes(' | ') || name.includes(' & ');
	}
	getCompoundApplicLines(name: string) {
		if (!this.isCompoundApplic(name)) {
			return [name];
		}
		const operator = name.includes('|') ? '|' : '&';
		const names = name.split(operator);
		let returnedArray =
			names.length == 2
				? [names[0].trim() + ' ' + operator, names[1].trim()]
				: [];
		if (returnedArray.length == 0) {
			//loop to split comp applic into applic array (if there are more than 2 applicabilities)
			for (let i = 0; i < names.length; i++) {
				//if last name, don't return operator
				if (i == names.length - 1) {
					returnedArray.push(names[i].trim());
				} else {
					returnedArray.push(names[i].trim() + ' ' + operator);
				}
			}
		}
		return returnedArray;
	}
	displayFeatureMenu(feature: extendedFeature) {
		//do not display feature menu for compound applicabilities
		if (!this.isCompoundApplic(feature.name)) {
			this.dialogService.displayFeatureMenu(feature).subscribe();
		}
	}

	openConfigMenu(header: string, editable: string) {
		this.dialogService.openConfigMenu(header, editable).subscribe();
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
				this.featureTrigger.menuData = {
					feature: data,
				};
				this.configTrigger.closeMenu();
				this.configGroupTrigger.closeMenu();
				this.valueTrigger.closeMenu();
				this.featureTrigger.openMenu();
				break;
			case 'CONFIG':
				this.configTrigger.menuData = {
					config: this.currentBranchService
						.findViewByName(data as unknown as string)
						.pipe(
							filter(
								(val) => val !== undefined
							) as OperatorFunction<
								view | viewWithChanges | undefined,
								view | viewWithChanges
							>
						),
				};
				this.configTrigger.openMenu();
				this.configGroupTrigger.closeMenu();
				this.valueTrigger.closeMenu();
				this.featureTrigger.closeMenu();
				break;
			case 'GROUP':
				this.configGroupTrigger.menuData = {
					group: this.currentBranchService
						.findGroup(data as unknown as string)
						.pipe(
							filter(
								(val) => val !== undefined
							) as OperatorFunction<
								| configGroup
								| configGroupWithChanges
								| undefined,
								configGroup | configGroupWithChanges
							>
						),
				};
				this.configGroupTrigger.openMenu();
				this.configTrigger.closeMenu();
				this.valueTrigger.closeMenu();
				this.featureTrigger.closeMenu();
				break;
			case 'VALUE':
				this.valueTrigger.menuData = {
					value: data,
				};
				this.configGroupTrigger.closeMenu();
				this.configTrigger.closeMenu();
				this.valueTrigger.openMenu();
				this.featureTrigger.closeMenu();
				break;

			default:
				break;
		}
	}

	getUniqueConfigurations(
		configurations: ExtendedNameValuePairWithChanges[]
	) {
		return configurations.filter(
			(v, i, a) => a.map((z) => z.name).indexOf(v.name) == i
		);
	}

	/**istanbul ignore next */
	sortMultiValue(values: string[]) {
		return values
			.sort((a, b) => a.toLowerCase().localeCompare(b.toLowerCase()))
			.toString();
	}
}
