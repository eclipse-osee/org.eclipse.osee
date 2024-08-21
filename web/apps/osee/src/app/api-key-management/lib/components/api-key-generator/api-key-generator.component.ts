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
import {
	ChangeDetectionStrategy,
	Component,
	signal,
	inject,
	WritableSignal,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatButton } from '@angular/material/button';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ApiKeyFormComponent } from '../api-key-form/api-key-form.component';
import { ApiKey, keyScope } from '../../types/apiKey';
import { ApiKeyService } from '../../services/api-key.service';
import { Subject, take, takeUntil, tap } from 'rxjs';
import { UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-api-key-generator',
	standalone: true,
	imports: [MatIcon, MatButton],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './api-key-generator.component.html',
})
export class ApiKeyGeneratorComponent {
	apiKeyService = inject(ApiKeyService);
	uiService = inject(UiService);
	dialog = inject(MatDialog);
	clipboard = inject(Clipboard);
	snackBar = inject(MatSnackBar);

	apiKeyValue: WritableSignal<string | null> = signal(null);
	keyIsVisible = signal<boolean>(false);
	apiKeys = signal<ApiKey[]>([]);

	private destroy$ = new Subject<void>();

	constructor() {
		toSignal(
			this.apiKeyService.getApiKeys().pipe(
				take(1),
				tap((returnedApiKeys: ApiKey[]) => {
					returnedApiKeys.forEach((apiKey) => {
						this.addTab(apiKey);
					});
				})
			)
		);
	}

	closeApiKey(): void {
		this.apiKeyValue.set(null);
		this.keyIsVisible.set(false);
	}

	filterScopes(scopes: keyScope[]) {
		return scopes.filter((scope) => scope.selected);
	}

	scopesToString(scopes: keyScope[]) {
		return scopes.map((scope) => scope.name).join(', ');
	}

	applyScopeNames(scopes: keyScope[]) {
		return scopes.map((scope: keyScope) => {
			if (!scope.name) {
				scope.name = 'no';
			}
			return scope;
		});
	}

	toggleKeyVisibility(): void {
		this.keyIsVisible.set(!this.keyIsVisible());
	}

	copyApiKey(): void {
		const apiKey = this.apiKeyValue();
		if (apiKey !== null) {
			this.clipboard.copy(apiKey);
			this.snackBar.open('API Key Copied!', 'Close', {
				duration: 1000,
			});
		}
	}

	createApiKey(apiKeyToCreate: ApiKey) {
		return this.apiKeyService
			.createApiKey(apiKeyToCreate)
			.pipe(
				takeUntil(this.destroy$),
				take(1),
				tap((uidAndKeyValue: { [key: string]: string }) => {
					this.apiKeyValue.set(uidAndKeyValue['keyValue']);
					apiKeyToCreate.uniqueID = uidAndKeyValue['uniqueID'];
					this.addTab(apiKeyToCreate);
				})
			)
			.subscribe();
	}

	addTab(newApiKey: ApiKey) {
		newApiKey.scopes = this.applyScopeNames(newApiKey.scopes);
		this.apiKeys.update((rows) => [...rows, newApiKey]);
	}

	deleteApiKey(apiKey: ApiKey): void {
		if (apiKey.uniqueID !== undefined) {
			this.apiKeyService.revokeApiKey(apiKey.uniqueID).subscribe();
			this.apiKeys.update((rows) => rows.filter((v) => v !== apiKey));
		} else {
			let errorMessage: string =
				'The API Key has no uniqueID, and cannot be revoked.';
			this.uiService.ErrorText = errorMessage;
			throw Error(errorMessage);
		}
	}

	openApiFormDialog(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = false;
		dialogConfig.restoreFocus = false;

		const dialogRef = this.dialog.open(ApiKeyFormComponent, dialogConfig);

		dialogRef
			.afterClosed()
			.pipe(
				takeUntil(this.destroy$),
				take(1),
				tap((apiKey: ApiKey) => {
					if (apiKey) {
						this.createApiKey({
							...apiKey,
							scopes: this.filterScopes(apiKey.scopes),
						});
					}
				})
			)
			.subscribe();
	}

	ngOnDestroy() {
		this.destroy$.next();
		this.destroy$.complete();
	}
}
