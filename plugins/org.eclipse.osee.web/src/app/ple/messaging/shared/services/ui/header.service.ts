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
import { Injectable } from '@angular/core';
import { BehaviorSubject, from, iif, of } from 'rxjs';
import { filter, map, mergeMap, reduce, shareReplay } from 'rxjs/operators';
import type {
	branchSummary,
	branchSummaryHeaderDetail,
	diffReportSummaryHeaderDetail,
	diffReportSummaryItem,
	element,
	elementHeaderDetail,
	message,
	messageHeaderDetail,
	structure,
	structureHeaderDetail,
	subMessage,
	subMessageHeaderDetail,
	transportType,
	transportTypeSummaryHeaderDetail,
} from '@osee/messaging/shared/types';
import { headerDetail } from '@osee/shared/types';

export const HeaderKeysEnum = {
	NONE: '',
	CHANGE_REPORT_ROW: 'changeReportRow',
	ELEMENT: 'element',
	IMPORT_ENUM_SET: 'importEnumSet',
	IMPORT_MESSAGE: 'importMessage',
	IMPORT_NODE: 'importNode',
	IMPORT_PLATFORM_TYPE: 'importPlatformType',
	MESSAGE: 'message',
	NODE_TRACE_REQ: 'nodeTraceReq',
	STRUCTURE: 'structure',
	SUBMESSAGE: 'submessage',
} as const;

export type HeaderKeys = (typeof HeaderKeysEnum)[keyof typeof HeaderKeysEnum];

@Injectable({
	providedIn: 'root',
})
export class HeaderService {
	private _allElements = new BehaviorSubject<elementHeaderDetail[]>([
		{
			header: 'name',
			description: 'Name of element',
			humanReadable: 'Name',
		},
		{
			header: 'platformType',
			description: 'Platform Type of Element',
			humanReadable: 'Type',
		},
		{
			header: 'interfaceElementIndexStart',
			description: 'Starting Index of Element Array',
			humanReadable: 'Start Index',
		},
		{
			header: 'interfaceElementIndexEnd',
			description: 'End Index of Element Array',
			humanReadable: 'End Index',
		},
		{
			header: 'logicalType',
			description: 'Primitive Type of Element',
			humanReadable: 'Logical Type',
		},
		{
			header: 'interfaceDefaultValue',
			description: 'Default value of Element or Element Array',
			humanReadable: 'Default',
		},
		{
			header: 'interfacePlatformTypeMinval',
			description: 'Minimum Value of Element',
			humanReadable: 'Min',
		},
		{
			header: 'interfacePlatformTypeMaxval',
			description: 'Maximum Value of Element',
			humanReadable: 'Max',
		},
		{
			header: 'interfacePlatformTypeDescription',
			description: 'Description of the Type',
			humanReadable: 'Type Description',
		},
		{
			header: 'beginWord',
			description: '(Computed) Beginning Word of Element/Element Array',
			humanReadable: 'Begin Word',
		},
		{
			header: 'endWord',
			description: '(Computed) Ending Word of Element/Element Array',
			humanReadable: 'End Word',
		},
		{
			header: 'beginByte',
			description: '(Computed) Beginning Byte of Element/Element Array',
			humanReadable: 'Begin Byte',
		},
		{
			header: 'endByte',
			description: '(Computed) Ending Byte of Element/Element Array',
			humanReadable: 'End Byte',
		},
		{
			header: 'interfaceElementAlterable',
			description: 'Whether or not a given Element is alterable',
			humanReadable: 'Alterable',
		},
		{
			header: 'description',
			description: 'Description of a given element',
			humanReadable: 'Description',
		},
		{
			header: 'notes',
			description:
				'Notes corresponding to a given element, for example, specific enum literal descriptions for a given element',
			humanReadable: 'Notes',
		},
		{
			header: 'applicability',
			description: 'Applicability of a given element',
			humanReadable: 'Applicability',
		},
		{
			header: 'units',
			description:
				'Units of the platform type associated with the given element',
			humanReadable: 'Units',
		},
		{
			header: 'enumLiteral',
			description: 'Enumerated Literals of Element',
			humanReadable: 'Enumerated Literals',
		},
	]).pipe(shareReplay({ bufferSize: 1, refCount: true }));

	private _allStructures = new BehaviorSubject<structureHeaderDetail[]>([
		{
			header: 'name',
			description: 'Name of structure',
			humanReadable: 'Name',
		},
		{
			header: 'description',
			description: 'Description of a given structure',
			humanReadable: 'Description',
		},
		{
			header: 'interfaceMinSimultaneity',
			description: 'Minimum occurences of a given structure',
			humanReadable: 'Min Simult.',
		},
		{
			header: 'interfaceMaxSimultaneity',
			description: 'Maximum occurences of a given structure',
			humanReadable: 'Max Simult.',
		},
		{
			header: 'interfaceTaskFileType',
			description: 'Type of Task File',
			humanReadable: 'Task File Type',
		},
		{
			header: 'interfaceStructureCategory',
			description: 'Category of Structure',
			humanReadable: 'Category',
		},
		{
			header: 'numElements',
			description: '(Computed) Number of elements in a given structure',
			humanReadable: 'Num. Elements',
		},
		{
			header: 'sizeInBytes',
			description: '(Computed) Size of structure, given in bytes',
			humanReadable: 'Size(B)',
		},
		{
			header: 'bytesPerSecondMinimum',
			description:
				'(Computed) Minimum rate of a given structure calculated as Minimum Simultaneity x Size In Bytes',
			humanReadable: 'Min BPS',
		},
		{
			header: 'bytesPerSecondMaximum',
			description:
				'(Computed) Maximum rate of a given structure calculated as Maximum Simultaneity x Size In Bytes',
			humanReadable: 'Max BPS',
		},
		{
			header: 'applicability',
			description: 'Applicability of a given structure',
			humanReadable: 'Applicability',
		},
		{
			header: 'txRate',
			description: 'Transmission Rate of Message',
			humanReadable: 'Tx Rate',
		},
	]).pipe(shareReplay({ bufferSize: 1, refCount: true }));

	private _allMessages = new BehaviorSubject<messageHeaderDetail[]>([
		{
			header: 'name',
			description: 'Name of message',
			humanReadable: 'Name',
		},
		{
			header: 'description',
			description: 'Description of a given message',
			humanReadable: 'Description',
		},
		{
			header: 'interfaceMessageNumber',
			description: 'Order of message',
			humanReadable: 'Message Number',
		},
		{
			header: 'interfaceMessagePeriodicity',
			description:
				'Periodicity of message(i.e. Aperiodic,OnDemand, Periodic)',
			humanReadable: 'Periodicity',
		},
		{
			header: 'interfaceMessageRate',
			description: 'Rate at which message is transmitted',
			humanReadable: 'TxRate',
		},
		{
			header: 'interfaceMessageWriteAccess',
			description: 'TBD',
			humanReadable: 'Read/Write',
		},
		{
			header: 'interfaceMessageType',
			description: 'Type of Message',
			humanReadable: 'Type',
		},
		{
			header: 'applicability',
			description: 'Applicability of a given message',
			humanReadable: 'Applicability',
		},
		{
			header: 'publisherNodes',
			description: 'Sender(s) of the message',
			humanReadable: 'Publisher Nodes',
		},
		{
			header: 'subscriberNodes',
			description: 'Recipient(s) of the message',
			humanReadable: 'Subscriber Nodes',
		},
	]);

	private _allSubMessages = new BehaviorSubject<subMessageHeaderDetail[]>([
		{
			header: 'name',
			description: 'Name of submessage',
			humanReadable: 'SubMessage Name',
		},
		{
			header: 'description',
			description: 'Description of submessage',
			humanReadable: 'SubMessage Description',
		},
		{
			header: 'interfaceSubMessageNumber',
			description: 'Order of submessage',
			humanReadable: 'SubMessage Number',
		},
		{
			header: 'applicability',
			description: 'Applicability of a given submessage',
			humanReadable: 'Applicability',
		},
	]);

	private _allBranchSummary = new BehaviorSubject<
		branchSummaryHeaderDetail[]
	>([
		{
			header: 'pcrNo',
			description: "Workflow number of the branch's associated artifact",
			humanReadable: 'Workflow',
		},
		{
			header: 'description',
			description: 'Description of the branch',
			humanReadable: 'Description',
		},
		{
			header: 'compareBranch',
			description: 'Branch being compared against',
			humanReadable: 'Compare Against',
		},
		{
			header: 'reportDate',
			description: 'Date the report was generated',
			humanReadable: 'Report Date',
		},
	]);

	private _allDiffReportSummary = new BehaviorSubject<
		diffReportSummaryHeaderDetail[]
	>([
		{
			header: 'changeType',
			description: 'Type of object that was changed',
			humanReadable: 'Change Type',
		},
		{
			header: 'action',
			description: 'Action taken on the changed object',
			humanReadable: 'Action',
		},
		{
			header: 'name',
			description: 'Name of the changed object',
			humanReadable: 'Name',
		},
		{
			header: 'details',
			description: 'Change details',
			humanReadable: 'Details',
		},
	]);

	private _allTransportTypes = new BehaviorSubject<
		transportTypeSummaryHeaderDetail[]
	>([
		{
			header: 'name',
			description: 'Name of transport type',
			humanReadable: 'Name',
		},
		{
			header: 'byteAlignValidation',
			description:
				'Whether or not to use byte validation rules on a per-word basis.',
			humanReadable: 'Byte Align Validation',
		},
		{
			header: 'byteAlignValidationSize',
			description:
				'Number of bytes used to validate word sizing if Byte Align Validation is on',
			humanReadable: 'Byte Align Validation Size',
		},
		{
			header: 'messageGeneration',
			description:
				'Whether or not to generate message information for MIM artifacts',
			humanReadable: 'Message Generation',
		},
		{
			header: 'messageGenerationPosition',
			description:
				"Location within a list for generation to use for MIM Artifacts if Message Generation is true. This is an array mapped to the related artifacts(NOTE: must be of the same artifact type). Position '0' is the first element in a list of elements. Position 'LAST' is the last element in a list of elements.",
			humanReadable: 'Message Generation Position',
		},
		{
			header: 'messageGenerationType',
			description:
				'Type of message information generation to use for MIM artifacts if Message Generation is true. Examples include Relational, Dynamic.',
			humanReadable: 'Message Generation Type',
		},
	]);

	private _allTransportTypeHeaders = this._allTransportTypes.pipe(
		mergeMap((transports) =>
			from(transports).pipe(
				map((transport) => transport.header),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as Extract<keyof transportType, string>[]
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allBranchSummaryHeaders = this._allBranchSummary.pipe(
		mergeMap((summary) =>
			from(summary).pipe(
				map((sum) => sum.header),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as Extract<keyof branchSummary, string>[]
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allDiffReportSummaryHeaders = this._allDiffReportSummary.pipe(
		mergeMap((summary) =>
			from(summary).pipe(
				map((sum) => sum.header),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as Extract<keyof diffReportSummaryItem, string>[]
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allElementsHeaders = this._allElements.pipe(
		mergeMap((elements) =>
			from(elements).pipe(
				map((element) => element.header),
				reduce((acc, curr) => [...acc, curr], [] as (keyof element)[])
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allStructureHeaders = this._allStructures.pipe(
		mergeMap((structures) =>
			from(structures).pipe(
				map((structure) => structure.header),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as Extract<keyof structure, string>[]
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allMessageHeaders = this._allMessages.pipe(
		mergeMap((messages) =>
			from(messages).pipe(
				map((message) => message.header),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as Extract<keyof message, string>[]
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allSubMessageHeaders = this._allSubMessages.pipe(
		mergeMap((submessages) =>
			from(submessages).pipe(
				map((submessage) => submessage.header),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as Extract<keyof subMessage, string>[]
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	constructor() {}

	get AllBranchSummaryHeaders() {
		return this._allBranchSummaryHeaders;
	}

	get AllBranchSummary() {
		return this._allBranchSummary;
	}

	get AllDiffReportSummaryHeaders() {
		return this._allDiffReportSummaryHeaders;
	}

	get AllDiffReportSummary() {
		return this._allDiffReportSummary;
	}

	get AllElementHeaders() {
		return this._allElementsHeaders;
	}

	get AllElements() {
		return this._allElements;
	}

	get AllStructureHeaders() {
		return this._allStructureHeaders;
	}

	get AllStructures() {
		return this._allStructures;
	}

	get AllMessages() {
		return this._allMessages;
	}

	get AllMessageHeaders() {
		return this._allMessageHeaders;
	}

	get AllSubMessages() {
		return this._allSubMessages;
	}

	get AllSubMessageHeaders() {
		return this._allSubMessageHeaders;
	}

	get AllTransportTypes() {
		return this._allTransportTypes;
	}

	get AllTransportTypeHeaders() {
		return this._allTransportTypeHeaders;
	}

	/**
	 * @deprecated transition to getTableHeaderByName
	 */
	getHeaderByName<T>(value: keyof T, type: string) {
		return iif(
			() => type === 'message',
			this.AllMessages.pipe(
				mergeMap((messages) =>
					from(messages).pipe(
						filter((message) => message.header === value)
					)
				)
			), //message obs
			iif(
				() => type === 'submessage',
				this.AllSubMessages.pipe(
					mergeMap((submessages) =>
						from(submessages).pipe(
							filter((submessage) => submessage.header === value)
						)
					)
				), //submessage obs
				iif(
					() => type === 'structure',
					this.AllStructures.pipe(
						mergeMap((structures) =>
							from(structures).pipe(
								filter(
									(structure) => structure.header === value
								)
							)
						)
					), //structure obs
					iif(
						() => type === 'element',
						this.AllElements.pipe(
							mergeMap((elements) =>
								from(elements).pipe(
									filter(
										(element) => element.header === value
									)
								)
							)
						), //element obs
						iif(
							() => type === 'branchSummary',
							this.AllBranchSummary.pipe(
								mergeMap((summary) =>
									from(summary).pipe(
										filter((sum) => sum.header === value)
									)
								)
							), //branchSummary obs
							iif(
								() => type === 'diffReportSummary',
								this.AllDiffReportSummary.pipe(
									mergeMap((summary) =>
										from(summary).pipe(
											filter(
												(sum) => sum.header === value
											)
										)
									)
								),
								iif(
									() => type === 'transportType',
									this.AllTransportTypes.pipe(
										mergeMap((summary) =>
											from(summary).pipe(
												filter(
													(sum) =>
														sum.header === value
												)
											)
										)
									), //transport type obs
									of<headerDetail<T>>()
								)
							)
						)
					)
				)
			)
		);
	}
}
