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
import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
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
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import {
	Observable,
	OperatorFunction,
	combineLatest,
	count,
	debounceTime,
	filter,
	from,
	iif,
	map,
	mergeMap,
	of,
	repeat,
	switchMap,
} from 'rxjs';
import { CurrentStructureService } from './current-structure.service';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class CurrentStructureSingleService extends CurrentStructureService {
	get structuresCount(): Observable<number> {
		return of(1);
	}
	private _filter = toObservable(this.ui.filter);
	private _structures = combineLatest([
		this.BranchId,
		this.branchInfoService.parentBranch,
		this.MessageId,
		this._submessageId,
		this.connectionId,
		this.ui.viewId,
		this._filter,
		this.singleStructureId,
	]).pipe(
		filter(
			([
				branchId,
				_parentBranch,
				messageId,
				subMessageId,
				connectionId,
				_viewId,
				_filterString,
				structureId,
			]) =>
				branchId !== '' &&
				messageId !== '' &&
				subMessageId !== '-1' &&
				connectionId !== '-1' &&
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
		return of(1);
	}

	/**
	 * no-op implementation
	 */
	//eslint-disable-next-line @typescript-eslint/no-empty-function
	set page(page: number) {}
	//eslint-disable-next-line @typescript-eslint/no-empty-function
	returnToFirstPage() {}
	get currentPageSize(): Observable<number> {
		return of(1);
	}
	//eslint-disable-next-line @typescript-eslint/no-empty-function
	set pageSize(page: number) {}
	get structures() {
		return this._structures;
	}

	private _parseDifferences(
		differences: changeInstance[] | undefined,
		_oldStructure: structure,
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
											RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT)
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
												map((_) => {
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
														previousValue: change
															.baselineVersion
															.applicabilityToken as applic,

														currentValue: change
															.currentVersion
															.applicabilityToken as applic,
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
															change.artId as `${number}`
														) || false,
												of(structure).pipe(
													map((_) => {
														const index =
															structure.elements?.findIndex(
																(el) =>
																	el.id ===
																	change.artId
															);
														let element =
															structure.elements[
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
														element.added = true;
														structure.elements[
															index
														] = element;
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
														.includes(
															change.artId as `${number}`
														),
												of(structure).pipe(
													map((_) => {
														const index =
															structure.elements?.findIndex(
																(el) =>
																	el.id ===
																	change.artId
															);
														let element =
															structure.elements[
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
														] = element;
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
																previousValue: {
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

																currentValue: {
																	id: structure
																		.description
																		.id,
																	typeId: structure
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
																previousValue: {
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
																currentValue: {
																	id: structure
																		.name
																		.id,
																	typeId: structure
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
														.includes(
															change.artId as `${number}`
														),
												iif(
													() =>
														change.itemTypeId ===
														ATTRIBUTETYPEIDENUM.DESCRIPTION,
													of(structure).pipe(
														map((structure) => {
															const index =
																structure.elements?.findIndex(
																	(el) =>
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
																element.changes.description =
																	{
																		previousValue:
																			{
																				id: element
																					.description
																					.id,
																				typeId: element
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
																				id: element
																					.description
																					.id,
																				typeId: element
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
															structure.elements[
																index
															] = element;
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
																const index =
																	structure.elements?.findIndex(
																		(el) =>
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
																	element.changes.name =
																		{
																			previousValue:
																				{
																					id: element
																						.name
																						.id,
																					typeId: element
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
																					id: element
																						.name
																						.id,
																					typeId: element
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
																structure.elements[
																	index
																] = element;
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
																			element.changes.interfaceElementAlterable =
																				{
																					previousValue:
																						{
																							id: element
																								.interfaceElementAlterable
																								.id,
																							typeId: element
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
																							id: element
																								.interfaceElementAlterable
																								.id,
																							typeId: element
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
																				element.changes.interfaceElementIndexEnd =
																					{
																						previousValue:
																							{
																								id: element
																									.interfaceElementIndexEnd
																									.id,
																								typeId: element
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
																								id: element
																									.interfaceElementIndexEnd
																									.id,
																								typeId: element
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
																					element.changes.interfaceElementIndexStart =
																						{
																							previousValue:
																								{
																									id: element
																										.interfaceElementIndexStart
																										.id,
																									typeId: element
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
																									id: element
																										.interfaceElementIndexStart
																										.id,
																									typeId: element
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
																						element.changes.enumLiteral =
																							{
																								previousValue:
																									{
																										id: element
																											.enumLiteral
																											.id,
																										typeId: element
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
																										id: element
																											.enumLiteral
																											.id,
																										typeId: element
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
																a.platformType.id?.toString()
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
																const index =
																	structure.elements?.findIndex(
																		(el) =>
																			el.platformType.id?.toString() ===
																			change.artId
																	);
																const element =
																	structure
																		.elements[
																		index
																	];
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
																						...new PlatformTypeSentinel(),
																						interfacePlatformTypeUnits:
																							{
																								id: '-1',
																								value: '',
																								typeId: '4026643196432874344',
																								gammaId:
																									'-1',
																							},
																					},
																				currentValue:
																					{
																						...new PlatformTypeSentinel(),
																						interfacePlatformTypeUnits:
																							{
																								id: change.itemId as `${number}`,
																								value: change
																									.currentVersion
																									.value as string,
																								typeId: change.itemTypeId as typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
																								gammaId:
																									change
																										.currentVersion
																										.gammaId as `${number}`,
																							},
																					},
																				transactionToken:
																					change
																						.currentVersion
																						.transactionToken,
																			};
																	} else {
																		element.changes.platformType.previousValue.interfacePlatformTypeUnits =
																			{
																				id: element
																					.changes
																					.platformType
																					.previousValue
																					.interfacePlatformTypeUnits
																					.id,
																				typeId: element
																					.changes
																					.platformType
																					.previousValue
																					.interfacePlatformTypeUnits
																					.typeId,
																				gammaId:
																					change
																						.baselineVersion
																						.gammaId as `${number}`,
																				value: change
																					.baselineVersion
																					.value as string,
																			};
																		element.changes.platformType.currentValue.interfacePlatformTypeUnits =
																			{
																				id: element
																					.changes
																					.platformType
																					.previousValue
																					.interfacePlatformTypeUnits
																					.id,
																				typeId: element
																					.changes
																					.platformType
																					.previousValue
																					.interfacePlatformTypeUnits
																					.typeId,
																				gammaId:
																					change
																						.currentVersion
																						.gammaId as `${number}`,
																				value: change
																					.currentVersion
																					.value as string,
																			};
																		element.changes.platformType.transactionToken =
																			change.currentVersion.transactionToken;
																	}
																}
																structure.hasElementChanges =
																	true;
																structure.elements[
																	index
																] = element;
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
																		const index =
																			structure.elements?.findIndex(
																				(
																					el
																				) =>
																					el.platformType.id?.toString() ===
																					change.artId
																			);
																		let el =
																			structure
																				.elements[
																				index
																			];
																		el =
																			this._elementChangeSetup(
																				structure
																					.elements[
																					index
																				]
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
																		structure.elements[
																			index
																		] = el;
																		structure.hasElementChanges =
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
														RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT &&
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
															RELATIONTYPEIDENUM.INTERFACESTRUCTURECONTENT &&
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
																	change.artIdB as `${number}`
																),
														of(change).pipe(
															map(() => {
																const index =
																	structure.elements?.findIndex(
																		(el) =>
																			el.id ===
																			change.artIdB
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
																element.added =
																	true;
																structure.hasElementChanges =
																	true;
																structure.elements[
																	index
																] = element;
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
																								{
																									id: initialEl
																										.name
																										.id,
																									typeId: initialEl
																										.name
																										.typeId,
																									gammaId:
																										'-1' as const,
																									value: '',
																								},
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
																									{
																										id: initialEl
																											.description
																											.id,
																										typeId: initialEl
																											.description
																											.typeId,
																										gammaId:
																											'-1' as const,
																										value: '',
																									},
																								transactionToken:
																									change
																										.currentVersion
																										.transactionToken,
																							},
																						notes: {
																							previousValue:
																								initialEl.notes,
																							currentValue:
																								{
																									id: initialEl
																										.notes
																										.id,
																									typeId: initialEl
																										.notes
																										.typeId,
																									gammaId:
																										'-1' as const,
																									value: '',
																								},
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
																									{
																										id: initialEl
																											.interfaceElementIndexEnd
																											.id,
																										typeId: initialEl
																											.interfaceElementIndexEnd
																											.typeId,
																										gammaId:
																											'-1' as const,
																										value: 0,
																									},
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
																									{
																										id: initialEl
																											.interfaceElementIndexStart
																											.id,
																										typeId: initialEl
																											.interfaceElementIndexStart
																											.typeId,
																										gammaId:
																											'-1' as const,
																										value: 0,
																									},
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
																									{
																										id: initialEl
																											.interfaceElementAlterable
																											.id,
																										typeId: initialEl
																											.interfaceElementAlterable
																											.typeId,
																										gammaId:
																											'-1' as const,
																										value: false,
																									},
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
																									{
																										id: initialEl
																											.enumLiteral
																											.id,
																										typeId: initialEl
																											.enumLiteral
																											.typeId,
																										gammaId:
																											'-1' as const,
																										value: '',
																									},
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
																									applicabilitySentinel,
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
																			deletedElement
																		) => {
																			const el: element =
																				{
																					...deletedElement,
																					deleted:
																						true,
																					added: false,
																				};
																			structure.elements.push(
																				el
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
																RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE &&
															structure.elements
																?.map(
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
																			structure.hasElementChanges =
																				true;
																			structure.elements[
																				index
																			] =
																				element;
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
																				structure.hasElementChanges =
																					true;
																				structure.elements[
																					index
																				] =
																					element;
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

			switchMap((_) => of(structure as structure | structureWithChanges))
		);
	}
}
