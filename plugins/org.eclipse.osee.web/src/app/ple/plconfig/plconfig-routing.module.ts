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
import { MimSingleDiffComponent } from '../diff-views/mim-single-diff/mim-single-diff.component';
import { PlconfigComponent } from './plconfig.component';

const routes: Routes = [
  // {
  //   path: ':branchType/:branchId',
  //   component:PlconfigComponent,
  // },
  // {
  //   path: ':branchType',
  //   component:PlconfigComponent,
  // },
  {
    path: '',
    component: PlconfigComponent,
    children: [
      {
        path: ':branchType',
        children: [
          {
            path: ':branchId',
            children: [
              {
                path: 'diff',
                resolve: { diff: DiffReportResolver }
              }
            ]
          },
        ]
      }
    ]
  },
  {
    path: 'diffOpen', component: MimSingleDiffComponent, outlet:'rightSideNav'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlconfigRoutingModule { }
