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
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DiffReportResolver } from 'src/app/resolvers/diff-report-resolver.resolver';
import { ConnectionViewComponent } from './connection-view.component';

const routes: Routes = [
  { path: '', component: ConnectionViewComponent },
  { path: ':branchType', component: ConnectionViewComponent },
  { path: ':branchType/:branchId', component: ConnectionViewComponent },
  {
    path: ':branchType/:branchId/diff', component: ConnectionViewComponent, resolve: {
    diff:DiffReportResolver
  }}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConnectionViewRoutingModule { }
