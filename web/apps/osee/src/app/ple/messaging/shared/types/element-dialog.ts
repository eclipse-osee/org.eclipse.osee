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

import type { element, PlatformType } from '@osee/messaging/shared/types';

export type ElementDialog = {
	id: string;
	name: string;
	//NOTE: do not touch. This is strictly for diffing purposes when creating a transaction(i.e. figuring out what occurred in the dialog)
	startingElement: element;
	element: element;
	type: PlatformType;
	mode: ElementDialogMode;
	allowArray: boolean;
	arrayChild: boolean;
	createdTypes: PlatformType[];
};

export type ElementDialogMode = 'add' | 'edit';
