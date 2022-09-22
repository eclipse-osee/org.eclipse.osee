/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MessagingRoutingModule } from './messaging-routing.module';
import { MessagingComponent } from './messaging.component';
import { PleSharedMaterialModule } from '../ple-shared-material/ple-shared-material.module';
import { MatIconModule } from '@angular/material/icon';


@NgModule({
  declarations: [MessagingComponent],
  imports: [
    CommonModule,
    PleSharedMaterialModule,
    MessagingRoutingModule,
    MatIconModule
  ]
})
export class MessagingModule { }
