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
	concatMap,
	filter,
	map,
	reduce,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import {
	BehaviorSubject,
	combineLatest,
	from,
	iif,
	Observable,
	of,
	OperatorFunction,
	Subject,
} from 'rxjs';
import { ImportHttpService } from './import-http.service';
import {
	ConnectionService,
	ElementService,
	EnumerationSetService,
	MessagesService,
	NodeService,
	StructuresService,
	SubMessagesService,
	TypesService,
	CrossReferenceService,
} from '@osee/messaging/shared/services';
import type {
	ImportOption,
	ImportSummary,
	subMessage,
	enumSet,
	node,
	structure,
	message,
	connection,
	importRelationMap,
} from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { TransactionService } from '@osee/shared/transactions';
import { relation, transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ImportService {
	constructor(
		private uiService: UiService,
		private importHttpService: ImportHttpService,
		private nodeService: NodeService,
		private connectionService: ConnectionService,
		private messagesService: MessagesService,
		private subMessageService: SubMessagesService,
		private structureService: StructuresService,
		private elementService: ElementService,
		private typesService: TypesService,
		private enumSetService: EnumerationSetService,
		private crossRefService: CrossReferenceService,
		private transactionService: TransactionService
	) {}

	private _done$ = new Subject();
	private _importFile$: BehaviorSubject<File | undefined> =
		new BehaviorSubject<File | undefined>(undefined);
	private _selectedImportOption$: BehaviorSubject<ImportOption | undefined> =
		new BehaviorSubject<ImportOption | undefined>(undefined);
	private _importSuccess$: BehaviorSubject<boolean | undefined> =
		new BehaviorSubject<boolean | undefined>(undefined);
	private _importInProgress$: Subject<boolean> = new Subject<boolean>();
	private _selectedConnectionId = new BehaviorSubject<string>('');

	private _connections = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) => this.connectionService.getConnections(branchId))
	);

	private _importSummary$ = combineLatest([
		this.branchId,
		this._selectedImportOption$,
		this._importFile$,
		this._importInProgress$,
		this._selectedConnectionId,
	]).pipe(
		filter(
			([branchId, importOption, file, inProgress, connectionId]) =>
				importOption !== undefined && file !== undefined && inProgress
		),
		switchMap(
			([branchId, importOption, file, inProgress, connectionId]) => {
				if (file?.name.endsWith('.json')) {
					return this.importHttpService.getImportSummary(
						importOption!.url
							.replace('<branchId>', branchId)
							.replace('<connectionId>', connectionId),
						file.name,
						file
					);
				}
				if (
					file?.name.endsWith('.xlsx') ||
					file?.name.endsWith('.xls') ||
					file?.name.endsWith('.zip')
				) {
					return of(new FormData()).pipe(
						tap((formData) => {
							formData.append(
								'file',
								new Blob([file!]),
								file?.name
							);
						}),
						switchMap((formData) =>
							this.importHttpService.getImportSummary(
								importOption!.url.replace(
									'<branchId>',
									branchId
								),
								file.name,
								formData
							)
						)
					);
				}
				return of(undefined);
			}
		),
		tap(() => (this.ImportInProgress = false)),
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

	private _allImportTx$ = combineLatest([
		this._importTx$,
		this._importSummary$,
		this.selectedImportOption,
		this.branchId,
	]).pipe(
		switchMap(([initTx, summary, option, branchId]) =>
			of(initTx)
				.pipe(
					// Connections
					concatMap((tx) =>
						iif(
							() => summary.connections.length > 0,
							from(summary.connections).pipe(
								map(
									(connection) =>
										({
											id: connection.id,
											name: connection.name,
											description: connection.description,
											transportType:
												connection.transportType,
										} as connection)
								),
								// Create connection
								concatMap((connection) =>
									this.connectionService
										.createConnectionNoRelations(
											branchId,
											connection,
											tx,
											connection.id
										)
										.pipe(
											// Create connection <-> transport type relation
											concatMap((_) =>
												iif(
													() =>
														option?.transportType !==
															undefined &&
														option.transportType !==
															'',
													this.connectionService
														.createTransportTypeRelation(
															option!
																.transportType ||
																'',
															connection.id
														)
														.pipe(
															switchMap((rel) =>
																this.connectionService.addRelation(
																	branchId,
																	rel,
																	tx
																)
															)
														),
													of(tx)
												)
											)
										)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Nodes
					concatMap((tx) =>
						iif(
							() => summary.nodes.length > 0,
							from(summary.nodes).pipe(
								map(
									(node) =>
										({
											id: node.id,
											name: node.name,
											description: node.description,
										} as node)
								),
								concatMap((node) =>
									this.nodeService.createNode(
										branchId,
										node,
										tx,
										node.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Messages
					concatMap((tx) =>
						iif(
							() => summary.messages.length > 0,
							from(summary.messages).pipe(
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
								concatMap((msg) =>
									this.messagesService.createMessage(
										branchId,
										msg,
										[],
										tx,
										msg.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Submessages
					concatMap((tx) =>
						iif(
							() => summary.subMessages.length > 0,
							from(summary.subMessages).pipe(
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
								concatMap((subMessage) =>
									this.subMessageService.createSubMessage(
										branchId,
										subMessage,
										[],
										tx,
										subMessage.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Structures
					concatMap((tx) =>
						iif(
							() => summary.structures.length > 0,
							from(summary.structures).pipe(
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
								concatMap((structure) =>
									this.structureService.createStructure(
										structure,
										branchId,
										[],
										tx,
										structure.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Elements
					concatMap((tx) =>
						iif(
							() => summary.elements.length > 0,
							from(summary.elements).pipe(
								concatMap((element) =>
									this.elementService.createElement(
										element,
										branchId,
										[],
										tx,
										element.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Platform Types
					concatMap((tx) =>
						iif(
							() => summary.platformTypes.length > 0,
							from(summary.platformTypes).pipe(
								concatMap((pType) =>
									this.typesService.createPlatformType(
										branchId,
										pType,
										[],
										tx,
										pType.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Enum Sets
					concatMap((tx) =>
						iif(
							() => summary.enumSets.length > 0,
							from(summary.enumSets).pipe(
								map(
									(e) =>
										({
											id: e.id,
											name: e.name,
											applicability: e.applicability,
											description: e.description,
										} as enumSet)
								),
								concatMap((enumSet) =>
									this.enumSetService.createEnumSet(
										branchId,
										enumSet,
										[],
										tx,
										enumSet.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Enums
					concatMap((tx) =>
						iif(
							() => summary.enums.length > 0,
							from(summary.enums).pipe(
								concatMap((enumeration) =>
									this.enumSetService.createEnum(
										branchId,
										enumeration,
										[],
										tx,
										enumeration.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Cross References
					concatMap((tx) =>
						iif(
							() => summary.crossReferences.length > 0,
							from(summary.crossReferences).pipe(
								concatMap((crossRef) =>
									this.crossRefService.createCrossReferenceTx(
										crossRef,
										branchId,
										tx,
										crossRef.id
									)
								),
								reduce(() => tx)
							),
							of(tx)
						)
					)
				)
				.pipe(
					// Connection <-> Node relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.connectionNodeRelations,
							this.connectionService.createNodeRelation,
							true,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Connection <-> Message relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.connectionMessageRelations,
							this.messagesService.createConnectionRelation,
							false,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Message <-> Publisher Node relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.messagePublisherNodeRelations,
							this.messagesService.createMessageNodeRelation,
							false,
							tx,
							branchId,
							true
						)
					)
				)
				.pipe(
					// Message <-> Subscriber Node relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.messageSubscriberNodeRelations,
							this.messagesService.createMessageNodeRelation,
							false,
							tx,
							branchId,
							false
						)
					)
				)
				.pipe(
					// Message <-> Submessage relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.messageSubmessageRelations,
							this.subMessageService.createMessageRelation,
							false,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Submessage <-> Structure relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.subMessageStructureRelations,
							this.structureService.createSubMessageRelation,
							false,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Structure <-> Element relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.structureElementRelations,
							this.elementService.createStructureRelation,
							false,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Element <-> Platform Type relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.elementPlatformTypeRelations,
							this.elementService.createPlatformTypeRelation,
							true,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Platform Type <-> Enum Set relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.platformTypeEnumSetRelations,
							this.enumSetService
								.createPlatformTypeToEnumSetRelation,
							true,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Enum Set <-> Enum relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.enumSetEnumRelations,
							this.enumSetService.createEnumToEnumSetRelation,
							false,
							tx,
							branchId
						)
					)
				)
				.pipe(
					// Cross Reference <-> Connection relations
					concatMap((tx) =>
						this._addRelationPipe(
							summary.connectionCrossReferenceRelations,
							this.crossRefService.createConnectionRelation,
							false,
							tx,
							branchId
						)
					)
				)
		)
	);

	private _addRelationPipe(
		map: importRelationMap,
		create:
			| ((
					artAId: string,
					artBId: string,
					flag?: boolean
			  ) => Observable<relation>)
			| ((artAId: string, artBId: string) => Observable<relation>),
		swap: boolean,
		tx: transaction,
		branchId: string,
		flag?: boolean
	) {
		return of(tx).pipe(
			concatMap((tx) =>
				iif(
					() => Object.keys(map).length > 0,
					from(Object.keys(map)).pipe(
						concatMap((artAId) =>
							from(map[artAId]).pipe(
								switchMap((artBId) =>
									create(
										swap ? artBId : artAId,
										swap ? artAId : artBId,
										flag
									).pipe(
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
						reduce(() => tx)
					),
					of(tx)
				)
			)
		);
	}

	private _sendTransaction$ = this._allImportTx$.pipe(
		take(1),
		switchMap((tx) =>
			this.transactionService.performMutation(tx).pipe(
				tap((res) => {
					this.ImportSuccess = res.results.success;
					this.ImportInProgress = false;
				})
			)
		)
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
		this.SelectedConnectionId = '';
		this.ImportInProgress = false;
		this.toggleDone = true;
		this.SelectedConnectionId = '';
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

	get selectedConnectionId() {
		return this._selectedConnectionId;
	}

	set SelectedConnectionId(id: string) {
		this._selectedConnectionId.next(id);
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
		return this._importInProgress$.asObservable();
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

	get connections() {
		return this._connections;
	}
}
