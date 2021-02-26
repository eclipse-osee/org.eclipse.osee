import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PleRoutingModule } from './ple-routing.module';
import { PleComponent } from './ple.component';


@NgModule({
  declarations: [PleComponent],
  imports: [
    CommonModule,
    PleRoutingModule
  ]
})
export class PleModule { }
