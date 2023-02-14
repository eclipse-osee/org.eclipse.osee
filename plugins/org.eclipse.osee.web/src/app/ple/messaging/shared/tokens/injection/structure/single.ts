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
import { forwardRef } from '@angular/core';
import { CurrentStructureSingleService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from './token';

export const SINGLE_STRUCTURE_SERVICE = {
	provide: forwardRef(() => STRUCTURE_SERVICE_TOKEN),
	useExisting: forwardRef(() => CurrentStructureSingleService),
};
