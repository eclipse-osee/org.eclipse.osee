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

import { PleRoutingModule } from './ple-routing.module';
import { PleComponent } from './ple.component';
import { PleSharedMaterialModule } from './ple-shared-material/ple-shared-material.module';

@NgModule({
	declarations: [PleComponent],
	imports: [CommonModule, PleRoutingModule, PleSharedMaterialModule],
})
export class PleModule {}
