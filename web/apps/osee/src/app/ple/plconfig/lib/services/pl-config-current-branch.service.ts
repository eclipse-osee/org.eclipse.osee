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
import { inject, Injectable } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { SideNavService } from '@osee/shared/services/layout';
import {
	ModificationType,
	changeInstance,
	changeTypeNumber,
	difference,
	ignoreType,
	itemTypeIdRelation,
} from '@osee/shared/types/change-report';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import { Observable, combineLatest, concat, from, iif, of } from 'rxjs';
import {
	concatMap,
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	reduce,
	repeat,
	repeatWhen,
	share,
	shareReplay,
	startWith,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { configurationValue, plConfigTable } from '../types/pl-config-table';
import { extendedFeatureWithChanges } from '../types/features/base';
import { viewWithChanges } from '../types/pl-config-applicui-branch-mapping';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import {
	configGroup,
	configuration,
	editConfiguration,
} from '../types/pl-config-configurations';
import { modifyFeature, writeFeature } from '../types/pl-config-features';
import { productType } from '../types/pl-config-product-types';
import {
	applicWithConstraints,
	featureConstraintData,
} from './../types/pl-config-feature-constraints';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigTypesService } from './pl-config-types.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';
import { applic } from '@osee/applicability/types';
import { transactionToken } from '@osee/transactions/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Injectable({
	providedIn: 'root',
})
export class PlConfigCurrentBranchService {
	private uiStateService = inject(PlConfigUIStateService);
	private branchService = inject(PlConfigBranchService);
	private typesService = inject(PlConfigTypesService);
	private sideNavService = inject(SideNavService);
	private uiService = inject(UiService);

	private _applicsWithFeatureConstraints: Observable<
		applicWithConstraints[]
	> = this.uiStateService.branchId.pipe(
		filter((val) => val !== ''),
		switchMap((branchId) =>
			this.branchService.getApplicsWithFeatureConstraints(branchId).pipe(
				repeatWhen((_) => this.uiStateService.updateReq),
				share()
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _productTypes = this.uiStateService.branchId.pipe(
		filter((id) => id !== '' && id !== '-1' && id !== undefined),
		switchMap((id) =>
			this.typesService
				.getProductTypes(id)
				.pipe(shareReplay({ bufferSize: 1, refCount: true }))
		)
	);

	private _features = this.uiService.id.pipe(
		filter((id) => id !== '' && id !== '-1' && id !== undefined),
		switchMap((id) =>
			this.branchService
				.getFeatures(id)
				.pipe(shareReplay({ bufferSize: 1, refCount: true }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	private _views = this.uiService.id.pipe(
		filter((id) => id !== '' && id !== '-1' && id !== undefined),
		switchMap((id) =>
			this.branchService
				.getViewsOrderedByName(id)
				.pipe(shareReplay({ bufferSize: 1, refCount: true }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get applicsWithFeatureConstraints() {
		return this._applicsWithFeatureConstraints;
	}

	public get productTypes() {
		return this._productTypes;
	}

	public get features() {
		return this._features;
	}
	public get views() {
		return this._views;
	}
	get differences() {
		return this.uiStateService.differences;
	}
	set difference(value: changeInstance[]) {
		this.uiStateService.difference = value;
	}

	set sideNav(value: {
		opened: boolean;
		field: string;
		currentValue: string | number | applic | boolean;
		previousValue?: string | number | applic | boolean;
		transaction?: transactionToken;
		user?: string;
		date?: string;
	}) {
		this.sideNavService.rightSideNav = value;
	}
	public get cfgGroups() {
		return this.uiStateService.branchId.pipe(
			filter((val) => val !== ''),
			switchMap((branchId) =>
				this.branchService.getCfgGroups(branchId).pipe(
					repeatWhen((_) => this.uiStateService.updateReq),
					share()
				)
			),
			share()
		);
	}

	public getCfgGroupsForView(viewId: string) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((val) => val !== ''),
			switchMap((id) =>
				this.branchService
					.getCfgGroupsForView(id, viewId)
					.pipe(repeat({ delay: () => this.uiService.update }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	public getView(viewId: string, useDiffs = false) {
		const query = this.uiStateService.branchId.pipe(
			take(1),
			filter((val) => val !== ''),
			switchMap((id) =>
				this.branchService
					.getView(id, viewId)
					.pipe(repeat({ delay: () => this.uiService.update }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
		if (useDiffs) {
			const ___differences = combineLatest([
				this.uiStateService.isInDiff,
				this.differences,
			]).pipe(
				take(1),
				map(([mode, diffs]) =>
					diffs !== undefined && diffs.length !== 0 && mode
						? diffs
						: []
				)
			);
			const ___diffsmultiemit = ___differences.pipe(
				concatMap((d) => from(d)),
				filter(
					(val) =>
						val.ignoreType !==
						ignoreType.DELETED_AND_DNE_ON_DESTINATION
				)
			);
			const ___viewsAttrChanges = ___diffsmultiemit.pipe(
				filter((d) => d.changeType.id === '222'), //filter to only be attributes
				filter(
					(d) =>
						d.itemTypeId === ATTRIBUTETYPEIDENUM.NAME ||
						d.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION ||
						d.itemTypeId === ATTRIBUTETYPEIDENUM.PRODUCT_TYPE
				)
			);
			const ___viewAttrChanges = ___viewsAttrChanges.pipe(
				filter((d) => d.artId === viewId),
				map((d) => d)
			);

			const ___viewsRelChanges = ___diffsmultiemit.pipe(
				filter((d) => d.changeType.id === '333'), //filter to only be relations
				filter(
					(d) =>
						(d.itemTypeId as itemTypeIdRelation).id ===
						RELATIONTYPEIDENUM.PRODUCT_LINE_CONFIGURATION_GROUP
				)
			);
			const __viewRelChanges = ___viewsRelChanges.pipe(
				filter((d) => viewId === d.artIdB),
				map((d) => d)
			);

			const __changes = concat(___viewAttrChanges, __viewRelChanges).pipe(
				reduce(
					(acc, curr) => [...acc, curr],
					[] as changeInstance<
						string | number | boolean | null | undefined
					>[]
				)
			);

			const setup = query.pipe(
				map((q) => {
					const queryWithChanges: viewWithChanges = {
						deleted: false,
						added: false,
						changes: {
							name: undefined,
							hasFeatureApplicabilities: undefined,
							productApplicabilities: undefined,
						},
						...q,
					};
					return queryWithChanges;
				})
			);
			const process = combineLatest([setup, __changes]).pipe(
				map(([q, d]) => {
					d.forEach((change) => {
						if (change.changeType.id === '222') {
							this.updateViewAttributes(change, q);
						}
						if (change.changeType.id === '333') {
							//TODO: currently not enough information on the create view definition to ascertain whether or not the relation has been deleted or added.
						}
					});
					return q;
				})
			);
			return process;
		}
		return query;
	}

	public getViewsByIds(viewIds: string[]) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((val) => val !== ''),
			switchMap((id) =>
				this.branchService
					.getViewsByIds(id, viewIds)
					.pipe(repeat({ delay: () => this.uiService.update }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	public getFeatureById(featureId: string) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((val) => val !== ''),
			switchMap((id) =>
				this.branchService
					.getFeatureById(id, featureId)
					.pipe(repeat({ delay: () => this.uiService.update }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}
	public getCfgGroupDetail(cfgGroup: string, useDiffs = false) {
		const query = this.uiStateService.branchId.pipe(
			filter((val) => val !== ''),
			switchMap((branchId) =>
				this.branchService.getCfgGroupDetail(branchId, cfgGroup).pipe(
					repeatWhen((_) => this.uiStateService.updateReq),
					share()
				)
			)
		);
		if (useDiffs) {
			const ___differences = combineLatest([
				this.uiStateService.isInDiff,
				this.differences,
			]).pipe(
				take(1),
				map(([mode, diffs]) =>
					diffs !== undefined && diffs.length !== 0 && mode
						? diffs
						: []
				)
			);
			const ___diffsmultiemit = ___differences.pipe(
				concatMap((d) => from(d)),
				filter(
					(val) =>
						val.ignoreType !==
						ignoreType.DELETED_AND_DNE_ON_DESTINATION
				)
			);
			const ___viewsAttrChanges = ___diffsmultiemit.pipe(
				filter((d) => d.changeType.id === '222'), //filter to only be attributes
				filter(
					(d) =>
						d.itemTypeId === ATTRIBUTETYPEIDENUM.NAME ||
						d.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION ||
						d.itemTypeId === ATTRIBUTETYPEIDENUM.PRODUCT_TYPE //TODO groups don't actually have product type???
				)
			);
			const ___viewAttrChanges = ___viewsAttrChanges.pipe(
				filter((d) => d.artId === cfgGroup),
				map((d) => d)
			);

			const ___viewsRelChanges = ___diffsmultiemit.pipe(
				filter((d) => d.changeType.id === '333'), //filter to only be relations
				filter(
					(d) =>
						(d.itemTypeId as itemTypeIdRelation).id ===
						RELATIONTYPEIDENUM.PRODUCT_LINE_CONFIGURATION_GROUP
				)
			);
			const __viewRelChanges = ___viewsRelChanges.pipe(
				filter((d) => cfgGroup === d.artId),
				map((d) => d)
			);

			const __changes = concat(___viewAttrChanges, __viewRelChanges).pipe(
				reduce(
					(acc, curr) => [...acc, curr],
					[] as changeInstance<
						string | number | boolean | null | undefined
					>[]
				)
			);

			const setup = query.pipe(
				map((q) => {
					const queryWithChanges: configGroup = {
						deleted: false,
						added: false,
						changes: {},
						...q,
					};
					return queryWithChanges;
				})
			);
			const process = combineLatest([setup, __changes]).pipe(
				map(([q, d]) => {
					d.forEach((change) => {
						if (change.changeType.id === '222') {
							this.updateGroupAttributes(change, q);
						}
						if (change.changeType.id === '333') {
							//TODO: need to use getViewByIds() to fetch the list of configurations TBD
						}
					});
					return q;
				})
			);
			return process;
		}
		return query;
	}
	public editConfiguration(featureId: string, body: string) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val !== ''),
			switchMap((value) =>
				this.branchService
					.modifyConfiguration(value, featureId, body)
					.pipe(
						tap((response) => {
							if (response.results.length === 0) {
								this.uiStateService.updateReqConfig = true;
								this.uiStateService.error = '';
							}
						})
					)
			)
		);
	}
	// Modifies feature value for configuration
	public modifyConfiguration(featureId: string, body: string) {
		return this.editConfiguration(featureId, body).pipe(
			tap((response) => {
				if (response.success) {
					this.uiStateService.updateReqConfig = true;
					this.uiStateService.error = '';
				}
			})
		);
	}

	public setApplicability(featureId: string, viewId: string, body: string[]) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((val) => val !== ''),
			switchMap((id) =>
				this.branchService
					.setApplicability(id, featureId, viewId, body)
					.pipe(
						tap((response) => {
							if (response.success) {
								this.uiStateService.updateReqConfig = true;
								this.uiStateService.error = '';
							}
						})
					)
			)
		);
	}
	public editConfigurationDetails(body: editConfiguration) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val !== ''),
			switchMap((val) =>
				this.branchService.editConfiguration(val, body).pipe(
					tap((response) => {
						if (response.success) {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						}
					})
				)
			)
		);
	}
	public addConfiguration(body: configuration) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val !== ''),
			switchMap((branchId) =>
				this.branchService.addConfiguration(branchId, body).pipe(
					tap((response) => {
						if (response.results.length === 0) {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						}
					})
				)
			)
		);
	}
	public deleteConfiguration(configId: string) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val !== ''),
			switchMap((val) =>
				this.branchService.deleteConfiguration(configId, val).pipe(
					tap((results) => {
						if (results.success) {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						} else {
							this.uiStateService.error = results.results[0];
						}
					})
				)
			)
		);
	}
	public addFeature(feature: writeFeature) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.addFeature(branchId, feature).pipe(
					tap((val) => {
						if (val.results.length > 0) {
							this.uiStateService.error = val.results[0];
						} else {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						}
					})
				)
			)
		);
	}

	public modifyFeature(feature: modifyFeature) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.modifyFeature(branchId, feature).pipe(
					tap((val) => {
						if (val.results.length > 0) {
							this.uiStateService.error = val.results[0];
						} else {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						}
					})
				)
			)
		);
	}

	public deleteFeature(feature: string) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.deleteFeature(branchId, feature).pipe(
					tap((val) => {
						if (val.results.length > 0) {
							this.uiStateService.error = val.results[0];
						}
					})
				)
			)
		);
	}
	public addConfigurationGroup(cfgGroup: ConfigurationGroupDefinition) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService
					.addConfigurationGroup(branchId, cfgGroup)
					.pipe(
						tap((val) => {
							if (val.results.length > 0) {
								this.uiStateService.error = val.results[0];
							} else {
								this.uiStateService.updateReqConfig = true;
							}
						})
					)
			)
		);
	}
	public deleteConfigurationGroup(id: string) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.deleteConfigurationGroup(branchId, id).pipe(
					tap((val) => {
						if (val.results.length > 0) {
							this.uiStateService.error = val.results[0];
						} else {
							this.uiStateService.updateReqConfig = true;
						}
					})
				)
			)
		);
	}
	public updateConfigurationGroup(cfgGroup: ConfigurationGroupDefinition) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService
					.updateConfigurationGroup(branchId, cfgGroup)
					.pipe(
						tap((val) => {
							if (val.results.length > 0) {
								this.uiStateService.error = val.results[0];
							} else {
								this.uiStateService.updateReqConfig = true;
							}
						})
					)
			)
		);
	}
	public addCompoundApplicability(compApplicName: string) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService
					.addCompoundApplicability(branchId, compApplicName)
					.pipe(
						tap((val) => {
							if (val.results.length > 0) {
								this.uiStateService.error = val.results[0];
							} else {
								this.uiStateService.updateReqConfig = true;
								this.uiStateService.error = '';
							}
						})
					)
			)
		);
	}
	public deleteCompoundApplicability(id: string) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService
					.deleteCompoundApplicability(branchId, id)
					.pipe(
						tap((val) => {
							if (val.results.length > 0) {
								this.uiStateService.error = val.results[0];
							} else {
								this.uiStateService.updateReqConfig = true;
							}
						})
					)
			)
		);
	}
	public addFeatureConstraint(data: featureConstraintData) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.addFeatureConstraint(data, branchId).pipe(
					tap((val) => {
						if (val.results.length > 0) {
							this.uiStateService.error = val.results[0];
						} else {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						}
					})
				)
			)
		);
	}
	public deleteFeatureConstraint(data: featureConstraintData) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.deleteFeatureConstraint(branchId, data).pipe(
					tap((val) => {
						if (val.results.length > 0) {
							this.uiStateService.error = val.results[0];
						} else {
							this.uiStateService.updateReqConfig = true;
							this.uiStateService.error = '';
						}
					})
				)
			)
		);
	}
	public getFeatureConstraintConflicts(
		childApplicId: number | string,
		parentApplicId: number | string
	) {
		return this.uiStateService.branchId.pipe(
			filter((val) => val != ''),
			switchMap((branchId) =>
				this.branchService.getFeatureConstraintConflicts(
					branchId,
					childApplicId,
					parentApplicId
				)
			)
		);
	}

	public getFeatureValues(
		configId: string,
		featureId: string,
		pageSize: number,
		pageNum: string | number,
		filter?: string
	) {
		return this.uiService.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.branchService.getFeatureValues(
					branchId,
					configId,
					featureId,
					pageNum,
					pageSize,
					filter
				)
			)
		);
	}

	public getFeatureValuesCount(
		configId: string,
		featureId: string,
		filter?: string
	) {
		return this.uiService.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.branchService.getFeatureValuesCount(
					branchId,
					configId,
					featureId,
					filter
				)
			)
		);
	}

	private __applicabilityTableData = combineLatest([
		this.uiService.id,
		this.uiStateService.filter$,
		this.uiService.viewId,
		this.uiStateService.currentPage$,
		this.uiStateService.currentPageSize$,
	]).pipe(
		filter(([branchId]) => branchId !== ''),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([branchId, filter, viewId, page, pageSize]) =>
			this.branchService
				.getApplicabilityTable(
					branchId,
					viewId,
					page + 1,
					pageSize,
					filter
				)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _applicabilityTableData = combineLatest([
		this.uiStateService.branchId,
		this.uiStateService.isInDiff,
		this.__applicabilityTableData,
		this.differences,
	]).pipe(
		switchMap(([branchId, mode, applic, differences]) =>
			iif(
				() =>
					mode &&
					applic.table.length !== 0 &&
					differences !== undefined &&
					differences.length !== 0,
				this.__parseDifferences(
					differences as changeInstance[],
					applic
				).pipe(
					switchMap((diffedTable) =>
						this.__parseTupleDifferences(
							differences as changeInstance[],
							branchId,
							diffedTable
						)
					)
				),
				of(applic)
			)
		),
		share(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _applicabilityTableDataCount = combineLatest([
		this.uiService.id,
		this.uiStateService.filter$,
		this.uiService.viewId,
	]).pipe(
		filter(([branchId]) => branchId !== ''),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([branchId, filter, viewId]) =>
			this.branchService
				.getApplicabilityTableCount(branchId, viewId, filter)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private __parseDifferences(
		differences: changeInstance[],
		tableData: plConfigTable
	) {
		const _tableData = structuredClone(tableData); //do not mutate the underlying observable data
		const _differences = differences.filter(
			(v) => v.ignoreType !== ignoreType.DELETED_AND_DNE_ON_DESTINATION
		);
		const _artDifferences = _differences.filter(
			(v) => v.changeType.id === changeTypeNumber.ARTIFACT_CHANGE
		);
		const _attrDifferences = _differences.filter(
			(v) => v.changeType.id === changeTypeNumber.ATTRIBUTE_CHANGE
		);
		const _relDifferences = _differences.filter(
			(v) => v.changeType.id === changeTypeNumber.RELATION_CHANGE
		);
		const _t2Differences = _differences.filter(
			(v) => v.changeType.id === changeTypeNumber.TUPLE_CHANGE
		);
		//parse artifacts
		_artDifferences.forEach((change) => {
			/**
			 * FEATURES
			 *
			 *
			 *
			 */
			if (
				change.itemTypeId === ARTIFACTTYPEIDENUM.FEATURE &&
				change.currentVersion.transactionToken.id !== '-1' &&
				change.deleted
			) {
				//mark entire row as deleted
				const featureIndex = _tableData.table.findIndex(
					(v) => v.id === change.artId
				);
				if (featureIndex !== -1) {
					_tableData.table[featureIndex].deleted = true;
				} else {
					_tableData.table.push({
						id: change.artId,
						name: 'DELETED_FEATURE',
						configurationValues: [],
						attributes: [],
						deleted: true,
					});
				}
			}
			if (
				change.itemTypeId === ARTIFACTTYPEIDENUM.FEATURE &&
				change.currentVersion.transactionToken.id !== '-1' &&
				change.currentVersion.modType === ModificationType.NEW &&
				change.currentVersion.value === null
			) {
				//TODO change this null change to "" once fix for JdbcStatement to normalize oracle's behavior with postgres
				//mark entire row as added
				const featureIndex = _tableData.table.findIndex(
					(v) => v.id === change.artId
				);
				if (featureIndex !== -1) {
					_tableData.table[featureIndex].added = true;
				}
			}
			/**
			 * END FEATURES
			 *
			 *
			 *
			 */
			/**
			 * CONFIGURATIONS
			 *
			 *
			 *
			 */
			if (
				(change.itemTypeId === ARTIFACTTYPEIDENUM.CONFIGURATION ||
					change.itemTypeId ===
						ARTIFACTTYPEIDENUM.CONFIGURATION_GROUP) &&
				change.currentVersion.transactionToken.id !== '-1' &&
				change.deleted
			) {
				//mark entire row as deleted
				const configIndex = _tableData.headers.findIndex(
					(v) => v.id === change.artId
				);
				if (configIndex !== -1) {
					_tableData.headers[configIndex].deleted = true;
				} else {
					_tableData.headers.push({
						id: change.artId,
						name: 'DELETED_CONFIG',
						deleted: true,
						gammaId: '',
						applicability: {
							id: '-1',
							name: 'DELETED_CONFIG',
							gammaId: '',
						},
						typeId: '-1',
					});
				}
			}
			if (
				(change.itemTypeId === ARTIFACTTYPEIDENUM.CONFIGURATION ||
					change.itemTypeId ===
						ARTIFACTTYPEIDENUM.CONFIGURATION_GROUP) &&
				change.currentVersion.transactionToken.id !== '-1' &&
				change.currentVersion.modType === ModificationType.NEW &&
				change.currentVersion.value === null
			) {
				//TODO change this null change to "" once fix for JdbcStatement to normalize oracle's behavior with postgres
				//mark entire row as added
				const configIndex = _tableData.headers.findIndex(
					(v) => v.id === change.artId
				);
				if (configIndex !== -1) {
					_tableData.headers[configIndex].added = true;
				}
			}
			/**
			 * END CONFIGURATIONS
			 *
			 *
			 *
			 */
		});
		//parse attributes
		_attrDifferences.forEach((change) => {
			if (_tableData.table.map((x) => x.id).includes(change.artId)) {
				const featureIndex = _tableData.table.findIndex(
					(v) => v.id === change.artId
				);
				if (featureIndex === -1) {
					return;
				}
				if (_tableData.table[featureIndex].changes === undefined) {
					_tableData.table[featureIndex].changes = {};
				}
				if (!_tableData.table[featureIndex].deleted) {
					const _changes: difference = {
						currentValue: change.currentVersion.value,
						previousValue: change.baselineVersion.value,
						transactionToken:
							change.currentVersion.transactionToken,
					};
					if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.name = _changes;
					}
					if (
						change.itemTypeId === ATTRIBUTETYPEIDENUM.DEFAULTVALUE
					) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.defaultValue =
							_changes;
					}
					if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.description =
							_changes;
					}
					if (
						change.itemTypeId === ATTRIBUTETYPEIDENUM.PRODUCT_TYPE
					) {
						if (
							_tableData.table[featureIndex].changes
								.productApplicabilities === undefined
						) {
							_tableData.table[
								featureIndex
							].changes.productApplicabilities = {
								currentValue: [],
								previousValue: [],
								transactionToken: { id: '-1', branchId: '-1' },
							};
							_tableData.table[
								featureIndex
							].changes.productApplicabilities.currentValue = [];
							_tableData.table[
								featureIndex
							].changes.productApplicabilities.previousValue = [];
							_tableData.table[
								featureIndex
							].changes.productApplicabilities.transactionToken =
								change.currentVersion.transactionToken;
						}
						_tableData.table[
							featureIndex
						].changes.productApplicabilities.currentValue.push(
							change.currentVersion.value as string
						);
					}
					if (change.itemTypeId === ATTRIBUTETYPEIDENUM.MULTIVALUED) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.multiValued =
							_changes;
					}
					if (
						change.itemTypeId === ATTRIBUTETYPEIDENUM.DEFAULTVALUE
					) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.defaultValue =
							_changes;
					}
				} else {
					const _changes: difference = {
						currentValue: change.destinationVersion.value,
						previousValue: change.baselineVersion.value,
						transactionToken:
							change.currentVersion.transactionToken,
					};
					if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
						_tableData.table[featureIndex].name =
							_changes.previousValue as string;
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.name = _changes;
					}
					if (
						change.itemTypeId === ATTRIBUTETYPEIDENUM.DEFAULTVALUE
					) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.defaultValue =
							_changes;
					}
					if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.description =
							_changes;
					}
					if (
						change.itemTypeId === ATTRIBUTETYPEIDENUM.PRODUCT_TYPE
					) {
						if (
							_tableData.table[featureIndex].changes
								.productApplicabilities === undefined
						) {
							_tableData.table[
								featureIndex
							].changes.productApplicabilities = {
								currentValue: [],
								previousValue: [],
								transactionToken: { id: '-1', branchId: '-1' },
							};
							_tableData.table[
								featureIndex
							].changes.productApplicabilities.currentValue = [];
							_tableData.table[
								featureIndex
							].changes.productApplicabilities.previousValue = [];
							_tableData.table[
								featureIndex
							].changes.productApplicabilities.transactionToken =
								change.currentVersion.transactionToken;
						}
						_tableData.table[
							featureIndex
						].changes.productApplicabilities.currentValue.push(
							change.destinationVersion.value as string
						);
					}
					if (change.itemTypeId === ATTRIBUTETYPEIDENUM.MULTIVALUED) {
						// @ts-expect-error TODO handle this better. This definitely isn't undefined per previous line(s)
						_tableData.table[featureIndex].changes.multiValued =
							_changes;
					}
				}
			}
		});
		//not parsing relations as it's hard to display in table
		//tuples will be parsed later
		return of(_tableData);
	}
	private __parseTupleDifferences(
		differences: changeInstance[],
		branchId: string,
		tableData: plConfigTable
	) {
		const _tableData = structuredClone(tableData); //do not mutate the underlying observable data
		const _differences = differences.filter(
			(v) => v.ignoreType !== ignoreType.DELETED_AND_DNE_ON_DESTINATION
		);
		const _t2Differences = _differences
			.filter((v) => v.changeType.id === changeTypeNumber.TUPLE_CHANGE)
			.sort((a, b) =>
				a.deleted && b.deleted
					? 0
					: a.deleted && !b.deleted
						? 1
						: !a.deleted && b.deleted
							? -1
							: 0
			);
		const _uniqueDifferences = [...new Set(_t2Differences)];
		const __differences = concat(from(_uniqueDifferences));
		const _appTokens = __differences.pipe(
			map((x) => {
				return {
					change: x,
					token: this.__getAppTokenFromDifference(x),
				};
			})
		);
		const _applics = _appTokens.pipe(
			concatMap(({ change, token }) =>
				combineLatest([
					//pair[0] is the feature name
					//pair[1] is the feature value
					this.__getNameValuePairFromAppToken(branchId, token[1]),
					of(change),
					of(token),
				])
			),
			map(([pair, change, token]) => {
				return {
					change: change,
					applic: {
						featureName: pair[0],
						featureValue: pair[1],
					},
					token: token,
				};
			})
		);
		return _applics.pipe(
			reduce((acc, curr) => {
				acc = this._processTupleModTypeNew(curr, acc);
				acc = this._processTupleModTypeDeleted(curr, acc);
				return acc;
			}, _tableData),
			startWith(_tableData)
		);
	}
	private _processTupleModTypeNew(
		value: {
			change: changeInstance;
			applic: { featureName: string; featureValue: string };
			token: `${number}`[];
		},
		data: plConfigTable
	) {
		const featureNames = data.table.map((x) => x.name.trim());
		const configIds = data.headers.map((x) => x.id);
		const _tableData = structuredClone(data);
		if (
			value.change.currentVersion.modType === ModificationType.NEW &&
			featureNames.includes(value.applic.featureName) &&
			configIds.includes(value.token[0])
		) {
			// mark the value in features[featureIndex].configurations(configValue index) as added
			if (
				_tableData.table.find(
					(f) =>
						f.name.trim() === value.applic.featureName.trim() &&
						f.configurationValues.find(
							(c) => c.id === value.token[0]
						) !== undefined &&
						f.configurationValues.find(
							(c) =>
								c.applicability.name.trim() ===
									value.applic.featureName +
										' = ' +
										value.applic.featureValue &&
								c.id === value.token[0]
						)
				) !== undefined
			) {
				// @ts-expect-error undefined
				_tableData.table
					.find(
						(f) =>
							f.name.trim() === value.applic.featureName.trim() &&
							f.configurationValues.find(
								(c) => c.id === value.token[0]
							) !== undefined &&
							f.configurationValues.find(
								(c) =>
									c.applicability.name.trim() ===
										value.applic.featureName +
											' = ' +
											value.applic.featureValue &&
									c.id === value.token[0]
							) !== undefined
					)
					.configurationValues.find(
						(c) =>
							c.applicability.name.trim() ===
								value.applic.featureName +
									' = ' +
									value.applic.featureValue &&
							c.id === value.token[0]
					).added = true;
				// @ts-expect-error undefined
				_tableData.table
					.find(
						(f) =>
							f.name.trim() === value.applic.featureName.trim() &&
							f.configurationValues.find(
								(c) => c.id === value.token[0]
							) !== undefined &&
							f.configurationValues.find(
								(c) =>
									c.applicability.name.trim() ===
										value.applic.featureName +
											' = ' +
											value.applic.featureValue &&
									c.id === value.token[0]
							) !== undefined
					)
					.configurationValues.find(
						(c) =>
							c.applicability.name.trim() ===
								value.applic.featureName +
									' = ' +
									value.applic.featureValue &&
							c.id === value.token[0]
					).applicability.added = true;
			}
		}
		return _tableData;
	}

	private _processTupleModTypeDeleted(
		value: {
			change: changeInstance;
			applic: { featureName: string; featureValue: string };
			token: `${number}`[];
		},
		data: plConfigTable
	) {
		//logic:
		// if feature not found add whole new row with a value for each configuration
		// if configuration not found add another configuration for the feature
		const featureNames = data.table.map((x) => x.name.trim());
		const featureIds = data.table.map((x) => x.id);
		const configIds = data.headers.map((x) => x.id);
		const _tableData = structuredClone(data);
		if (value.change.currentVersion.modType === ModificationType.DELETED) {
			if (
				featureNames.includes(value.applic.featureName) &&
				configIds.includes(value.token[0]) &&
				_tableData.table.find(
					(f) =>
						f.name.trim() === value.applic.featureName.trim() &&
						f.configurationValues !== undefined &&
						f.configurationValues.find(
							(c) => c.id === value.token[0]
						) !== undefined &&
						f.configurationValues.find(
							(c) =>
								c.applicability.name.trim() ===
									value.applic.featureName +
										' = ' +
										value.applic.featureValue &&
								c.id === value.token[0]
						)
				) !== undefined
			) {
				//just mark it as deleted
				// @ts-expect-error undefined
				_tableData.table
					.find(
						(f) =>
							f.name.trim() === value.applic.featureName.trim() &&
							f.configurationValues !== undefined &&
							f.configurationValues.find(
								(c) => c.id === value.token[0]
							) !== undefined &&
							f.configurationValues.find(
								(c) =>
									c.applicability.name.trim() ===
										value.applic.featureName +
											' = ' +
											value.applic.featureValue &&
									c.id === value.token[0]
							) !== undefined
					)
					.configurationValues.find(
						(c) =>
							c.applicability.name.trim() ===
								value.applic.featureName +
									' = ' +
									value.applic.featureValue &&
							c.id === value.token[0]
					).deleted = true;
				// @ts-expect-error undefined
				_tableData.table
					.find(
						(f) =>
							f.name.trim() === value.applic.featureName.trim() &&
							f.configurationValues !== undefined &&
							f.configurationValues.find(
								(c) => c.id === value.token[0]
							) !== undefined &&
							f.configurationValues.find(
								(c) =>
									c.applicability.name.trim() ===
										value.applic.featureName +
											' = ' +
											value.applic.featureValue &&
									c.id === value.token[0]
							) !== undefined
					)
					.configurationValues.find(
						(c) =>
							c.applicability.name.trim() ===
								value.applic.featureName +
									' = ' +
									value.applic.featureValue &&
							c.id === value.token[0]
					).applicability.deleted = true;
			} else if (featureNames.includes(value.applic.featureName)) {
				const header =
					_tableData.headers.find((x) => x.id === value.token[0])
						?.name || '';
				_tableData.table
					.find(
						(f) => f.name.trim() === value.applic.featureName.trim()
					)
					?.configurationValues.push({
						id: value.token[0],
						name: header,
						gammaId: '',
						applicability: {
							deleted: true,
							gammaId: '',
							id: value.token[1],
							name:
								value.applic.featureName +
								' = ' +
								value.applic.featureValue,
						},
						typeId: '-1',
						deleted: true,
					});
			} else if (featureIds.includes(value.token[0])) {
				const configValues: configurationValue[] =
					_tableData.headers.map((x) => {
						return {
							id: x.id,
							name: x.name,
							gammaId: '-1',
							typeId: x.typeId,
							applicability: {
								id: value.token[1],
								name:
									value.applic.featureName +
									' = ' +
									value.applic.featureValue,
								gammaId: '',
								deleted: true,
							},
							deleted: true,
						};
					});
				//covered by above check
				// @ts-expect-error undefined
				_tableData.table.find(
					(f) => f.id === value.token[0]
				).configurationValues = configValues;
				// @ts-expect-error undefined
				_tableData.table.find((f) => f.id === value.token[0]).deleted =
					true;
			} else {
				const configValues: configurationValue[] =
					_tableData.headers.map((x) => {
						return {
							id: x.id,
							name: x.name,
							gammaId: '',
							typeId: x.typeId,
							applicability: {
								id: value.token[1],
								name:
									value.applic.featureName +
									' = ' +
									value.applic.featureValue,
								gammaId: '',
								deleted: true,
							},
							deleted: true,
						};
					});
				_tableData.table.push({
					id: '-1',
					name: value.applic.featureName,
					configurationValues: configValues,
					attributes: [],
					deleted: true,
				});
			}
		}
		return _tableData;
	}

	private __getNameValuePairFromAppToken(branchId: string, appId: string) {
		return this.branchService.getApplicabilityToken(branchId, appId).pipe(
			filter((appToken) => appToken.id !== '1'),
			map((appToken) => appToken.name.split(' = '))
		);
	}
	private __getAppTokenFromDifference(change: changeInstance) {
		return typeof change.currentVersion.value === 'string' &&
			change.currentVersion.value !== null &&
			change.currentVersion.value.split('|', 2).length === 2
			? (change.currentVersion.value
					.split('|', 2)[1]
					.split(', ') as `${number}`[])
			: [];
	}
	get applicabilityTableData() {
		return this._applicabilityTableData;
	}

	get applicabilityTableDataCount() {
		return this._applicabilityTableDataCount;
	}

	updateFeatureAttributes(
		change: changeInstance,
		feature: extendedFeatureWithChanges
	) {
		if (feature.changes === undefined) {
			feature.changes = {};
		}
		const changes: difference = {
			currentValue: change.currentVersion.value,
			previousValue: change.baselineVersion.value,
			transactionToken: change.currentVersion.transactionToken,
		};
		if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
			if (
				feature.name === (change.currentVersion.value as string) &&
				!feature.deleted
			) {
				feature.changes.name = changes;
			}
			if (feature.deleted) {
				feature.name = change.currentVersion.value as string;
				feature.changes.name = {
					currentValue: change.destinationVersion.value,
					previousValue: change.baselineVersion.value,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
			if (
				feature.description ===
					(change.currentVersion.value as string) &&
				!feature.deleted
			) {
				feature.changes.description = changes;
			}
			if (feature.deleted) {
				feature.description = change.currentVersion.value as string;
				feature.changes.description = {
					currentValue: change.destinationVersion.value,
					previousValue: change.baselineVersion.value,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.PRODUCT_TYPE) {
			if (feature.productApplicabilities === undefined) {
				feature.productApplicabilities = [];
			}
			if (feature.changes.productApplicabilities === undefined) {
				feature.changes.productApplicabilities = [];
			}
			if (
				feature.productApplicabilities?.some(
					(val) => val === (change.currentVersion.value as string)
				) &&
				!feature.deleted &&
				!feature.changes.productApplicabilities?.some(
					(val) => val.currentValue === change.currentVersion.value
				) &&
				change.currentVersion.transactionToken.id !== '-1'
			) {
				feature.changes.productApplicabilities?.push(changes);
			}
			if (
				feature.deleted &&
				!feature.productApplicabilities?.some(
					(val) => val === (change.currentVersion.value as string)
				) &&
				change.currentVersion.transactionToken.id !== '-1'
			) {
				feature.productApplicabilities?.push(
					change.currentVersion.value as string
				);
				if (
					!feature.changes.productApplicabilities?.some(
						(val) =>
							val.previousValue ===
							(change.baselineVersion.value as string)
					)
				) {
					feature.changes.productApplicabilities?.push({
						currentValue: change.destinationVersion.value,
						previousValue: change.baselineVersion.value,
						transactionToken:
							change.currentVersion.transactionToken,
					});
				}
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.MULTIVALUED) {
			if (
				feature.multiValued ===
					((change.currentVersion.value as string) === 'true') &&
				!feature.deleted
			) {
				feature.changes.multiValued = {
					currentValue:
						(change.currentVersion.value as string) === 'true',
					previousValue:
						(change.baselineVersion.value as string) === 'true',
					transactionToken: change.currentVersion.transactionToken,
				};
			}
			if (feature.deleted) {
				feature.multiValued =
					(change.currentVersion.value as string) === 'true';
				feature.changes.multiValued = {
					currentValue:
						(change.destinationVersion.value as string) === 'true',
					previousValue:
						(change.baselineVersion.value as string) === 'true',
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.FEATUREVALUE) {
			if (
				feature.valueType === (change.currentVersion.value as string) &&
				!feature.deleted
			) {
				feature.changes.valueType = changes;
			}
			if (feature.deleted) {
				feature.valueType = change.currentVersion.value as string;
				feature.changes.valueType = {
					currentValue: change.destinationVersion.value,
					previousValue: change.baselineVersion.value,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DEFAULTVALUE) {
			if (
				feature.defaultValue ===
					(change.currentVersion.value as string) &&
				!feature.deleted
			) {
				feature.changes.defaultValue = changes;
			}
			if (feature.deleted) {
				feature.defaultValue = change.currentVersion.value as string;
				feature.changes.defaultValue = {
					currentValue: change.destinationVersion.value,
					previousValue: change.baselineVersion.value,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.VALUE) {
			if (feature.changes.values === undefined) {
				feature.changes.values = [];
			}
			if (
				feature.values?.some(
					(val) => val === (change.currentVersion.value as string)
				) &&
				!feature.deleted &&
				!feature.changes.values?.some(
					(val) => val.currentValue === change.currentVersion.value
				) &&
				change.currentVersion.transactionToken.id !== '-1'
			) {
				feature.changes.values?.push(changes);
			}
			if (
				feature.deleted &&
				!feature.values?.some(
					(val) => val === (change.currentVersion.value as string)
				) &&
				change.currentVersion.transactionToken.id !== '-1'
			) {
				feature.values?.push(change.currentVersion.value as string);
				if (
					!feature.changes.values?.some(
						(val) =>
							val.previousValue ===
							(change.baselineVersion.value as string)
					)
				) {
					feature.changes.values?.push({
						currentValue: change.destinationVersion.value,
						previousValue: change.baselineVersion.value,
						transactionToken:
							change.currentVersion.transactionToken,
					});
				}
			}
		}
	}
	updateGroupAttributes(change: changeInstance, group: configGroup) {
		if (group.changes === undefined) {
			group.changes = {};
		}
		const changes: difference = {
			currentValue: change.currentVersion.value,
			previousValue: change.baselineVersion.value,
			transactionToken: change.currentVersion.transactionToken,
		};
		if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
			if (
				group.name === (change.currentVersion.value as string) &&
				!group.deleted
			) {
				group.changes.name = changes as difference<string>;
			}
			if (group.deleted) {
				group.name = change.currentVersion.value as string;
				group.changes.name = {
					currentValue: change.destinationVersion.value as string,
					previousValue: change.baselineVersion.value as string,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		}
	}
	updateViewAttributes(change: changeInstance, view: viewWithChanges) {
		if (view.changes === undefined) {
			view.changes = {};
		}
		const changes: difference = {
			currentValue: change.currentVersion.value,
			previousValue: change.baselineVersion.value,
			transactionToken: change.currentVersion.transactionToken,
		};
		if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
			if (
				view.name === (change.currentVersion.value as string) &&
				!view.deleted
			) {
				view.changes.name = changes;
			}
			if (view.deleted) {
				view.name = change.currentVersion.value as string;
				view.changes.name = {
					currentValue: change.destinationVersion.value,
					previousValue: change.baselineVersion.value,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.PRODUCT_TYPE) {
			if (view.changes.productApplicabilities === undefined) {
				view.changes.productApplicabilities = [];
			}
			if (
				view.productApplicabilities?.some(
					(val) => val === (change.currentVersion.value as string)
				) &&
				!view.deleted &&
				!view.changes.productApplicabilities?.some(
					(val) => val.currentValue === change.currentVersion.value
				) &&
				change.currentVersion.transactionToken.id !== '-1'
			) {
				view.changes.productApplicabilities?.push(changes);
			}
			if (
				view.deleted &&
				!view.productApplicabilities?.some(
					(val) => val === (change.currentVersion.value as string)
				) &&
				change.currentVersion.transactionToken.id !== '-1'
			) {
				view.productApplicabilities?.push(
					change.currentVersion.value as string
				);
				if (
					!view.changes.productApplicabilities?.some(
						(val) =>
							val.currentValue ===
							(change.currentVersion.value as string)
					)
				) {
					view.changes.productApplicabilities?.push(changes);
				}
			}
		} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
			if (
				view.description === (change.currentVersion.value as string) &&
				!view.deleted
			) {
				view.changes.description = changes as difference<string>;
			}
			if (view.deleted) {
				view.description = change.currentVersion.value as string;
				view.changes.description = {
					currentValue: change.destinationVersion.value as string,
					previousValue: change.baselineVersion.value as string,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		}
		return view;
	}

	createProductType(productType: productType) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((id) => id !== '' && id !== '-1' && id !== undefined),
			switchMap((id) =>
				this.typesService.createProductType(id, productType)
			),
			switchMap((response) =>
				response.success
					? of(response).pipe(
							tap(
								(_) =>
									(this.uiStateService.updateReqConfig = true)
							)
						)
					: of(response)
			)
		);
	}

	updateProductType(productType: productType) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((id) => id !== '' && id !== '-1' && id !== undefined),
			switchMap((id) =>
				this.typesService.updateProductType(id, productType)
			),
			switchMap((response) =>
				response.success
					? of(response).pipe(
							tap(
								(_) =>
									(this.uiStateService.updateReqConfig = true)
							)
						)
					: of(response)
			)
		);
	}

	deleteProductType(productTypeId: string) {
		return this.uiStateService.branchId.pipe(
			take(1),
			filter((id) => id !== '' && id !== '-1' && id !== undefined),
			switchMap((id) =>
				this.typesService.deleteProductType(id, productTypeId)
			),
			switchMap((response) =>
				response.success
					? of(response).pipe(
							tap(
								(_) =>
									(this.uiStateService.updateReqConfig = true)
							)
						)
					: of(response)
			)
		);
	}
}
