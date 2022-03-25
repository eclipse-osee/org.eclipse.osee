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
import { of } from "rxjs";
import { response } from "../../../connection-view/mocks/Response.mock";
import { CurrentTypesService } from "../../services/current-types.service";
import { enumerationSet } from "../../../shared/types/enum";
import { logicalTypeFormDetail } from "../../../shared/types/logicaltype";
import { PlatformType } from "../../../shared/types/platformType";
import { enumerationSetMock } from "../returnObjects/enumerationset.mock";
import { transactionResultMock } from '../../../../../transactions/transaction.mock';
import { TypesUIService } from '../../../shared/services/ui/types-ui.service';
import { settingsDialogData } from '../../../shared/types/settingsdialog';

export const currentTypesServiceMock: Partial<CurrentTypesService> = {  
    createType(body: PlatformType | Partial<PlatformType>) {
        return of(transactionResultMock)
    },
    updatePreferences(preferences: settingsDialogData) {
        return of(transactionResultMock)
    },
    inEditMode: of(true)
}