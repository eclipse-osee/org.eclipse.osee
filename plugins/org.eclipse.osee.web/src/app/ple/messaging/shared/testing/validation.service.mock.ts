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
import { ValidationService } from '@osee/messaging/shared/services';
import { of } from 'rxjs';
import { connectionValidationResponseMock } from './validation-response.mock';

export const validationServiceMock: Partial<ValidationService> = {
	getConnectionValidation(branchId, connectionId, viewId) {
		return of(connectionValidationResponseMock);
	},
};
