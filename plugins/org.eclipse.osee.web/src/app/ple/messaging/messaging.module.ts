import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MessagingRoutingModule } from './messaging-routing.module';
import { MessagingComponent } from './messaging.component';
import { PleSharedMaterialModule } from '../ple-shared-material/ple-shared-material.module';


@NgModule({
  declarations: [MessagingComponent],
  imports: [
    CommonModule,
    PleSharedMaterialModule,
    MessagingRoutingModule,
  ]
})
export class MessagingModule { }
