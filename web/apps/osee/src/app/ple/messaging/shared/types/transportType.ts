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
import {
	element,
	message,
	structure,
	subMessage,
} from '@osee/messaging/shared/types';
import { applicabilitySentinel, hasApplic } from '@osee/applicability/types';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

export const INTERFACELEVELS = [
	'message',
	'submessage',
	'structure',
	'element',
] as const;

export type interfaceLevels =
	(typeof INTERFACELEVELS)[keyof typeof INTERFACELEVELS];
export type transportTypeId = {
	id: `${number}`;
};

export type transportTypeGammaId = {
	gammaId: `${number}`;
};

export type transportType = {} & Required<TransportTypeForm> &
	Required<computedTransportTypeAttributes> &
	Required<transportTypeRelations> &
	Required<hasApplic>;

export type transportTypeAttributes = {
	name: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>;
	byteAlignValidation: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.BYTEALIGNVALIDATION
	>;
	messageGeneration: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATION
	>;
	byteAlignValidationSize: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.BYTEALIGNVALIDATIONSIZE
	>;
	messageGenerationType: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATIONTYPE
	>;
	messageGenerationPosition: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATIONPOSITION
	>;
	minimumPublisherMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MINIMUMPUBLISHERMULTIPLICITY
	>;
	maximumPublisherMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MAXIMUMPUBLISHERMULTIPLICITY
	>;
	minimumSubscriberMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MINIMUMSUBSCRIBERMULTIPLICITY
	>;
	maximumSubscriberMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MAXIMUMSUBSCRIBERMULTIPLICITY
	>;
	availableMessageHeaders: attribute<
		(keyof message)[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLEMESSAGEHEADERS
	>;
	availableSubmessageHeaders: attribute<
		(keyof subMessage)[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLESUBMESSAGEHEADERS
	>;
	availableStructureHeaders: attribute<
		(keyof structure | 'txRate' | 'publisher' | 'messageNumber')[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLESTRUCTUREHEADERS
	>;
	availableElementHeaders: attribute<
		(keyof element)[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLEELEMENTHEADERS
	>;
	interfaceLevelsToUse: attribute<
		interfaceLevels[],
		typeof ATTRIBUTETYPEIDENUM.INTERFACELEVELSTOUSE
	>;
	dashedPresentation: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.DASHEDPRESENTATION
	>;
	spareAutoNumbering: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.SPAREAUTONUMBERING
	>;
};

export type computedTransportTypeAttributes = {
	directConnection: boolean;
};
export type transportTypeRelations = object;

export type TransportTypeForm = {} & transportTypeAttributes &
	transportTypeId &
	transportTypeGammaId &
	hasApplic;

export type createTransportType = {
	availableMessageHeaders: string;
	availableSubmessageHeaders: string;
	availableStructureHeaders: string;
	availableElementHeaders: string;
	interfaceLevelsToUse: string;
} & Omit<
	transportTypeAttributes,
	| 'availableMessageHeaders'
	| 'availableSubmessageHeaders'
	| 'availableStructureHeaders'
	| 'availableElementHeaders'
	| 'interfaceLevelsToUse'
> &
	hasApplic &
	Partial<transportTypeId>;

export class TransportType implements TransportTypeForm {
	name: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME> = {
		id: '-1',
		typeId: '1152921504606847088',
		gammaId: '-1',
		value: '',
	};
	byteAlignValidation: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.BYTEALIGNVALIDATION
	> = {
		id: '-1',
		typeId: '1682639796635579163',
		gammaId: '-1',
		value: false,
	};
	messageGeneration: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATION
	> = {
		id: '-1',
		typeId: '6696101226215576386',
		gammaId: '-1',
		value: false,
	};
	byteAlignValidationSize: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.BYTEALIGNVALIDATIONSIZE
	> = {
		id: '-1',
		typeId: '6745328086388470469',
		gammaId: '-1',
		value: 0,
	};
	messageGenerationType: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATIONTYPE
	> = {
		id: '-1',
		typeId: '7121809480940961886',
		gammaId: '-1',
		value: '',
	};
	messageGenerationPosition: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATIONPOSITION
	> = {
		id: '-1',
		typeId: '7004358807289801815',
		gammaId: '-1',
		value: '',
	};
	minimumPublisherMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MINIMUMPUBLISHERMULTIPLICITY
	> = {
		id: '-1',
		typeId: '7904304476851517',
		gammaId: '-1',
		value: 0,
	};
	maximumPublisherMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MAXIMUMPUBLISHERMULTIPLICITY
	> = {
		id: '-1',
		typeId: '8536169210675063038',
		gammaId: '-1',
		value: 0,
	};
	minimumSubscriberMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MINIMUMSUBSCRIBERMULTIPLICITY
	> = {
		id: '-1',
		typeId: '6433031401579983113',
		gammaId: '-1',
		value: 0,
	};
	maximumSubscriberMultiplicity: attribute<
		number,
		typeof ATTRIBUTETYPEIDENUM.MAXIMUMSUBSCRIBERMULTIPLICITY
	> = {
		id: '-1',
		typeId: '7284240818299786725',
		gammaId: '-1',
		value: 0,
	};
	applicability = applicabilitySentinel;
	dashedPresentation: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.DASHEDPRESENTATION
	> = {
		id: '-1',
		typeId: '3564212740439618526',
		gammaId: '-1',
		value: false,
	};
	spareAutoNumbering: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.SPAREAUTONUMBERING
	> = {
		id: '-1',
		typeId: '6696101226215576390',
		gammaId: '-1',
		value: false,
	};
	availableMessageHeaders: attribute<
		(keyof message)[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLEMESSAGEHEADERS
	> = {
		id: '-1',
		typeId: '2811393503797133191',
		gammaId: '-1',
		value: [],
	};
	availableSubmessageHeaders: attribute<
		(keyof subMessage)[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLESUBMESSAGEHEADERS
	> = {
		id: '-1',
		typeId: '3432614776670156459',
		gammaId: '-1',
		value: [],
	};
	availableStructureHeaders: attribute<
		(keyof structure | 'txRate' | 'publisher' | 'messageNumber')[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLESTRUCTUREHEADERS
	> = {
		id: '-1',
		typeId: '3020789555488549747',
		gammaId: '-1',
		value: [],
	};
	availableElementHeaders: attribute<
		(keyof element)[],
		typeof ATTRIBUTETYPEIDENUM.AVAILABLEELEMENTHEADERS
	> = {
		id: '-1',
		typeId: '3757258106573748121',
		gammaId: '-1',
		value: [],
	};
	interfaceLevelsToUse: attribute<
		interfaceLevels[],
		typeof ATTRIBUTETYPEIDENUM.INTERFACELEVELSTOUSE
	> = {
		id: '-1',
		typeId: '1668394842614655222',
		gammaId: '-1',
		value: ['message', 'submessage', 'structure', 'element'],
	};

	id: `${number}` = '-1';
	gammaId: `${number}` = '-1';
	constructor(transportType?: transportType) {
		if (transportType) {
			this.name = transportType.name;
			this.byteAlignValidation = transportType.byteAlignValidation;
			this.byteAlignValidationSize =
				transportType.byteAlignValidationSize;
			this.messageGeneration = transportType.messageGeneration;
			this.messageGenerationType = transportType.messageGenerationType;
			this.messageGenerationPosition =
				transportType.messageGenerationPosition;
			this.minimumPublisherMultiplicity =
				transportType.minimumPublisherMultiplicity;
			this.maximumPublisherMultiplicity =
				transportType.maximumPublisherMultiplicity;
			this.minimumSubscriberMultiplicity =
				transportType.minimumSubscriberMultiplicity;
			this.maximumSubscriberMultiplicity =
				transportType.maximumSubscriberMultiplicity;
			this.dashedPresentation = transportType.dashedPresentation;
			this.applicability = transportType.applicability;
			this.availableMessageHeaders =
				transportType.availableMessageHeaders;
			this.availableSubmessageHeaders =
				transportType.availableSubmessageHeaders;
			this.availableStructureHeaders =
				transportType.availableStructureHeaders;
			this.availableElementHeaders =
				transportType.availableElementHeaders;
			this.interfaceLevelsToUse = transportType.interfaceLevelsToUse;
			this.id = transportType.id;
			this.gammaId = transportType.gammaId;
			this.spareAutoNumbering = transportType.spareAutoNumbering;
		}
	}
}

export function getTransportTypeSentinel() {
	const transportType: transportType = {
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
		id: '-1',
		gammaId: '-1',
		applicability: { id: '1', name: 'Base' },
		directConnection: false,
	};
	return transportType;
}
