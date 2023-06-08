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
export interface transportType extends transportTypeAttributes {
	id?: string;
}

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
}
