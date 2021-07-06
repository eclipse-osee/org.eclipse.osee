import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MessagingComponent } from './messaging.component';

const routes: Routes = [
  { path: '', component: MessagingComponent },
  { path: ':branchType/:branchId/messages/:messageId/:subMessageId/:name/elements', loadChildren: () => import('./message-element-interface/message-element-interface.module').then(m => m.MessageElementInterfaceModule) },
  { path: ':branchType/:branchId/messages', loadChildren: () => import('./message-interface/message-interface.module').then(m => m.MessageInterfaceModule) },
  { path: ':branchType/:branchId/types', loadChildren: () => import('./types-interface/types-interface.module').then(m => m.TypesInterfaceModule) },
  { path: ':branchType/:branchId/types/:type', loadChildren: () => import('./types-interface/types-interface.module').then(m => m.TypesInterfaceModule) },
  { path: 'typeSearch', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: ':branchType/typeSearch', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
  { path: ':branchType/:branchId/typeSearch', loadChildren: () => import('./type-element-search/type-element-search.module').then(m => m.TypeElementSearchModule) },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessagingRoutingModule { }
