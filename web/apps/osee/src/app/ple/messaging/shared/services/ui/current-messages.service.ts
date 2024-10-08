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
import { effect, inject, Injectable, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import type {
	message,
	messageWithChanges,
	settingsDialogData,
	subMessage,
	subMessageWithChanges,
} from '@osee/messaging/shared/types';
import {
	ApplicabilityListUIService,
	CurrentBranchInfoService,
} from '@osee/shared/services';
import { SideNavService } from '@osee/shared/services/layout';
import {
	changeInstance,
	changeTypeEnum,
	itemTypeIdRelation,
} from '@osee/shared/types/change-report';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import { addRelation, deleteRelation } from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	legacyTransaction,
	relation,
	transactionToken,
} from '@osee/transactions/types';
import {
	BehaviorSubject,
	combineLatest,
	from,
	iif,
	of,
	Subject,
	throwError,
} from 'rxjs';
import {
	concatMap,
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	mergeMap,
	reduce,
	repeatWhen,
	scan,
	share,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { MessagesService } from '../http/messages.service';
import { StructuresService } from '../http/structures.service';
import { SubMessagesService } from '../http/sub-messages.service';
import { MessageUiService } from './messages-ui.service';
import { PreferencesUIService } from './preferences-ui.service';
import { WarningDialogService } from './warning-dialog.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentMessagesService {
	private messageService = inject(MessagesService);
	private subMessageService = inject(SubMessagesService);
	private structureService = inject(StructuresService);
	private ui = inject(MessageUiService);
	private applicabilityService = inject(ApplicabilityListUIService);
	private preferenceService = inject(PreferencesUIService);
	private branchInfoService = inject(CurrentBranchInfoService);
	private sideNavService = inject(SideNavService);
	private warningDialogService = inject(WarningDialogService);

	private _currentPage$ = toObservable(this.ui.currentPage);
	private _currentPageSize$ = toObservable(this.ui.currentPageSize);
	private _filter = toObservable(this.ui.filter);
	private _messagesList = combineLatest([
		this._filter,
		this.BranchId,
		this.connectionId,
		this.viewId,
		this.currentPage,
		this._currentPageSize$,
	]).pipe(
		filter(
			([_filter, branchId, connection, _viewId, _page, _pageSize]) =>
				connection !== '-1' && branchId !== ''
		),
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([filter, branchId, connection, viewId, page, pageSize]) =>
			this.messageService
				.getFilteredMessages(
					filter,
					branchId,
					connection,
					viewId,
					page + 1,
					pageSize
				)
				.pipe(
					repeatWhen((_) => this.ui.UpdateRequired),
					share()
				)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _messagesListCount = combineLatest([
		this._filter,
		this.BranchId,
		this.connectionId,
		this.viewId,
	]).pipe(
		filter(
			([_filter, branchId, connection, _viewId]) =>
				connection !== '-1' && branchId !== ''
		),
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([filter, branchId, connection, viewId]) =>
			this.messageService
				.getFilteredMessagesCount(filter, branchId, connection, viewId)
				.pipe(
					repeatWhen((_) => this.ui.UpdateRequired),
					share()
				)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _messages = combineLatest([
		this.ui.isInDiff,
		this._messagesList,
		this.viewId,
	]).pipe(
		switchMap(([diffState, messageList, viewId]) =>
			iif(
				() => diffState,
				this.differences.pipe(
					filter((val) => val !== undefined),
					switchMap((differences) =>
						of(
							this.parseIntoMessagesAndSubmessages(
								differences as changeInstance[],
								messageList
							)
						).pipe(
							switchMap((messagesWithDifferences) =>
								from(messagesWithDifferences).pipe(
									mergeMap((message) =>
										iif(
											() =>
												(message as messageWithChanges)
													.deleted,
											this.getMessageFromParent(
												message.id,
												viewId
											).pipe(
												switchMap((parentMessage) =>
													this.mergeMessages(
														message as messageWithChanges,
														parentMessage
													)
												)
											),
											of(message)
										).pipe(
											mergeMap((message) =>
												from(message.subMessages).pipe(
													mergeMap((submessage) =>
														iif(
															() =>
																(
																	submessage as subMessageWithChanges
																).deleted,
															this.getSubMessageFromParent(
																message.id,
																submessage.id ||
																	''
															).pipe(
																switchMap(
																	(
																		parentSubMessage
																	) =>
																		this.mergeSubMessage(
																			submessage as subMessageWithChanges,
																			parentSubMessage
																		)
																)
															), //deleted submessage
															of(submessage) //not deleted submessage
														)
													)
												)
											),
											//find deleted sub message details of all messages and merge their contents with parent branch details
											//merge back into array and set message.subMessages to it
											reduce(
												(acc, curr) => [...acc, curr],
												[] as (
													| subMessage
													| subMessageWithChanges
												)[]
											),
											switchMap((submessagearray) =>
												this.mergeSubmessagesIntoMessage(
													message,
													submessagearray
												)
											)
										)
									)
								)
							)
						)
					),
					scan(
						(acc, curr) => [...acc, curr],
						[] as (message | messageWithChanges)[]
					),
					map((array) =>
						array.sort((a, b) => Number(a.id) - Number(b.id))
					)
					//find deleted messages and merge their contents with parent branch details
				),
				of(messageList)
			)
		)
	);
	private _allMessages = combineLatest([
		this.BranchId,
		this.connectionId,
		this.viewId,
	]).pipe(
		share(),
		switchMap((x) =>
			this.messageService.getFilteredMessages('', x[0], x[1], x[2]).pipe(
				repeatWhen((_) => this.ui.UpdateRequired),
				share()
			)
		)
	);

	private _done = new Subject<boolean>();
	private _differences = new BehaviorSubject<changeInstance[] | undefined>(
		undefined
	);

	private _expandedRows = signal<(message | messageWithChanges)[]>([]);
	private _expandedRows$ = toObservable(this._expandedRows);

	private _currentTx = inject(CurrentTransactionService);

	get currentPage() {
		return this._currentPage$;
	}

	get messages() {
		return this._messages;
	}

	get messagesCount() {
		return this._messagesListCount;
	}

	get allMessages() {
		return this._allMessages;
	}
	messageFilter = this.ui.filter;

	private _returnToFirstPageOnFilterChange = effect(
		() => {
			//very low chance this happens and it keeps the read working...
			if (this.messageFilter() !== crypto.randomUUID()) {
				this.ui.currentPage.set(0);
				this.clearRows();
			}
		},
		{ allowSignalWrites: true }
	);

	set branch(id: string) {
		this.ui.BranchIdString = id;
	}

	get BranchId() {
		return this.ui.BranchId;
	}

	set branchId(value: string) {
		this.ui.BranchIdString = value;
	}

	set connection(id: `${number}`) {
		this.ui.connectionIdString = id;
	}

	get connectionId() {
		return this.ui.connectionId;
	}

	get connectionIdSignal() {
		return this.ui.connectionIdSignal;
	}

	get viewId() {
		return this.ui.viewId;
	}

	set messageId(value: string) {
		this.ui.messageId = value;
	}

	set subMessageId(value: `${number}`) {
		this.ui.subMessageId = value;
	}

	set submessageToStructureBreadCrumbs(value: string) {
		this.ui.subMessageToStructureBreadCrumbs = value;
	}

	set singleStructureId(value: string) {
		this.ui.singleStructureId = value;
	}

	get applic() {
		return this.applicabilityService.applic;
	}

	get preferences() {
		return this.preferenceService.preferences;
	}

	get BranchPrefs() {
		return this.preferenceService.BranchPrefs;
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

	set DiffMode(value: boolean) {
		this.ui.DiffMode = value;
	}
	get isInDiff() {
		return this.ui.isInDiff;
	}

	get differences() {
		return this._differences;
	}
	set difference(value: changeInstance[]) {
		this._differences.next(value);
	}

	set branchType(value: 'working' | 'baseline' | '') {
		this.ui.typeValue = value;
	}

	get expandedRows() {
		return this._expandedRows;
	}

	set addExpandedRow(value: message | messageWithChanges) {
		this._expandedRows.update((rows) => [...rows, value]);
	}

	set removeExpandedRow(value: message | messageWithChanges) {
		this._expandedRows.update((rows) =>
			rows.filter((v) => v.id !== value.id)
		);
	}

	clearRows() {
		this._expandedRows.set([]);
	}

	getPaginatedSubMessages(pageNum: string | number) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.subMessageService.getPaginatedFilteredSubMessages(
					id,
					'',
					pageNum
				)
			)
		);
	}

	getPaginatedSubmessagesByName(
		name: string,
		count: number,
		pageNum: string | number
	) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.subMessageService.getPaginatedSubmessagesByName(
					id,
					name,
					count,
					pageNum
				)
			)
		);
	}

	getSubmessagesByNameCount(name: string) {
		return this.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.subMessageService.getSubmessagesByNameCount(id, name)
			)
		);
	}

	private mergeMessages(message: messageWithChanges, parentMessage: message) {
		message.name = parentMessage.name;
		message.description = parentMessage.description;
		message.interfaceMessageNumber = parentMessage.interfaceMessageNumber;
		message.interfaceMessagePeriodicity =
			parentMessage.interfaceMessagePeriodicity;
		message.interfaceMessageRate = parentMessage.interfaceMessageRate;
		message.interfaceMessageType = parentMessage.interfaceMessageType;
		message.interfaceMessageWriteAccess =
			parentMessage.interfaceMessageWriteAccess;
		return of(message);
	}

	private mergeSubMessage(
		submessage: subMessageWithChanges,
		parentSubMessage: subMessage
	) {
		submessage.name = parentSubMessage.name;
		submessage.description = parentSubMessage.description;
		submessage.interfaceSubMessageNumber.value =
			parentSubMessage.description.value;
		submessage.applicability = parentSubMessage.applicability;
		return of(submessage);
	}
	private mergeSubmessagesIntoMessage(
		message: message | messageWithChanges,
		submessages: (subMessage | subMessageWithChanges)[]
	) {
		message.subMessages = submessages;
		return of(message);
	}
	getMessageFromParent(messageId: string, viewId: string) {
		return combineLatest([
			this.branchInfoService.currentBranch,
			this.connectionId,
		]).pipe(
			take(1),
			switchMap(([details, connectionId]) =>
				this.messageService.getMessage(
					details.parentBranch.id,
					messageId,
					connectionId,
					viewId
				)
			)
		);
	}

	getSubMessageFromParent(messageId: string, subMessageId: string) {
		return combineLatest([
			this.branchInfoService.currentBranch,
			this.connectionId,
		]).pipe(
			take(1),
			switchMap(([details, connectionId]) =>
				this.subMessageService.getSubMessage(
					details.parentBranch.id,
					connectionId,
					messageId,
					subMessageId
				)
			)
		);
	}

	private _getSubMessageAttributes(body: subMessage) {
		const {
			id,
			gammaId,
			applicability,
			autogenerated,
			...remainingAttributes
		} = body;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		return attributes;
	}
	partialUpdateSubMessage(current: subMessage, previous: subMessage) {
		const previousAttributes = this._getSubMessageAttributes(previous);
		const {
			id,
			gammaId,
			applicability,
			autogenerated,
			...remainingAttributes
		} = current;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		const addAttributes = attributes.filter((v) => v.id === '-1');
		const modifyAttributes = attributes
			.filter((v) => v.id !== '-1')
			.filter(
				(v) =>
					previousAttributes.filter(
						(x) =>
							x.id === v.id &&
							x.typeId === v.typeId &&
							x.gammaId === v.gammaId &&
							x.value !== v.value
					).length > 0
			);
		const deleteAttributes = previousAttributes.filter(
			(v) => !attributes.map((x) => x.id).includes(v.id)
		);
		return this.warningDialogService.openSubMessageDialog(current).pipe(
			switchMap((_) =>
				this._currentTx.modifyArtifactAndMutate(
					`Modifying ${id || '-1'}`,
					id || '-1',
					applicability,
					{
						set: modifyAttributes,
						add: addAttributes,
						delete: deleteAttributes,
					}
				)
			)
		);
	}

	private _getMessageAttributes(body: message) {
		const {
			id,
			gammaId,
			applicability,
			subMessages,
			publisherNodes,
			subscriberNodes,
			added,
			deleted,
			changes,
			hasSubMessageChanges,
			...remainingAttributes
		} = body;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		return attributes;
	}
	partialUpdateMessage(current: message, previous: message) {
		const previousAttributes = this._getMessageAttributes(previous);
		const {
			id,
			gammaId,
			applicability,
			subMessages,
			publisherNodes,
			subscriberNodes,
			added,
			deleted,
			changes,
			hasSubMessageChanges,
			...remainingAttributes
		} = current;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		const addAttributes = attributes.filter((v) => v.id === '-1');
		const modifyAttributes = attributes
			.filter((v) => v.id !== '-1')
			.filter(
				(v) =>
					previousAttributes.filter(
						(x) =>
							x.id === v.id &&
							x.typeId === v.typeId &&
							x.gammaId === v.gammaId &&
							x.value !== v.value
					).length > 0
			);
		const deleteAttributes = previousAttributes.filter(
			(v) => !attributes.map((x) => x.id).includes(v.id)
		);
		return this.warningDialogService.openMessageDialog(current).pipe(
			switchMap((_) =>
				this._currentTx.modifyArtifactAndMutate(
					`Modifying ${id || '-1'}`,
					id || '-1',
					applicability,
					{
						set: modifyAttributes,
						add: addAttributes,
						delete: deleteAttributes,
					}
				)
			)
		);
	}

	relateSubMessage(
		messageId: `${number}`,
		subMessageId: `${number}`,
		afterSubMessage?: string
	) {
		if (messageId === '-1' || messageId === '0') {
			return throwError(() => {
				return new Error(
					'Message being related to cannot have an id of -1 or 0'
				);
			});
		}
		if (subMessageId === '-1' || subMessageId === '0') {
			return throwError(() => {
				return new Error(
					'Submessage being related to cannot have an id of -1 or 0'
				);
			});
		}
		const messageRelation = {
			typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGECONTENT,
			aArtId: messageId,
			bArtId: subMessageId,
			afterArtifact: afterSubMessage || 'end',
		};
		return this.warningDialogService
			.openMessageDialog({ id: messageId })
			.pipe(
				switchMap((_) =>
					this._currentTx.addRelationAndMutate(
						`Relating ${subMessageId} to ${messageId}`,
						messageRelation
					)
				)
			);
	}

	createSubMessage(
		body: subMessage,
		messageId: `${number}`,
		afterSubMessage?: string,
		overrideComment?: string,
		extraRelations: relation[] = []
	) {
		const {
			id,
			gammaId,
			applicability,
			autogenerated,
			...remainingAttributes
		} = body;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		const msgRelation = {
			typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGECONTENT,
			sideA: messageId,
			afterArtifact: afterSubMessage || 'end',
		};
		const results = this._currentTx.createArtifact(
			overrideComment ||
				`Creating submessage ${body.name.value} on ${messageId}${
					' after' + afterSubMessage || ' at end'
				}`,
			ARTIFACTTYPEIDENUM.SUBMESSAGE,
			applicability,
			[msgRelation, ...extraRelations],
			...attributes
		);
		const tx = results.tx;
		return this.warningDialogService
			.openMessageDialog({ id: messageId })
			.pipe(map((_) => tx))
			.pipe(this._currentTx.performMutation());
	}
	copySubMessage(
		body: subMessage,
		messageId: `${number}`,
		afterSubMessage?: string
	) {
		const branchId = this.ui.BranchId.pipe(
			take(1),
			filter((id) => id !== '' && id !== '-1')
		);
		const connectionId = this.connectionId.pipe(
			take(1),
			filter((id) => id !== '-1')
		);
		const structures = combineLatest([branchId, connectionId]).pipe(
			switchMap(([id, connection]) =>
				this.structureService.getFilteredStructures(
					'',
					id,
					messageId,
					body.id || '-1',
					connection,
					'-1',
					1,
					0
				)
			)
		);
		const structureIds = structures.pipe(
			concatMap((st) => from(st).pipe(map((structure) => structure.id))),
			reduce((acc, curr) => [...acc, curr], [] as string[])
		);
		const structureRelations = structureIds.pipe(
			concatMap((st) =>
				from(st).pipe(
					map((st) => {
						return {
							typeId: RELATIONTYPEIDENUM.INTERFACESUBMESSAGECONTENT,
							sideB: st,
						};
					})
				)
			),
			reduce((acc, curr) => [...acc, curr], [] as relation[])
		);
		return structureRelations.pipe(
			tap((_) => {
				//before creating the tx, zeroize the attributes to -1
				body.name.id = '-1';
				body.description.id = '-1';
				body.interfaceSubMessageNumber.id = '-1';
			}),
			switchMap((rel) =>
				this.createSubMessage(
					body,
					messageId,
					afterSubMessage,
					`Copying ${body.name.value} to ${messageId}`,
					rel
				)
			)
		);
	}

	createMessage(body: message) {
		const {
			id,
			gammaId,
			applicability,
			publisherNodes,
			subscriberNodes,
			subMessages,
			added,
			deleted,
			changes,
			hasSubMessageChanges,
			...remainingAttributes
		} = body;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		const preExistingSubMsgs = subMessages.filter(
			(v) => v.id !== '-1' && v.id !== '0'
		);
		const preExistingPubNodes = publisherNodes.filter(
			(v) => v.id !== '-1' && v.id !== '0'
		);
		const preExistingSubNodes = subscriberNodes.filter(
			(v) => v.id !== '-1' && v.id !== '0'
		);
		const preExistingSubMsgRelations = preExistingSubMsgs.map((v) => {
			return {
				typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGECONTENT,
				sideB: v.id,
			};
		});
		const preExistingPubNodesRels = preExistingPubNodes.map((v) => {
			return {
				typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGEPUBNODE,
				sideB: v.id,
			};
		});
		const preExistingSubNodesRels = preExistingSubNodes.map((v) => {
			return {
				typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGESUBNODE,
				sideB: v.id,
			};
		});
		const connectionRel = {
			typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONCONTENT,
			sideA: this.ui.connectionIdSignal(),
		};
		const results = this._currentTx.createArtifactAndMutate(
			`Creating message ${body.name.value}`,
			ARTIFACTTYPEIDENUM.MESSAGE,
			applicability,
			[
				...preExistingSubMsgRelations,
				...preExistingPubNodesRels,
				...preExistingSubNodesRels,
				connectionRel,
			],
			...attributes
		);
		return results;
	}

	deleteMessage(messageId: `${number}`) {
		return this.BranchId.pipe(
			take(1),
			switchMap((branch) =>
				this.warningDialogService
					.openMessageDialog({ id: messageId })
					.pipe(map((_) => branch))
			),
			switchMap((branchId) =>
				this.messageService.deleteMessage(branchId, messageId).pipe(
					switchMap((transaction) =>
						this.messageService.performMutation(transaction).pipe(
							tap(() => {
								this.ui.updateMessages = true;
							})
						)
					)
				)
			)
		);
	}

	removeMessage(messageId: `${number}`) {
		return combineLatest([this.connectionId, this.BranchId]).pipe(
			take(1),
			switchMap(([branch, connection]) =>
				this.warningDialogService
					.openMessageDialog({ id: messageId })
					.pipe(map((_) => [branch, connection]))
			),
			switchMap(([connectionId, branchId]) =>
				this.messageService
					.createConnectionRelation(connectionId, messageId)
					.pipe(
						switchMap((relation) =>
							this.messageService
								.deleteRelation(branchId, relation)
								.pipe(
									switchMap((transaction) =>
										this.messageService
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

	removeSubMessage(submessageId: `${number}`, messageId: `${number}`) {
		if (messageId === '-1' || messageId === '0') {
			return throwError(() => {
				return new Error(
					'Message being unrelated to cannot have an id of -1 or 0'
				);
			});
		}
		if (submessageId === '-1' || submessageId === '0') {
			return throwError(() => {
				return new Error(
					'Submessage being unrelated to cannot have an id of -1 or 0'
				);
			});
		}
		const messageRelation = {
			typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGECONTENT,
			aArtId: messageId,
			bArtId: submessageId,
		};
		return this.warningDialogService
			.openSubMessageDialog({ id: submessageId })
			.pipe(
				switchMap((_) =>
					this._currentTx.deleteRelationAndMutate(
						`Removing ${submessageId} from ${messageId}`,
						messageRelation
					)
				)
			);
	}
	deleteSubMessage(submessageId: `${number}`) {
		return this.warningDialogService
			.openSubMessageDialog({ id: submessageId })
			.pipe(
				switchMap((_) =>
					this._currentTx.deleteArtifactAndMutate(
						`Deleting submessage ${submessageId}`,
						submessageId
					)
				)
			);
	}

	updatePreferences(preferences: settingsDialogData) {
		return this.createUserPreferenceBranchTransaction(
			preferences.editable
		).pipe(
			take(1),
			switchMap((transaction) =>
				this.messageService.performMutation(transaction).pipe(
					take(1),
					tap(() => {
						this.ui.updateMessages = true;
					})
				)
			)
		);
	}

	changeMessageRelationOrder(
		connectionId: `${number}`,
		messageId: `${number}`,
		afterArtifactId: string
	) {
		let tx = this._currentTx.createTransaction(
			`Changing relation order of ${messageId} to ${connectionId}`
		);
		tx = deleteRelation(tx, {
			typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONCONTENT,
			aArtId: connectionId,
			bArtId: messageId,
		});
		tx = addRelation(tx, {
			typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONCONTENT,
			aArtId: connectionId,
			bArtId: messageId,
			afterArtifact: afterArtifactId ?? 'end',
		});

		return this.warningDialogService
			.openMessageDialog({ id: messageId })
			.pipe(
				map((_) => tx),
				this._currentTx.performMutation()
			);
	}

	private createUserPreferenceBranchTransaction(editMode: boolean) {
		return combineLatest(
			this.preferences,
			this.BranchId,
			this.BranchPrefs
		).pipe(
			take(1),
			switchMap(([prefs, branch, branchPrefs]) =>
				iif(
					() => prefs.hasBranchPref,
					of<legacyTransaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								setAttributes: [
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
					of<legacyTransaction>({
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
			)
		);
	}

	set toggleDone(value: unknown) {
		this._done.next(true);
	}

	get done() {
		return this._done;
	}

	parseIntoMessagesAndSubmessages(
		changes: changeInstance[],
		_oldMessageList: (message | messageWithChanges)[]
	) {
		const messageList = JSON.parse(JSON.stringify(_oldMessageList)) as (
			| message
			| messageWithChanges
		)[];
		const newMessages: changeInstance[] = [];
		const newMessagesId: string[] = [];
		const newSubmessages: changeInstance[] = [];
		const newSubmessagesId: string[] = [];
		changes.forEach((change) => {
			//this loop is solely just for building a list of deleted nodes/connections
			if (
				change.itemTypeId === ARTIFACTTYPEIDENUM.SUBMESSAGE &&
				!newMessagesId.includes(change.artId) &&
				!newSubmessagesId.includes(change.artId)
			) {
				//deleted submessage
				newSubmessagesId.push(change.artId);
			} else if (
				change.itemTypeId === ARTIFACTTYPEIDENUM.MESSAGE &&
				!newMessagesId.includes(change.artId) &&
				!newSubmessagesId.includes(change.artId)
			) {
				//deleted message
				newMessagesId.push(change.artId);
			} else if (
				typeof change.itemTypeId === 'object' &&
				'id' in change.itemTypeId &&
				change.itemTypeId.id ===
					RELATIONTYPEIDENUM.INTERFACECONNECTIONCONTENT
			) {
				if (!newMessagesId.includes(change.artId)) {
					newMessagesId.push(change.artId);
				} else if (!newMessagesId.includes(change.artIdB)) {
					newMessagesId.push(change.artIdB);
				}
			} else if (
				typeof change.itemTypeId === 'object' &&
				'id' in change.itemTypeId &&
				change.itemTypeId.id ===
					RELATIONTYPEIDENUM.INTERFACEMESSAGECONTENT
			) {
				if (!newSubmessagesId.includes(change.artId)) {
					newSubmessagesId.push(change.artId);
				}
			}
		});
		changes
			.sort(
				(a, b) =>
					['111', '333', '222', '444'].indexOf(a.changeType.id) -
					['111', '333', '222', '444'].indexOf(b.changeType.id)
			)
			.forEach((change) => {
				if (messageList.find((val) => val.id === change.artId)) {
					//logic for message update
					const messageIndex = messageList.indexOf(
						messageList.find(
							(val) => val.id === change.artId
						) as message
					);
					messageList[messageIndex] = this.messageChange(
						change,
						messageList[messageIndex]
					);
					const messageChanges = (
						messageList[messageIndex] as messageWithChanges
					).changes;
					if (
						messageChanges.applicability !== undefined &&
						messageChanges.name !== undefined &&
						messageChanges.description !== undefined &&
						messageChanges.interfaceMessageNumber !== undefined &&
						messageChanges.interfaceMessagePeriodicity !==
							undefined &&
						messageChanges.interfaceMessageRate !== undefined &&
						messageChanges.interfaceMessageType !== undefined &&
						messageChanges.interfaceMessageWriteAccess !==
							undefined &&
						(messageList[messageIndex] as messageWithChanges)
							.deleted !== true
					) {
						(
							messageList[messageIndex] as messageWithChanges
						).added = true;
					} else {
						(
							messageList[messageIndex] as messageWithChanges
						).added = false;
					}
					if (
						!(messageList[messageIndex] as messageWithChanges)
							.hasSubMessageChanges
					) {
						(
							messageList[messageIndex] as messageWithChanges
						).hasSubMessageChanges = false;
					}
				} else if (
					messageList.find(
						(val) =>
							val.subMessages.find(
								(val2) => val2.id === change.artId
							) !== undefined
					)
				) {
					//logic for submessage update
					const filteredMessages = messageList.filter((val) =>
						val.subMessages.find((val2) => val2.id === change.artId)
					);
					filteredMessages.forEach((_, index) => {
						const subMessageIndex = filteredMessages[
							index
						].subMessages.indexOf(
							filteredMessages[index].subMessages.find(
								(val2) => val2.id === change.artId
							) as Required<subMessage>
						);
						filteredMessages[index].subMessages[subMessageIndex] =
							this.subMessageChange(
								change,
								filteredMessages[index].subMessages[
									subMessageIndex
								]
							);
						(
							filteredMessages[index] as messageWithChanges
						).hasSubMessageChanges = true;
						const messageChanges = (
							filteredMessages[index].subMessages[
								subMessageIndex
							] as subMessageWithChanges
						).changes;
						if (
							messageChanges.name !== undefined &&
							messageChanges.description !== undefined &&
							messageChanges.interfaceSubMessageNumber !==
								undefined &&
							messageChanges.applicability !== undefined &&
							(
								filteredMessages[index].subMessages[
									subMessageIndex
								] as subMessageWithChanges
							).deleted !== true
						) {
							(
								filteredMessages[index].subMessages[
									subMessageIndex
								] as subMessageWithChanges
							).added = true;
						} else {
							(
								filteredMessages[index].subMessages[
									subMessageIndex
								] as subMessageWithChanges
							).added = false;
						}
						///update main list
						const messageIndex = messageList.indexOf(
							messageList.find(
								(val) => val.id === filteredMessages[index].id
							) as message | messageWithChanges
						);
						messageList[messageIndex] = filteredMessages[index];
					});
				} else if (
					(newMessagesId.includes(change.artId) ||
						newMessagesId.includes(change.artIdB)) &&
					change.deleted
				) {
					newMessages.push(change);
				} else if (
					(newSubmessagesId.includes(change.artId) ||
						newSubmessagesId.includes(change.artIdB)) &&
					change.deleted
				) {
					newSubmessages.push(change);
				}
			});
		newMessages.sort((a, b) => Number(a.artId) - Number(b.artId));
		newSubmessages.sort((a, b) => Number(a.artId) - Number(b.artId));
		const messages = this.splitByArtId(newMessages);
		messages.forEach((value) => {
			//create deleted messages
			const tempMessage = this.messageDeletionChanges(value);
			if (!isNaN(+tempMessage.id) && tempMessage.id !== '-1') {
				messageList.push(tempMessage);
			}
		});
		const submessages = this.splitByArtId(newSubmessages);
		submessages.forEach((_) => {
			//create deleted submessages
		});
		messageList.forEach((m) => {
			m.subMessages = m.subMessages.sort(
				(a, b) => Number(a.id) - Number(b.id)
			);
		});
		return messageList.sort((a, b) => Number(a.id) - Number(b.id));
	}

	private splitByArtId(changes: changeInstance[]): changeInstance[][] {
		const returnValue: changeInstance[][] = [];
		let prev: Partial<changeInstance> | undefined = undefined;
		let tempArray: changeInstance[] = [];
		changes.forEach((value, _index) => {
			if (prev !== undefined) {
				if (prev.artId === value.artId) {
					//condition where equal, add to array
					tempArray.push(value);
				} else {
					prev = Object.assign(prev, value);
					returnValue.push(tempArray);
					tempArray = [];
					tempArray.push(value);
					//condition where not equal, set prev to value, push old array onto returnValue, create new array
				}
			} else {
				tempArray = [];
				tempArray.push(value);
				prev = {};
				prev = Object.assign(prev, value);
				//create new array, push prev onto array, set prev
			}
		});
		if (tempArray.length !== 0) {
			returnValue.push(tempArray);
		}
		return returnValue;
	}

	private messageDeletionChanges(changes: changeInstance[]) {
		let tempMessage: messageWithChanges = {
			added: false,
			deleted: true,
			hasSubMessageChanges: false,
			changes: {},
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			applicability: {
				id: '1',
				name: 'Base',
			},
			subMessages: [],
			interfaceMessageRate: {
				id: '-1',
				typeId: '2455059983007225763',
				gammaId: '-1',
				value: '',
			},
			interfaceMessagePeriodicity: {
				id: '-1',
				typeId: '3899709087455064789',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageWriteAccess: {
				id: '-1',
				typeId: '2455059983007225754',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageType: {
				id: '-1',
				typeId: '2455059983007225770',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageNumber: {
				id: '-1',
				typeId: '2455059983007225768',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageExclude: {
				id: '-1',
				typeId: '2455059983007225811',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageIoMode: {
				id: '-1',
				typeId: '2455059983007225813',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageModeCode: {
				id: '-1',
				typeId: '2455059983007225810',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRateVer: {
				id: '-1',
				typeId: '2455059983007225805',
				gammaId: '-1',
				value: '',
			},
			interfaceMessagePriority: {
				id: '-1',
				typeId: '2455059983007225806',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageProtocol: {
				id: '-1',
				typeId: '2455059983007225809',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptWordCount: {
				id: '-1',
				typeId: '2455059983007225807',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptCmdWord: {
				id: '-1',
				typeId: '2455059983007225808',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRunBeforeProc: {
				id: '-1',
				typeId: '2455059983007225812',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageVer: {
				id: '-1',
				typeId: '2455059983007225804',
				gammaId: '-1',
				value: '',
			},
			publisherNodes: [],
			subscriberNodes: [],
		};
		changes.forEach((value) => {
			tempMessage = this.parseMessageDeletionChange(value, tempMessage);
		});
		return tempMessage;
	}
	parseMessageDeletionChange(
		change: changeInstance,
		message: messageWithChanges
	): messageWithChanges {
		message.id = change.artId as `${number}`;
		if (message.changes === undefined) {
			message.changes = {};
		}
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.destinationVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				message.changes.name = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.name.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.name.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				message.changes.description = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.description.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.description.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACEMESSAGENUMBER
			) {
				message.changes.interfaceMessageNumber = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageNumber.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageNumber.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPERIODICITY
			) {
				message.changes.interfaceMessagePeriodicity = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessagePeriodicity.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessagePeriodicity.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE
			) {
				message.changes.interfaceMessageRate = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageRate.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageRate.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE
			) {
				message.changes.interfaceMessageType = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageType.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageType.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEWRITEACCESS
			) {
				message.changes.interfaceMessageWriteAccess = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageWriteAccess.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as boolean,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageWriteAccess.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as boolean,
					},
					transactionToken: changes.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			message.changes.applicability = {
				previousValue: change.baselineVersion
					.applicabilityToken as applic,
				currentValue: change.currentVersion
					.applicabilityToken as applic,
				transactionToken: change.currentVersion.transactionToken,
			};
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			message.id = change.artIdB as `${number}`;
			message.applicability = change.currentVersion
				.applicabilityToken as applic;
		}
		return message;
	}

	private messageChange(
		change: changeInstance,
		message: message | messageWithChanges
	) {
		return this.parseMessageChange(change, this.initializeMessage(message));
	}
	private parseMessageChange(
		change: changeInstance,
		message: messageWithChanges
	) {
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				message.changes.name = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.name.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.name.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				message.changes.description = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.description.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.description.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACEMESSAGENUMBER
			) {
				message.changes.interfaceMessageNumber = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageNumber.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageNumber.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPERIODICITY
			) {
				message.changes.interfaceMessagePeriodicity = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessagePeriodicity.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessagePeriodicity.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE
			) {
				message.changes.interfaceMessageRate = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageRate.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageRate.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE
			) {
				message.changes.interfaceMessageType = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageType.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageType.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEWRITEACCESS
			) {
				message.changes.interfaceMessageWriteAccess = {
					previousValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageWriteAccess.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as boolean,
					},
					currentValue: {
						id: change.itemId as `${number}`,
						typeId: message.interfaceMessageWriteAccess.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as boolean,
					},
					transactionToken: changes.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (change.currentVersion.transactionToken.id !== '-1') {
				message.changes.applicability = {
					previousValue: change.baselineVersion
						.applicabilityToken as applic,
					currentValue: change.currentVersion
						.applicabilityToken as applic,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
			if (
				((change.itemTypeId as itemTypeIdRelation).id =
					RELATIONTYPEIDENUM.INTERFACEMESSAGECONTENT)
			) {
				message.hasSubMessageChanges = true;
				const submessageIndex = message.subMessages.findIndex(
					(val) => val.id === change.artIdB
				);
				if (submessageIndex !== -1) {
					message.subMessages[submessageIndex] =
						this.subMessageChange(
							change,
							message.subMessages[submessageIndex]
						);
				} else {
					const submessage: subMessageWithChanges = {
						added: false,
						deleted: true,
						changes: {},
						applicability: {
							id: '1',
							name: 'Base',
						},
						id: change.artIdB as `${number}`,
						gammaId: '-1',
						name: {
							id: '-1',
							typeId: '1152921504606847088',
							gammaId: '-1',
							value: '',
						},
						description: {
							id: '-1',
							typeId: '1152921504606847090',
							gammaId: '-1',
							value: '',
						},
						interfaceSubMessageNumber: {
							id: '-1',
							typeId: '2455059983007225769',
							gammaId: '-1',
							value: '',
						},
					};
					message.subMessages.push(submessage);
				}
			}
		}
		return message;
	}
	private isMessageWithChanges(
		message: message | messageWithChanges
	): message is messageWithChanges {
		return (message as messageWithChanges).changes !== undefined;
	}
	private initializeMessage(message: message | messageWithChanges) {
		let tempMessage: messageWithChanges;
		if (!this.isMessageWithChanges(message)) {
			tempMessage = message as messageWithChanges;
			tempMessage.changes = {};
		} else {
			tempMessage = message;
		}
		return tempMessage;
	}

	private subMessageChange(
		change: changeInstance,
		submessage: subMessage | subMessageWithChanges
	) {
		return this.parseSubMessageChange(
			change,
			this.initializeSubMessage(submessage)
		);
	}
	parseSubMessageChange(
		change: changeInstance,
		submessage: subMessageWithChanges
	) {
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				submessage.changes.name = changes;
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				submessage.changes.description = changes;
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACESUBMESSAGENUMBER
			) {
				submessage.changes.interfaceSubMessageNumber = changes;
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (change.currentVersion.transactionToken.id !== '-1') {
				submessage.changes.applicability = {
					previousValue: change.baselineVersion.applicabilityToken,
					currentValue: change.currentVersion.applicabilityToken,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
			submessage.added = true;
		}
		return submessage;
	}
	initializeSubMessage(submessage: subMessage | subMessageWithChanges) {
		let tempMessage: subMessageWithChanges;
		if (!this.isSubMessageWithChanges(submessage)) {
			tempMessage = submessage as subMessageWithChanges;
			tempMessage.changes = {};
		} else {
			tempMessage = submessage;
		}
		return tempMessage;
	}
	isSubMessageWithChanges(
		submessage: subMessage | subMessageWithChanges
	): submessage is subMessageWithChanges {
		return (submessage as subMessageWithChanges)?.changes !== undefined;
	}

	get initialRoute() {
		return combineLatest([
			this.ui.type,
			this.BranchId,
			this.connectionId,
		]).pipe(
			switchMap(([type, id, connection]) =>
				of(
					'/ple/messaging/' +
						'connections/' +
						type +
						'/' +
						id +
						'/' +
						connection +
						'/messages/'
				)
			)
		);
	}

	get endOfRoute() {
		return this.isInDiff.pipe(
			switchMap((val) => iif(() => val, of('/diff'), of('')))
		);
	}

	get connectionsRoute() {
		return combineLatest([this.ui.type, this.BranchId]).pipe(
			switchMap(([type, BranchId]) =>
				of('/ple/messaging/connections/' + type + '/' + BranchId)
			)
		);
	}

	validateMessage(art: `${number}`) {
		return this.warningDialogService.openMessageDialogForValidation(art);
	}

	validateSubmessage(art: `${number}`) {
		return this.warningDialogService.openSubMessageDialogForValidation(art);
	}
}
