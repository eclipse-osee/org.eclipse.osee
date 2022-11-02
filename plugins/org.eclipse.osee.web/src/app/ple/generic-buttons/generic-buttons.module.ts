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
import { TwoLayerAddButtonComponent } from './two-layer-add-button/two-layer-add-button.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ScrollToTopButtonComponent } from './scroll-to-top-button/scroll-to-top-button.component';

@NgModule({
	declarations: [TwoLayerAddButtonComponent, ScrollToTopButtonComponent],
	imports: [CommonModule, MatButtonModule, MatIconModule],
	exports: [ScrollToTopButtonComponent, TwoLayerAddButtonComponent],
})
export class GenericButtonsModule {}
