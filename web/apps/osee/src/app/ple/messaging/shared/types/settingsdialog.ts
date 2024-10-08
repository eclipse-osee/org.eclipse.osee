import type { PlatformType } from './platformType';
import type { element } from './element';
import type { structure } from './structure';

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
export type settingsDialogData = {
	branchId: string;
	allowedHeaders1: (
		| keyof structure
		| 'txRate'
		| 'publisher'
		| 'subscriber'
		| 'messageNumber'
		| 'messagePeriodicity'
		| ' '
	)[];
	allHeaders1: (
		| keyof structure
		| 'txRate'
		| 'publisher'
		| 'subscriber'
		| 'messageNumber'
		| 'messagePeriodicity'
		| ' '
	)[];
	allowedHeaders2: (keyof (element & PlatformType))[];
	allHeaders2: (keyof (element & PlatformType))[];
	editable: boolean;
	headers1Label: string;
	headers2Label: string;
	headersTableActive: boolean;
	wordWrap: boolean;
};
