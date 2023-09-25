/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import { connection } from '@osee/messaging/shared/types';
import { applicabilitySentinel } from '@osee/shared/types/applicability';
import { AddNodeDialog } from './add-node-dialog';

export class DefaultAddNodeDialog implements AddNodeDialog {
	connection: connection = {
		id: '',
		name: '',
		description: '',
		transportType: {
			id: '',
			name: '',
			byteAlignValidation: false,
			messageGeneration: false,
			byteAlignValidationSize: 0,
			messageGenerationType: '',
			messageGenerationPosition: '',
			minimumPublisherMultiplicity: 0,
			maximumPublisherMultiplicity: 0,
			minimumSubscriberMultiplicity: 0,
			maximumSubscriberMultiplicity: 0,
			directConnection: false,
			spareAutoNumbering: false,
			applicability: applicabilitySentinel,
			dashedPresentation: false,
			availableMessageHeaders: [],
			availableSubmessageHeaders: [],
			availableStructureHeaders: [],
			availableElementHeaders: [],
			interfaceLevelsToUse: [],
		},
		nodes: [],
	};
	node = {
		id: '',
		name: '',
		description: '',
		interfaceNodeNumber: '',
		interfaceNodeGroupId: '',
		interfaceNodeBackgroundColor: '',
		interfaceNodeAddress: '',
		interfaceNodeBuildCodeGen: false,
		interfaceNodeCodeGen: false,
		interfaceNodeCodeGenName: '',
		nameAbbrev: '',
		interfaceNodeToolUse: false,
		interfaceNodeType: '',
		notes: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	};

	constructor(connection: connection) {
		this.connection = connection;
	}
}
