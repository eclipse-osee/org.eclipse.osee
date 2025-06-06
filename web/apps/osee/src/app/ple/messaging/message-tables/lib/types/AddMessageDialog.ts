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
import type { ConnectionNode, subMessage } from '@osee/messaging/shared/types';
import { applic } from '@osee/applicability/types';

export type AddMessageDialog = {
	id: string;
	name: string;
	description: string;
	interfaceMessageRate: string;
	interfaceMessagePeriodicity: string;
	interfaceMessageWriteAccess: boolean;
	interfaceMessageDoubleBuffer: boolean;
	interfaceMessageType: string;
	interfaceMessageNumber: string;
	applicability: applic;
	publisherNodes: ConnectionNode[];
	subscriberNodes: ConnectionNode[];
	subMessages: subMessage[];
};
