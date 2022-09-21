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
import { Route, RouterModule, Routes, UrlSegment, UrlSegmentGroup } from '@angular/router';
import { DiffReportResolver } from 'src/app/resolvers/diff-report-resolver.resolver';
import { MimSingleDiffComponent } from '../../diff-views/mim-single-diff/mim-single-diff.component';
import { MimHeaderComponent } from '../shared/components/mim-header/mim-header.component';
import { SingleStructureTableComponent } from './components/single-structure-table/single-structure-table.component';
import { UsermenuComponent } from './components/usermenu/usermenu/usermenu.component';
import { MessageElementInterfaceComponent } from './message-element-interface.component';

const routes: Routes = [
  {
    path: '', children: [
      {
        path: '',
        component: MessageElementInterfaceComponent,
        children: [
        ]
      },
      {
        path: 'diff',
        component: MessageElementInterfaceComponent,
        resolve: { diff: DiffReportResolver }
      },
    ]
  },
  {
    path: ':structureId',
    children: [
      {
        path: '',
        component: SingleStructureTableComponent,
      }, 
      {
        path: 'diff',
        component: SingleStructureTableComponent,
        resolve: { diff: DiffReportResolver }
      },    
    ]
  },
  {
    path: '', component: MimSingleDiffComponent, outlet:'rightSideNav'
  },
  { path: '', component: UsermenuComponent, outlet: 'userMenu' },
  { path: '',component: MimHeaderComponent, outlet:'navigationHeader' }
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessageElementInterfaceRoutingModule { }
