import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: 'ple', loadChildren: () => import('./ple/ple.module').then(m => m.PleModule) },
  {
    path: '', //todo remove when main app page is made
    redirectTo: 'ple',
    pathMatch:'full'
  },
  { path: '404', loadChildren: () => import('./page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
  {
    path: '**',
    redirectTo:'/404',
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
