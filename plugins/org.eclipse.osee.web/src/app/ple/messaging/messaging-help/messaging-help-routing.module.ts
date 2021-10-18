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
import { MessagingHelpComponent } from './messaging-help.component';

const routes: Routes = [{ path: '', component: MessagingHelpComponent }, { path: 'columnDescriptions', loadChildren: () => import('./column-descriptions-message-help/column-descriptions-message-help.module').then(m => m.ColumnDescriptionsMessageHelpModule) }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessagingHelpRoutingModule { }
