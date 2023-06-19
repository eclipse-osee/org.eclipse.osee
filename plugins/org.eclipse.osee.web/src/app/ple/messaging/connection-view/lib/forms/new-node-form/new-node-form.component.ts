/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { nodeData } from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { applic } from '@osee/shared/types/applicability';

@Component({
	selector: 'osee-new-node-form',
	standalone: true,
	imports: [
		CommonModule,
		MatFormFieldModule,
		MatInputModule,
		FormsModule,
		MatButtonModule,
		MatSlideToggleModule,
		ApplicabilitySelectorComponent,
	],
	templateUrl: './new-node-form.component.html',
})
export class NewNodeFormComponent {
	@Input() node!: nodeData;
	@Input() fieldPrefix: string = '';

	updateApplic(applic: applic) {
		this.node.applicability = applic;
	}
}
