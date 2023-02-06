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
import { InjectionToken } from '@angular/core';
import { CurrentStructureService } from '@osee/messaging/shared/services';

/**
 * Token that will provide access to structure information
 */
export const STRUCTURE_SERVICE_TOKEN =
	new InjectionToken<CurrentStructureService>('STRUCTURE SERVICE TOKEN');
