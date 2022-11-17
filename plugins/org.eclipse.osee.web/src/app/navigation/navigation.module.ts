/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';

import { TopLevelNavigationComponent } from './top-level-navigation/top-level-navigation.component';
import { NavigationRoutingModule } from './navigation-routing.module';
import { MatDividerModule } from '@angular/material/divider';

@NgModule({
	declarations: [TopLevelNavigationComponent],
	imports: [
		MatSidenavModule,
		MatToolbarModule,
		MatListModule,
		MatIconModule,
		MatDividerModule,
		CommonModule,
		NavigationRoutingModule,
	],
})
export class NavigationModule {}
