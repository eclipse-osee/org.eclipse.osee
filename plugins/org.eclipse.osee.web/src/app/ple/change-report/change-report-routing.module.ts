/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChangeReportComponent } from './change-report.component';

const routes: Routes = [
	{ path: '', component: ChangeReportComponent },
	{ path: ':branchType', component: ChangeReportComponent },
	{ path: ':branchType/:branchId', component: ChangeReportComponent },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class ChangeReportRoutingModule {}
