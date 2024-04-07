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
import { Component, Input, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import {
	MatExpansionPanel,
	MatExpansionPanelHeader,
	MatExpansionPanelTitle,
} from '@angular/material/expansion';
import {
	AttributesEditorComponent,
	ExpandIconComponent,
} from '@osee/shared/components';
import { FormDirective } from '@osee/shared/directives';
import { TransactionService } from '@osee/shared/transactions';
import {
	attribute,
	attributeType,
	modifyArtifact,
	transaction,
} from '@osee/shared/types';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { tab } from '../../../types/artifact-explorer.data';

@Component({
	selector: 'osee-attributes-editor-panel',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		AttributesEditorComponent,
		FormDirective,
		MatExpansionPanel,
		MatExpansionPanelHeader,
		MatExpansionPanelTitle,
		ExpandIconComponent,
	],
	templateUrl: './attributes-editor-panel.component.html',
})
export class AttributesEditorPanelComponent {
	@Input() tab!: tab;

	enum$ = new Observable<string[]>();

	constructor(private transactionService: TransactionService) {}

	saveChanges() {
		if (this.updatedAttributes.value.length > 0) {
			const tx: transaction = {
				branch: this.tab.branchId,
				txComment:
					'Attribute changes for artifact: ' + this.tab.artifact.name,
			};
			const attributes: attributeType[] =
				this.updatedAttributes.value.map((attr) => {
					return { typeId: attr.typeId, value: attr.value };
				});
			const modifyArtifact: modifyArtifact = {
				id: this.tab.artifact.id,
				setAttributes: attributes,
			};
			tx.modifyArtifacts = [modifyArtifact];
			this.transactionService
				.performMutation(tx)
				.pipe(
					tap(() => {
						this.updatedAttributes.next([]);
					})
				)
				.subscribe();
		}
	}

	// Panel open/close state handling

	panelOpen = new BehaviorSubject<boolean>(true);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}

	// Handle output attributes returned from attribute editor

	updatedAttributes = new BehaviorSubject<attribute[]>([]);
	handleUpdatedAttributes(updatedAttributes: attribute[]) {
		this.updatedAttributes.next(updatedAttributes);
	}

	hasChanges(): boolean {
		return this.updatedAttributes.value.length > 0;
	}

	// Handle form status change

	@ViewChild('attributesEditorForm') _attributesEditorForm!: NgForm;
}
