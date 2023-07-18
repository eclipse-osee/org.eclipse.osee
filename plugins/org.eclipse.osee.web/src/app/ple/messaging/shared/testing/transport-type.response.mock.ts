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
import { applicabilitySentinel } from '@osee/shared/types/applicability';

export const ethernetTransportType: Required<transportType> = {
	id: '1233456',
	name: 'Ethernet',
	byteAlignValidation: true,
	messageGeneration: false,
	byteAlignValidationSize: 4,
	messageGenerationType: '',
	messageGenerationPosition: '',
	minimumPublisherMultiplicity: 1,
	maximumPublisherMultiplicity: 1,
	minimumSubscriberMultiplicity: 1,
	maximumSubscriberMultiplicity: 1,
	directConnection: true,
	dashedPresentation: false,
	availableMessageHeaders: [],
	availableSubmessageHeaders: [],
	availableStructureHeaders: [],
	availableElementHeaders: [],
	interfaceLevelsToUse: ['message', 'submessage', 'structure', 'element'],
	applicability: applicabilitySentinel,
};

export const nonDirectTransportType: Required<transportType> = {
	id: '1233456',
	name: 'Not Direct',
	byteAlignValidation: true,
	messageGeneration: true,
	byteAlignValidationSize: 8,
	messageGenerationType: '',
	messageGenerationPosition: '',
	minimumPublisherMultiplicity: 0,
	maximumPublisherMultiplicity: 0,
	minimumSubscriberMultiplicity: 0,
	maximumSubscriberMultiplicity: 0,
	directConnection: false,
	dashedPresentation: false,
	availableMessageHeaders: [],
	availableSubmessageHeaders: [],
	availableStructureHeaders: [],
	availableElementHeaders: [],
	interfaceLevelsToUse: ['message', 'submessage', 'structure', 'element'],
	applicability: applicabilitySentinel,
};
