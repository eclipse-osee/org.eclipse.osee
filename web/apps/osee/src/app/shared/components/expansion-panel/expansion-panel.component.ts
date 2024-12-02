/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { NgClass } from '@angular/common';
import { Component, OnInit, effect, input, signal } from '@angular/core';
import {
	MatExpansionPanel,
	MatExpansionPanelHeader,
	MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { ExpandIconComponent } from '../expand-icon/expand-icon.component';

@Component({
	selector: 'osee-expansion-panel',
	imports: [
		MatExpansionPanel,
		MatExpansionPanelHeader,
		MatExpansionPanelTitle,
		NgClass,
		ExpandIconComponent,
	],
	template: `<mat-expansion-panel
		[expanded]="panelOpen()"
		class="[&>div>div]:tw-px-0"
		hideToggle>
		<mat-expansion-panel-header
			(click)="togglePanel()"
			[ngClass]="panelOpen() ? 'tw-shadow-md' : ''">
			<mat-panel-title class="tw-flex tw-gap-4">
				<osee-expand-icon [open]="panelOpen()" />
				<div class="tw-font-bold">{{ title() }}</div>
			</mat-panel-title>
		</mat-expansion-panel-header>
		<ng-content></ng-content>
	</mat-expansion-panel>`,
})
export class ExpansionPanelComponent implements OnInit {
	openDefault = input(false);
	title = input('');
	panelOpen = signal(false);

	openEffect = effect(() => this.panelOpen.set(this.openDefault()));

	ngOnInit(): void {
		this.panelOpen.set(this.openDefault());
	}

	// panel open/close state handling
	togglePanel() {
		this.panelOpen.set(!this.panelOpen());
	}
}
