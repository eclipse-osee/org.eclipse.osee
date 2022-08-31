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
import { ApplicabilityListService } from "../services/http/applicability-list.service";

export const applicabilityListServiceMock: Partial<ApplicabilityListService> = {
    getApplicabilities(branchId: string | number) {
        return of([{id:'1',name:'Base'},{id:'2',name:'Second'}])
    },
    getViews(branchId: string | number) {
        return of([{id:'1',name:'Product A'},{id:'2',name:'Product B'}])
    }
}