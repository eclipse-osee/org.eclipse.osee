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
import type {
	structure,
	element,
	elementWithChanges,
	structureWithChanges,
} from '@osee/messaging/shared/types';
import {
	combineLatest,
	share,
	debounceTime,
	distinctUntilChanged,
	filter,
	switchMap,
	repeat,
	shareReplay,
	of,
	concatMap,
	from,
	iif,
	map,
	OperatorFunction,
	reduce,
	take,
	BehaviorSubject,
	Observable,
} from 'rxjs';
import { applic } from '@osee/shared/types/applicability';
import {
	changeInstance,
	changeTypeNumber,
	ModificationType,
	ignoreType,
} from '@osee/shared/types/change-report';
import {
	ATTRIBUTETYPEIDENUM,
	RelationTypeId,
} from '@osee/shared/types/constants';
import { CurrentStructureService } from './current-structure.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentStructureMultiService extends CurrentStructureService {
	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(10);
	private _structuresNoDiff = combineLatest([
		this.ui.filter,
		this.ui.BranchId,
		this.ui.messageId,
		this.ui.subMessageId,
		this.ui.connectionId,
		this.ui.viewId,
		this.currentPage,
		this.currentPageSize,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		filter(
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
				branchId !== '' &&
				messageId !== '' &&
				subMessageId !== '' &&
				connectionId !== ''
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
			this.SubMessageId,
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
		this.ui.filter,
		this.ui.BranchId,
		this.ui.messageId,
		this.ui.subMessageId,
		this.ui.connectionId,
		this.ui.viewId,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		filter(
			([
				filter,
				branchId,
				messageId,
				subMessageId,
				connectionId,
				viewId,
			]) =>
				branchId !== '' &&
				messageId !== '' &&
				subMessageId !== '' &&
				connectionId !== ''
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

	get structuresCount() {
		return this._structuresCount;
	}

	get structures() {
		return this._structures;
	}

	private _parseDifferences(
		differences: changeInstance[] | undefined,
		_oldStructures: Required<structure>[],
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
									'id' in b.itemTypeId
								) {
									const relFactor =
										[
											RelationTypeId.INTERFACESTRUCTURECONTENT,
											RelationTypeId.INTERFACESUBMESSAGECONTENT,
											RelationTypeId.INTERFACEELEMENTPLATFORMTYPE,
										].indexOf(a.itemTypeId.id) -
										[
											RelationTypeId.INTERFACESTRUCTURECONTENT,
											RelationTypeId.INTERFACESUBMESSAGECONTENT,
											RelationTypeId.INTERFACEELEMENTPLATFORMTYPE,
										].indexOf(b.itemTypeId.id) -
										((a.itemTypeId.id ===
											RelationTypeId.INTERFACESTRUCTURECONTENT &&
											b.itemTypeId.id ===
												RelationTypeId.INTERFACESTRUCTURECONTENT) ||
										(a.itemTypeId.id ===
											RelationTypeId.INTERFACESUBMESSAGECONTENT &&
											b.itemTypeId.id ===
												RelationTypeId.INTERFACESUBMESSAGECONTENT)
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
											.includes(val.artId) ||
										structures
											.map((a) => a.id)
											.includes(val.artIdB) ||
										!(
											(
												structures
													.map((a) => a.elements)
													.flat() as (
													| element
													| elementWithChanges
													| undefined
												)[]
											).includes(undefined) &&
											(structures
												.map((a) =>
													a.elements?.map((b) => b.id)
												)
												.flat()
												.includes(val.artId) ||
												structures
													.map((a) =>
														a.elements?.map(
															(b) => b.id
														)
													)
													.flat()
													.includes(val.artIdB))
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
														.includes(change.artId),
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
																					val
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
																									.applicabilityToken,
																							currentValue:
																								change
																									.currentVersion
																									.applicabilityToken,
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
																						?.map(
																							(
																								a
																							) =>
																								a.id
																						)
																						.includes(
																							change.artId
																						),
																				of(
																					structure
																				).pipe(
																					map(
																						(
																							val
																						) => {
																							const index =
																								structure.elements?.findIndex(
																									(
																										el
																									) =>
																										el.id ===
																										change.artId
																								);
																							structure.elements[
																								index
																							] =
																								this._elementChangeSetup(
																									structure
																										.elements[
																										index
																									]
																								);
																							(
																								structure
																									.elements[
																									index
																								] as elementWithChanges
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
																								structure
																									.elements[
																									index
																								] as elementWithChanges
																							).added =
																								true;
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
																							?.map(
																								(
																									a
																								) =>
																									a.id
																							)
																							.includes(
																								change.artId
																							),
																					of(
																						structure
																					).pipe(
																						map(
																							(
																								val
																							) => {
																								const index =
																									structure.elements?.findIndex(
																										(
																											el
																										) =>
																											el.id ===
																											change.artId
																									);
																								structure.elements[
																									index
																								] =
																									this._elementChangeSetup(
																										structure
																											.elements[
																											index
																										]
																									);
																								(
																									structure
																										.elements[
																										index
																									] as elementWithChanges
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
														iif(
															() =>
																structures
																	.map(
																		(a) =>
																			a.id
																	)
																	.includes(
																		change.artId
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
																									val
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
																													.applicabilityToken,
																											currentValue:
																												change
																													.currentVersion
																													.applicabilityToken,
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
																						change.artId ===
																						structure.id,
																					of(
																						structure
																					).pipe(
																						map(
																							(
																								val
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
																												.applicabilityToken,
																										currentValue:
																											change
																												.currentVersion
																												.applicabilityToken,
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
														.includes(change.artId),
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
																									change
																										.baselineVersion
																										.value,
																								currentValue:
																									change
																										.currentVersion
																										.value,
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
																										change
																											.baselineVersion
																											.value,
																									currentValue:
																										change
																											.currentVersion
																											.value,
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
																											change
																												.baselineVersion
																												.value,
																										currentValue:
																											change
																												.currentVersion
																												.value,
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
																												change
																													.baselineVersion
																													.value,
																											currentValue:
																												change
																													.currentVersion
																													.value,
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
																													change
																														.baselineVersion
																														.value,
																												currentValue:
																													change
																														.currentVersion
																														.value,
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
																														change
																															.baselineVersion
																															.value,
																													currentValue:
																														change
																															.currentVersion
																															.value,
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
																						?.map(
																							(
																								a
																							) =>
																								a.id
																						)
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
																															(
																																el as elementWithChanges
																															).changes.description =
																																{
																																	previousValue:
																																		change
																																			.baselineVersion
																																			.value as string,
																																	currentValue:
																																		change
																																			.currentVersion
																																			.value as string,
																																	transactionToken:
																																		change
																																			.currentVersion
																																			.transactionToken,
																																};
																															return el as elementWithChanges;
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
																																(
																																	el as elementWithChanges
																																).changes.name =
																																	{
																																		previousValue:
																																			change
																																				.baselineVersion
																																				.value as string,
																																		currentValue:
																																			change
																																				.currentVersion
																																				.value as string,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																return el as elementWithChanges;
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
																																	(
																																		el as elementWithChanges
																																	).changes.interfaceElementAlterable =
																																		{
																																			previousValue:
																																				change
																																					.baselineVersion
																																					.value as boolean,
																																			currentValue:
																																				change
																																					.currentVersion
																																					.value as boolean,
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	return el as elementWithChanges;
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
																																		(
																																			el as elementWithChanges
																																		).changes.interfaceElementIndexStart =
																																			{
																																				previousValue:
																																					change
																																						.baselineVersion
																																						.value as number,
																																				currentValue:
																																					change
																																						.currentVersion
																																						.value as number,
																																				transactionToken:
																																					change
																																						.currentVersion
																																						.transactionToken,
																																			};
																																		return el as elementWithChanges;
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
																																			(
																																				el as elementWithChanges
																																			).changes.interfaceElementIndexEnd =
																																				{
																																					previousValue:
																																						change
																																							.baselineVersion
																																							.value as number,
																																					currentValue:
																																						change
																																							.currentVersion
																																							.value as number,
																																					transactionToken:
																																						change
																																							.currentVersion
																																							.transactionToken,
																																				};
																																			return el as elementWithChanges;
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
																																				(
																																					el as elementWithChanges
																																				).changes.notes =
																																					{
																																						previousValue:
																																							change
																																								.baselineVersion
																																								.value as string,
																																						currentValue:
																																							change
																																								.currentVersion
																																								.value as string,
																																						transactionToken:
																																							change
																																								.currentVersion
																																								.transactionToken,
																																					};
																																				return el as elementWithChanges;
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
																																					(
																																						el as elementWithChanges
																																					).changes.enumLiteral =
																																						{
																																							previousValue:
																																								change
																																									.baselineVersion
																																									.value as string,
																																							currentValue:
																																								change
																																									.currentVersion
																																									.value as string,
																																							transactionToken:
																																								change
																																									.currentVersion
																																									.transactionToken,
																																						};
																																					return el as elementWithChanges;
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
																						[] as (
																							| element
																							| elementWithChanges
																						)[]
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
																			b.platformTypeId?.toString()
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
																									a.platformTypeId?.toString()
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
																													element.platformTypeId?.toString(),
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
																																(
																																	el as elementWithChanges
																																).changes.units =
																																	{
																																		previousValue:
																																			change
																																				.baselineVersion
																																				.value as string,
																																		currentValue:
																																			change
																																				.currentVersion
																																				.value as string,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																return el as elementWithChanges;
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
																																	(
																																		el as elementWithChanges
																																	).changes.platformTypeName2 =
																																		{
																																			previousValue:
																																				change
																																					.baselineVersion
																																					.value as string,
																																			currentValue:
																																				change
																																					.currentVersion
																																					.value as string,
																																			transactionToken:
																																				change
																																					.currentVersion
																																					.transactionToken,
																																		};
																																	return el as elementWithChanges;
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
																							[] as (
																								| element
																								| elementWithChanges
																							)[]
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
															RelationTypeId.INTERFACESUBMESSAGECONTENT &&
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
																	change.artIdB
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
																					struct as structureWithChanges,
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
																RelationTypeId.INTERFACESTRUCTURECONTENT &&
															structures
																.map(
																	(a) => a.id
																)
																.includes(
																	change.artId
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
																		change.artIdB
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
																									change.artIdB
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
																																(
																																	el as elementWithChanges
																																).changes.name =
																																	{
																																		previousValue:
																																			'',
																																		currentValue:
																																			el.name,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.description =
																																	{
																																		previousValue:
																																			'',
																																		currentValue:
																																			el.description,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.interfaceElementAlterable =
																																	{
																																		previousValue:
																																			false,
																																		currentValue:
																																			el.interfaceElementAlterable,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.interfaceElementIndexEnd =
																																	{
																																		previousValue: 0,
																																		currentValue:
																																			el.interfaceElementIndexEnd,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.interfaceElementIndexStart =
																																	{
																																		previousValue: 0,
																																		currentValue:
																																			el.interfaceElementIndexStart,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.notes =
																																	{
																																		previousValue:
																																			'',
																																		currentValue:
																																			el.notes,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.enumLiteral =
																																	{
																																		previousValue:
																																			'',
																																		currentValue:
																																			el.notes,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
																																).changes.platformTypeName2 =
																																	{
																																		previousValue:
																																			'',
																																		currentValue:
																																			el.platformTypeName2,
																																		transactionToken:
																																			change
																																				.currentVersion
																																				.transactionToken,
																																	};
																																(
																																	el as elementWithChanges
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
																																	el as elementWithChanges
																																).added =
																																	true;
																																return el as elementWithChanges;
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
																								[] as (
																									| element
																									| elementWithChanges
																								)[]
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
																																	platformTypeName2:
																																		{
																																			previousValue:
																																				initialEl.platformTypeName2,
																																			currentValue:
																																				'',
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
																												val,
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
																	RelationTypeId.INTERFACEELEMENTPLATFORMTYPE &&
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
																		change.artId
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
																																	val
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
																																						(
																																							element as elementWithChanges
																																						)
																																							.changes
																																							.platformTypeName2 ===
																																						undefined
																																					) {
																																						(
																																							element as elementWithChanges
																																						).changes.platformTypeName2 =
																																							{
																																								previousValue:
																																									'',
																																								currentValue:
																																									type.name,
																																								transactionToken:
																																									change
																																										.currentVersion
																																										.transactionToken,
																																							};
																																					} else if (
																																						(
																																							element as elementWithChanges
																																						)
																																							.changes
																																							.platformTypeName2 !==
																																							undefined &&
																																						(
																																							element as elementWithChanges
																																						)
																																							.changes
																																							.platformTypeName2
																																							?.currentValue !==
																																							element.platformTypeName2
																																					) {
																																						(
																																							element as elementWithChanges
																																						).changes.platformTypeName2!.currentValue =
																																							type.name;
																																						(
																																							element as elementWithChanges
																																						).changes.platformTypeName2!.transactionToken =
																																							change.currentVersion.transactionToken;
																																					}
																																					return element as elementWithChanges;
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
																									[] as (
																										| element
																										| elementWithChanges
																									)[]
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
																																		val
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
																																							(
																																								element as elementWithChanges
																																							)
																																								.changes
																																								.platformTypeName2 ===
																																							undefined
																																						) {
																																							(
																																								element as elementWithChanges
																																							).changes.platformTypeName2 =
																																								{
																																									previousValue:
																																										type.name,
																																									currentValue:
																																										'',
																																									transactionToken:
																																										change
																																											.currentVersion
																																											.transactionToken,
																																								};
																																						} else if (
																																							(
																																								element as elementWithChanges
																																							)
																																								.changes
																																								.platformTypeName2 !==
																																								undefined &&
																																							(
																																								element as elementWithChanges
																																							)
																																								.changes
																																								.platformTypeName2
																																								?.currentValue !==
																																								element.platformTypeName2
																																						) {
																																							(
																																								element as elementWithChanges
																																							).changes.platformTypeName2!.previousValue =
																																								type.name;
																																							(
																																								element as elementWithChanges
																																							).changes.platformTypeName2!.transactionToken =
																																								change.currentVersion.transactionToken;
																																						} else {
																																							(
																																								element as elementWithChanges
																																							).changes.platformTypeName2!.previousValue =
																																								type.name;
																																						}
																																						return element as elementWithChanges;
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
																										[] as (
																											| element
																											| elementWithChanges
																										)[]
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
								// tap((valueToDebug) => {
								//   console.log(valueToDebug)
								// })
							)
						),
						switchMap((val) =>
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
