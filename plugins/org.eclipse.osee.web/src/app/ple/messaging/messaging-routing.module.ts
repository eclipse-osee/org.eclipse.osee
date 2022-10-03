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
import navigationStructure from '../../navigation/top-level-navigation/top-level-navigation-structure';
import { MessagingComponent } from './messaging.component';

const transports = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Transport Type Manager');
const imports = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Import Page');
const reports = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Reports');
const types = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Type View');
const structureNames = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Structure Names');
const typeSearch = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Find Elements By Type');
const connections = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Connection View');
const help=navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children.find(page => page.label === 'Help');
const routes: Routes = [
  { path: '', component: MessagingComponent },
  { path: ':branchType/:branchId/:connection/messages/:messageId/:subMessageId/elements', title:connections?.pageTitle||'OSEE', loadChildren: () => import('./message-element-interface/message-element-interface.module').then(m => m.MessageElementInterfaceModule) },
  { path: ':branchType/:branchId/:connection/messages', title:connections?.pageTitle||'OSEE', loadChildren: () => import('./message-interface/message-interface.module').then(m => m.MessageInterfaceModule) },
  { path: 'types', title:types?.pageTitle||'OSEE', loadChildren: () => import('./types-interface/types-interface.module').then(m => m.TypesInterfaceModule) },
  { path: 'connections', title:connections?.pageTitle||'OSEE', loadChildren: () => import('./connection-view/connection-view.module').then(m => m.ConnectionViewModule) },
  { path: 'typeSearch', title:typeSearch?.pageTitle||'OSEE', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: ':branchType/typeSearch', title:typeSearch?.pageTitle||'OSEE', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: ':branchType/:branchId/typeSearch', title:typeSearch?.pageTitle||'OSEE', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: 'help', title:help?.pageTitle||'OSEE', loadChildren: () => import('./messaging-help/messaging-help.module').then(m => m.MessagingHelpModule) },
  { path: 'structureNames', title:structureNames?.pageTitle||'OSEE', loadChildren: () => import('./structure-names/structure-names.module').then(m => m.StructureNamesModule) },
  { path: ':branchType/:branchId/type/:typeId',title:'OSEE - MIM - Type Detail View', loadChildren: () => import('./type-detail/type-detail.module').then(m => m.TypeDetailModule) },
  { path: 'reports', title: reports?.pageTitle||'OSEE', loadChildren: () => import('./reports/reports.module').then(m => m.ReportsModule) },
  { path: 'import', title: imports?.pageTitle||'OSEE', loadChildren: () => import('./import/import.module').then(m => m.ImportModule) },
  { path: 'transports',title: transports?.pageTitle||'OSEE', loadChildren: () => import('./transports/transports.module').then(m => m.TransportsModule) },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessagingRoutingModule { }
