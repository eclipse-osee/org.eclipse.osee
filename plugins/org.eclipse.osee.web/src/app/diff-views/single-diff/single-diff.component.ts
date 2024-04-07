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
import {
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatLine } from '@angular/material/core';
import { MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { SideNavService } from '@osee/shared/services/layout';
import { TransactionService } from '@osee/shared/transactions';
import { applic } from '@osee/shared/types/applicability';
import { transactionInfo } from '@osee/shared/types/change-report';
import { Observable, iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
	selector: 'osee-single-diff',
	templateUrl: './single-diff.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		NgIf,
		AsyncPipe,
		MatButton,
		MatIcon,
		MatLine,
		MatLabel,
	],
	animations: [
		trigger('expandButton', [
			state('closed', style({ transform: 'rotate(180deg)' })),
			state('open', style({ transform: 'rotate(0deg)' })),
			transition(
				'open => closed',
				animate('500ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
			transition(
				'closed => open',
				animate('500ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
		]),
	],
})
export class SingleDiffComponent {
	contents = this.sideNavService.rightSideNavContent.pipe(
		switchMap((content) =>
			of(content).pipe(
				switchMap((val) =>
					iif(
						() =>
							val.transaction?.id !== undefined &&
							val.transaction.id !== '-1',
						this.transactionService.getTransaction(
							val?.transaction?.id || '-1'
						),
						of(undefined)
					).pipe(
						switchMap((tx) => of({ navInfo: val, transaction: tx }))
					)
				)
			)
		)
	);

	transactionInfo!: Observable<transactionInfo>;
	opened = this.sideNavService.rightSideNavOpened;
	constructor(
		private transactionService: TransactionService,
		private sideNavService: SideNavService
	) {}

	isApplic(
		value: string | number | boolean | applic | undefined
	): value is applic {
		return typeof value === 'object' && value?.name !== undefined;
	}

	viewDiff(open: boolean, value: string | number | applic, header: string) {
		this.sideNavService.rightSideNav = {
			opened: open,
			field: header,
			currentValue: value,
		};
	}
}

export default SingleDiffComponent;
