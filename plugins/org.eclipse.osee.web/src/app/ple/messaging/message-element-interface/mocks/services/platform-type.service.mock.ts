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
import { PlatformTypeService } from "../../services/platform-type.service";
import { platformTypesMock } from "../../../shared/mocks/PlatformTypes.mock";

export const platformTypeServiceMock: Partial<PlatformTypeService> = {
    getType(branchId: string, typeId: string) {
        return of(platformTypesMock[0])
    },
    getTypes(branchId: string) {
        return of(platformTypesMock)
    }
}