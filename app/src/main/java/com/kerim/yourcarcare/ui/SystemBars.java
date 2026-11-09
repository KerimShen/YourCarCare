package com.kerim.yourcarcare.ui;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * New Android versions draw the app behind the status bar, navigation bar and keyboard
 * ("edge-to-edge"). This adds padding so our content doesn't end up hidden behind them.
 */
public final class SystemBars {

    private SystemBars() {
    }

    public static void applyPadding(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            // ime() = the on-screen keyboard, so form fields stay visible while typing
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
