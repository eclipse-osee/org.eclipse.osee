import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlconfigComponent } from './plconfig.component';

const routes: Routes = [
  {
    path: ':branchType/:branchId',
    component:PlconfigComponent,
  },
  {
    path: ':branchType',
    component:PlconfigComponent,
  },
  {
    path: '',
    component: PlconfigComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlconfigRoutingModule { }
