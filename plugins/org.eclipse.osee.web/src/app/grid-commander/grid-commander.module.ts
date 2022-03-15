import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GridCommanderRoutingModule } from './grid-commander-routing.module';
import { GridCommanderComponent } from './grid-commander.component';


@NgModule({
  declarations: [
    GridCommanderComponent
  ],
  imports: [
    CommonModule,
    GridCommanderRoutingModule
  ]
})
export class GridCommanderModule { }
