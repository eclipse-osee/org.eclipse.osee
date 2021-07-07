import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConnectionViewComponent } from './connection-view.component';

const routes: Routes = [
  { path: '', component: ConnectionViewComponent },
  { path: ':branchType', component: ConnectionViewComponent },
  { path: ':branchType/:branchId', component: ConnectionViewComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConnectionViewRoutingModule { }
