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
import { ConnectionValidationResult } from '@osee/messaging/shared/types';

export const connectionValidationResponseMock: ConnectionValidationResult = {
	branch: '1',
	viewId: '-1',
	connectionName: 'Name',
	passed: true,
	errors: ['Error 1', 'Error 2'],
};
