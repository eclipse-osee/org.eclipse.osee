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
import { MessagingComponent } from './messaging.component';

const routes: Routes = [
  { path: '', component: MessagingComponent },
  { path: ':branchType/:branchId/messages/:messageId/:subMessageId/:name/elements', loadChildren: () => import('./message-element-interface/message-element-interface.module').then(m => m.MessageElementInterfaceModule) },
  { path: ':branchType/:branchId/:connection/messages/:messageId/:subMessageId/:name/elements', loadChildren: () => import('./message-element-interface/message-element-interface.module').then(m => m.MessageElementInterfaceModule) },
  { path: ':branchType/:branchId/messages', loadChildren: () => import('./message-interface/message-interface.module').then(m => m.MessageInterfaceModule) },
  { path: ':branchType/:branchId/:connection/messages', loadChildren: () => import('./message-interface/message-interface.module').then(m => m.MessageInterfaceModule) },
  { path: ':branchType/:branchId/types', loadChildren: () => import('./types-interface/types-interface.module').then(m => m.TypesInterfaceModule) },
  { path: ':branchType/:branchId/types/:type', loadChildren: () => import('./types-interface/types-interface.module').then(m => m.TypesInterfaceModule) },
  { path: 'connections', loadChildren: () => import('./connection-view/connection-view.module').then(m => m.ConnectionViewModule) },
  { path: 'typeSearch', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: ':branchType/typeSearch', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: ':branchType/:branchId/typeSearch', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: 'help', loadChildren: () => import('./messaging-help/messaging-help.module').then(m => m.MessagingHelpModule) },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessagingRoutingModule { }
