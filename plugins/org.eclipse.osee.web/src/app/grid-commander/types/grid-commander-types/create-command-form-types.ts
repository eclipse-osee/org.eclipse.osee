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
export interface commandObject {
	name: string;
	description: string;
	contentURL: string;
	httpMethod?: string | null;
	customCommand: boolean;
}

export interface parameterObject {
	name: string;
	description: string;
	defaultValue: string;
	isValidatorUsed: boolean;
	validatorType: string;
}
