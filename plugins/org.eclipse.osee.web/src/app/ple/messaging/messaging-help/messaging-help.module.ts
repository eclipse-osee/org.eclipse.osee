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

import { MessagingHelpRoutingModule } from './messaging-help-routing.module';
import { MessagingHelpComponent } from './messaging-help.component';
import { MatButtonModule } from '@angular/material/button';


@NgModule({
  declarations: [MessagingHelpComponent],
  imports: [
    CommonModule,
    MatButtonModule,
    MessagingHelpRoutingModule
  ]
})
export class MessagingHelpModule { }
