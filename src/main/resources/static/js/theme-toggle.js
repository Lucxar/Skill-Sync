/**
 * ThesisIsComing — Theme Toggle
 *
 * Handles dark/light mode switching with localStorage persistence.
 * Toggle button must have id="theme-toggle" with a child <i> for the icon.
 *
 * FOUC Prevention — inline this in <head> as the FIRST child (before CSS links):
 * <script>(function(){var t=localStorage.getItem('tic-theme')||'dark';document.documentElement.setAttribute('data-bs-theme',t)})()</script>
 */
(function () {
    'use strict';

    var STORAGE_KEY = 'tic-theme';
    var DARK = 'dark';
    var LIGHT = 'light';

    function getTheme() {
        return localStorage.getItem(STORAGE_KEY) || DARK;
    }

    function setTheme(theme) {
        document.documentElement.setAttribute('data-bs-theme', theme);
        localStorage.setItem(STORAGE_KEY, theme);
    }

    function updateToggleButton(btn, theme) {
        var icon = btn.querySelector('i');
        if (icon) {
            if (theme === DARK) {
                icon.className = 'bi bi-sun-fill';
            } else {
                icon.className = 'bi bi-moon-fill';
            }
        }
        if (theme === DARK) {
            btn.setAttribute('aria-label', 'Zum hellen Modus wechseln');
        } else {
            btn.setAttribute('aria-label', 'Zum dunklen Modus wechseln');
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        var btn = document.getElementById('theme-toggle');
        if (!btn) return;

        // Set initial icon state based on current theme
        updateToggleButton(btn, getTheme());

        btn.addEventListener('click', function () {
            var current = getTheme();
            var next = current === DARK ? LIGHT : DARK;
            setTheme(next);
            updateToggleButton(btn, next);
        });
    });
})();
