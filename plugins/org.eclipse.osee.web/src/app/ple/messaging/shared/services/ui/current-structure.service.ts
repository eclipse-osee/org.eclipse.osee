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
import { Injectable, signal } from '@angular/core';
import {
	BehaviorSubject,
	combineLatest,
	concat,
	concatMap,
	filter,
	from,
	iif,
	map,
	Observable,
	of,
	reduce,
	repeat,
	share,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { StructuresUiService } from './structures-ui.service';
import { applic } from '@osee/shared/types/applicability';
import { transactionToken } from '@osee/shared/types/change-report';
import { StructuresService } from '../http/structures.service';
import { ElementService } from '../http/element.service';
import { TypesUIService } from './types-ui.service';
import { PreferencesUIService } from './preferences-ui.service';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { SideNavService } from '@osee/shared/services/layout';
import { EnumsService } from '../http/enums.service';
import { QueryService } from '../http/query.service';
import {
	changeInstance,
	transactionResult,
} from '@osee/shared/types/change-report';
import { MessagesService } from '../http/messages.service';
import type {
	structure,
	structureWithChanges,
	PlatformType,
	element,
	settingsDialogData,
	elementWithChanges,
	ElementDialog,
} from '@osee/messaging/shared/types';
import { relation, transaction } from '@osee/shared/types';
import type { MimQuery } from '@osee/messaging/shared/query';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export abstract class CurrentStructureService {
	private _types = this.typeService.types;
	private _expandedRows = signal<(structure | structureWithChanges)[]>([]);
	private _expandedRows$ = toObservable(this._expandedRows);
	constructor(
		protected ui: StructuresUiService,
		protected structure: StructuresService,
		protected messages: MessagesService,
		protected elements: ElementService,
		protected typeService: TypesUIService,
		protected preferenceService: PreferencesUIService,
		protected branchInfoService: CurrentBranchInfoService,
		protected sideNavService: SideNavService,
		protected enumListService: EnumsService,
		protected queryService: QueryService
	) {}

	abstract get currentPage(): Observable<number>;

	abstract set page(page: number);

	abstract returnToFirstPage(): void;

	abstract get currentPageSize(): Observable<number>;

	abstract set pageSize(page: number);

	get expandedRows() {
		return this._expandedRows$;
	}

	get message() {
		return combineLatest([
			this.BranchId,
			this.connectionId,
			this.MessageId,
			this.SubMessageId,
			this.ui.viewId,
		]).pipe(
			switchMap(([branch, connection, id, submessageId, viewId]) =>
				this.messages.getMessage(branch, connection, id, viewId).pipe(
					repeat({ delay: () => this.ui.UpdateRequired }),
					tap((value) => {
						this.BreadCrumb =
							value.name +
							' > ' +
							value.subMessages.find(
								(submessage) => submessage.id === submessageId
							)!.name;
					}),
					share()
				)
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}
	set addExpandedRow(value: structure) {
		this._expandedRows.update((rows) => [...rows, value]);
	}

	set removeExpandedRow(value: structure) {
		this._expandedRows.update((rows) =>
			rows.filter((v) => v.id !== value.id)
		);
	}

	clearRows() {
		this._expandedRows.set([]);
	}
	abstract get structures(): Observable<(structure | structureWithChanges)[]>;

	abstract get structuresCount(): Observable<number>;

	set filter(value: string) {
		this.ui.filterString = value;
		this.returnToFirstPage();
	}

	set branchId(value: string) {
		this.ui.BranchIdString = value;
	}

	get BranchId() {
		return this.ui.BranchId;
	}

	get branchType() {
		return this.ui.branchType;
	}

	set BranchType(value: 'working' | 'baseline' | '') {
		this.ui.BranchType = value;
	}
	get BranchType() {
		return this.ui.branchType.getValue();
	}

	get viewId() {
		return this.ui.viewId;
	}

	set ViewId(id: string) {
		this.ui.ViewId = id;
	}

	set messageId(value: string) {
		this.ui.messageIdString = value;
	}

	get MessageId() {
		return this.ui.messageId;
	}

	get SubMessageId() {
		return this.ui.subMessageId;
	}

	set subMessageId(value: string) {
		this.ui.subMessageIdString = value;
	}

	set connection(id: string) {
		this.ui.connectionIdString = id;
	}

	get connectionId() {
		return this.ui.connectionId;
	}

	get breadCrumbs() {
		return this.ui.subMessageBreadCrumbs;
	}

	set BreadCrumb(value: string) {
		this.ui.subMessageBreadCrumbsString = value;
	}

	set singleStructureIdValue(value: string) {
		this.ui.singleStructureIdValue = value;
	}

	get singleStructureId() {
		return this.ui.singleStructureId;
	}

	get preferences() {
		return this.preferenceService.preferences;
	}

	get BranchPrefs() {
		return this.preferenceService.BranchPrefs;
	}

	set toggleDone(value: boolean) {
		this.ui.toggleDone = value;
	}

	get done() {
		return this.ui.done;
	}

	get updated() {
		return this.ui.UpdateRequired;
	}

	get sideNavContent() {
		return this.sideNavService.rightSideNavContent;
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

	set update(value: boolean) {
		this.ui.updateMessages = value;
	}

	get availableStructures() {
		return this.BranchId.pipe(
			switchMap((id) =>
				this.structure
					.getStructures(id)
					.pipe(shareReplay({ bufferSize: 1, refCount: true }))
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	getPaginatedStructures(pageNum: string | number) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.structure.getPaginatedFilteredStructures(id, '', pageNum)
			)
		);
	}

	getPaginatedStructuresFilteredByName(
		name: string,
		count: number,
		pageNum: string | number
	) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.structure.getPaginatedStructuresFilteredByName(
					id,
					name,
					count,
					pageNum
				)
			)
		);
	}

	getStructuresFilteredByNameCount(name: string) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.structure.getStructuresFilteredByNameCount(id, name)
			)
		);
	}

	get availableElements() {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) => this.elements.getFilteredElements(id, ''))
		);
	}

	getType(typeId: string) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) => this.typeService.getType(typeId))
		);
	}

	getPaginatedElements(count: number, pageNum: string | number) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.elements.getPaginatedFilteredElements(
					id,
					'',
					count,
					pageNum.toString()
				)
			)
		);
	}

	getPaginatedElementsByName(
		name: string,
		count: number,
		pageNum: string | number
	) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.elements.getPaginatedElementsByName(
					id,
					name,
					count,
					pageNum
				)
			)
		);
	}

	getElementsByNameCount(name: string) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) => this.elements.getElementsByNameCount(id, name))
		);
	}

	get types() {
		return this._types;
	}

	getPaginatedFilteredTypes(
		filter: string,
		count: number,
		pageNum: number | string
	) {
		return this.typeService.getPaginatedFilteredTypes(
			filter,
			count,
			pageNum.toString()
		);
	}

	getFilteredTypesCount(filter: string) {
		return this.typeService.getFilteredTypesCount(filter);
	}

	get differences() {
		return this.ui.differences;
	}
	set difference(value: changeInstance[]) {
		this.ui.difference = value;
	}

	get isInDiff() {
		return this.ui.isInDiff;
	}

	set DiffMode(value: boolean) {
		this.ui.DiffMode = value;
	}

	get connectionsRoute() {
		return combineLatest([this.branchType, this.BranchId]).pipe(
			switchMap(([branchType, BranchId]) =>
				of('/ple/messaging/connections/' + branchType + '/' + BranchId)
			)
		);
	}

	updatePlatformTypeValue(type: Partial<PlatformType>) {
		return this.typeService
			.changeType(type)
			.pipe(
				switchMap((transaction) =>
					this.typeService.performMutation(transaction)
				)
			);
	}

	createStructure(body: Partial<structure>, afterStructure?: string) {
		delete body.elements;
		return combineLatest([this.BranchId, this.SubMessageId]).pipe(
			take(1),
			switchMap(([branch, submessageId]) =>
				this.structure
					.createSubMessageRelation(
						submessageId,
						undefined,
						afterStructure
					)
					.pipe(
						take(1),
						switchMap((relation) =>
							this.structure
								.createStructure(body, branch, [relation])
								.pipe(
									take(1),
									switchMap((transaction) =>
										this.structure
											.performMutation(transaction)
											.pipe(
												tap(() => {
													this.ui.updateMessages =
														true;
												})
											)
									)
								)
						)
					)
			)
		);
	}

	copyStructure(body: structure, afterStructure?: string) {
		const branchId = this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== '' && id !== '-1')
		);
		const subMsgRelation = this.SubMessageId.pipe(
			take(1),
			filter((sub) => sub !== '' && sub !== '-1'),
			switchMap((sub) =>
				this.structure.createSubMessageRelation(
					sub,
					undefined,
					afterStructure
				)
			)
		);
		const elementRelations = of(body.elements || []).pipe(
			concatMap((elements) =>
				from(elements).pipe(
					filter(
						(element) =>
							element.id !== '-1' &&
							element.id !== '' &&
							element.id !== undefined
					),
					switchMap((el) =>
						this.elements.createStructureRelation(undefined, el.id)
					)
				)
			),
			reduce((acc, curr) => [...acc, curr], [] as relation[])
		);
		const {
			elements,
			numElements,
			id,
			sizeInBytes,
			autogenerated,
			incorrectlySized,
			bytesPerSecondMaximum,
			bytesPerSecondMinimum,
			...structure
		} = body;
		const transaction = combineLatest([
			subMsgRelation,
			elementRelations,
			branchId,
		]).pipe(
			switchMap(([sub, element, branch]) =>
				this.structure.createStructure(structure, branch, [
					sub,
					...element,
				])
			)
		);
		return transaction.pipe(
			switchMap((tx) => this.structure.performMutation(tx)),
			tap((_) => (this.ui.updateMessages = true))
		);
	}

	relateStructure(structureId: string, afterStructure?: string) {
		return combineLatest([this.BranchId, this.SubMessageId]).pipe(
			take(1),
			switchMap(([branch, submessageId]) =>
				this.structure
					.createSubMessageRelation(
						submessageId,
						structureId,
						afterStructure
					)
					.pipe(
						take(1),
						switchMap((relation) =>
							this.structure.addRelation(branch, relation).pipe(
								take(1),
								switchMap((transaction) =>
									this.structure
										.performMutation(transaction)
										.pipe(
											tap(() => {
												this.ui.updateMessages = true;
											})
										)
								)
							)
						)
					)
			)
		);
	}
	partialUpdateStructure(body: Partial<structure>) {
		return this.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.structure.changeStructure(body, branchId).pipe(
					take(1),
					switchMap((transaction) =>
						this.structure.performMutation(transaction).pipe(
							tap(() => {
								this.ui.updateMessages = true;
							})
						)
					)
				)
			)
		);
	}

	partialUpdateElement(body: Partial<element>) {
		return this.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.elements.changeElement(body, branchId).pipe(
					take(1),
					switchMap((transaction) =>
						this.elements.performMutation(transaction).pipe(
							tap(() => {
								this.ui.updateMessages = true;
							})
						)
					)
				)
			)
		);
	}

	createNewElement(
		body: Partial<element>,
		structureId: string,
		typeId: string,
		afterElement?: string
	) {
		const { units, autogenerated, arrayElements, ...element } = body;
		return combineLatest([
			this.BranchId,
			this.elements.createStructureRelation(
				structureId,
				undefined,
				afterElement
			),
			this.elements.createPlatformTypeRelation(typeId),
		]).pipe(
			take(1),
			switchMap(([branchId, structureRelation, platformRelation]) =>
				of([structureRelation, platformRelation]).pipe(
					// Remove platform type relation for array headers
					map((relations) =>
						element.interfaceElementArrayHeader
							? relations.slice(0, 1)
							: relations
					),
					switchMap((relations) =>
						this.elements
							.createElement(
								element,
								branchId,
								relations,
								undefined,
								'parent-element'
							)
							.pipe(
								take(1),
								switchMap((transaction) => {
									if (
										arrayElements === undefined ||
										arrayElements.length === 0
									) {
										return of(transaction);
									}
									return of(arrayElements).pipe(
										switchMap((arrayElements) => {
											if (!arrayElements) {
												return of([]);
											}
											return of(arrayElements);
										}),
										concatMap((arrElems) =>
											from(arrElems).pipe(
												switchMap((elem) =>
													iif(
														() =>
															elem.id !== '' &&
															elem.id != '-1',
														this.elements
															.createElementArrayRelation(
																'parent-element',
																elem.id
															)
															.pipe(
																switchMap(
																	(rel) =>
																		this.elements.addRelation(
																			branchId,
																			rel,
																			transaction
																		)
																)
															),
														this.elements
															.createElementArrayRelation(
																'parent-element',
																elem.name +
																	'_' +
																	elem
																		.platformType
																		.id +
																	'_' +
																	elem.description +
																	'_' +
																	elem.notes
															)
															.pipe(
																switchMap(
																	(rel) =>
																		this.elements.addRelation(
																			branchId,
																			rel,
																			transaction
																		)
																),
																switchMap((_) =>
																	this.elements.createPlatformTypeRelation(
																		elem
																			.platformType
																			.id
																	)
																),
																switchMap(
																	(rel) =>
																		this.elements.createElement(
																			elem,
																			branchId,
																			[
																				rel,
																			],
																			transaction,
																			elem.name +
																				'_' +
																				elem
																					.platformType
																					.id +
																				'_' +
																				elem.description +
																				'_' +
																				elem.notes
																		)
																)
															)
													)
												)
											)
										),
										reduce(() => transaction)
									);
								}),
								switchMap((transaction) =>
									this.elements
										.performMutation(transaction)
										.pipe(
											tap(() => {
												this.ui.updateMessages = true;
											})
										)
								)
							)
					)
				)
			)
		);
	}

	createNewArrayElement(
		body: Partial<element>,
		headerElementId: string,
		typeId: string,
		afterElement?: string
	) {
		const { units, autogenerated, arrayElements, ...element } = body;
		return combineLatest([
			this.BranchId,
			this.elements.createElementArrayRelation(
				headerElementId,
				undefined,
				afterElement
			),
			this.elements.createPlatformTypeRelation(typeId),
		]).pipe(
			take(1),
			switchMap(([branchId, arrayRelation, platformRelation]) =>
				of([arrayRelation, platformRelation]).pipe(
					switchMap((relations) =>
						this.elements
							.createElement(
								element,
								branchId,
								relations,
								undefined,
								'parent-element'
							)
							.pipe(
								take(1),
								switchMap((transaction) =>
									this.elements
										.performMutation(transaction)
										.pipe(
											tap(() => {
												this.ui.updateMessages = true;
											})
										)
								)
							)
					)
				)
			)
		);
	}

	relateElement(
		structureId: string,
		elementId: string,
		afterElement?: string
	) {
		return this.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.elements
					.createStructureRelation(
						structureId,
						elementId,
						afterElement
					)
					.pipe(
						take(1),
						switchMap((relation) =>
							this.structure.addRelation(branchId, relation).pipe(
								take(1),
								switchMap((transaction) =>
									this.structure
										.performMutation(transaction)
										.pipe(
											tap(() => {
												this.ui.updateMessages = true;
											})
										)
								)
							)
						)
					)
			)
		);
	}

	relateArrayElement(
		headerElementId: string,
		elementId: string,
		afterElementId?: string
	) {
		return this.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.elements
					.createElementArrayRelation(
						headerElementId,
						elementId,
						afterElementId
					)

					.pipe(
						take(1),
						switchMap((relation) =>
							this.structure.addRelation(branchId, relation).pipe(
								take(1),
								switchMap((transaction) =>
									this.structure
										.performMutation(transaction)
										.pipe(
											tap(() => {
												this.ui.updateMessages = true;
											})
										)
								)
							)
						)
					)
			)
		);
	}

	changeElementRelationOrder(
		structureId: string,
		elementId: string,
		afterArtifactId: string
	) {
		return this.ui.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.elements
					.createStructureRelation(structureId, elementId)
					.pipe(
						switchMap((relation) =>
							this.elements
								.deleteRelation(branchId, relation)
								.pipe(
									switchMap((transaction) =>
										this.elements
											.createStructureRelation(
												structureId,
												elementId,
												afterArtifactId
											)
											.pipe(
												switchMap((createRelation) =>
													this.elements
														.addRelation(
															branchId,
															createRelation,
															transaction
														)
														.pipe(
															switchMap(
																(transaction) =>
																	this.elements
																		.performMutation(
																			transaction
																		)
																		.pipe(
																			tap(
																				() => {
																					this.ui.updateMessages =
																						true;
																				}
																			)
																		)
															)
														)
												)
											)
									)
								)
						)
					)
			)
		);
	}

	changeElementArrayRelationOrder(
		arrayHeaderId: string,
		elementId: string,
		afterArtifactId: string
	) {
		return this.ui.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.elements
					.createElementArrayRelation(arrayHeaderId, elementId)
					.pipe(
						switchMap((relation) =>
							this.elements
								.deleteRelation(branchId, relation)
								.pipe(
									switchMap((transaction) =>
										this.elements
											.createElementArrayRelation(
												arrayHeaderId,
												elementId,
												afterArtifactId
											)
											.pipe(
												switchMap((createRelation) =>
													this.elements
														.addRelation(
															branchId,
															createRelation,
															transaction
														)
														.pipe(
															switchMap(
																(transaction) =>
																	this.elements
																		.performMutation(
																			transaction
																		)
																		.pipe(
																			tap(
																				() => {
																					this.ui.updateMessages =
																						true;
																				}
																			)
																		)
															)
														)
												)
											)
									)
								)
						)
					)
			)
		);
	}

	changeElementFromDialog(dialog: ElementDialog) {
		const relatePlatformTypeTx = this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				combineLatest([
					this.elements.createPlatformTypeRelation(
						'' + (dialog.element?.platformType?.id || -1),
						dialog.element.id
					),
					this.elements.createPlatformTypeRelation(
						dialog.type.id || '',
						dialog.element.id
					),
				]).pipe(
					take(1),
					switchMap(([deleteRelation, addRelation]) =>
						this.elements.deleteRelation(id, deleteRelation).pipe(
							//create delete transaction
							take(1),
							switchMap((deleteTransaction) =>
								this.elements
									.addRelation(
										id,
										addRelation,
										deleteTransaction
									)
									.pipe(
										//create add transaction and merge with delete transaction
										take(1),
										switchMap((addTransaction) =>
											this.elements
												.changeElement(
													{
														id: dialog.element.id,
														enumLiteral:
															dialog.type.enumSet
																?.description,
													},
													id,
													addTransaction
												)
												.pipe(take(1))
										)
									)
							)
						)
					)
				)
			)
		);

		const tx = iif(
			() =>
				dialog.element.platformType !== undefined &&
				dialog.element.platformType.id !== dialog.type.id,
			this.BranchId.pipe(
				take(1),
				switchMap((id) =>
					relatePlatformTypeTx.pipe(
						take(1),
						switchMap((tx) =>
							this.elements.changeElement(
								this._removeExtraPropsFromElement(
									dialog.element,
									true
								),
								id,
								tx
							)
						)
					)
				)
			),
			this.BranchId.pipe(
				take(1),
				switchMap((id) =>
					this.elements.changeElement(
						this._removeExtraPropsFromElement(dialog.element),
						id
					)
				)
			)
		);

		return tx.pipe(
			switchMap((tx) =>
				this.elements
					.performMutation(tx)
					.pipe(tap(() => (this.ui.updateMessages = true)))
			)
		);
	}

	private _removeExtraPropsFromElement(
		element: Partial<element>,
		deleteEnumLiteral?: boolean
	) {
		delete element.platformType;
		delete element.arrayElements;
		if (deleteEnumLiteral) {
			delete element.enumLiteral;
		}
		return element;
	}
	changeElementPlatformType(
		structureId: string,
		elementId: string,
		type: PlatformType
	) {
		//need to modify to change element's enumLiteral attribute
		return combineLatest([
			this.BranchId,
			this.connectionId,
			this.MessageId,
			this.SubMessageId,
		]).pipe(
			take(1),
			switchMap(([branchId, connection, message, submessage]) =>
				this.elements
					.getElement(
						branchId,
						message,
						submessage,
						structureId,
						elementId,
						connection
					)
					.pipe(
						take(1),
						switchMap((element) =>
							combineLatest([
								this.elements.createPlatformTypeRelation(
									'' + (element.platformType.id || -1),
									elementId
								),
								this.elements.createPlatformTypeRelation(
									type.id || '',
									elementId
								),
							]).pipe(
								//create relations for delete/add ops
								take(1),
								switchMap(([deleteRelation, addRelation]) =>
									this.elements
										.deleteRelation(
											branchId,
											deleteRelation
										)
										.pipe(
											//create delete transaction
											take(1),
											switchMap((deleteTransaction) =>
												this.elements
													.addRelation(
														branchId,
														addRelation,
														deleteTransaction
													)
													.pipe(
														//create add transaction and merge with delete transaction
														take(1),
														switchMap(
															(addTransaction) =>
																this.elements
																	.changeElement(
																		{
																			id: elementId,
																			enumLiteral:
																				type
																					.enumSet
																					?.description,
																		},
																		branchId,
																		addTransaction
																	)
																	.pipe(
																		take(1),
																		switchMap(
																			(
																				transaction
																			) =>
																				this.elements
																					.performMutation(
																						transaction
																					)
																					.pipe(
																						tap(
																							() => {
																								this.ui.updateMessages =
																									true;
																							}
																						)
																					)
																		)
																	)
														)
													)
											)
										)
								)
							)
						)
					)
			)
		);
	}

	updatePreferences(preferences: settingsDialogData) {
		return concat(
			this._deleteColumnPrefs(),
			this._deleteBranchPrefs(),
			this._setBranchPrefs(preferences.editable),
			this._setColumnPrefs(preferences)
		).pipe(
			take(4),
			reduce((acc, curr) => [...acc, curr], [] as transactionResult[]),
			tap(() => {
				this.ui.updateMessages = true;
			})
		);
	}

	private _deleteBranchPrefs() {
		return this.preferences.pipe(
			take(1),
			switchMap((prefs) =>
				iif(
					() => prefs.hasBranchPref,
					of<transaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								deleteAttributes: [
									{ typeName: 'MIM Branch Preferences' },
								],
							},
						],
					}),
					of(undefined)
				)
			),
			switchMap((transaction) =>
				iif(
					() => transaction !== undefined,
					this.structure.performMutation(transaction!),
					of()
				)
			)
		);
	}

	private _deleteColumnPrefs() {
		return this.preferences.pipe(
			take(1),
			switchMap((prefs) =>
				iif(
					() => prefs.columnPreferences.length !== 0,
					of<transaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								deleteAttributes: [
									{ typeName: 'MIM Column Preferences' },
								],
							},
						],
					}),
					of(undefined)
				)
			),
			switchMap((transaction) =>
				iif(
					() => transaction !== undefined,
					this.structure.performMutation(transaction!),
					of()
				)
			)
		);
	}

	private _setBranchPrefs(editMode: boolean) {
		return combineLatest([
			this.preferences,
			this.BranchId,
			this.BranchPrefs,
		]).pipe(
			take(1),
			switchMap(([prefs, branch, branchPrefs]) =>
				iif(
					() => prefs.hasBranchPref,
					of<transaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								addAttributes: [
									{
										typeName: 'MIM Branch Preferences',
										value: [
											...branchPrefs,
											`${branch}:${editMode}`,
										],
									},
								],
							},
						],
					}),
					of<transaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								addAttributes: [
									{
										typeName: 'MIM Branch Preferences',
										value: `${branch}:${editMode}`,
									},
								],
							},
						],
					})
				)
			),
			switchMap((transaction) =>
				this.structure.performMutation(transaction)
			)
		);
	}

	private _setColumnPrefs(newPreferences: settingsDialogData) {
		return combineLatest([
			this.preferences,
			this.createColumnPreferences(newPreferences),
		]).pipe(
			take(1),
			switchMap(([prefs, [columns, allColumns]]) =>
				of<transaction>({
					branch: '570',
					txComment: 'Updating MIM User Preferences',
					modifyArtifacts: [
						{
							id: prefs.id,
							addAttributes: [
								{
									typeName: 'MIM Column Preferences',
									value: allColumns,
								},
							],
						},
					],
				})
			),
			switchMap((transaction) =>
				this.structure.performMutation(transaction)
			)
		);
	}
	private createColumnPreferences(preferences: settingsDialogData) {
		let columnPrefs: string[] = [];
		let allColumns: string[] = [];
		let temp = preferences.allHeaders1.concat(preferences.allHeaders2);
		let allHeaders = temp.filter((item, pos) => temp.indexOf(item) === pos);
		preferences.allowedHeaders1
			.concat(preferences.allowedHeaders2)
			.forEach((header) => {
				if (
					allHeaders.includes(header) &&
					!(
						allColumns.includes(`${header}:true`) ||
						allColumns.includes(`${header}:false`)
					)
				) {
					allColumns.push(`${header}:true`);
					columnPrefs.push(`${header}:true`);
				} else if (
					!(
						allColumns.includes(`${header}:true`) ||
						allColumns.includes(`${header}:false`)
					)
				) {
					allColumns.push(`${header}:false`);
				}
			});
		return of([columnPrefs, allColumns]);
	}

	removeStructureFromSubmessage(structureId: string, submessageId: string) {
		return this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== undefined && id !== '' && id !== '-1'),
			switchMap((branchId) =>
				this.structure
					.deleteSubmessageRelation(
						branchId,
						submessageId,
						structureId
					)
					.pipe(
						switchMap((transaction) =>
							this.structure.performMutation(transaction).pipe(
								tap(() => {
									this.ui.updateMessages = true;
								})
							)
						)
					)
			)
		);
	}
	removeElementFromStructure(element: element, structure: structure) {
		return this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== undefined && id !== '' && id !== '-1'),
			switchMap((branchId) =>
				this.elements
					.createStructureRelation(structure.id, element.id)
					.pipe(
						switchMap((relation) =>
							this.elements
								.deleteRelation(branchId, relation)
								.pipe(
									switchMap((transaction) =>
										this.elements
											.performMutation(transaction)
											.pipe(
												tap(() => {
													this.ui.updateMessages =
														true;
												})
											)
									)
								)
						)
					)
			)
		);
	}

	removeElementFromArray(element: element, headerElement: element) {
		return this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== undefined && id !== '' && id !== '-1'),
			switchMap((branchId) =>
				this.elements
					.createElementArrayRelation(headerElement.id, element.id)
					.pipe(
						switchMap((relation) =>
							this.elements
								.deleteRelation(branchId, relation)
								.pipe(
									switchMap((transaction) =>
										this.elements
											.performMutation(transaction)
											.pipe(
												tap(() => {
													this.ui.updateMessages =
														true;
												})
											)
									)
								)
						)
					)
			)
		);
	}

	deleteElement(element: element) {
		return this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== undefined && id !== '' && id !== '-1'),
			switchMap((branchId) =>
				this.elements.deleteElement(branchId, element.id).pipe(
					switchMap((transaction) =>
						this.elements.performMutation(transaction).pipe(
							tap(() => {
								this.ui.updateMessages = true;
							})
						)
					)
				)
			)
		);
	}

	deleteStructure(structureId: string) {
		return this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== undefined && id !== '' && id !== '-1'),
			switchMap((branchId) =>
				this.structure.deleteStructure(branchId, structureId).pipe(
					switchMap((transaction) =>
						this.structure.performMutation(transaction).pipe(
							tap(() => {
								this.ui.updateMessages = true;
							})
						)
					)
				)
			)
		);
	}

	protected _elementChangeSetup(
		element: element | elementWithChanges
	): elementWithChanges {
		if ((element as elementWithChanges).changes === undefined) {
			(element as elementWithChanges).changes = {};
		}
		return element as elementWithChanges;
	}
	protected _structureChangeSetup(
		structure: structure | structureWithChanges
	): structureWithChanges {
		if ((structure as structureWithChanges).changes === undefined) {
			(structure as structureWithChanges).changes = {};
		}
		return structure as structureWithChanges;
	}
	query<T = unknown>(query: MimQuery<T>) {
		return this.BranchId.pipe(
			switchMap((id) =>
				this.queryService
					.query<T>(id, query)
					.pipe(shareReplay({ bufferSize: 1, refCount: true }))
			)
		);
	}
}
