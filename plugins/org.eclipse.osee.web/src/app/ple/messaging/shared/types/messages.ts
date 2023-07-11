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
import type { difference } from '@osee/shared/types/change-report';
import type { applic } from '@osee/shared/types/applicability';
import type { subMessage, subMessageWithChanges } from './sub-messages';
import type { ConnectionNode } from './connection-nodes';
import type { nodeData } from './node';

export interface message {
	id: string;
	name: string;
	description: string;
	subMessages: Array<subMessage | subMessageWithChanges>;
	interfaceMessageRate: string;
	interfaceMessagePeriodicity: string;
	interfaceMessageWriteAccess: boolean;
	interfaceMessageType: string;
	interfaceMessageNumber: string;
	interfaceMessageExclude: boolean;
	interfaceMessageIoMode: string;
	interfaceMessageModeCode: string;
	interfaceMessageRateVer: string;
	interfaceMessagePriority: string;
	interfaceMessageProtocol: string;
	interfaceMessageRptWordCount: string;
	interfaceMessageRptCmdWord: string;
	interfaceMessageRunBeforeProc: boolean;
	interfaceMessageVer: string;
	applicability?: applic;
	publisherNodes: Array<ConnectionNode>;
	subscriberNodes: Array<ConnectionNode>;
}

export interface messageWithChanges extends message {
	added: boolean;
	deleted: boolean;
	hasSubMessageChanges: boolean;
	changes: messageChanges;
}

export interface messageChanges {
	name?: difference;
	description?: difference;
	interfaceMessageRate?: difference;
	interfaceMessagePeriodicity?: difference;
	interfaceMessageWriteAccess?: difference;
	interfaceMessageType?: difference;
	interfaceMessageNumber?: difference;
	interfaceMessageExclude?: difference;
	interfaceMessageIoMode?: difference;
	interfaceMessageModeCode?: difference;
	interfaceMessageRateVer?: difference;
	interfaceMessagePriority?: difference;
	interfaceMessageProtocol?: difference;
	interfaceMessageRptWordCount?: difference;
	interfaceMessageRptCmdWord?: difference;
	interfaceMessageRunBeforeProc?: difference;
	interfaceMessageVer?: difference;
	applicability?: difference;
}

export interface messageToken
	extends Pick<
		message,
		| 'id'
		| 'name'
		| 'description'
		| 'subMessages'
		| 'interfaceMessageRate'
		| 'interfaceMessagePeriodicity'
		| 'interfaceMessageWriteAccess'
		| 'interfaceMessageType'
		| 'interfaceMessageNumber'
		| 'applicability'
		| 'interfaceMessageExclude'
		| 'interfaceMessageIoMode'
		| 'interfaceMessageModeCode'
		| 'interfaceMessageRateVer'
		| 'interfaceMessagePriority'
		| 'interfaceMessageProtocol'
		| 'interfaceMessageRptWordCount'
		| 'interfaceMessageRptCmdWord'
		| 'interfaceMessageRunBeforeProc'
		| 'interfaceMessageVer'
	> {
	publisherNodes: nodeData[];
	subscriberNodes: nodeData[];
}
