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
import { DialogService } from "../services/dialog.service";
import { extendedFeature } from "../types/features/base";
import { testDataResponse } from "./mockTypes";

export const DialogServiceMock: Partial<DialogService> = {
    openConfigMenu(header: string, editable: string) {
        return of(testDataResponse);
    },
    displayFeatureMenu(feature: extendedFeature) {
        return of(testDataResponse)
    }
}