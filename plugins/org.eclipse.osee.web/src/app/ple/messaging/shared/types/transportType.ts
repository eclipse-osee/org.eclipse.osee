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
import {
	applicabilitySentinel,
	hasApplic,
} from '@osee/shared/types/applicability';

export const INTERFACELEVELS = [
	'message',
	'submessage',
	'structure',
	'element',
] as const;

export type interfaceLevels =
	(typeof INTERFACELEVELS)[keyof typeof INTERFACELEVELS];
export interface transportTypeId {
	id: string;
}

export interface transportType
	extends Required<TransportTypeForm>,
		Required<computedTransportTypeAttributes>,
		Required<transportTypeRelations>,
		Required<hasApplic> {}

export interface transportTypeAttributes {
	name: string;
	byteAlignValidation: boolean;
	messageGeneration: boolean;
	byteAlignValidationSize: number;
	messageGenerationType: string;
	messageGenerationPosition: string;
	minimumPublisherMultiplicity: number;
	maximumPublisherMultiplicity: number;
	minimumSubscriberMultiplicity: number;
	maximumSubscriberMultiplicity: number;
	availableMessageHeaders: (keyof message)[];
	availableSubmessageHeaders: (keyof subMessage)[];
	availableStructureHeaders: (
		| keyof structure
		| 'txRate'
		| 'publisher'
		| 'messageNumber'
	)[];
	availableElementHeaders: (keyof element)[];
	interfaceLevelsToUse: interfaceLevels[];
	dashedPresentation: boolean;
	spareAutoNumbering: boolean;
}

export interface computedTransportTypeAttributes {
	directConnection: boolean;
}
export interface transportTypeRelations {}

export interface TransportTypeForm
	extends transportTypeAttributes,
		Partial<transportTypeId>,
		hasApplic {}

export interface createTransportType
	extends Omit<
			transportTypeAttributes,
			| 'availableMessageHeaders'
			| 'availableSubmessageHeaders'
			| 'availableStructureHeaders'
			| 'availableElementHeaders'
			| 'interfaceLevelsToUse'
		>,
		hasApplic,
		Partial<transportTypeId> {
	availableMessageHeaders: string;
	availableSubmessageHeaders: string;
	availableStructureHeaders: string;
	availableElementHeaders: string;
	interfaceLevelsToUse: string;
}

export class TransportType implements TransportTypeForm {
	name: string = '';
	byteAlignValidation: boolean = false;
	messageGeneration: boolean = false;
	byteAlignValidationSize: number = 0;
	messageGenerationType: string = '';
	messageGenerationPosition: string = '';
	minimumPublisherMultiplicity: number = 0;
	maximumPublisherMultiplicity: number = 0;
	minimumSubscriberMultiplicity: number = 0;
	maximumSubscriberMultiplicity: number = 0;
	applicability = applicabilitySentinel;
	dashedPresentation: boolean = false;
	spareAutoNumbering: boolean = false;
	availableMessageHeaders: (keyof message)[] = [];
	availableSubmessageHeaders: (keyof subMessage)[] = [];
	availableStructureHeaders: (
		| keyof structure
		| 'txRate'
		| 'publisher'
		| 'messageNumber'
	)[] = [];
	availableElementHeaders: (keyof element)[] = [];
	interfaceLevelsToUse: interfaceLevels[] = [
		'message',
		'submessage',
		'structure',
		'element',
	];
	id: string = '';
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
		}
	}
}
/**
 * This function makes transport type be in a form that the transaction builder will like
 * @todo Eventually make this return with typeIds and gammas i.e. create a createArtifact
 */
export function serialize<T extends TransportTypeForm>(
	body: T
): createTransportType {
	return {
		availableMessageHeaders:
			'[' + body.availableMessageHeaders.toString() + ']',
		availableSubmessageHeaders:
			'[' + body.availableStructureHeaders.toString() + ']',
		availableStructureHeaders:
			'[' + body.availableStructureHeaders.toString() + ']',
		availableElementHeaders:
			'[' + body.availableElementHeaders.toString() + ']',
		interfaceLevelsToUse: '[' + body.interfaceLevelsToUse.toString() + ']',
		name: body.name,
		byteAlignValidation: body.byteAlignValidation,
		messageGeneration: body.messageGeneration,
		byteAlignValidationSize: body.byteAlignValidationSize,
		messageGenerationType: body.messageGenerationType,
		messageGenerationPosition: body.messageGenerationPosition,
		minimumPublisherMultiplicity: body.minimumPublisherMultiplicity,
		maximumPublisherMultiplicity: body.maximumPublisherMultiplicity,
		minimumSubscriberMultiplicity: body.minimumSubscriberMultiplicity,
		maximumSubscriberMultiplicity: body.maximumSubscriberMultiplicity,
		dashedPresentation: body.dashedPresentation,
		spareAutoNumbering: body.spareAutoNumbering,
		applicability: body.applicability,
		id: body.id,
	};
}
