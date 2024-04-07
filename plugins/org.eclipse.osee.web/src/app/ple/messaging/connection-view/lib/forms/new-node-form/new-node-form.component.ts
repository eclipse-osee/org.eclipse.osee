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
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { nodeData } from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { applic } from '@osee/shared/types/applicability';

@Component({
	selector: 'osee-new-node-form',
	standalone: true,
	imports: [
		CommonModule,
		MatFormField,
		MatLabel,
		MatInput,
		FormsModule,
		MatButton,
		MatSlideToggle,
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
