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
import { NamedId } from '@osee/shared/types';

export type gridCommanderUser = {
	usersContexts: UsersContext[];
} & NamedId;

export type UsersContext = {
	commands: Command[];
	attributes?: ContextAttributes[];
} & NamedId;

export type ContextAttributes = {
	description: string;
	//Can specify attributes in more detail if necessary
	[key: string]: string;
};

export type CommandGroups = {
	contextGroup: string;
	commands: Command[];
};

export type Command = {
	contextGroup: string;
	idIntValue: number;
	idString: string;
	attributes: CommandAttributes;
	parameter: Parameter | null;
} & NamedId;

export type CommandAttributes = {
	description?: string;
	'content url'?: string;
	'http method'?: string;
	'custom command'?: boolean;
};

export type Parameter = {
	idIntValue?: number;
	idString?: string;
	typeAsString: string;
	attributes: ParameterAttributes;
} & NamedId;

export type ParameterAttributes = {
	description?: string;
	'default value'?: string;
	'content url'?: string;
	'is validator used'?: boolean;
	'validator type'?: string;
};
