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
import type { hasApplic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import type { hasChanges } from '@osee/shared/types/change-report';
import type { nodeData } from './node';
import type { subMessage, subMessageWithChanges } from './sub-messages';

export type message = Required<messageAttr> &
	messageRelations &
	hasApplic &
	Partial<messageChanges> & {
		id: `${number}`;
		gammaId: `${number}`;
	};

export type messageRelations = {
	subMessages: (subMessage | subMessageWithChanges)[];
	publisherNodes: nodeData[];
	subscriberNodes: nodeData[];
};
export type messageAttr = {
	name: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>;
	description: attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>;
	interfaceMessageRate: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE
	>;
	interfaceMessagePeriodicity: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPERIODICITY
	>;
	interfaceMessageWriteAccess: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEWRITEACCESS
	>;
	interfaceMessageType: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE
	>;
	interfaceMessageNumber: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGENUMBER
	>;
	interfaceMessageExclude: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEEXCLUDE
	>;
	interfaceMessageDoubleBuffer: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEDOUBLEBUFFER
	>;
	interfaceMessageIoMode: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEIOCODE
	>;
	interfaceMessageModeCode: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEMODECODE
	>;
	interfaceMessageRateVer: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATEVER
	>;
	interfaceMessagePriority: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPRIORITY
	>;
	interfaceMessageProtocol: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPROTOCOL
	>;
	interfaceMessageRptWordCount: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERPTWORDCOUNT
	>;
	interfaceMessageRptCmdWord: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERPTCMDWORD
	>;
	interfaceMessageRunBeforeProc: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERUNBEFOREPROC
	>;
	interfaceMessageVer: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEVER
	>;
};

export type _messageChanges = hasChanges<messageAttr> & hasChanges<hasApplic>;

export type messageChanges = {
	added: boolean;
	deleted: boolean;
	hasSubMessageChanges: boolean;
	changes: _messageChanges;
};

export type messageWithChanges = {} & Required<message>;

export type messageToken = {
	publisherNodes: nodeData[];
	subscriberNodes: nodeData[];
} & Pick<
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
	| 'interfaceMessageDoubleBuffer'
	| 'interfaceMessageIoMode'
	| 'interfaceMessageModeCode'
	| 'interfaceMessageRateVer'
	| 'interfaceMessagePriority'
	| 'interfaceMessageProtocol'
	| 'interfaceMessageRptWordCount'
	| 'interfaceMessageRptCmdWord'
	| 'interfaceMessageRunBeforeProc'
	| 'interfaceMessageVer'
>;
