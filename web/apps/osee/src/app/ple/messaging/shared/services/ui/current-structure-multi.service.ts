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
import { Injectable } from '@angular/core';
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import type {
	element,
	structure,
	structureWithChanges,
} from '@osee/messaging/shared/types';
import {
	ModificationType,
	changeInstance,
	changeTypeNumber,
	ignoreType,
} from '@osee/shared/types/change-report';
import {
	RELATIONTYPEID,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	BehaviorSubject,
	Observable,
	OperatorFunction,
	combineLatest,
	concatMap,
	debounceTime,
	distinctUntilChanged,
	filter,
	from,
	iif,
	map,
	of,
	reduce,
	repeat,
	share,
	shareReplay,
	switchMap,
	take,
} from 'rxjs';
import { CurrentStructureService } from './current-structure.service';
import { toObservable } from '@angular/core/rxjs-interop';

/**
 * Note these type guards are pretty specific to multi service, however they might be useful to single service...TBD
 */
const _rels = [
	RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT,
	RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT,
	RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE,
] as const;

type StructureRels = (typeof _rels)[number];
function _isStructureRel(rel2: RELATIONTYPEID): rel2 is StructureRels {
	return !!_rels.find((rel) => rel2 === rel);
}

@Injectable({
	providedIn: 'root',
})
export class CurrentStructureMultiService extends CurrentStructureService {
	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(25);
	private _filter = toObservable(this.ui.filter);
	private _structuresNoDiff = combineLatest([
		this._filter,
		this.ui.BranchId,
		this.ui.messageId,
		this._submessageId,
		this.connectionId,
		this.ui.viewId,
		this.currentPage,
		this.currentPageSize,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		filter(
			([
				_filter,
				branchId,
				messageId,
				subMessageId,
				connectionId,
				_viewId,
				_page,
				_pageSize,
			]) =>
				branchId !== '' &&
				messageId !== '' &&
				subMessageId !== '-1' &&
				connectionId !== '-1'
		),
		switchMap(
			([
				filter,
				branchId,
				messageId,
				subMessageId,
				connectionId,
				viewId,
				page,
				pageSize,
			]) =>
				this.structure
					.getFilteredStructures(
						filter,
						branchId,
						messageId,
						subMessageId,
						connectionId,
						viewId,
						page + 1,
						pageSize
					)
					.pipe(
						repeat({ delay: () => this.ui.UpdateRequired }),
						share()
					)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _structures = combineLatest([
		this.ui.isInDiff,
		this.differences,
		this._structuresNoDiff,
		combineLatest([
			this.BranchId,
			this.branchInfoService.parentBranch,
			this.MessageId,
			this._submessageId,
			this.connectionId,
			this.ui.viewId,
		]),
	]).pipe(
		switchMap(
			([
				isInDiff,
				differences,
				structures,
				[
					branchId,
					parentBranch,
					messageId,
					subMessageId,
					connectionId,
					viewId,
				],
			]) =>
				isInDiff && differences !== undefined && differences.length > 0
					? this._parseDifferences(
							differences,
							structures,
							parentBranch,
							branchId,
							messageId,
							subMessageId,
							connectionId,
							viewId
						)
					: of(structures)
		)
	);
	private _structuresCount = combineLatest([
		this._filter,
		this.ui.BranchId,
		this.ui.messageId,
		this._submessageId,
		this.connectionId,
		this.ui.viewId,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		filter(
			([
				_filter,
				branchId,
				messageId,
				subMessageId,
				connectionId,
				_viewId,
			]) =>
				branchId !== '' &&
				messageId !== '' &&
				subMessageId !== '-1' &&
				connectionId !== '-1'
		),
		switchMap(
			([
				filter,
				branchId,
				messageId,
				subMessageId,
				connectionId,
				viewId,
			]) =>
				this.structure
					.getFilteredStructuresCount(
						filter,
						branchId,
						messageId,
						subMessageId,
						connectionId,
						viewId
					)
					.pipe(
						repeat({ delay: () => this.ui.UpdateRequired }),
						share()
					)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get currentPage() {
		return this._currentPage$;
	}

	set page(page: number) {
		this._currentPage$.next(page);
	}

	get currentPageSize(): Observable<number> {
		return this._currentPageSize$;
	}
	set pageSize(page: number) {
		this._currentPageSize$.next(page);
	}
	returnToFirstPage() {
		this.page = 0;
	}

	get structuresCount() {
		return this._structuresCount;
	}

	get structures() {
		return this._structures;
	}

	private _parseDifferences(
		differences: changeInstance[] | undefined,
		_oldStructures: structure[],
		parentBranch: string,
		branchId: string,
		messageId: string,
		subMessageId: string,
		connectionId: string,
		viewId: string
	) {
		let structures = JSON.parse(
			JSON.stringify(_oldStructures)
		) as Required<structure>[];
		if (differences !== undefined && structures !== undefined) {
			return of(differences).pipe(
				filter((val) => val !== undefined) as OperatorFunction<
					changeInstance[] | undefined,
					changeInstance[]
				>,
				switchMap((differenceArray) =>
					of(differenceArray).pipe(
						map((differenceArray) =>
							differenceArray.sort((a, b) => {
								if (
									a.changeType.id ===
										changeTypeNumber.RELATION_CHANGE &&
									typeof a.itemTypeId === 'object' &&
									'id' in a.itemTypeId &&
									b.changeType.id ===
										changeTypeNumber.RELATION_CHANGE &&
									typeof b.itemTypeId === 'object' &&
									'id' in b.itemTypeId &&
									_isStructureRel(a.itemTypeId.id) &&
									_isStructureRel(b.itemTypeId.id)
								) {
									const relFactor =
										[
											RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT,
											RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT,
											RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE,
										].indexOf(a.itemTypeId.id) -
										[
											RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT,
											RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT,
											RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE,
										].indexOf(b.itemTypeId.id) -
										((a.itemTypeId.id ===
											RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT &&
											b.itemTypeId.id ===
												RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT) ||
										(a.itemTypeId.id ===
											RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT &&
											b.itemTypeId.id ===
												RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT)
											? [
													ModificationType.NEW,
													ModificationType.DELETED,
												].indexOf(
													a.currentVersion.modType
												) -
												[
													ModificationType.NEW,
													ModificationType.DELETED,
												].indexOf(
													b.currentVersion.modType
												)
											: 0) -
										(Number(a.artIdB) - Number(b.artIdB));
									return (
										['111', '222', '333', '444'].indexOf(
											a.changeType.id
										) -
										['111', '222', '333', '444'].indexOf(
											b.changeType.id
										) -
										relFactor
									);
								}
								return (
									['111', '222', '333', '444'].indexOf(
										a.changeType.id
									) -
									['111', '222', '333', '444'].indexOf(
										b.changeType.id
									)
								);
							})
						),
						concatMap((differences) =>
							from(differences).pipe(
								filter(
									(val) =>
										val.ignoreType !==
										ignoreType.DELETED_AND_DNE_ON_DESTINATION
								),
								filter(
									(val) =>
										val.changeType.id !==
										changeTypeNumber.TUPLE_CHANGE
								),
								filter(
									(val) =>
										structures
											.map((a) => a.id)
											.includes(
												val.artId as `${number}`
											) ||
										structures
											.map((a) => a.id)
											.includes(
												val.artIdB as `${number}`
											) ||
										!(
											(
												structures
													.map((a) => a.elements)
													.flat() as (
													| element
													| undefined
												)[]
											).includes(undefined) &&
											(structures
												.map((a) =>
													a.elements?.map((b) => b.id)
												)
												.flat()
												.includes(
													val.artId as `${number}`
												) ||
												structures
													.map((a) =>
														a.elements?.map(
															(b) => b.id
														)
													)
													.flat()
													.includes(
														val.artIdB as `${number}`
													))
										) ||
										val.artId === subMessageId
								),
								concatMap((change) =>
									iif(
										() =>
											change.changeType.id ===
											changeTypeNumber.ARTIFACT_CHANGE,
										iif(
											() =>
												change.currentVersion
													.modType ===
													ModificationType.NEW &&
												change.baselineVersion
													.modType ===
													ModificationType.NONE &&
												change.destinationVersion
													.modType ===
													ModificationType.NONE &&
												!change.deleted,
											iif(
												() =>
													structures
														.map((a) => a.id)
														.includes(
															change.artId as `${number}`
														),
												of(structures).pipe(
													take(1),
													concatMap((structures) =>
														from(structures).pipe(
															switchMap(
																(structure) =>
																	iif(
																		() =>
																			change.artId ===
																			structure.id,
																		of(
																			structure
																		).pipe(
																			map(
																				(
																					_
																				) => {
																					structure =
																						this._structureChangeSetup(
																							structure
																						);
																					structure.applicability =
																						change
																							.currentVersion
																							.applicabilityToken as applic;
																					(
																						structure as structureWithChanges
																					).changes.applicability =
																						{
																							previousValue:
																								change
																									.baselineVersion
																									.applicabilityToken as applic,

																							currentValue:
																								change
																									.currentVersion
																									.applicabilityToken as applic,
																							transactionToken:
																								change
																									.currentVersion
																									.transactionToken,
																						};
																					(
																						structure as structureWithChanges
																					).added =
																						true;
																					return structure as structureWithChanges;
																				}
																			)
																		),
																		of(
																			structure
																		)
																	)
															)
														)
													),
													reduce(
														(acc, curr) => [
															...acc,
															curr,
														],
														[] as (
															| structure
															| structureWithChanges
														)[]
													)
												),
												iif(
													() =>
														structures
															.map((a) =>
																a.elements?.map(
																	(b) => b.id
																)
															)
															.flat()
															.includes(
																change.artId as `${number}`
															),
													of(structures).pipe(
														take(1),
														concatMap(
															(structures) =>
																from(
																	structures
																).pipe(
																	switchMap(
																		(
																			structure
																		) =>
																			iif(
																				() =>
																					structure.elements
																						?.map(
																							(
																								a
																							) =>
																								a.id
																						)
																						.includes(
																							change.artId as `${number}`
																						),
																				of(
																					structure
																				).pipe(
																					map(
																						(
																							_
																						) => {
																							const index =
																								structure.elements?.findIndex(
																									(
																										el
																									) =>
																										el.id ===
																										change.artId
																								);
																							let element =
																								structure
																									.elements[
																									index
																								];
																							element =
																								this._elementChangeSetup(
																									element
																								);
																							if (
																								this._elementIsDiffed(
																									element
																								)
																							) {
																								element.changes.applicability =
																									{
																										previousValue:
																											change
																												.baselineVersion
																												.applicabilityToken as applic,
																										currentValue:
																											change
																												.currentVersion
																												.applicabilityToken as applic,
																										transactionToken:
																											change
																												.currentVersion
																												.transactionToken,
																									};
																							}
																							structure.elements[
																								index
																							] =
																								element;
																							(
																								structure as structureWithChanges
																							).hasElementChanges =
																								true;
																							return structure as structureWithChanges;
																						}
																					)
																				),
																				of(
																					structure
																				)
																			)
																	)
																)
														),
														reduce(
															(acc, curr) => [
																...acc,
																curr,
															],
															[] as (
																| structure
																| structureWithChanges
															)[]
														)
													),
													of(change)
												)
											),
											iif(
												() =>
													change.currentVersion
														.modType ===
														ModificationType.NEW &&
													change.baselineVersion
														.modType ===
														ModificationType.NEW,
												iif(
													() => !change.deleted,
													iif(
														() =>
															structures
																.map((a) =>
																	a.elements?.map(
																		(b) =>
																			b.id
																	)
																)
																.flat()
																.includes(
																	change.artId as `${number}`
																),
														of(structures).pipe(
															take(1),
															concatMap(
																(structures) =>
																	from(
																		structures
																	).pipe(
																		switchMap(
																			(
																				structure
																			) =>
																				iif(
																					() =>
																						structure.elements
																							?.map(
																								(
																									a
																								) =>
																									a.id
																							)
																							.includes(
																								change.artId as `${number}`
																							),
																					of(
																						structure
																					).pipe(
																						map(
																							(
																								_
																							) => {
																								const index =
																									structure.elements?.findIndex(
																										(
																											el
																										) =>
																											el.id ===
																											change.artId
																									);
																								let element =
																									structure
																										.elements[
																										index
																									];

																								element =
																									this._elementChangeSetup(
																										structure
																											.elements[
																											index
																										]
																									);
																								if (
																									this._elementIsDiffed(
																										element
																									)
																								) {
																									element.changes.applicability =
																										{
																											previousValue:
																												change
																													.baselineVersion
																													.applicabilityToken as applic,
																											currentValue:
																												change
																													.currentVersion
																													.applicabilityToken as applic,
																											transactionToken:
																												change
																													.currentVersion
																													.transactionToken,
																										};
																								}
																								structure.elements[
																									index
																								] =
																									element;
																								structure.hasElementChanges =
																									true;
																								return structure as structureWithChanges;
																							}
																						)
																					),
																					of(
																						structure
																					)
																				)
																		)
																	)
															),
															reduce(
																(acc, curr) => [
																	...acc,
																	curr,
																],
																[] as (
																	| structure
																	| structureWithChanges
																)[]
															)
														),
														iif(
															() =>
																structures
																	.map(
																		(a) =>
																			a.id
																	)
																	.includes(
																		change.artId as `${number}`
																	),
															of(structures).pipe(
																take(1),
																concatMap(
																	(
																		structures
																	) =>
																		from(
																			structures
																		).pipe(
																			switchMap(
																				(
																					structure
																				) =>
																					iif(
																						() =>
																							change.artId ===
																							structure.id,
																						of(
																							structure
																						).pipe(
																							map(
																								(
																									_
																								) => {
																									structure =
																										this._structureChangeSetup(
																											structure
																										);
																									(
																										structure as structureWithChanges
																									).changes.applicability =
																										{
																											previousValue:
																												change
																													.baselineVersion
																													.applicabilityToken as applic,

																											currentValue:
																												change
																													.currentVersion
																													.applicabilityToken as applic,
																											transactionToken:
																												change
																													.currentVersion
																													.transactionToken,
																										};
																									return structure as structureWithChanges;
																								}
																							)
																						),
																						of(
																							structure
																						)
																					)
																			)
																		)
																),
																reduce(
																	(
																		acc,
																		curr
																	) => [
																		...acc,
																		curr,
																	],
																	[] as (
																		| structure
																		| structureWithChanges
																	)[]
																)
															),
															of(change)
														)
													),
													iif(
														() =>
															structures
																.map(
																	(a) => a.id
																)
																.includes(
																	change.artId as `${number}`
																),
														of(structures).pipe(
															take(1),
															concatMap(
																(structures) =>
																	from(
																		structures
																	).pipe(
																		switchMap(
																			(
																				structure
																			) =>
																				iif(
																					() =>
																						change.artId ===
																						structure.id,
																					of(
																						structure
																					).pipe(
																						map(
																							(
																								_
																							) => {
																								structure =
																									this._structureChangeSetup(
																										structure
																									);
																								(
																									structure as structureWithChanges
																								).changes.applicability =
																									{
																										previousValue:
																											change
																												.baselineVersion
																												.applicabilityToken as applic,

																										currentValue:
																											change
																												.currentVersion
																												.applicabilityToken as applic,
																										transactionToken:
																											change
																												.currentVersion
																												.transactionToken,
																									};
																								return structure as structureWithChanges;
																							}
																						)
																					),
																					of(
																						structure
																					)
																				)
																		)
																	)
															),
															reduce(
																(acc, curr) => [
																	...acc,
																	curr,
																],
																[] as (
																	| structure
																	| structureWithChanges
																)[]
															)
														),
														of(change)
													)
												),
												of(change)
											)
										),
										iif(
											() =>
												change.changeType.id ===
												changeTypeNumber.ATTRIBUTE_CHANGE,
											iif(
												() =>
													structures
														.map((a) => a.id)
														.includes(
															change.artId as `${number}`
														),
												of(structures).pipe(
													take(1),
													concatMap((structures) =>
														from(structures).pipe(
															switchMap(
																(structure) =>
																	iif(
																		() =>
																			change.artId ===
																			structure.id,
																		iif(
																			() =>
																				change.itemTypeId ===
																				ATTRIBUTETYPEIDENUM.DESCRIPTION,
																			of(
																				structure
																			).pipe(
																				map(
																					(
																						structure
																					) => {
																						structure =
																							this._structureChangeSetup(
																								structure
																							);
																						(
																							structure as structureWithChanges
																						).changes.description =
																							{
																								previousValue:
																									{
																										id: structure
																											.description
																											.id,
																										typeId: structure
																											.description
																											.typeId,
																										gammaId:
																											change
																												.baselineVersion
																												.gammaId as `${number}`,
																										value: change
																											.baselineVersion
																											.value as string,
																									},
																								currentValue:
																									{
																										id: structure
																											.description
																											.id,
																										typeId: structure
																											.description
																											.typeId,
																										gammaId:
																											change
																												.baselineVersion
																												.gammaId as `${number}`,
																										value: change
																											.currentVersion
																											.value as string,
																									},
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							};
																						return structure as structureWithChanges;
																					}
																				)
																			),
																			iif(
																				() =>
																					change.itemTypeId ===
																					ATTRIBUTETYPEIDENUM.NAME,
																				of(
																					structure
																				).pipe(
																					map(
																						(
																							structure
																						) => {
																							structure =
																								this._structureChangeSetup(
																									structure
																								);
																							(
																								structure as structureWithChanges
																							).changes.name =
																								{
																									previousValue:
																										{
																											id: structure
																												.name
																												.id,
																											typeId: structure
																												.name
																												.typeId,
																											gammaId:
																												change
																													.baselineVersion
																													.gammaId as `${number}`,
																											value: change
																												.baselineVersion
																												.value as string,
																										},

																									currentValue:
																										{
																											id: structure
																												.name
																												.id,
																											typeId: structure
																												.name
																												.typeId,
																											gammaId:
																												change
																													.baselineVersion
																													.gammaId as `${number}`,
																											value: change
																												.currentVersion
																												.value as string,
																										},
																									transactionToken:
																										change
																											.currentVersion
																											.transactionToken,
																								};
																							return structure as structureWithChanges;
																						}
																					)
																				),
																				iif(
																					() =>
																						change.itemTypeId ===
																						ATTRIBUTETYPEIDENUM.INTERFACEMAXSIMULTANEITY,
																					of(
																						structure
																					).pipe(
																						map(
																							(
																								structure
																							) => {
																								structure =
																									this._structureChangeSetup(
																										structure
																									);
																								(
																									structure as structureWithChanges
																								).changes.interfaceMaxSimultaneity =
																									{
																										previousValue:
																											{
																												id: structure
																													.interfaceMaxSimultaneity
																													.id,
																												typeId: structure
																													.interfaceMaxSimultaneity
																													.typeId,
																												gammaId:
																													change
																														.baselineVersion
																														.gammaId as `${number}`,
																												value: change
																													.baselineVersion
																													.value as string,
																											},

																										currentValue:
																											{
																												id: structure
																													.interfaceMaxSimultaneity
																													.id,
																												typeId: structure
																													.interfaceMaxSimultaneity
																													.typeId,
																												gammaId:
																													change
																														.baselineVersion
																														.gammaId as `${number}`,
																												value: change
																													.currentVersion
																													.value as string,
																											},
																										transactionToken:
																											change
																												.currentVersion
																												.transactionToken,
																									};
																								return structure as structureWithChanges;
																							}
																						)
																					),
																					iif(
																						() =>
																							change.itemTypeId ===
																							ATTRIBUTETYPEIDENUM.INTERFACEMINSIMULTANEITY,
																						of(
																							structure
																						).pipe(
																							map(
																								(
																									structure
																								) => {
																									structure =
																										this._structureChangeSetup(
																											structure
																										);
																									(
																										structure as structureWithChanges
																									).changes.interfaceMinSimultaneity =
																										{
																											previousValue:
																												{
																													id: structure
																														.interfaceMinSimultaneity
																														.id,
																													typeId: structure
																														.interfaceMinSimultaneity
																														.typeId,
																													gammaId:
																														change
																															.baselineVersion
																															.gammaId as `${number}`,
																													value: change
																														.baselineVersion
																														.value as string,
																												},

																											currentValue:
																												{
																													id: structure
																														.interfaceMinSimultaneity
																														.id,
																													typeId: structure
																														.interfaceMinSimultaneity
																														.typeId,
																													gammaId:
																														change
																															.baselineVersion
																															.gammaId as `${number}`,
																													value: change
																														.currentVersion
																														.value as string,
																												},
																											transactionToken:
																												change
																													.currentVersion
																													.transactionToken,
																										};
																									return structure as structureWithChanges;
																								}
																							)
																						),
																						iif(
																							() =>
																								change.itemTypeId ===
																								ATTRIBUTETYPEIDENUM.INTERFACESTRUCTURECATEGORY,
																							of(
																								structure
																							).pipe(
																								map(
																									(
																										structure
																									) => {
																										structure =
																											this._structureChangeSetup(
																												structure
																											);
																										(
																											structure as structureWithChanges
																										).changes.interfaceStructureCategory =
																											{
																												previousValue:
																													{
																														id: structure
																															.interfaceStructureCategory
																															.id,
																														typeId: structure
																															.interfaceStructureCategory
																															.typeId,
																														gammaId:
																															change
																																.baselineVersion
																																.gammaId as `${number}`,
																														value: change
																															.baselineVersion
																															.value as string,
																													},

																												currentValue:
																													{
																														id: structure
																															.interfaceStructureCategory
																															.id,
																														typeId: structure
																															.interfaceStructureCategory
																															.typeId,
																														gammaId:
																															change
																																.baselineVersion
																																.gammaId as `${number}`,
																														value: change
																															.currentVersion
																															.value as string,
																													},
																												transactionToken:
																													change
																														.currentVersion
																														.transactionToken,
																											};
																										return structure as structureWithChanges;
																									}
																								)
																							),
																							iif(
																								() =>
																									change.itemTypeId ===
																									ATTRIBUTETYPEIDENUM.INTERFACETASKFILETYPE,
																								of(
																									structure
																								).pipe(
																									map(
																										(
																											structure
																										) => {
																											structure =
																												this._structureChangeSetup(
																													structure
																												);
																											(
																												structure as structureWithChanges
																											).changes.interfaceTaskFileType =
																												{
																													previousValue:
																														{
																															id: structure
																																.interfaceTaskFileType
																																.id,
																															typeId: structure
																																.interfaceTaskFileType
																																.typeId,
																															gammaId:
																																change
																																	.baselineVersion
																																	.gammaId as `${number}`,
																															value: change
																																.baselineVersion
																																.value as number,
																														},

																													currentValue:
																														{
																															id: structure
																																.interfaceTaskFileType
																																.id,
																															typeId: structure
																																.interfaceTaskFileType
																																.typeId,
																															gammaId:
																																change
																																	.baselineVersion
																																	.gammaId as `${number}`,
																															value: change
																																.currentVersion
																																.value as number,
																														},
																													transactionToken:
																														change
																															.currentVersion
																															.transactionToken,
																												};
																											return structure as structureWithChanges;
																										}
																									)
																								),
																								of()
																							)
																						)
																					)
																				)
																			)
																		),
																		of(
																			structure
																		) //default case
																	)
															)
														)
													),
													reduce(
														(acc, curr) => [
															...acc,
															curr,
														],
														[] as (
															| structure
															| structureWithChanges
														)[]
													)
												), //structures
												iif(
													() =>
														structures
															.map((a) =>
																a.elements?.map(
																	(b) => b.id
																)
															)
															.flat()
															.includes(
																change.artId as `${number}`
															),
													of(structures).pipe(
														take(1),
														concatMap(
															(structures) =>
																from(
																	structures
																).pipe(
																	switchMap(
																		(
																			structure
																		) =>
																			iif(
																				() =>
																					structure.elements
																						?.map(
																							(
																								a
																							) =>
																								a.id
																						)
																						.includes(
																							change.artId as `${number}`
																						),
																				of(
																					structure
																				).pipe(
																					concatMap(
																						(
																							structure
																						) =>
																							from(
																								structure.elements
																							).pipe(
																								switchMap(
																									(
																										element
																									) =>
																										iif(
																											() =>
																												change.artId ===
																												element.id,
																											iif(
																												() =>
																													change.itemTypeId ===
																													ATTRIBUTETYPEIDENUM.DESCRIPTION,
																												of(
																													element
																												).pipe(
																													map(
																														(
																															el
																														) => {
																															el =
																																this._elementChangeSetup(
																																	el
																																);
																															if (
																																this._elementIsDiffed(
																																	el
																																)
																															) {
																																el.changes.description =
																																	{
																																		previousValue:
																																			{
																																				id: el
																																					.description
																																					.id,
																																				typeId: el
																																					.description
																																					.typeId,
																																				gammaId:
																																					change
																																						.baselineVersion
																																						.gammaId as `${number}`,
																																				value: change
																																					.baselineVersion
																																					.value as string,
																																			},
																																		currentValue:
																																			{
																																				id: el
																																					.description
																																					.id,
																																				typeId: el
																																					.description
																																					.typeId,
																																				gammaId:
																																					change
																																						.currentVersion
																																						.gammaId as `${number}`,
																																				value: change
																																					.currentVersion
																																					.value as string,
																																			},
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																															}
																															return el;
																														}
																													)
																												),
																												iif(
																													() =>
																														change.itemTypeId ===
																														ATTRIBUTETYPEIDENUM.NAME,
																													of(
																														element
																													).pipe(
																														map(
																															(
																																el
																															) => {
																																el =
																																	this._elementChangeSetup(
																																		el
																																	);
																																if (
																																	this._elementIsDiffed(
																																		el
																																	)
																																) {
																																	el.changes.name =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.name
																																						.id,
																																					typeId: el
																																						.name
																																						.typeId,
																																					gammaId:
																																						change
																																							.baselineVersion
																																							.gammaId as `${number}`,
																																					value: change
																																						.baselineVersion
																																						.value as string,
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.name
																																						.id,
																																					typeId: el
																																						.name
																																						.typeId,
																																					gammaId:
																																						change
																																							.currentVersion
																																							.gammaId as `${number}`,
																																					value: change
																																						.currentVersion
																																						.value as string,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																}
																																return el;
																															}
																														)
																													),
																													iif(
																														() =>
																															change.itemTypeId ===
																															ATTRIBUTETYPEIDENUM.INTERFACEELEMENTALTERABLE,
																														of(
																															element
																														).pipe(
																															map(
																																(
																																	el
																																) => {
																																	el =
																																		this._elementChangeSetup(
																																			el
																																		);
																																	if (
																																		this._elementIsDiffed(
																																			el
																																		)
																																	) {
																																		el.changes.interfaceElementAlterable =
																																			{
																																				previousValue:
																																					{
																																						id: el
																																							.interfaceElementAlterable
																																							.id,
																																						typeId: el
																																							.interfaceElementAlterable
																																							.typeId,
																																						gammaId:
																																							change
																																								.baselineVersion
																																								.gammaId as `${number}`,
																																						value: change
																																							.baselineVersion
																																							.value as boolean,
																																					},
																																				currentValue:
																																					{
																																						id: el
																																							.interfaceElementAlterable
																																							.id,
																																						typeId: el
																																							.interfaceElementAlterable
																																							.typeId,
																																						gammaId:
																																							change
																																								.currentVersion
																																								.gammaId as `${number}`,
																																						value: change
																																							.currentVersion
																																							.value as boolean,
																																					},
																																				transactionToken:
																																					change
																																						.currentVersion
																																						.transactionToken,
																																			};
																																	}
																																	return el;
																																}
																															)
																														),
																														iif(
																															() =>
																																change.itemTypeId ===
																																ATTRIBUTETYPEIDENUM.INTERFACEELEMENTSTART,
																															of(
																																element
																															).pipe(
																																map(
																																	(
																																		el
																																	) => {
																																		el =
																																			this._elementChangeSetup(
																																				el
																																			);
																																		if (
																																			this._elementIsDiffed(
																																				el
																																			)
																																		) {
																																			el.changes.interfaceElementIndexStart =
																																				{
																																					previousValue:
																																						{
																																							id: el
																																								.interfaceElementIndexStart
																																								.id,
																																							typeId: el
																																								.interfaceElementIndexStart
																																								.typeId,
																																							gammaId:
																																								change
																																									.baselineVersion
																																									.gammaId as `${number}`,
																																							value: change
																																								.baselineVersion
																																								.value as number,
																																						},
																																					currentValue:
																																						{
																																							id: el
																																								.interfaceElementIndexStart
																																								.id,
																																							typeId: el
																																								.interfaceElementIndexStart
																																								.typeId,
																																							gammaId:
																																								change
																																									.currentVersion
																																									.gammaId as `${number}`,
																																							value: change
																																								.currentVersion
																																								.value as number,
																																						},
																																					transactionToken:
																																						change
																																							.currentVersion
																																							.transactionToken,
																																				};
																																		}
																																		return el;
																																	}
																																)
																															),
																															iif(
																																() =>
																																	change.itemTypeId ===
																																	ATTRIBUTETYPEIDENUM.INTERFACEELEMENTEND,
																																of(
																																	element
																																).pipe(
																																	map(
																																		(
																																			el
																																		) => {
																																			el =
																																				this._elementChangeSetup(
																																					el
																																				);
																																			if (
																																				this._elementIsDiffed(
																																					el
																																				)
																																			) {
																																				el.changes.interfaceElementIndexEnd =
																																					{
																																						previousValue:
																																							{
																																								id: el
																																									.interfaceElementIndexEnd
																																									.id,
																																								typeId: el
																																									.interfaceElementIndexEnd
																																									.typeId,
																																								gammaId:
																																									change
																																										.baselineVersion
																																										.gammaId as `${number}`,
																																								value: change
																																									.baselineVersion
																																									.value as number,
																																							},
																																						currentValue:
																																							{
																																								id: el
																																									.interfaceElementIndexEnd
																																									.id,
																																								typeId: el
																																									.interfaceElementIndexEnd
																																									.typeId,
																																								gammaId:
																																									change
																																										.currentVersion
																																										.gammaId as `${number}`,
																																								value: change
																																									.currentVersion
																																									.value as number,
																																							},
																																						transactionToken:
																																							change
																																								.currentVersion
																																								.transactionToken,
																																					};
																																			}
																																			return el;
																																		}
																																	)
																																),
																																iif(
																																	() =>
																																		change.itemTypeId ===
																																		ATTRIBUTETYPEIDENUM.NOTES,
																																	of(
																																		element
																																	).pipe(
																																		map(
																																			(
																																				el
																																			) => {
																																				el =
																																					this._elementChangeSetup(
																																						el
																																					);
																																				if (
																																					this._elementIsDiffed(
																																						el
																																					)
																																				) {
																																					el.changes.notes =
																																						{
																																							previousValue:
																																								{
																																									id: el
																																										.notes
																																										.id,
																																									typeId: el
																																										.notes
																																										.typeId,
																																									gammaId:
																																										change
																																											.baselineVersion
																																											.gammaId as `${number}`,
																																									value: change
																																										.baselineVersion
																																										.value as string,
																																								},
																																							currentValue:
																																								{
																																									id: el
																																										.notes
																																										.id,
																																									typeId: el
																																										.notes
																																										.typeId,
																																									gammaId:
																																										change
																																											.currentVersion
																																											.gammaId as `${number}`,
																																									value: change
																																										.currentVersion
																																										.value as string,
																																								},
																																							transactionToken:
																																								change
																																									.currentVersion
																																									.transactionToken,
																																						};
																																				}
																																				return el;
																																			}
																																		)
																																	),
																																	iif(
																																		() =>
																																			change.itemTypeId ===
																																			ATTRIBUTETYPEIDENUM.INTERFACEENUMLITERAL,
																																		of(
																																			element
																																		).pipe(
																																			map(
																																				(
																																					el
																																				) => {
																																					el =
																																						this._elementChangeSetup(
																																							el
																																						);
																																					if (
																																						this._elementIsDiffed(
																																							el
																																						)
																																					) {
																																						el.changes.enumLiteral =
																																							{
																																								previousValue:
																																									{
																																										id: el
																																											.enumLiteral
																																											.id,
																																										typeId: el
																																											.enumLiteral
																																											.typeId,
																																										gammaId:
																																											change
																																												.baselineVersion
																																												.gammaId as `${number}`,
																																										value: change
																																											.baselineVersion
																																											.value as string,
																																									},
																																								currentValue:
																																									{
																																										id: el
																																											.enumLiteral
																																											.id,
																																										typeId: el
																																											.enumLiteral
																																											.typeId,
																																										gammaId:
																																											change
																																												.currentVersion
																																												.gammaId as `${number}`,
																																										value: change
																																											.currentVersion
																																											.value as string,
																																									},
																																								transactionToken:
																																									change
																																										.currentVersion
																																										.transactionToken,
																																							};
																																					}
																																					return el;
																																				}
																																			)
																																		),
																																		of(
																																			element
																																		)
																																	)
																																)
																															)
																														)
																													)
																												)
																											),
																											of(
																												element
																											) //default case
																										)
																								)
																							)
																					),
																					reduce(
																						(
																							acc,
																							curr
																						) => [
																							...acc,
																							curr,
																						],
																						[] as element[]
																					),
																					map(
																						(
																							val
																						) => {
																							structure.elements =
																								val;
																							(
																								structure as structureWithChanges
																							).hasElementChanges =
																								true;
																							return structure;
																						}
																					)
																				),
																				of(
																					structure
																				)
																			)
																	)
																)
														),
														reduce(
															(acc, curr) => [
																...acc,
																curr,
															],
															[] as (
																| structure
																| structureWithChanges
															)[]
														)
													), //elements
													iif(
														() =>
															structures
																.map((a) =>
																	a.elements.map(
																		(b) =>
																			b.platformType.id?.toString()
																	)
																)
																.flat()
																.includes(
																	change.artId
																),
														of(structures).pipe(
															take(1),
															concatMap(
																(structures) =>
																	from(
																		structures
																	).pipe(
																		switchMap(
																			(
																				structure
																			) =>
																				iif(
																					() =>
																						structure.elements
																							.map(
																								(
																									a
																								) =>
																									a.platformType.id?.toString()
																							)
																							.flat()
																							.includes(
																								change.artId
																							),
																					of(
																						structure
																					).pipe(
																						concatMap(
																							(
																								structure
																							) =>
																								from(
																									structure.elements
																								).pipe(
																									switchMap(
																										(
																											element
																										) =>
																											iif(
																												() =>
																													change.artId ===
																													element.platformType.id?.toString(),
																												iif(
																													() =>
																														change.itemTypeId ===
																														ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
																													of(
																														element
																													).pipe(
																														map(
																															(
																																el
																															) => {
																																el =
																																	this._elementChangeSetup(
																																		el
																																	);
																																if (
																																	this._elementIsDiffed(
																																		el
																																	)
																																) {
																																	if (
																																		el
																																			.changes
																																			.platformType ===
																																		undefined
																																	) {
																																		el.changes.platformType =
																																			{
																																				previousValue:
																																					{
																																						...new PlatformTypeSentinel(),
																																						interfacePlatformTypeUnits:
																																							{
																																								...new PlatformTypeSentinel()
																																									.interfacePlatformTypeUnits,
																																								gammaId:
																																									change
																																										.baselineVersion
																																										.gammaId as `${number}`,
																																								value: change
																																									.baselineVersion
																																									.value as string,
																																							},
																																					},
																																				currentValue:
																																					{
																																						...new PlatformTypeSentinel(),
																																						interfacePlatformTypeUnits:
																																							{
																																								...new PlatformTypeSentinel()
																																									.interfacePlatformTypeUnits,
																																								gammaId:
																																									change
																																										.currentVersion
																																										.gammaId as `${number}`,
																																								value: change
																																									.currentVersion
																																									.value as string,
																																							},
																																					},
																																				transactionToken:
																																					change
																																						.currentVersion
																																						.transactionToken,
																																			};
																																	} else if (
																																		el
																																			.changes
																																			.platformType !==
																																			undefined &&
																																		el
																																			.changes
																																			.platformType
																																			?.currentValue
																																			.interfacePlatformTypeUnits
																																			.value !==
																																			element
																																				.platformType
																																				.interfacePlatformTypeUnits
																																				.value
																																	) {
																																		el.changes.platformType!.transactionToken =
																																			change.currentVersion.transactionToken;
																																	}
																																}

																																return el;
																															}
																														)
																													),
																													iif(
																														() =>
																															change.itemTypeId ===
																															ATTRIBUTETYPEIDENUM.NAME,
																														of(
																															element
																														).pipe(
																															map(
																																(
																																	el
																																) => {
																																	el =
																																		this._elementChangeSetup(
																																			el
																																		);
																																	if (
																																		this._elementIsDiffed(
																																			el
																																		)
																																	) {
																																		el.changes.platformType =
																																			{
																																				previousValue:
																																					{
																																						...(el
																																							.changes
																																							.platformType
																																							?.previousValue ||
																																							new PlatformTypeSentinel()),

																																						name: {
																																							id: el
																																								.platformType
																																								.name
																																								.id,
																																							typeId: el
																																								.platformType
																																								.name
																																								.typeId,
																																							gammaId:
																																								change
																																									.baselineVersion
																																									.gammaId as `${number}`,
																																							value: change
																																								.baselineVersion
																																								.value as string,
																																						},
																																					},
																																				currentValue:
																																					{
																																						...(el
																																							.changes
																																							.platformType
																																							?.currentValue ||
																																							new PlatformTypeSentinel()),
																																						name: {
																																							id: el
																																								.platformType
																																								.name
																																								.id,
																																							typeId: el
																																								.platformType
																																								.name
																																								.typeId,
																																							gammaId:
																																								change
																																									.baselineVersion
																																									.gammaId as `${number}`,
																																							value: change
																																								.currentVersion
																																								.value as string,
																																						},
																																					},
																																				transactionToken:
																																					change
																																						.currentVersion
																																						.transactionToken,
																																			};
																																	}
																																	return el;
																																}
																															)
																														),
																														of(
																															element
																														)
																													)
																												),
																												of(
																													element
																												)
																											)
																									)
																								)
																						),
																						reduce(
																							(
																								acc,
																								curr
																							) => [
																								...acc,
																								curr,
																							],
																							[] as element[]
																						),
																						map(
																							(
																								val
																							) => {
																								structure.elements =
																									val;
																								(
																									structure as structureWithChanges
																								).hasElementChanges =
																									true;
																								return structure;
																							}
																						)
																					),
																					of(
																						structure
																					)
																				)
																		)
																	)
															)
														),
														of()
													)
												)
											),
											iif(
												() =>
													change.changeType.id ===
													changeTypeNumber.RELATION_CHANGE,
												iif(
													() =>
														typeof change.itemTypeId ===
															'object' &&
														'id' in
															change.itemTypeId &&
														change.itemTypeId.id ===
															RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT &&
														change.artId ===
															subMessageId,
													iif(
														() =>
															change
																.currentVersion
																.modType ===
																ModificationType.NEW &&
															change
																.baselineVersion
																.modType ===
																ModificationType.NONE &&
															structures
																.map(
																	(a) => a.id
																)
																.includes(
																	change.artIdB as `${number}`
																),
														of(structures).pipe(
															take(1),
															concatMap(
																(structures) =>
																	from(
																		structures
																	).pipe(
																		switchMap(
																			(
																				structure
																			) =>
																				iif(
																					() =>
																						change.artIdB ===
																						structure.id,
																					of(
																						structure
																					).pipe(
																						map(
																							(
																								struct
																							) => {
																								struct =
																									this._structureChangeSetup(
																										struct
																									);
																								(
																									struct as structureWithChanges
																								).added =
																									true;
																								return struct as structureWithChanges;
																							}
																						)
																					),
																					of(
																						structure
																					)
																				)
																		)
																	)
															),
															reduce(
																(acc, curr) => [
																	...acc,
																	curr,
																],
																[] as (
																	| structure
																	| structureWithChanges
																)[]
															)
														),
														iif(
															() =>
																change
																	.currentVersion
																	.modType ===
																	ModificationType.DELETED &&
																change
																	.baselineVersion
																	.modType !==
																	ModificationType.NONE &&
																change
																	.baselineVersion
																	.modType !==
																	ModificationType.DELETED &&
																change
																	.baselineVersion
																	.modType !==
																	ModificationType.DELETED_ON_DESTINATION,
															this.structure
																.getStructure(
																	parentBranch,
																	messageId,
																	subMessageId,
																	viewId,
																	change.artIdB,
																	connectionId
																)
																.pipe(
																	map(
																		(
																			initialStruct
																		) => {
																			return {
																				...initialStruct,
																				deleted:
																					true,
																				added: false,
																				changes:
																					{
																						name: {
																							previousValue:
																								initialStruct.name,
																							currentValue:
																								'',
																							transactionToken:
																								change
																									.currentVersion
																									.transactionToken,
																						},
																						description:
																							{
																								previousValue:
																									initialStruct.description,
																								currentValue:
																									'',
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						interfaceMaxSimultaneity:
																							{
																								previousValue:
																									initialStruct.interfaceMaxSimultaneity,
																								currentValue:
																									'',
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						interfaceMinSimultaneity:
																							{
																								previousValue:
																									initialStruct.interfaceMinSimultaneity,
																								currentValue:
																									'',
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						interfaceTaskFileType:
																							{
																								previousValue:
																									initialStruct.interfaceTaskFileType,
																								currentValue:
																									'',
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						interfaceStructureCategory:
																							{
																								previousValue:
																									initialStruct.interfaceStructureCategory,
																								currentValue:
																									'',
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						applicability:
																							{
																								previousValue:
																									initialStruct.applicability,
																								currentValue:
																									'',
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						numElements:
																							true,
																					},
																			};
																		}
																	),
																	map(
																		(
																			struct
																		) => {
																			structures =
																				[
																					...structures,

																					struct as unknown as structureWithChanges,
																				];
																			return structures as (
																				| Required<structure>
																				| structureWithChanges
																			)[];
																		}
																	)
																),
															of(structures)
														)
													),
													iif(
														() =>
															typeof change.itemTypeId ===
																'object' &&
															'id' in
																change.itemTypeId &&
															change.itemTypeId
																.id ===
																RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT &&
															structures
																.map(
																	(a) => a.id
																)
																.includes(
																	change.artId as `${number}`
																),
														iif(
															() =>
																change
																	.currentVersion
																	.modType ===
																	ModificationType.NEW &&
																change
																	.baselineVersion
																	.modType ===
																	ModificationType.NONE &&
																structures
																	.map((a) =>
																		a.elements?.map(
																			(
																				b
																			) =>
																				b.id
																		)
																	)
																	.flat()
																	.includes(
																		change.artIdB as `${number}`
																	),
															of(structures).pipe(
																take(1),
																concatMap(
																	(
																		structures
																	) =>
																		from(
																			structures
																		).pipe(
																			switchMap(
																				(
																					structure
																				) =>
																					iif(
																						() =>
																							structure.elements
																								?.map(
																									(
																										a
																									) =>
																										a.id
																								)
																								.includes(
																									change.artIdB as `${number}`
																								),
																						of(
																							structure
																						).pipe(
																							concatMap(
																								(
																									structure
																								) =>
																									from(
																										structure.elements
																									).pipe(
																										switchMap(
																											(
																												element
																											) =>
																												iif(
																													() =>
																														change.artIdB ===
																														element.id,
																													of(
																														element
																													).pipe(
																														map(
																															(
																																el
																															) => {
																																el =
																																	this._elementChangeSetup(
																																		el
																																	);
																																if (
																																	this._elementIsDiffed(
																																		el
																																	)
																																) {
																																	el.changes.name =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.name
																																						.id,
																																					typeId: el
																																						.name
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: '',
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.name
																																						.id,
																																					typeId: el
																																						.name
																																						.typeId,
																																					gammaId:
																																						el
																																							.name
																																							.gammaId,
																																					value: el
																																						.name
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.description =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.description
																																						.id,
																																					typeId: el
																																						.description
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: '',
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.description
																																						.id,
																																					typeId: el
																																						.description
																																						.typeId,
																																					gammaId:
																																						el
																																							.description
																																							.gammaId,
																																					value: el
																																						.description
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.interfaceElementAlterable =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.interfaceElementAlterable
																																						.id,
																																					typeId: el
																																						.interfaceElementAlterable
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: false,
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.interfaceElementAlterable
																																						.id,
																																					typeId: el
																																						.interfaceElementAlterable
																																						.typeId,
																																					gammaId:
																																						el
																																							.interfaceElementAlterable
																																							.gammaId,
																																					value: el
																																						.interfaceElementAlterable
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.interfaceElementIndexEnd =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.interfaceElementIndexEnd
																																						.id,
																																					typeId: el
																																						.interfaceElementIndexEnd
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: 0,
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.interfaceElementIndexEnd
																																						.id,
																																					typeId: el
																																						.interfaceElementIndexEnd
																																						.typeId,
																																					gammaId:
																																						el
																																							.interfaceElementIndexEnd
																																							.gammaId,
																																					value: el
																																						.interfaceElementIndexEnd
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.interfaceElementIndexStart =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.interfaceElementIndexStart
																																						.id,
																																					typeId: el
																																						.interfaceElementIndexStart
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: 0,
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.interfaceElementIndexStart
																																						.id,
																																					typeId: el
																																						.interfaceElementIndexStart
																																						.typeId,
																																					gammaId:
																																						el
																																							.interfaceElementIndexStart
																																							.gammaId,
																																					value: el
																																						.interfaceElementIndexStart
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.notes =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.notes
																																						.id,
																																					typeId: el
																																						.notes
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: '',
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.notes
																																						.id,
																																					typeId: el
																																						.notes
																																						.typeId,
																																					gammaId:
																																						el
																																							.notes
																																							.gammaId,
																																					value: el
																																						.notes
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.enumLiteral =
																																		{
																																			previousValue:
																																				{
																																					id: el
																																						.enumLiteral
																																						.id,
																																					typeId: el
																																						.enumLiteral
																																						.typeId,
																																					gammaId:
																																						'-1',
																																					value: '',
																																				},
																																			currentValue:
																																				{
																																					id: el
																																						.enumLiteral
																																						.id,
																																					typeId: el
																																						.enumLiteral
																																						.typeId,
																																					gammaId:
																																						el
																																							.enumLiteral
																																							.gammaId,
																																					value: el
																																						.enumLiteral
																																						.value,
																																				},
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.platformType =
																																		{
																																			previousValue:
																																				new PlatformTypeSentinel(),
																																			currentValue:
																																				el.platformType,
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	el.changes.applicability =
																																		{
																																			previousValue:
																																				change
																																					.baselineVersion
																																					.applicabilityToken as applic,
																																			currentValue:
																																				change
																																					.currentVersion
																																					.applicabilityToken as applic,
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																}

																																el.added =
																																	true;
																																return el;
																															}
																														)
																													),
																													of(
																														element
																													)
																												)
																										)
																									)
																							),
																							reduce(
																								(
																									acc,
																									curr
																								) => [
																									...acc,
																									curr,
																								],
																								[] as element[]
																							),
																							map(
																								(
																									val
																								) => {
																									structure.elements =
																										val;
																									(
																										structure as structureWithChanges
																									).hasElementChanges =
																										true;
																									return structure;
																								}
																							)
																						),
																						of(
																							structure
																						)
																					)
																			)
																		)
																),
																reduce(
																	(
																		acc,
																		curr
																	) => [
																		...acc,
																		curr,
																	],
																	[] as (
																		| structure
																		| structureWithChanges
																	)[]
																)
															),
															iif(
																() =>
																	change
																		.currentVersion
																		.modType ===
																		ModificationType.DELETED &&
																	change
																		.baselineVersion
																		.modType !==
																		ModificationType.NONE &&
																	change
																		.baselineVersion
																		.modType !==
																		ModificationType.DELETED &&
																	change
																		.baselineVersion
																		.modType !==
																		ModificationType.DELETED_ON_DESTINATION,
																of(
																	structures
																).pipe(
																	take(1),
																	concatMap(
																		(
																			structures
																		) =>
																			from(
																				structures
																			).pipe(
																				concatMap(
																					(
																						structure
																					) =>
																						iif(
																							() =>
																								change.artId ===
																								structure.id,
																							of(
																								structure
																							).pipe(
																								concatMap(
																									(
																										structure
																									) =>
																										this.elements
																											.getElement(
																												parentBranch,
																												messageId,
																												subMessageId,
																												structure.id,
																												change.artIdB,
																												connectionId
																											)
																											.pipe(
																												map(
																													(
																														initialEl
																													) => {
																														return {
																															...initialEl,
																															changes:
																																{
																																	name: {
																																		previousValue:
																																			initialEl.name,
																																		currentValue:
																																			'',
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	},
																																	description:
																																		{
																																			previousValue:
																																				initialEl.description,
																																			currentValue:
																																				'',
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																	notes: {
																																		previousValue:
																																			initialEl.notes,
																																		currentValue:
																																			'',
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	},
																																	platformType:
																																		{
																																			previousValue:
																																				initialEl.platformType,
																																			currentValue:
																																				new PlatformTypeSentinel(),
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																	interfaceElementIndexEnd:
																																		{
																																			previousValue:
																																				initialEl.interfaceElementIndexEnd,
																																			currentValue:
																																				'',
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																	interfaceElementIndexStart:
																																		{
																																			previousValue:
																																				initialEl.interfaceElementIndexStart,
																																			currentValue:
																																				'',
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																	interfaceElementAlterable:
																																		{
																																			previousValue:
																																				initialEl.interfaceElementAlterable,
																																			currentValue:
																																				'',
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																	enumLiteral:
																																		{
																																			previousValue:
																																				initialEl.enumLiteral,
																																			currentValue:
																																				'',
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																	applicability:
																																		{
																																			previousValue:
																																				initialEl.applicability,
																																			currentValue:
																																				'',
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		},
																																},
																															deleted:
																																true,
																														};
																													}
																												)
																											)
																								),
																								map(
																									(
																										val
																									) => {
																										structure.elements =
																											[
																												...structure.elements,
																												val as unknown as Required<element>,
																											];
																										structure.numElements =
																											structure.elements.length;
																										structure =
																											this._structureChangeSetup(
																												structure
																											);
																										(
																											structure as structureWithChanges
																										).changes.numElements =
																											true;
																										structure.elements.sort(
																											(
																												a,
																												b
																											) =>
																												Number(
																													a.id
																												) -
																												Number(
																													b.id
																												)
																										);
																										return structure;
																									}
																								)
																							),
																							of(
																								structure
																							)
																						)
																				)
																			)
																	),
																	reduce(
																		(
																			acc,
																			curr
																		) => [
																			...acc,
																			curr,
																		],
																		[] as (
																			| structure
																			| structureWithChanges
																		)[]
																	)
																),
																of(change)
															)
														),
														iif(
															() =>
																typeof change.itemTypeId ===
																	'object' &&
																'id' in
																	change.itemTypeId &&
																change
																	.itemTypeId
																	.id ===
																	RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE &&
																structures
																	.map((a) =>
																		a.elements?.map(
																			(
																				b
																			) =>
																				b.id
																		)
																	)
																	.flat()
																	.includes(
																		change.artId as `${number}`
																	),
															iif(
																() =>
																	change
																		.currentVersion
																		.modType ===
																		ModificationType.NEW &&
																	change
																		.baselineVersion
																		.modType ===
																		ModificationType.NONE,
																of(
																	structures
																).pipe(
																	take(1),
																	concatMap(
																		(
																			structures
																		) =>
																			from(
																				structures
																			).pipe(
																				concatMap(
																					(
																						structure
																					) =>
																						iif(
																							() =>
																								structure.elements
																									?.map(
																										(
																											a
																										) =>
																											a.id
																									)
																									.includes(
																										change.artId as `${number}`
																									),
																							of(
																								structure
																							).pipe(
																								concatMap(
																									(
																										structure
																									) =>
																										from(
																											structure.elements
																										).pipe(
																											concatMap(
																												(
																													element
																												) =>
																													iif(
																														() =>
																															change.artId ===
																															element.id,
																														of(
																															element
																														).pipe(
																															concatMap(
																																(
																																	_
																																) =>
																																	this.typeService
																																		.getTypeFromBranch(
																																			branchId,
																																			change.artIdB
																																		)
																																		.pipe(
																																			map(
																																				(
																																					type
																																				) => {
																																					element =
																																						this._elementChangeSetup(
																																							element
																																						);
																																					if (
																																						this._elementIsDiffed(
																																							element
																																						)
																																					) {
																																						if (
																																							element
																																								.changes
																																								.platformType ===
																																							undefined
																																						) {
																																							element.changes.platformType =
																																								{
																																									previousValue:
																																										new PlatformTypeSentinel(),
																																									currentValue:
																																										{
																																											...new PlatformTypeSentinel(),
																																											name: type.name,
																																										},
																																									transactionToken:
																																										change
																																											.currentVersion
																																											.transactionToken,
																																								};
																																						} else if (
																																							element
																																								.changes
																																								.platformType !==
																																								undefined &&
																																							element
																																								.changes
																																								.platformType
																																								?.currentValue
																																								.name !==
																																								element
																																									.platformType
																																									.name
																																						) {
																																							element.changes.platformType!.currentValue.name =
																																								type.name;
																																							element.changes.platformType!.transactionToken =
																																								change.currentVersion.transactionToken;
																																						}
																																					}
																																					return element;
																																				}
																																			)
																																		)
																															)
																														),
																														of(
																															element
																														)
																													)
																											)
																										)
																								),
																								reduce(
																									(
																										acc,
																										curr
																									) => [
																										...acc,
																										curr,
																									],
																									[] as element[]
																								),
																								map(
																									(
																										val
																									) => {
																										structure.elements =
																											val;
																										(
																											structure as structureWithChanges
																										).hasElementChanges =
																											true;
																										return structure;
																									}
																								)
																							),
																							of(
																								structure
																							)
																						)
																				)
																			)
																	),
																	reduce(
																		(
																			acc,
																			curr
																		) => [
																			...acc,
																			curr,
																		],
																		[] as (
																			| structure
																			| structureWithChanges
																		)[]
																	)
																),
																iif(
																	() =>
																		change
																			.currentVersion
																			.modType ===
																			ModificationType.DELETED &&
																		change
																			.baselineVersion
																			.modType !==
																			ModificationType.NONE,
																	of(
																		structures
																	).pipe(
																		take(1),
																		concatMap(
																			(
																				structures
																			) =>
																				from(
																					structures
																				).pipe(
																					concatMap(
																						(
																							structure
																						) =>
																							iif(
																								() =>
																									structure.elements
																										?.map(
																											(
																												a
																											) =>
																												a.id
																										)
																										.includes(
																											change.artId as `${number}`
																										),
																								of(
																									structure
																								).pipe(
																									concatMap(
																										(
																											structure
																										) =>
																											from(
																												structure.elements
																											).pipe(
																												concatMap(
																													(
																														element
																													) =>
																														iif(
																															() =>
																																change.artId ===
																																element.id,
																															of(
																																element
																															).pipe(
																																concatMap(
																																	(
																																		_
																																	) =>
																																		this.typeService
																																			.getTypeFromBranch(
																																				branchId,
																																				change.artIdB
																																			)
																																			.pipe(
																																				map(
																																					(
																																						type
																																					) => {
																																						element =
																																							this._elementChangeSetup(
																																								element
																																							);
																																						if (
																																							this._elementIsDiffed(
																																								element
																																							)
																																						) {
																																							if (
																																								element
																																									.changes
																																									.platformType ===
																																								undefined
																																							) {
																																								element.changes.platformType =
																																									{
																																										previousValue:
																																											{
																																												...element
																																													.changes
																																													.platformType!
																																													.previousValue,
																																												name: type.name,
																																											},
																																										currentValue:
																																											new PlatformTypeSentinel(),
																																										transactionToken:
																																											change
																																												.currentVersion
																																												.transactionToken,
																																									};
																																							} else if (
																																								element
																																									.changes
																																									.platformType !==
																																									undefined &&
																																								element
																																									.changes
																																									.platformType
																																									?.currentValue
																																									.name !==
																																									element
																																										.platformType
																																										.name
																																							) {
																																								element.changes.platformType!.previousValue.name =
																																									type.name;
																																								element.changes.platformType!.transactionToken =
																																									change.currentVersion.transactionToken;
																																							} else {
																																								element.changes.platformType!.previousValue.name =
																																									type.name;
																																							}
																																						}
																																						return element;
																																					}
																																				)
																																			)
																																)
																															),
																															of(
																																element
																															)
																														)
																												)
																											)
																									),
																									reduce(
																										(
																											acc,
																											curr
																										) => [
																											...acc,
																											curr,
																										],
																										[] as element[]
																									),
																									map(
																										(
																											val
																										) => {
																											structure.elements =
																												val;
																											(
																												structure as structureWithChanges
																											).hasElementChanges =
																												true;
																											return structure;
																										}
																									)
																								),
																								of(
																									structure
																								)
																							)
																					)
																				)
																		),
																		reduce(
																			(
																				acc,
																				curr
																			) => [
																				...acc,
																				curr,
																			],
																			[] as (
																				| structure
																				| structureWithChanges
																			)[]
																		)
																	),
																	of(
																		structures
																	)
																)
															),
															of(change)
														)
													)
												),
												of()
											)
										)
									)
								)
							)
						),
						switchMap((_) =>
							of(
								structures as (
									| structure
									| structureWithChanges
								)[]
							)
						)
					)
				)
			);
		} else {
			return of(_oldStructures);
		}
	}
}
