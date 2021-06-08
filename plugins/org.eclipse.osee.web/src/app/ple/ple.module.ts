import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PleRoutingModule } from './ple-routing.module';
import { PleComponent } from './ple.component';
import { PleSharedMaterialModule } from './ple-shared-material/ple-shared-material.module';


@NgModule({
  declarations: [PleComponent],
  imports: [
    CommonModule,
    PleRoutingModule,
    PleSharedMaterialModule
  ]
})
export class PleModule { }
