import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GridCommanderComponent } from './grid-commander.component';

const routes: Routes = [{ path: '', component: GridCommanderComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GridCommanderRoutingModule { }
