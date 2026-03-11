/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import * as forge from 'node-forge';

import { DragAndDropUploadComponent } from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { HttpLoadingService } from '@osee/shared/services/network';

import { UserPublicCertificateManagementService } from '../../services/user-public-certificate-management.service';
import {
	CertificateSummary,
	CertificateProcessResult,
} from '../../types/user-public-certificate-types';

@Component({
	selector: 'osee-user-public-certificate-management',
	standalone: true,
	imports: [
		CommonModule,
		DragAndDropUploadComponent,
		MatButton,
		MatCard,
		MatIcon,
		MatProgressSpinner,
		MatTooltip,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './user-public-certificate-management.component.html',
})
export class UserPublicCertificateManagementComponent {
	private readonly service = inject(UserPublicCertificateManagementService);
	private readonly loadingService = inject(HttpLoadingService);
	protected readonly uiService = inject(UiService);

	protected readonly $loadingGlobal = this.loadingService.isLoading;
	protected readonly loadingGlobal = toSignal(this.$loadingGlobal, {
		initialValue: 'false',
	});

	protected readonly loadingGlobalBusy = computed<boolean>(() => {
		return this.loadingGlobal() === 'true';
	});

	protected readonly existingPemResource =
		this.service.getPublicCertificateResource();

	protected readonly existingPem = computed<string | null>(() => {
		const pem = this.existingPemResource.value();
		if (typeof pem !== 'string') return null;

		const trimmed = pem.trim();
		return trimmed.length ? this.normalizePem(trimmed) : null;
	});

	protected readonly existingSummary = computed<CertificateSummary | null>(
		() => {
			const pem = this.existingPem();
			if (!pem) return null;

			try {
				return this.summarizePem(pem);
			} catch {
				return null;
			}
		}
	);

	protected readonly selectedFile = signal<File | null>(null);
	protected readonly candidatePem = signal<string | null>(null);
	protected readonly candidateSummary = signal<CertificateSummary | null>(
		null
	);
	protected readonly isProcessingCandidate = signal<boolean>(false);

	protected readonly hasExistingCertificate = computed<boolean>(() => {
		const pem = this.existingPem();
		return typeof pem === 'string' && pem.trim().length > 0;
	});

	protected readonly disableDownloadReason = computed<string | null>(() => {
		if (this.loadingGlobalBusy()) return 'A request is in progress.';
		if (this.existingPemResource.isLoading())
			return 'Certificate is still loading.';
		if (!this.existingPem()) return 'No certificate is currently on file.';
		return null;
	});

	protected readonly disableDeleteReason = computed<string | null>(() => {
		if (this.loadingGlobalBusy()) return 'A request is in progress.';
		if (this.existingPemResource.isLoading())
			return 'Certificate is still loading.';
		if (!this.existingPem()) return 'No certificate is currently on file.';
		return null;
	});

	protected readonly disableReplaceReason = computed<string | null>(() => {
		if (this.loadingGlobalBusy()) return 'A request is in progress.';
		if (this.isProcessingCandidate())
			return 'Selected certificate is still being processed.';
		if (!this.selectedFile())
			return 'Select a certificate file to replace the current one.';
		if (!this.candidatePem())
			return 'The selected file isn’t a valid X.509 certificate.';
		if (
			this.candidateSummary() &&
			!this.candidateSummary()!.isDateValidNow
		) {
			return 'Selected certificate is expired or not yet valid.';
		}
		return null;
	});

	protected async onFilesSelected(files: File[]): Promise<void> {
		this.uiService.ErrorText = '';

		const file = files[0] ?? null;
		this.selectedFile.set(file);
		this.candidatePem.set(null);
		this.candidateSummary.set(null);

		if (!file) return;

		this.isProcessingCandidate.set(true);
		try {
			const result = await this.processCertificateFromFile(file);
			this.candidatePem.set(result.pem);
			this.candidateSummary.set(result.summary);

			if (!result.summary.isDateValidNow) {
				this.uiService.ErrorText =
					'Selected certificate is expired or not yet valid. Please choose a valid certificate.';
			}
		} catch (error: unknown) {
			this.uiService.ErrorText = this.toErrorMessage(error);
		} finally {
			this.isProcessingCandidate.set(false);
		}
	}

	protected async onDownloadExisting(): Promise<void> {
		this.uiService.ErrorText = '';

		try {
			const response = await this.service.downloadPublicCertificate();
			const pem = this.normalizePem(response.body ?? '');
			const fileName =
				this.tryParseFileNameFromContentDisposition(
					response.headers.get('Content-Disposition')
				) ?? 'public-cert.pem';

			this.downloadTextFile({
				fileName,
				contents: pem,
				mimeType: 'application/x-pem-file',
			});
		} catch (error: unknown) {
			this.uiService.ErrorText = this.toErrorMessage(error);
		}
	}

	protected async onDeleteExisting(): Promise<void> {
		if (!this.existingPem()) return;

		this.uiService.ErrorText = '';
		try {
			await this.service.deletePublicCertificate();
		} catch (error: unknown) {
			this.uiService.ErrorText = this.toErrorMessage(error);
		}
	}

	protected async onReplace(): Promise<void> {
		this.uiService.ErrorText = '';

		const pem = this.candidatePem();
		if (!pem) {
			this.uiService.ErrorText =
				'No certificate is ready to replace the current one.';
			return;
		}

		try {
			await this.service.uploadPublicCertificate({ certificatePem: pem });

			this.selectedFile.set(null);
			this.candidatePem.set(null);
			this.candidateSummary.set(null);
		} catch (error: unknown) {
			this.uiService.ErrorText = this.toErrorMessage(error);
		}
	}

	private async processCertificateFromFile(
		file: File
	): Promise<CertificateProcessResult> {
		const text = await file.text();
		const pem = this.isPemCertificate(text)
			? this.normalizePem(text)
			: this.convertDerToPem(await file.arrayBuffer());

		const summary = this.summarizePem(pem);
		return { pem, summary };
	}

	private summarizePem(pem: string): CertificateSummary {
		const cert = forge.pki.certificateFromPem(pem);

		const cnAttr = cert.subject.attributes.find(
			(a) => a.name === 'commonName'
		);
		const subjectCn =
			typeof cnAttr?.value === 'string' ? cnAttr.value : null;

		const notBefore = cert.validity.notBefore;
		const notAfter = cert.validity.notAfter;
		const now = new Date();

		return {
			subjectCn,
			notBefore,
			notAfter,
			isDateValidNow: now >= notBefore && now <= notAfter,
		};
	}

	private isPemCertificate(text: string): boolean {
		return text.includes('-----BEGIN CERTIFICATE-----');
	}

	private normalizePem(pem: string): string {
		return pem.replace(/\r\n/g, '\n').trim() + '\n';
	}

	private convertDerToPem(arrayBuffer: ArrayBuffer): string {
		const bytes = this.bytesToStringDecoder(new Uint8Array(arrayBuffer));
		const asn1Obj = forge.asn1.fromDer(bytes);
		const cert = forge.pki.certificateFromAsn1(asn1Obj);
		return forge.pki.certificateToPem(cert);
	}

	private bytesToStringDecoder(bytes: Uint8Array): string {
		let result = '';
		for (const byteValue of bytes) {
			result += String.fromCharCode(byteValue);
		}
		return result;
	}

	private tryParseFileNameFromContentDisposition(
		value: string | null
	): string | null {
		if (!value) return null;
		const match = /filename="([^"]+)"/i.exec(value);
		return match?.[1] ?? null;
	}

	private downloadTextFile(params: {
		readonly fileName: string;
		readonly contents: string;
		readonly mimeType: string;
	}): void {
		const blob = new Blob([params.contents], { type: params.mimeType });
		const url = URL.createObjectURL(blob);

		try {
			const anchor = document.createElement('a');
			anchor.href = url;
			anchor.download = params.fileName;
			anchor.rel = 'noopener';
			anchor.click();
		} finally {
			URL.revokeObjectURL(url);
		}
	}

	private toErrorMessage(error: unknown): string {
		if (error instanceof Error) return error.message;
		return 'Request failed.';
	}
}
