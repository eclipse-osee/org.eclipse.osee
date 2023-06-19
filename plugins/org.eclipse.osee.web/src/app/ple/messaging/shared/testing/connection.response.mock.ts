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

import type { connection } from '@osee/messaging/shared/types';
import { nodesMock } from 'src/app/ple/messaging/shared/testing/nodes-response.mock';
import { ethernetTransportType } from './transport-type.response.mock';

export const connectionMock: connection = {
	name: 'connection1',
	description: '',
	transportType: ethernetTransportType,
	nodes: nodesMock,
	applicability: {
		id: '1',
		name: 'Base',
	},
};
