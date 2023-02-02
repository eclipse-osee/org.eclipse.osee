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
import { NamedId } from '../../../types/NamedId';

export interface gridCommanderUser extends NamedId {
	usersContexts: UsersContext[];
}

export interface UsersContext extends NamedId {
	commands: Command[];
	attributes?: ContextAttributes[];
}

export interface ContextAttributes {
	description: string;
	//Can specify attributes in more detail if necessary
	[key: string]: string;
}

export interface CommandGroups {
	contextGroup: string;
	commands: Command[];
}

export interface Command extends NamedId {
	contextGroup: string;
	idIntValue: number;
	idString: string;
	attributes: CommandAttributes;
	parameter: Parameter | null;
}

export interface CommandAttributes {
	description?: string;
	'content url'?: string;
	'http method'?: string;
	'custom command'?: boolean;
}

export interface Parameter extends NamedId {
	idIntValue?: number;
	idString?: string;
	typeAsString: string;
	attributes: ParameterAttributes;
}

export interface ParameterAttributes {
	description?: string;
	'default value'?: string;
	'content url'?: string;
	'is validator used'?: Boolean;
	'validator type'?: string;
}
