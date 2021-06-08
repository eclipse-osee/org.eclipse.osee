import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TypesInterfaceComponent } from './types-interface.component';

const routes: Routes = [
  { path: '', component: TypesInterfaceComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TypesInterfaceRoutingModule { }
