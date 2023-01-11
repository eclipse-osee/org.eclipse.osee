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
import { of } from 'rxjs';
import { CurrentTypesService } from '../services/current-types.service';
import { PlatformType } from '../../../shared/types/platformType';
import { transactionResultMock } from '../../../../../transactions/transaction.mock';
import { settingsDialogData } from '../../../shared/types/settingsdialog';

export const currentTypesServiceMock: Partial<CurrentTypesService> = {
	createType(body: PlatformType | Partial<PlatformType>) {
		return of(transactionResultMock);
	},
	updatePreferences(preferences: settingsDialogData) {
		return of(transactionResultMock);
	},
	inEditMode: of(true),
};
