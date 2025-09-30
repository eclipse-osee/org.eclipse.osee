/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Attachment } from '../types/team-workflow';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AttachmentTestingService {
  // Simulated latency
  private readonly latencyMs = 150;

  // Simple in-memory store keyed by workflowId
  private store = new Map<string, Attachment[]>();

  listAttachments(workflowId: string): Observable<Attachment[]> {
    const list = this.ensureSeeded(workflowId);
    // Return a cloned copy to avoid accidental mutations
    return of(this.cloneList(list)).pipe(delay(this.latencyMs));
  }

  uploadAttachments(workflowId: string, files: File[]): Observable<Attachment[]> {
    const now = new Date().toISOString();
    const current = this.ensureSeeded(workflowId);

    const created: Attachment[] = files.map((f, i) => ({
      id: `new-${workflowId}-${Date.now()}-${i}`,
      fileName: f.name,
      contentType: f.type || 'application/octet-stream',
      sizeBytes: f.size,
      createdAt: now,
      updatedAt: now,
    }));

    this.store.set(workflowId, [...current, ...created]);
    return of(this.cloneList(created)).pipe(delay(this.latencyMs));
  }

  updateAttachment(workflowId: string, attachmentId: string, file: File): Observable<Attachment> {
    const list = this.ensureSeeded(workflowId);
    const idx = list.findIndex((a) => a.id === attachmentId);
    const baseCreatedAt = idx >= 0 ? list[idx].createdAt : new Date().toISOString();

    const updated: Attachment = {
      ...(idx >= 0 ? list[idx] : { id: attachmentId, fileName: '' }),
      fileName: file.name,
      contentType: file.type || 'application/octet-stream',
      sizeBytes: file.size,
      createdAt: baseCreatedAt,
      updatedAt: new Date().toISOString(),
    };

    if (idx >= 0) {
      const next = [...list];
      next[idx] = updated;
      this.store.set(workflowId, next);
    } else {
      // If not found, append (useful during testing)
      this.store.set(workflowId, [...list, updated]);
    }

    return of(structuredClone(updated)).pipe(delay(this.latencyMs));
  }

  deleteAttachment(workflowId: string, attachmentId: string): Observable<void> {
    const list = this.ensureSeeded(workflowId);
    const next = list.filter((a) => a.id !== attachmentId);
    this.store.set(workflowId, next);
    return of(undefined).pipe(delay(this.latencyMs));
  }

  getDownloadUrl(workflowId: string, attachmentId: string): Observable<{ url: string }> {
    // A harmless URL for testing; UI can call window.open on it
    const url = `about:blank#${encodeURIComponent(workflowId)}-${encodeURIComponent(attachmentId)}`;
    return of({ url }).pipe(delay(this.latencyMs));
  }

  downloadAttachmentBlob(workflowId: string, attachmentId: string): Observable<Blob> {
    const content = `Mock content for ${attachmentId} (workflow ${workflowId})`;
    const blob = new Blob([content], { type: 'text/plain' });
    return of(blob).pipe(delay(this.latencyMs));
  }

  // Helpers

  private ensureSeeded(workflowId: string): Attachment[] {
    if (!this.store.has(workflowId)) {
      const now = new Date().toISOString();
      const seeded: Attachment[] = [
        {
          id: `att-${workflowId}-001`,
          fileName: 'requirements.pdf',
          contentType: 'application/pdf',
          sizeBytes: 123456,
          createdAt: now,
          updatedAt: now,
        },
        {
          id: `att-${workflowId}-002`,
          fileName: 'screenshot.png',
          contentType: 'image/png',
          sizeBytes: 98765,
          createdAt: now,
          updatedAt: now,
        },
      ];
      this.store.set(workflowId, seeded);
    }
    return this.store.get(workflowId) as Attachment[];
  }

  private cloneList<T>(arr: T[]): T[] {
    try {
      return structuredClone(arr);
    } catch {
      return JSON.parse(JSON.stringify(arr)) as T[];
    }
  }
}
