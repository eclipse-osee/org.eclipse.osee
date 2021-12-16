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
import { MimSingleDiffComponent } from '../../diff-views/mim-single-diff/mim-single-diff.component';
import { UsermenuComponent } from './components/usermenu/usermenu/usermenu.component';
import { MessageInterfaceComponent } from './message-interface.component';

const routes: Routes = [
  {
    path: '', component: MessageInterfaceComponent, children: [
    
  ] },
  { path: 'diff', component: MessageInterfaceComponent, resolve: { diff: DiffReportResolver } },
  {
    path: '', component: MimSingleDiffComponent, outlet:'rightSideNav'
  },
  { path: '', component: UsermenuComponent, outlet: 'userMenu' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessageInterfaceRoutingModule { }
