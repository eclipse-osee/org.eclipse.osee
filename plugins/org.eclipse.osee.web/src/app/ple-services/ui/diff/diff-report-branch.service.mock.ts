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
import { testBranchListing } from "src/app/ple/plconfig/testing/mockBranchService";
import { changeReportMock } from "../../http/change-report.mock";
import { differenceReportMock } from "../../http/difference-report.mock";
import { DiffReportBranchService } from "./diff-report-branch.service";

export const diffReportBranchServiceMock: Partial<DiffReportBranchService> = {
    differences: of(changeReportMock),
    parentBranch:of(testBranchListing[0].parentBranch.id),
    differenceReport: of(differenceReportMock)
}