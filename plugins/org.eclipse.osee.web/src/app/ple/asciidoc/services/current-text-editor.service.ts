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
import { Injectable } from '@angular/core';
import { TransactionService } from '@osee/shared/transactions';
import { transaction } from '@osee/shared/types';
import {
	combineLatest,
	distinctUntilChanged,
	filter,
	of,
	repeatWhen,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { AttributeService } from 'src/app/ple-services/http/attribute.service';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';
import { TextEditorUiService } from './text-editor-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentTextEditorService {
	// This utilizes the two services to create markdown content obs

	constructor(
		private txtEditServe: TextEditorUiService,
		private attrServe: AttributeService,
		private transactionServe: TransactionService
	) {}

	private _mdContent$ = combineLatest([
		this.txtEditServe.id,
		this.txtEditServe.artifactId,
	]).pipe(
		//remove all cases you dont want to hit api with, ie not 0, -1, empty string (defaults)
		//destructuring array, 1to1 index match naming
		filter(
			([branchId, artifactID]) =>
				branchId != '0' &&
				branchId != '-1' &&
				branchId != '' &&
				artifactID != '0' &&
				artifactID != '-1' &&
				artifactID != ''
		),
		switchMap(([branchId, artifactID]) =>
			this.attrServe
				.getMarkDownContent(
					branchId,
					artifactID,
					ATTRIBUTETYPEIDENUM.MARKDOWNCONTENT
				)
				.pipe(repeatWhen((_) => this.txtEditServe.update))
		)
	);

	private _createUpdateTransaction = combineLatest([
		this.txtEditServe.id,
		this.txtEditServe.artifactId,
		this.userInput,
	]).pipe(
		take(1),
		distinctUntilChanged(),
		filter(
			([branchId, artifactID]) =>
				branchId != '0' &&
				branchId != '-1' &&
				branchId != '' &&
				artifactID != '0' &&
				artifactID != '-1' &&
				artifactID != ''
		),
		switchMap(([branchId, artifactID, userInput]) =>
			of<transaction>({
				branch: branchId,
				txComment: 'Updating markdown Content',
				modifyArtifacts: [
					{
						id: artifactID,
						setAttributes: [
							{
								typeId: ATTRIBUTETYPEIDENUM.MARKDOWNCONTENT,
								value: userInput,
							},
						],
					},
				],
			})
		)
	);
	private _updateArtifact = this._createUpdateTransaction.pipe(
		switchMap((tx) => this.transactionServe.performMutation(tx)),
		tap((_) => (this.txtEditServe.updated = true))
	);

	get mdContent() {
		return this._mdContent$;
	}

	get userInput() {
		return this.txtEditServe.userInput;
	}

	set userInputValue(value: string) {
		this.txtEditServe.userInputUpdate = value;
	}

	get updateArtifact() {
		return this._updateArtifact;
	}
}
