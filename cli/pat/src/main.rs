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
use anyhow::Result;
use clap::Parser;
use cli_logging::initialize_logging;
use pat_lib::{PatCliOptions, project_repository};

#[cfg(not(target_env = "msvc"))]
#[global_allocator]
static GLOBAL: mimalloc::MiMalloc = mimalloc::MiMalloc;

#[tracing::instrument(err)]
fn main() -> Result<()> {
    let args = PatCliOptions::parse();
    let header_span = initialize_logging(&args.verbose, "projection");

    project_repository(args.options, header_span)
}
