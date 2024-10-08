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

import { of } from 'rxjs';
import { HelpHttpService } from '@osee/shared/services/help';
import { helpPagesResponseMock } from '@osee/shared/testing';

export const helpHttpServiceMock: Partial<HelpHttpService> = {
	getHelpPage(id: string) {
		return of(helpPagesResponseMock[0]);
	},
	getHelpPages(appName: string) {
		return of(helpPagesResponseMock);
	},
};
