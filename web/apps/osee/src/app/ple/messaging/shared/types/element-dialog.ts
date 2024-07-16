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

export interface ElementDialog {
	id: string;
	name: string;
	element: Partial<element>;
	type: PlatformType;
	mode: ElementDialogMode;
	allowArray: boolean;
	arrayChild: boolean;
}

export type ElementDialogMode = 'add' | 'edit';
