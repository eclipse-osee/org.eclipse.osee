/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import {
	filter,
	map,
	reduce,
	shareReplay,
	startWith,
	switchMap,
	take,
	takeUntil,
	tap,
} from 'rxjs/operators';
import {
	BehaviorSubject,
	combineLatest,
	from,
	iif,
	of,
	OperatorFunction,
	Subject,
} from 'rxjs';
import { ImportHttpService } from './import-http.service';
import {
	ImportOption,
	nodeToken,
	ImportSummary,
	subMessage,
	enumSet,
	node,
	structure,
	message,
	ConnectionService,
	ElementService,
	EnumerationSetService,
	MessagesService,
	NodeService,
	StructuresService,
	SubMessagesService,
	TypesService,
} from '@osee/messaging/shared';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';
import { TransactionService } from '@osee/shared/transactions';
import { transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ImportService {
	constructor(
		private uiService: BranchUIService,
		private importHttpService: ImportHttpService,
		private nodeService: NodeService,
		private connectionService: ConnectionService,
		private messagesService: MessagesService,
		private subMessageService: SubMessagesService,
		private structureService: StructuresService,
		private elementService: ElementService,
		private typesService: TypesService,
		private enumSetService: EnumerationSetService,
		private transactionService: TransactionService
	) {}

	private _done$ = new Subject();
	private _importFile$: BehaviorSubject<File | undefined> =
		new BehaviorSubject<File | undefined>(undefined);
	private _selectedImportOption$: BehaviorSubject<ImportOption | undefined> =
		new BehaviorSubject<ImportOption | undefined>(undefined);
	private _importSuccess$: BehaviorSubject<boolean | undefined> =
		new BehaviorSubject<boolean | undefined>(undefined);
	private _importInProgress$: BehaviorSubject<boolean> =
		new BehaviorSubject<boolean>(false);

	private _importSummary$ = combineLatest([
		this.branchId,
		this._selectedImportOption$,
		this._importFile$,
		this._importInProgress$,
	]).pipe(
		switchMap(([branchId, importOption, file, inProgress]) =>
			iif(
				() =>
					importOption !== undefined &&
					file !== undefined &&
					inProgress,
				of(new FormData()).pipe(
					tap((formData) => {
						formData.append('file', new Blob([file!]), file?.name);
					}),
					switchMap((formData) =>
						this.importHttpService.getImportSummary(
							importOption!.url.replace('<branchId>', branchId),
							formData
						)
					)
				),
				of(undefined)
			)
		),
		filter((v) => v !== undefined) as OperatorFunction<
			ImportSummary | undefined,
			ImportSummary
		>,
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _importTx$ = this.branchId.pipe(
		switchMap((branchId) =>
			of<transaction>({
				branch: branchId,
				txComment: 'MIM Import',
				createArtifacts: [],
				addRelations: [],
			})
		)
	);

	private _connectionId$ = this._importSummary$.pipe(
		switchMap((summary) =>
			summary.connectionId === '-1'
				? of('connection')
				: of(summary.connectionId)
		)
	);

	private _nodes$ = this._importSummary$.pipe(
		switchMap((summary) =>
			iif(
				() =>
					summary?.primaryNode !== null &&
					summary?.secondaryNode != null,
				of([summary!.primaryNode, summary!.secondaryNode]),
				of([] as nodeToken[])
			)
		)
	);

	private _messages$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.messages))
	);

	private _subMessages$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.subMessages))
	);

	private _structures$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.structures))
	);

	private _elements$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.elements))
	);

	private _platformTypes$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.platformTypes))
	);

	private _enumSets$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.enumSets))
	);

	private _enums$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.enums))
	);

	private _messageSubMessageRelations$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.messageSubmessageRelations))
	);

	private _subMessageStructureRelations$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.subMessageStructureRelations))
	);

	private _structureElementRelations$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.structureElementRelations))
	);

	private _elementPlatformTypeRelations$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.elementPlatformTypeRelations))
	);

	private _platformTypeEnumSetRelations$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.platformTypeEnumSetRelations))
	);

	private _enumSetEnumRelations$ = this._importSummary$.pipe(
		switchMap((summary) => of(summary.enumSetEnumRelations))
	);

	private _nodesTx$ = combineLatest([
		this._importSummary$,
		this._importTx$,
		this._nodes$,
		this._connectionId$,
		this.branchId,
	]).pipe(
		switchMap(([summary, tx, nodes, connectionId, branchId]) =>
			of(nodes).pipe(
				switchMap((nodes) =>
					from(nodes).pipe(
						switchMap((node, index) =>
							of(node).pipe(
								filter((n) => n.name !== ''),
								tap((node) =>
									(index % 2 === 1 &&
										summary.createSecondaryNode) ||
									(index % 2 === 0 &&
										summary.createPrimaryNode)
										? this.nodeService.createNode(
												branchId,
												{ name: node.name } as node,
												tx,
												node.id
										  )
										: ''
								),
								switchMap((node) =>
									summary.createPrimaryNode ||
									summary.createSecondaryNode
										? this.connectionService
												.createNodeRelation(
													node.id!,
													index % 2 === 1,
													connectionId
												)
												.pipe(
													switchMap((nodeRelation) =>
														this.connectionService.addRelation(
															branchId,
															nodeRelation,
															tx
														)
													)
												)
										: ''
								)
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _connectionTx$ = combineLatest([
		this._importTx$,
		this.selectedImportOption,
		this._nodes$,
		this._connectionId$,
		this.branchId,
	]).pipe(
		switchMap(([tx, option, nodes, connectionId, branchId]) =>
			of(nodes).pipe(
				switchMap((nodes) =>
					nodes.length === 2 &&
					nodes[0].name !== '' &&
					nodes[1].name !== '' &&
					connectionId === 'connection'
						? this.connectionService
								.createConnectionNoRelations(
									branchId,
									{
										name:
											nodes[0].name + '_' + nodes[1].name,
										description: '',
									},
									tx,
									connectionId
								)
								.pipe(
									switchMap((connectionTx) =>
										iif(
											() =>
												option?.transportType !==
													undefined &&
												option.transportType !== '',
											this.connectionService
												.createTransportTypeRelation(
													option?.transportType || '',
													connectionId
												)
												.pipe(
													switchMap((rel) =>
														this.connectionService.addRelation(
															branchId,
															rel,
															connectionTx
														)
													)
												),
											of(tx)
										)
									)
								)
						: of(tx)
				)
			)
		)
	);

	private _messagesTx$ = combineLatest([
		this._importTx$,
		this._messages$,
		this._connectionId$,
		this.branchId,
	]).pipe(
		switchMap(([tx, messages, connectionId, branchId]) =>
			of(messages).pipe(
				switchMap((msgs) =>
					from(msgs).pipe(
						switchMap((message) =>
							of(message).pipe(
								map(
									(m) =>
										({
											id: m.id,
											name: m.name,
											description: m.description,
											interfaceMessageRate:
												m.interfaceMessageRate,
											interfaceMessagePeriodicity:
												m.interfaceMessagePeriodicity,
											interfaceMessageWriteAccess:
												m.interfaceMessageWriteAccess,
											interfaceMessageType:
												m.interfaceMessageType,
											interfaceMessageNumber:
												m.interfaceMessageNumber,
										} as message)
								),
								switchMap((msg) =>
									this.messagesService.createMessage(
										branchId,
										msg,
										[],
										tx,
										msg.id
									)
								),
								switchMap((_) =>
									this.messagesService
										.createConnectionRelation(
											connectionId,
											message.id
										)
										.pipe(
											switchMap((relation) =>
												this.messagesService.addRelation(
													branchId,
													relation,
													tx
												)
											)
										)
								),
								switchMap((_) =>
									this.messagesService
										.createNodeRelation(
											message.id,
											message.initiatingNode.id
										)
										.pipe(
											switchMap((relation) =>
												this.messagesService.addRelation(
													branchId,
													relation,
													tx
												)
											)
										)
								)
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _subMessagesTx$ = combineLatest([
		this._importTx$,
		this._subMessages$,
		this.branchId,
	]).pipe(
		switchMap(([tx, subMessages, branchId]) =>
			of(subMessages).pipe(
				switchMap((subMsgs) =>
					from(subMsgs).pipe(
						map(
							(s) =>
								({
									id: s.id,
									name: s.name,
									description: s.description,
									interfaceSubMessageNumber:
										s.interfaceSubMessageNumber,
								} as subMessage)
						),
						switchMap((subMessage) =>
							this.subMessageService.createSubMessage(
								branchId,
								subMessage,
								[],
								tx,
								subMessage.id
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _structuresTx$ = combineLatest([
		this._importTx$,
		this._structures$,
		this.branchId,
	]).pipe(
		switchMap(([tx, structures, branchId]) =>
			of(structures).pipe(
				switchMap((structs) =>
					from(structs).pipe(
						map(
							(s) =>
								({
									id: s.id,
									name: s.name,
									description: s.description,
									interfaceMaxSimultaneity:
										s.interfaceMaxSimultaneity,
									interfaceMinSimultaneity:
										s.interfaceMinSimultaneity,
									interfaceTaskFileType:
										s.interfaceTaskFileType,
									interfaceStructureCategory:
										s.interfaceStructureCategory,
								} as structure)
						),
						switchMap((structure) =>
							this.structureService.createStructure(
								structure,
								branchId,
								[],
								tx,
								structure.id
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _elementsTx$ = combineLatest([
		this._importTx$,
		this._elements$,
		this.branchId,
	]).pipe(
		switchMap(([tx, elements, branchId]) =>
			of(elements).pipe(
				switchMap((elements) =>
					from(elements).pipe(
						switchMap((element) =>
							this.elementService.createElement(
								element,
								branchId,
								[],
								tx,
								element.id
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _platformTypesTx$ = combineLatest([
		this._importTx$,
		this._platformTypes$,
		this.branchId,
	]).pipe(
		switchMap(([tx, pTypes, branchId]) =>
			of(pTypes).pipe(
				switchMap((pTypes) =>
					from(pTypes).pipe(
						switchMap((pType) =>
							this.typesService.createPlatformType(
								branchId,
								pType,
								[],
								tx,
								pType.id
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _enumSetTx$ = combineLatest([
		this._importTx$,
		this._enumSets$,
		this.branchId,
	]).pipe(
		switchMap(([tx, enumSets, branchId]) =>
			of(enumSets).pipe(
				switchMap((enumSets) =>
					from(enumSets).pipe(
						map(
							(e) =>
								({
									id: e.id,
									name: e.name,
									applicability: e.applicability,
									description: e.description,
								} as enumSet)
						),
						switchMap((enumSet) =>
							this.enumSetService.createEnumSet(
								branchId,
								enumSet,
								[],
								tx,
								enumSet.id
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _enumsTx$ = combineLatest([
		this._importTx$,
		this._enums$,
		this.branchId,
	]).pipe(
		switchMap(([tx, enums, branchId]) =>
			of(enums).pipe(
				switchMap((enums) =>
					from(enums).pipe(
						switchMap((enumeration) =>
							this.enumSetService.createEnum(
								branchId,
								enumeration,
								[],
								tx,
								enumeration.id
							)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _messageSubMessageRelationsTx$ = combineLatest([
		this._importTx$,
		this._messageSubMessageRelations$,
		this.branchId,
	]).pipe(
		switchMap(([tx, relations, branchId]) =>
			from(Object.keys(relations)).pipe(
				switchMap((msgId) =>
					from(relations[msgId]).pipe(
						switchMap((subMsgId) =>
							this.subMessageService
								.createMessageRelation(msgId, subMsgId)
								.pipe(
									switchMap((relation) =>
										this.subMessageService.addRelation(
											branchId,
											relation,
											tx
										)
									)
								)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _subMessageStructureRelationsTx$ = combineLatest([
		this._importTx$,
		this._subMessageStructureRelations$,
		this.branchId,
	]).pipe(
		switchMap(([tx, relations, branchId]) =>
			from(Object.keys(relations)).pipe(
				switchMap((subMsgId) =>
					from(relations[subMsgId]).pipe(
						switchMap((structureId) =>
							this.structureService
								.createSubMessageRelation(subMsgId, structureId)
								.pipe(
									switchMap((relation) =>
										this.structureService.addRelation(
											branchId,
											relation,
											tx
										)
									)
								)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _structureElementRelationsTx$ = combineLatest([
		this._importTx$,
		this._structureElementRelations$,
		this.branchId,
	]).pipe(
		switchMap(([tx, relations, branchId]) =>
			from(Object.keys(relations)).pipe(
				switchMap((structureId) =>
					from(relations[structureId]).pipe(
						switchMap((elementId) =>
							this.elementService
								.createStructureRelation(structureId, elementId)
								.pipe(
									switchMap((relation) =>
										this.elementService.addRelation(
											branchId,
											relation,
											tx
										)
									)
								)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _elementPlatformTypeRelationsTx$ = combineLatest([
		this._importTx$,
		this._elementPlatformTypeRelations$,
		this.branchId,
	]).pipe(
		switchMap(([tx, relations, branchId]) =>
			from(Object.keys(relations)).pipe(
				switchMap((elementId) =>
					from(relations[elementId]).pipe(
						switchMap((pTypeId) =>
							this.elementService
								.createPlatformTypeRelation(pTypeId, elementId)
								.pipe(
									switchMap((relation) =>
										this.elementService.addRelation(
											branchId,
											relation,
											tx
										)
									)
								)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _platformTypeEnumSetRelationsTx$ = combineLatest([
		this._importTx$,
		this._platformTypeEnumSetRelations$,
		this.branchId,
	]).pipe(
		switchMap(([tx, relations, branchId]) =>
			from(Object.keys(relations)).pipe(
				switchMap((pTypeId) =>
					from(relations[pTypeId]).pipe(
						switchMap((enumSetId) =>
							this.enumSetService
								.createPlatformTypeToEnumSetRelation(
									enumSetId,
									pTypeId
								)
								.pipe(
									switchMap((relation) =>
										this.enumSetService.addRelation(
											branchId,
											relation,
											tx
										)
									)
								)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	private _enumSetEnumRelationsTx$ = combineLatest([
		this._importTx$,
		this._enumSetEnumRelations$,
		this.branchId,
	]).pipe(
		switchMap(([tx, relations, branchId]) =>
			from(Object.keys(relations)).pipe(
				switchMap((enumSetId) =>
					from(relations[enumSetId]).pipe(
						switchMap((enumId) =>
							this.enumSetService
								.createEnumToEnumSetRelation(enumSetId, enumId)
								.pipe(
									switchMap((relation) =>
										this.enumSetService.addRelation(
											branchId,
											relation,
											tx
										)
									)
								)
						)
					)
				),
				reduce(() => tx, tx),
				startWith(tx)
			)
		)
	);

	// The last parameter of the below combineLatest is getting lost and not making it into the final combined tx. Having this placeholder as the last parameter causes just the placeholder to get lost instead.
	private _placeholderTx$ = this._importTx$;

	// combineLatest can only support up to 6 observables, so we need multiple combineLatest
	private _combined1$ = combineLatest([
		this._nodesTx$,
		this._connectionTx$,
		this._messagesTx$,
		this._subMessagesTx$,
		this._structuresTx$,
		this._elementsTx$,
	]);
	private _combined2$ = combineLatest([
		this._platformTypesTx$,
		this._enumSetTx$,
		this._enumsTx$,
		this._messageSubMessageRelationsTx$,
		this._subMessageStructureRelationsTx$,
	]);
	private _combined3$ = combineLatest([
		this._structureElementRelationsTx$,
		this._elementPlatformTypeRelationsTx$,
		this._platformTypeEnumSetRelationsTx$,
		this._enumSetEnumRelationsTx$,
		this._placeholderTx$,
	]);

	private _combineTxs(branchId: string, ...txs: transaction[]) {
		const importTx: transaction = {
			branch: branchId,
			txComment: 'MIM Import',
			createArtifacts: [],
			addRelations: [],
		};
		return from(txs).pipe(
			startWith(importTx),
			reduce((curr, acc) => {
				acc.addRelations?.push(...curr.addRelations!);
				acc.createArtifacts?.push(...curr.createArtifacts!);
				return acc;
			}, importTx)
		);
	}

	private _sendTransaction$ = combineLatest([
		this.branchId,
		this._combined1$,
		this._combined2$,
		this._combined3$,
	]).pipe(
		take(1),
		switchMap(([branchId, combined1, combined2, combined3]) =>
			this._combineTxs(
				branchId,
				...combined1,
				...combined2,
				...combined3
			).pipe(
				switchMap((importTx) =>
					this.transactionService.performMutation(importTx).pipe(
						tap((res) => {
							this.ImportSuccess = res.results.success;
							this.ImportInProgress = false;
						})
					)
				)
			)
		),
		takeUntil(this._done$)
	);

	performImport() {
		this.sendTransaction
			.pipe(
				take(1),
				switchMap((_) =>
					of().pipe(tap((_) => (this.toggleDone = true)))
				)
			)
			.subscribe();
	}

	reset() {
		this.ImportFile = undefined;
		this.ImportSuccess = undefined;
		this.SelectedImportOption = undefined;
		this.ImportInProgress = false;
		this.toggleDone = true;
	}

	get branchId() {
		return this.uiService.id;
	}

	set BranchId(value: string) {
		this.uiService.idValue = value;
	}

	get branchType() {
		return this.uiService.type;
	}

	get importFile() {
		return this._importFile$.asObservable();
	}

	set ImportFile(importFile: File | undefined) {
		this._importFile$.next(importFile);
	}

	get selectedImportOption() {
		return this._selectedImportOption$.asObservable();
	}

	set SelectedImportOption(importOption: ImportOption | undefined) {
		this._selectedImportOption$.next(importOption);
	}

	get importSummary() {
		return this._importSummary$;
	}

	set toggleDone(done: unknown) {
		this._done$.next(done);
		this._done$.complete();
	}

	get importSuccess() {
		return this._importSuccess$;
	}

	set ImportSuccess(value: boolean | undefined) {
		this._importSuccess$.next(value);
	}

	get importInProgress() {
		return this._importInProgress$;
	}

	set ImportInProgress(value: boolean) {
		this._importInProgress$.next(value);
	}

	get sendTransaction() {
		return this._sendTransaction$;
	}

	get importOptions() {
		return this.importHttpService.getImportOptions();
	}
}
