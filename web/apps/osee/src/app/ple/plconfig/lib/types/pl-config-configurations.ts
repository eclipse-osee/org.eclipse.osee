/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { difference, hasChanges } from '@osee/shared/types/change-report';
import { NamedIdAndDescription } from '@osee/shared/types';

export interface configuration {
	name: string;
	description: string;
	copyFrom?: string;
	configurationGroup?: string[];
	productApplicabilities?: string[];
}
export interface editConfiguration extends configuration {
	configurationGroup?: string[];
}

export type configGroup = _configGroup & _configGroupChanges;

type _configGroup = NamedIdAndDescription & { configurations: string[] };

type __configGroupChanges = hasChanges<{
	name: string;
	configurations: string[];
	description: string;
}>;
type _configGroupChanges = {
	deleted?: boolean;
	added?: boolean;
	changes?: __configGroupChanges;
};
