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
import type { ConnectionNode } from '@osee/messaging/shared/types';

export interface AddMessageDialog {
	id: string;
	name: string;
	description: string;
	interfaceMessageRate: string;
	interfaceMessagePeriodicity: string;
	interfaceMessageWriteAccess: boolean;
	interfaceMessageType: string;
	interfaceMessageNumber: string;
	initiatingNode: ConnectionNode;
}
