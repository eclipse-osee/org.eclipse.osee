import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TypeElementSearchComponent } from './type-element-search.component';

const routes: Routes = [
  { path: '', component: TypeElementSearchComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TypeElementSearchRoutingModule { }
