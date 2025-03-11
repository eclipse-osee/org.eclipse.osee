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
// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";
import tailwind from "@astrojs/tailwind";
import starlightImageZoomPlugin from "starlight-image-zoom";

// https://astro.build/config
export default defineConfig({
  integrations: [
    starlight({
      title: "OSEE",
      favicon: "/favicon.ico",
      customCss: ["./src/tailwind.css"],
      social: {
        github: "https://github.com/eclipse-osee/org.eclipse.osee",
      },
      sidebar: [
        {
          label: "Getting Started",
          items: [{ label: "What is OSEE?", slug: "getting-started/overview" }],
        },
        {
          label: "MIM",
          items: [
            { label: "MIM Overview", slug: "mim/mim-overview" },
            { label: "Example ICD", slug: "mim/example-icd" },
            { label: "Data Model", slug: "mim/datamodel" },
            {
              label: "Guides",
              items: [
                {
                  label: "Creating an ICD",
                  collapsed: true,
                  items: [
                    {
                      label: "Creating an ICD",
                      slug: "mim/guides/create-icd",
                    },
                    {
                      label: "Creating Elements",
                      slug: "mim/guides/create-elements",
                    },
                  ],
                },
                {
                  label: "Editing ICDs",
                  slug: "mim/guides/edit-icd",
                },
                {
                  label: "Peer Review Workflow",
                  slug: "mim/guides/peer-review",
                },
                {
                  label: "Traceability",
                  slug: "mim/guides/traceability",
                },
              ],
            },
            {
              label: "Pages",
              items: [
                {
                  label: "List Configuration",
                  slug: "mim/pages/list-config",
                },
                {
                  label: "Platform Types",
                  slug: "mim/pages/platform-types",
                },
                {
                  label: "Reports",
                  slug: "mim/pages/reports",
                },
                {
                  label: "Transport Types",
                  slug: "mim/pages/transport-types",
                },
                {
                  label: "Cross-References",
                  slug: "mim/pages/cross-references",
                },
                {
                  label: "Import",
                  slug: "mim/pages/import",
                },
              ],
            },
          ],
        },
        {
          label: "Zenith",
          items: [
            { label: "Zenith Overview", slug: "zenith/zenith-overview" },
            { label: "Configuration", slug: "zenith/configuration" },
            { label: "Importing Test Results", slug: "zenith/import" },
          ],
        },
        {
          label: "BAT",
          items: [{ label: "BAT Overview", slug: "bat/bat-overview" }],
        },
      ],
      plugins: [
        starlightImageZoomPlugin({
          showCaptions: false,
        }),
      ],
    }),
    tailwind({
      // Disable the default base styles:
      applyBaseStyles: false,
    }),
  ],
  site: "https://eclipse-osee.github.io",
  base: "/org.eclipse.osee",
});
