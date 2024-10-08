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
import { nodesMock } from './nodes-response.mock';
import { ethernetTransportType } from './transport-type.response.mock';

export const connectionMock: connection = {
	id: '1',
	gammaId: '1',
	name: {
		id: '-1',
		typeId: '1152921504606847088',
		gammaId: '-1',
		value: 'connection1',
	},
	description: {
		id: '-1',
		typeId: '1152921504606847090',
		gammaId: '-1',
		value: '',
	},
	transportType: ethernetTransportType,
	nodes: nodesMock,
	applicability: {
		id: '1',
		name: 'Base',
	},
};
