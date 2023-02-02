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
export interface executedCommand {
	id?: string;
	name: string;
	executionFrequency: number;
	commandTimestamp: Date | string | number;
	parameterizedCommand: string;
	favorite?: boolean;
	isValidated?: boolean;
}

export interface parameterizedCommand {
	name: string;
	parameterValue: string;
	contentURL?: string;
	httpMethod?: string;
}

export interface commandHistoryObject {
	'Artifact Id': string;
	Command: string;
	Parameters: string;
	'Times Used': string;
	'Last Used': string;
	Validated: string;
	Favorite: string;
}
