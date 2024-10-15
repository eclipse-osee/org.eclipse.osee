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
	name: Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>;
	description: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>
	>;
	interfaceMessageRate: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE>
	>;
	interfaceMessagePeriodicity: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPERIODICITY
		>
	>;
	interfaceMessageWriteAccess: Required<
		attribute<
			boolean,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEWRITEACCESS
		>
	>;
	interfaceMessageType: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE>
	>;
	interfaceMessageNumber: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGENUMBER>
	>;
	interfaceMessageExclude: Required<
		attribute<boolean, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEEXCLUDE>
	>;
	interfaceMessageIoMode: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEIOCODE>
	>;
	interfaceMessageModeCode: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEMODECODE>
	>;
	interfaceMessageRateVer: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATEVER>
	>;
	interfaceMessagePriority: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPRIORITY>
	>;
	interfaceMessageProtocol: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPROTOCOL>
	>;
	interfaceMessageRptWordCount: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERPTWORDCOUNT
		>
	>;
	interfaceMessageRptCmdWord: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERPTCMDWORD>
	>;
	interfaceMessageRunBeforeProc: Required<
		attribute<
			boolean,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERUNBEFOREPROC
		>
	>;
	interfaceMessageVer: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEVER>
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
