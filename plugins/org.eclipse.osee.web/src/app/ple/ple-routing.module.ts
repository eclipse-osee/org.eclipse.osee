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
import { PleComponent } from './ple.component';

const routes: Routes = [
  {
    path: '',
    component: PleComponent,
  },
  {
    path: 'plconfig',
    loadChildren: () => import('./plconfig/plconfig.module').then(m => m.PlconfigModule),
  },
  { path: 'messaging', loadChildren: () => import('./messaging/messaging.module').then(m => m.MessagingModule) },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PleRoutingModule { }
