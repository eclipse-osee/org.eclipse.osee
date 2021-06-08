import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PleComponent } from './ple.component';

const routes: Routes = [
  {
    path: 'plconfig',
    loadChildren: () => import('./plconfig/plconfig.module').then(m => m.PlconfigModule),
  },
  {
    path: '',
    component: PleComponent,
  },
  { path: 'messaging', loadChildren: () => import('./messaging/messaging.module').then(m => m.MessagingModule) },
  {
    path: '**',//todo remove when pl app page is made
    redirectTo:'plconfig',
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PleRoutingModule { }
