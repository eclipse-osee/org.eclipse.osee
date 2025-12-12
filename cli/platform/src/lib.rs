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
use is_wsl::is_wsl;
use notify::PollWatcher;
use thiserror::Error;

pub fn is_in_wsl() -> bool {
    is_wsl()
}

pub fn get_line_ending<'a>() -> &'a str {
    #[cfg(windows)]
    let line_ending: &str = match is_in_wsl() {
        true => "\n",
        false => "\r\n",
    };

    #[cfg(not(windows))]
    let line_ending: &str = "\n";
    line_ending
}

#[derive(Debug)]
pub enum NotifyWatcher {
    Recommended(notify::RecommendedWatcher),
    Poll(notify::PollWatcher),
}

impl notify::Watcher for NotifyWatcher {
    fn new<F: notify::EventHandler>(
        event_handler: F,
        config: notify::Config,
    ) -> notify::Result<Self>
    where
        Self: Sized,
    {
        let wsl = is_in_wsl();
        if wsl {
            return Ok(Self::Poll(PollWatcher::new(event_handler, config)?));
        }
        Ok(Self::Recommended(notify::recommended_watcher(
            event_handler,
        )?))
    }

    fn watch(
        &mut self,
        path: &std::path::Path,
        recursive_mode: notify::RecursiveMode,
    ) -> notify::Result<()> {
        match self {
            NotifyWatcher::Poll(poll_watcher) => poll_watcher.watch(path, recursive_mode),
            NotifyWatcher::Recommended(recommended) => recommended.watch(path, recursive_mode),
        }
    }

    fn unwatch(&mut self, path: &std::path::Path) -> notify::Result<()> {
        match self {
            NotifyWatcher::Recommended(recommended) => recommended.unwatch(path),
            NotifyWatcher::Poll(poll_watcher) => poll_watcher.unwatch(path),
        }
    }

    fn kind() -> notify::WatcherKind
    where
        Self: Sized,
    {
        notify::WatcherKind::NullWatcher
    }
}

#[derive(Debug, Error)]
pub enum NotifyWatcherError {
    #[error("{}",.0)]
    NotifyError(#[from] notify::Error),
}
