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
import { test, expect, type APIRequestContext } from '@ngx-playwright/test';
import * as path from 'node:path';
import * as fs from 'node:fs';
import * as os from 'node:os';
import forge from 'node-forge';
import { API_BASE, AUTH_HEADER } from '../../../shared/test-config';

const CERTIFICATE_MANAGEMENT_PATH = '/profile/certificate-management';
const CERT_SUBJECT_CN = 'OSEE Test User';

/**
 * Generates a self-signed PEM certificate with a 1-year validity window.
 * Uses a 1024-bit key for speed — this is a test-only certificate.
 */
function generateSelfSignedCert(): { pem: string; filePath: string } {
	const keys = forge.pki.rsa.generateKeyPair(1024);
	const cert = forge.pki.createCertificate();

	cert.publicKey = keys.publicKey;
	cert.serialNumber = '01';
	cert.validity.notBefore = new Date();
	cert.validity.notAfter = new Date();
	cert.validity.notAfter.setFullYear(
		cert.validity.notBefore.getFullYear() + 1
	);

	const attrs = [{ name: 'commonName', value: CERT_SUBJECT_CN }];
	cert.setSubject(attrs);
	cert.setIssuer(attrs);
	cert.setExtensions([{ name: 'extKeyUsage', emailProtection: true }]);
	cert.sign(keys.privateKey, forge.md.sha256.create());

	const pem = forge.pki.certificateToPem(cert);

	const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'osee-cert-'));
	const filePath = path.join(tmpDir, 'test-cert.pem');
	fs.writeFileSync(filePath, pem);

	return { pem, filePath };
}

/**
 * Deletes the user's certificate via the API to ensure a clean state.
 */
async function deleteExistingCertificate(
	request: APIRequestContext
): Promise<void> {
	const response = await request.delete(
		`${API_BASE}/orcs/user/public-certificate`,
		{ headers: AUTH_HEADER }
	);
	expect(response.status()).toBeLessThan(500);
}

/**
 * Uploads a certificate via the API to set up a known state.
 */
async function uploadCertificateViaApi(
	request: APIRequestContext,
	pemContent: string
): Promise<void> {
	const response = await request.put(
		`${API_BASE}/orcs/user/public-certificate`,
		{
			headers: { ...AUTH_HEADER, 'Content-Type': 'text/plain' },
			data: pemContent,
		}
	);
	expect(response.ok()).toBeTruthy();
}

let certData: { pem: string; filePath: string };

test.beforeAll(() => {
	certData = generateSelfSignedCert();
});

test.afterAll(() => {
	if (certData?.filePath) {
		fs.rmSync(path.dirname(certData.filePath), {
			recursive: true,
			force: true,
		});
	}
});

test.describe('Certificate Management - Email Path Contract', () => {
	test('the missing-certificate email notice contains the certificate management page path', async ({
		request,
	}) => {
		// The endpoint uses the same code path as the actual email assembly.
		// This test ensures the link in the email resolves to the real page.
		const response = await request.get(
			`${API_BASE}/orcs/user/public-certificate/missing-notice`,
			{ headers: AUTH_HEADER }
		);
		expect(response.ok()).toBeTruthy();

		const noticeText = await response.text();

		// The notice must contain the certificate management sub-path.
		expect(noticeText).toContain('/profile/certificate-management');
	});

	test('the certificate management page is accessible at the expected route', async ({
		page,
	}) => {
		const response = await page.goto(CERTIFICATE_MANAGEMENT_PATH);
		expect(response?.ok()).toBeTruthy();

		// The component selector should be present in the DOM
		await expect(
			page.locator('osee-user-public-certificate-management')
		).toBeVisible();
	});
});

test.describe('Certificate Management - Upload Workflow', () => {
	test.beforeEach(async ({ request }) => {
		await deleteExistingCertificate(request);
	});

	test('can select a certificate file and see it ready for upload', async ({
		page,
	}) => {
		await page.goto(CERTIFICATE_MANAGEMENT_PATH);

		// Upload via the hidden file input inside the drag-and-drop component
		const fileInput = page.locator(
			'osee-drag-and-drop-upload input[type="file"]'
		);
		await fileInput.setInputFiles(certData.filePath);

		// After processing, the file name should appear
		await expect(page.getByText('test-cert.pem')).toBeVisible();

		// The Update button should become enabled
		const updateButton = page.getByRole('button', { name: 'Update' });
		await expect(updateButton).toBeEnabled();
	});

	test('can upload a certificate and see it reflected on the page', async ({
		page,
	}) => {
		await page.goto(CERTIFICATE_MANAGEMENT_PATH);

		const fileInput = page.locator(
			'osee-drag-and-drop-upload input[type="file"]'
		);
		await fileInput.setInputFiles(certData.filePath);

		// Wait for processing to complete and click Update
		const updateButton = page.getByRole('button', { name: 'Update' });
		await expect(updateButton).toBeEnabled();
		await updateButton.click();

		// After upload, the certificate details should appear (Subject CN from our generated cert)
		await expect(page.getByText(CERT_SUBJECT_CN)).toBeVisible({
			timeout: 10000,
		});
	});
});

test.describe('Certificate Management - Existing Certificate Actions', () => {
	test.beforeEach(async ({ request }) => {
		await uploadCertificateViaApi(request, certData.pem);
	});

	test('displays certificate details when one is on file', async ({
		page,
	}) => {
		await page.goto(CERTIFICATE_MANAGEMENT_PATH);

		// Should show the Subject CN from the uploaded cert
		await expect(page.getByText(CERT_SUBJECT_CN)).toBeVisible({
			timeout: 10000,
		});

		// Download and Delete buttons should be present and enabled
		await expect(
			page.getByRole('button', { name: 'Download' })
		).toBeEnabled();
		await expect(
			page.getByRole('button', { name: 'Delete' })
		).toBeEnabled();
	});

	test('can delete an existing certificate', async ({ page, request }) => {
		await page.goto(CERTIFICATE_MANAGEMENT_PATH);

		await expect(page.getByText(CERT_SUBJECT_CN)).toBeVisible({
			timeout: 10000,
		});

		// Accept the confirmation dialog
		page.on('dialog', (dialog) => dialog.accept());
		await page.getByRole('button', { name: 'Delete' }).click();

		// After deletion, the "no certificate" state should appear
		await expect(
			page.getByText('No certificate is currently on file.')
		).toBeVisible({ timeout: 10000 });

		// Verify via API that the certificate is actually gone
		const response = await request.get(
			`${API_BASE}/orcs/user/public-certificate`,
			{ headers: AUTH_HEADER }
		);
		expect(response.status()).toBe(204);
	});

	test('can download an existing certificate', async ({ page }) => {
		await page.goto(CERTIFICATE_MANAGEMENT_PATH);

		await expect(page.getByText(CERT_SUBJECT_CN)).toBeVisible({
			timeout: 10000,
		});

		// Listen for the download event
		const downloadPromise = page.waitForEvent('download');
		await page.getByRole('button', { name: 'Download' }).click();
		const download = await downloadPromise;

		// Verify the downloaded file has a .pem extension and contains certificate data
		expect(download.suggestedFilename()).toMatch(/\.pem$/);

		const downloadPath = await download.path();
		expect(downloadPath).toBeTruthy();
		const content = fs.readFileSync(downloadPath!, 'utf-8');
		expect(content).toContain('-----BEGIN CERTIFICATE-----');
	});
});
