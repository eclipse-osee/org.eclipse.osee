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
import { applicabilitySentinel } from '@osee/applicability/types';
import { AddNodeDialog } from './add-node-dialog';

export class DefaultAddNodeDialog implements AddNodeDialog {
	connection: connection = {
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
		applicability: applicabilitySentinel,
		transportType: {
			id: '-1',
			gammaId: '-1',
			directConnection: false,
			applicability: applicabilitySentinel,
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			byteAlignValidation: {
				id: '-1',
				typeId: '1682639796635579163',
				gammaId: '-1',
				value: false,
			},
			byteAlignValidationSize: {
				id: '-1',
				typeId: '6745328086388470469',
				gammaId: '-1',
				value: 0,
			},
			messageGeneration: {
				id: '-1',
				typeId: '6696101226215576386',
				gammaId: '-1',
				value: false,
			},
			messageGenerationType: {
				id: '-1',
				typeId: '7121809480940961886',
				gammaId: '-1',
				value: '',
			},
			messageGenerationPosition: {
				id: '-1',
				typeId: '7004358807289801815',
				gammaId: '-1',
				value: '',
			},
			minimumPublisherMultiplicity: {
				id: '-1',
				typeId: '7904304476851517',
				gammaId: '-1',
				value: 0,
			},
			maximumPublisherMultiplicity: {
				id: '-1',
				typeId: '8536169210675063038',
				gammaId: '-1',
				value: 0,
			},
			minimumSubscriberMultiplicity: {
				id: '-1',
				typeId: '6433031401579983113',
				gammaId: '-1',
				value: 0,
			},
			maximumSubscriberMultiplicity: {
				id: '-1',
				typeId: '7284240818299786725',
				gammaId: '-1',
				value: 0,
			},
			availableMessageHeaders: {
				id: '-1',
				typeId: '2811393503797133191',
				gammaId: '-1',
				value: [],
			},
			availableSubmessageHeaders: {
				id: '-1',
				typeId: '3432614776670156459',
				gammaId: '-1',
				value: [],
			},
			availableStructureHeaders: {
				id: '-1',
				typeId: '3020789555488549747',
				gammaId: '-1',
				value: [],
			},
			availableElementHeaders: {
				id: '-1',
				typeId: '3757258106573748121',
				gammaId: '-1',
				value: [],
			},
			interfaceLevelsToUse: {
				id: '-1',
				typeId: '1668394842614655222',
				gammaId: '-1',
				value: ['message', 'submessage', 'structure', 'element'],
			},
			dashedPresentation: {
				id: '-1',
				typeId: '3564212740439618526',
				gammaId: '-1',
				value: false,
			},
			spareAutoNumbering: {
				id: '-1',
				typeId: '6696101226215576390',
				gammaId: '-1',
				value: false,
			},
		},
		nodes: [],
	};
	node = {
		id: '-1' as const,
		gammaId: '-1' as const,
		name: {
			id: '-1' as const,
			typeId: '1152921504606847088' as const,
			gammaId: '-1' as const,
			value: '',
		},
		description: {
			id: '-1' as const,
			typeId: '1152921504606847090' as const,
			gammaId: '-1' as const,
			value: '',
		},
		applicability: applicabilitySentinel,
		interfaceNodeNumber: {
			id: '-1' as const,
			typeId: '5726596359647826657' as const,
			gammaId: '-1' as const,
			value: '',
		},
		interfaceNodeGroupId: {
			id: '-1' as const,
			typeId: '5726596359647826658' as const,
			gammaId: '-1' as const,
			value: '',
		},
		interfaceNodeBackgroundColor: {
			id: '-1' as const,
			typeId: '5221290120300474048' as const,
			gammaId: '-1' as const,
			value: '',
		},
		interfaceNodeAddress: {
			id: '-1' as const,
			typeId: '5726596359647826656' as const,
			gammaId: '-1' as const,
			value: '',
		},
		interfaceNodeBuildCodeGen: {
			id: '-1' as const,
			typeId: '5806420174793066197' as const,
			gammaId: '-1' as const,
			value: false,
		},
		interfaceNodeCodeGen: {
			id: '-1' as const,
			typeId: '4980834335211418740' as const,
			gammaId: '-1' as const,
			value: false,
		},
		interfaceNodeCodeGenName: {
			id: '-1' as const,
			typeId: '5390401355909179776' as const,
			gammaId: '-1' as const,
			value: '',
		},
		nameAbbrev: {
			id: '-1' as const,
			typeId: '8355308043647703563' as const,
			gammaId: '-1' as const,
			value: '',
		},
		interfaceNodeToolUse: {
			id: '-1' as const,
			typeId: '5863226088234748106' as const,
			gammaId: '-1' as const,
			value: false,
		},
		interfaceNodeType: {
			id: '-1' as const,
			typeId: '6981431177168910500' as const,
			gammaId: '-1' as const,
			value: '',
		},
		notes: {
			id: '-1' as const,
			typeId: '1152921504606847085' as const,
			gammaId: '-1' as const,
			value: '',
		},
	};

	constructor(connection: connection) {
		this.connection = connection;
	}
}
