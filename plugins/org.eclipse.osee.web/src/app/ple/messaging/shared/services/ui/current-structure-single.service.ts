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
	structureWithChanges,
	elementWithChanges,
} from '@osee/messaging/shared/types';
import {
	combineLatest,
	filter,
	switchMap,
	of,
	count,
	from,
	iif,
	map,
	mergeMap,
	repeat,
	OperatorFunction,
	debounceTime,
	Observable,
} from 'rxjs';
import { applic } from '@osee/shared/types/applicability';
import {
	changeInstance,
	ignoreType,
	changeTypeNumber,
	ModificationType,
} from '@osee/shared/types/change-report';
import {
	ATTRIBUTETYPEIDENUM,
	RelationTypeId,
} from '@osee/shared/types/constants';
import { CurrentStructureService } from './current-structure.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentStructureSingleService extends CurrentStructureService {
	get structuresCount(): Observable<number> {
		return of(1);
	}
	private _structures = combineLatest([
		this.BranchId,
		this.branchInfoService.parentBranch,
		this.MessageId,
		this.SubMessageId,
		this.connectionId,
		this.ui.viewId,
		this.ui.filter,
		this.singleStructureId,
	]).pipe(
		filter(
			([
				branchId,
				parentBranch,
				messageId,
				subMessageId,
				connectionId,
				viewId,
				filterString,
				structureId,
			]) =>
				branchId !== '' &&
				messageId !== '' &&
				subMessageId !== '' &&
				connectionId !== '' &&
				structureId !== ''
		),
		debounceTime(500),
		switchMap(
			([
				branchId,
				parentBranch,
				messageId,
				subMessageId,
				connectionId,
				viewId,
				filterString,
				structureId,
			]) =>
				combineLatest([
					this.structure.getStructure(
						branchId,
						messageId,
						subMessageId,
						structureId,
						connectionId,
						viewId,
						filterString
					),
					this.ui.isInDiff,
					this.differences,
				]).pipe(
					repeat({ delay: () => this.ui.UpdateRequired }),
					switchMap(([structure, isInDiff, differences]) =>
						isInDiff &&
						differences !== undefined &&
						differences.length > 0
							? this._parseDifferences(
									differences,
									structure,
									parentBranch,
									branchId,
									messageId,
									subMessageId,
									connectionId
							  )
							: //no differences
							  of(structure)
					)
				)
		),
		map((data) => [data])
	);
	/**
	 * no-op implementation
	 */
	get currentPage() {
		return of();
	}

	/**
	 * no-op implementation
	 */
	set page(page: number) {}

	get currentPageSize(): Observable<number> {
		return of();
	}
	set pageSize(page: number) {}
	get structures() {
		return this._structures;
	}

	private _parseDifferences(
		differences: changeInstance[] | undefined,
		_oldStructure: Required<structure>,
		parentBranch: string,
		branch: string,
		message: string,
		submessage: string,
		connection: string
	) {
		let structure = JSON.parse(
			JSON.stringify(_oldStructure)
		) as Required<structure>;
		return of(differences).pipe(
			filter((val) => val !== undefined) as OperatorFunction<
				changeInstance[] | undefined,
				changeInstance[]
			>,
			switchMap((differenceArray) =>
				of(differenceArray).pipe(
					map((differenceArray) =>
						differenceArray.sort(
							(a, b) =>
								['111', '222', '333', '444'].indexOf(
									a.changeType.id
								) -
								['111', '222', '333', '444'].indexOf(
									b.changeType.id
								)
						)
					),
					mergeMap((differences) =>
						from(differences).pipe(
							filter(
								(val) =>
									val.ignoreType !==
									ignoreType.DELETED_AND_DNE_ON_DESTINATION
							),
							filter(
								(val) =>
									val.artId === structure.id ||
									val.artIdB === structure.id ||
									val.itemId === structure.id ||
									structure.elements
										?.map((a) => a.id)
										.some(
											(el) =>
												el === val.artId ||
												el === val.artIdB ||
												el === val.itemId
										) ||
									false ||
									(typeof val.itemTypeId === 'object' &&
										'id' in val.itemTypeId &&
										val.itemTypeId.id ===
											RelationTypeId.INTERFACESTRUCTURECONTENT)
							),
							mergeMap((change) =>
								iif(
									() =>
										change.changeType.id ===
										changeTypeNumber.ARTIFACT_CHANGE,
									iif(
										() =>
											change.currentVersion.modType ===
												ModificationType.NEW &&
											change.baselineVersion.modType ===
												ModificationType.NONE &&
											change.destinationVersion
												.modType ===
												ModificationType.NONE &&
											!change.deleted,
										iif(
											() => change.artId === structure.id,
											of(structure).pipe(
												map((val) => {
													structure =
														this._structureChangeSetup(
															structure
														);
													structure.applicability =
														change.currentVersion
															.applicabilityToken as applic;
													(
														structure as structureWithChanges
													).changes.applicability = {
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
													).added = true;
													return structure as structureWithChanges;
												})
											),
											iif(
												() =>
													structure.elements
														?.map((a) => a.id)
														.includes(
															change.artId
														) || false,
												of(structure).pipe(
													map((val) => {
														let index =
															structure.elements?.findIndex(
																(el) =>
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
															structure.elements[
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
															structure.elements[
																index
															] as elementWithChanges
														).added = true;
														(
															structure as structureWithChanges
														).hasElementChanges =
															true;
														return structure as structureWithChanges;
													})
												),
												of()
											) //check if in element array, and mark as added, else check type of object (structure/element) and mark as deleted
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
												() =>
													!change.deleted &&
													structure.elements
														?.map((a) => a.id)
														.includes(change.artId),
												of(structure).pipe(
													map((val) => {
														let index =
															structure.elements?.findIndex(
																(el) =>
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
															structure.elements[
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
													})
												),
												of()
											),
											of()
										) //deleted/changed
									),
									iif(
										() =>
											change.changeType.id ===
											changeTypeNumber.ATTRIBUTE_CHANGE,
										iif(
											() => change.artId === structure.id,
											iif(
												() =>
													change.itemTypeId ===
													ATTRIBUTETYPEIDENUM.DESCRIPTION,
												of(structure).pipe(
													map((structure) => {
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
													})
												),
												iif(
													() =>
														change.itemTypeId ===
														ATTRIBUTETYPEIDENUM.NAME,
													of(structure).pipe(
														map((structure) => {
															structure =
																this._structureChangeSetup(
																	structure
																);
															(
																structure as structureWithChanges
															).changes.name = {
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
														})
													),
													iif(
														() =>
															change.itemTypeId ===
															ATTRIBUTETYPEIDENUM.INTERFACEMAXSIMULTANEITY,
														of(structure).pipe(
															map((structure) => {
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
															})
														),
														iif(
															() =>
																change.itemTypeId ===
																ATTRIBUTETYPEIDENUM.INTERFACEMINSIMULTANEITY,
															of(structure).pipe(
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
											iif(
												() =>
													structure.elements
														?.map((a) => a.id)
														.includes(change.artId),
												iif(
													() =>
														change.itemTypeId ===
														ATTRIBUTETYPEIDENUM.DESCRIPTION,
													of(structure).pipe(
														map((structure) => {
															let index =
																structure.elements?.findIndex(
																	(el) =>
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
															(
																structure as structureWithChanges
															).hasElementChanges =
																true;
															return structure as structureWithChanges;
														})
													),
													iif(
														() =>
															change.itemTypeId ===
															ATTRIBUTETYPEIDENUM.NAME,
														of(structure).pipe(
															map((structure) => {
																let index =
																	structure.elements?.findIndex(
																		(el) =>
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
																(
																	structure as structureWithChanges
																).hasElementChanges =
																	true;
																return structure as structureWithChanges;
															})
														),
														iif(
															() =>
																change.itemTypeId ===
																ATTRIBUTETYPEIDENUM.INTERFACEELEMENTALTERABLE,
															of(structure).pipe(
																map(
																	(
																		structure
																	) => {
																		let index =
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
																		(
																			structure as structureWithChanges
																		).hasElementChanges =
																			true;
																		return structure as structureWithChanges;
																	}
																)
															),
															iif(
																() =>
																	change.itemTypeId ===
																	ATTRIBUTETYPEIDENUM.INTERFACEELEMENTEND,
																of(
																	structure
																).pipe(
																	map(
																		(
																			structure
																		) => {
																			let index =
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
																			(
																				structure as structureWithChanges
																			).hasElementChanges =
																				true;
																			return structure as structureWithChanges;
																		}
																	)
																),
																iif(
																	() =>
																		change.itemTypeId ===
																		ATTRIBUTETYPEIDENUM.INTERFACEELEMENTSTART,
																	of(
																		structure
																	).pipe(
																		map(
																			(
																				structure
																			) => {
																				let index =
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
																				(
																					structure as structureWithChanges
																				).hasElementChanges =
																					true;
																				return structure as structureWithChanges;
																			}
																		)
																	),
																	iif(
																		() =>
																			change.itemTypeId ===
																			ATTRIBUTETYPEIDENUM.INTERFACEENUMLITERAL,
																		of(
																			structure
																		).pipe(
																			map(
																				(
																					structure
																				) => {
																					let index =
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
																					(
																						structure as structureWithChanges
																					).hasElementChanges =
																						true;
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
												iif(
													() =>
														structure.elements
															?.map((a) =>
																a.platformTypeId?.toString()
															)
															.includes(
																change.artId
															),
													iif(
														() =>
															change.itemTypeId ===
															ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
														of(structure).pipe(
															map((structure) => {
																let index =
																	structure.elements?.findIndex(
																		(el) =>
																			el.platformTypeId?.toString() ===
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
																(
																	structure as structureWithChanges
																).hasElementChanges =
																	true;
																return structure as structureWithChanges;
															})
														),
														iif(
															() =>
																change.itemTypeId ===
																ATTRIBUTETYPEIDENUM.NAME,
															of(structure).pipe(
																map(
																	(
																		structure
																	) => {
																		let index =
																			structure.elements?.findIndex(
																				(
																					el
																				) =>
																					el.platformTypeId?.toString() ===
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
																		(
																			structure as structureWithChanges
																		).hasElementChanges =
																			true;
																		return structure as structureWithChanges;
																	}
																)
															),
															of(structure)
														)
													),
													of(structure)
												)
											) //element has changed attributes
										),
										iif(
											() =>
												change.changeType.id ===
												changeTypeNumber.RELATION_CHANGE,
											iif(
												() =>
													typeof change.itemTypeId ===
														'object' &&
													'id' in change.itemTypeId &&
													change.itemTypeId.id ===
														RelationTypeId.INTERFACESUBMESSAGECONTENT &&
													change.artIdB ===
														structure.id,
												of(change), //mark structure as added/deleted
												iif(
													() =>
														typeof change.itemTypeId ===
															'object' &&
														'id' in
															change.itemTypeId &&
														change.itemTypeId.id ===
															RelationTypeId.INTERFACESTRUCTURECONTENT &&
														change.artId ===
															structure.id,
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
															structure.elements
																?.map(
																	(a) => a.id
																)
																.includes(
																	change.artIdB
																),
														of(change).pipe(
															map(() => {
																let index =
																	structure.elements?.findIndex(
																		(el) =>
																			el.id ===
																			change.artIdB
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
																(
																	structure
																		.elements[
																		index
																	] as elementWithChanges
																).added = true;
																return structure as structureWithChanges;
															})
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
															this.elements
																.getElement(
																	parentBranch,
																	message,
																	submessage,
																	structure.id,
																	change.artIdB,
																	connection
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
																			};
																		}
																	),
																	map(
																		(
																			element
																		) => {
																			structure.elements.push(
																				{
																					...element,
																					deleted:
																						true,
																				}
																			);
																			structure.numElements++;
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
																			return structure as structureWithChanges;
																		}
																	)
																),
															of(change)
														)
													), //check if an element relation changed on specific structure id
													iif(
														() =>
															typeof change.itemTypeId ===
																'object' &&
															'id' in
																change.itemTypeId &&
															change.itemTypeId
																.id ===
																RelationTypeId.INTERFACEELEMENTPLATFORMTYPE &&
															structure.elements
																?.map(
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
																	ModificationType.NONE,
															this.typeService
																.getTypeFromBranch(
																	branch,
																	change.artIdB
																)
																.pipe(
																	map(
																		(
																			type
																		) => {
																			let index =
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
																			if (
																				(
																					structure
																						.elements[
																						index
																					] as elementWithChanges
																				)
																					.changes
																					.platformTypeName2 ===
																				undefined
																			) {
																				(
																					structure
																						.elements[
																						index
																					] as elementWithChanges
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
																					structure
																						.elements[
																						index
																					] as elementWithChanges
																				)
																					.changes
																					.platformTypeName2 !==
																					undefined &&
																				(
																					structure
																						.elements[
																						index
																					] as elementWithChanges
																				)
																					.changes
																					.platformTypeName2
																					?.currentValue !==
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					)
																						.platformTypeName2
																			) {
																				(
																					structure
																						.elements[
																						index
																					] as elementWithChanges
																				).changes.platformTypeName2!.currentValue =
																					type.name;
																				(
																					structure
																						.elements[
																						index
																					] as elementWithChanges
																				).changes.platformTypeName2!.transactionToken =
																					change.currentVersion.transactionToken;
																			}
																			(
																				structure as structureWithChanges
																			).hasElementChanges =
																				true;
																			return structure as structureWithChanges;
																		}
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
																this.typeService
																	.getTypeFromBranch(
																		branch,
																		change.artIdB
																	)
																	.pipe(
																		map(
																			(
																				type
																			) => {
																				let index =
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
																				if (
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					)
																						.changes
																						.platformTypeName2 ===
																					undefined
																				) {
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
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
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					)
																						.changes
																						.platformTypeName2 !==
																						undefined &&
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					)
																						.changes
																						.platformTypeName2
																						?.currentValue !==
																						(
																							structure
																								.elements[
																								index
																							] as elementWithChanges
																						)
																							.platformTypeName2
																				) {
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					).changes.platformTypeName2!.previousValue =
																						type.name;
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					).changes.platformTypeName2!.transactionToken =
																						change.currentVersion.transactionToken;
																				} else {
																					(
																						structure
																							.elements[
																							index
																						] as elementWithChanges
																					).changes.platformTypeName2!.previousValue =
																						type.name;
																				}
																				(
																					structure as structureWithChanges
																				).hasElementChanges =
																					true;
																				return structure as structureWithChanges;
																			}
																		)
																	),
																of()
															)
														),
														of()
													)
												)
											),
											iif(
												() =>
													change.changeType.id ===
													changeTypeNumber.TUPLE_CHANGE,
												//these should be ignored
												of(),
												of()
											)
										)
									)
								)
							),
							// tap((val) => {
							//   console.log(val)
							//   console.log(structure)
							// }),
							count()
						)
					)
				)
			),

			switchMap((val) =>
				of(structure as structure | structureWithChanges)
			)
		);
	}
}
