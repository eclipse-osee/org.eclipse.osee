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
import type { transportType } from '@osee/messaging/shared/types';
import { applicabilitySentinel } from '@osee/applicability/types';

export const ethernetTransportType: Required<transportType> = {
	id: '12345',
	gammaId: '9329804',
	name: {
		id: '-1',
		typeId: '1152921504606847088',
		gammaId: '-1',
		value: 'Ethernet',
	},
	byteAlignValidation: {
		id: '-1',
		typeId: '1682639796635579163',
		gammaId: '-1',
		value: true,
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
	messageGenerationPosition: {
		id: '-1',
		typeId: '7004358807289801815',
		gammaId: '-1',
		value: '',
	},
	messageGenerationType: {
		id: '-1',
		typeId: '7121809480940961886',
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
	directConnection: true,
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
	applicability: applicabilitySentinel,
};

export const nonDirectTransportType: Required<transportType> = {
	id: '12345',
	gammaId: '9329804',
	name: {
		id: '-1',
		typeId: '1152921504606847088',
		gammaId: '-1',
		value: 'Not Direct',
	},
	byteAlignValidation: {
		id: '-1',
		typeId: '1682639796635579163',
		gammaId: '-1',
		value: true,
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
		value: true,
	},
	messageGenerationPosition: {
		id: '-1',
		typeId: '7004358807289801815',
		gammaId: '-1',
		value: '',
	},
	messageGenerationType: {
		id: '-1',
		typeId: '7121809480940961886',
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
	directConnection: false,
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
	applicability: applicabilitySentinel,
};
