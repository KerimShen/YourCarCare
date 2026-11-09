package com.kerim.yourcarcare.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.logic.VignetteStatus;
import com.kerim.yourcarcare.model.Vignette;

import java.time.LocalDate;
import java.util.List;

/**
 * Controls the vignette section of the car detail screen (layout: section_vignette.xml).
 * Kept in its own class so CarDetailActivity doesn't grow too big.
 */
public class VignetteCard {

    private final Context context;
    private final TextView textType;
    private final TextView textValidUntil;
    private final TextView textStatus;
    private final MaterialButton buttonAdd;
    private final MaterialButton buttonHistory;
    private final RecyclerView historyList;
    private final VignetteAdapter adapter;

    private boolean historyExpanded = false;
    private boolean hasVignettes = false;

    /** @param sectionRoot the included section_vignette layout */
    public VignetteCard(View sectionRoot, VignetteAdapter.OnVignetteClickListener listener) {
        context = sectionRoot.getContext();
        textType = sectionRoot.findViewById(R.id.text_vignette_type);
        textValidUntil = sectionRoot.findViewById(R.id.text_vignette_valid_until);
        textStatus = sectionRoot.findViewById(R.id.text_vignette_status);
        buttonAdd = sectionRoot.findViewById(R.id.button_add_vignette);
        buttonHistory = sectionRoot.findViewById(R.id.button_vignette_history);
        historyList = sectionRoot.findViewById(R.id.vignette_history_list);

        adapter = new VignetteAdapter(listener);
        historyList.setAdapter(adapter);
    }

    public void setOnAddClickListener(View.OnClickListener onClick) {
        buttonAdd.setOnClickListener(onClick);
    }

    public void setOnHistoryClickListener(View.OnClickListener onClick) {
        buttonHistory.setOnClickListener(onClick);
    }

    /** Shows or hides the history list and changes the button text to match. */
    public void setHistoryExpanded(boolean expanded) {
        historyExpanded = expanded;
        updateHistoryVisibility();
    }

    /** Shows the status of the most relevant vignette and fills the history list. */
    public void show(List<Vignette> vignettes, LocalDate today) {
        hasVignettes = !vignettes.isEmpty();
        adapter.submitList(vignettes);
        updateHistoryVisibility();

        VignetteStatus status = VignetteStatus.of(vignettes, today);
        if (status.state == VignetteStatus.State.NONE) {
            textType.setVisibility(View.GONE);
            textValidUntil.setVisibility(View.GONE);
            setStatus(context.getString(R.string.vignette_none), neutralColor());
            return;
        }

        Vignette vignette = status.vignette;
        String expiry = Dates.format(vignette.getExpiryDate());
        textType.setVisibility(View.VISIBLE);
        textValidUntil.setVisibility(View.VISIBLE);
        textType.setText(vignette.type.getLabelRes());
        textValidUntil.setText(context.getString(R.string.vignette_valid_until, expiry));

        switch (status.state) {
            case VALID:
                setStatus(daysLeftText(status.daysLeft),
                        ContextCompat.getColor(context, R.color.status_ok));
                break;
            case EXPIRING_SOON:
                String text = status.daysLeft == 0
                        ? context.getString(R.string.vignette_expires_today)
                        : daysLeftText(status.daysLeft);
                setStatus(text, ContextCompat.getColor(context, R.color.status_warning));
                break;
            case UPCOMING:
                setStatus(context.getString(R.string.vignette_starts_on,
                        Dates.format(vignette.startDate)), neutralColor());
                break;
            case EXPIRED:
                // colorError is the theme's red; it already has a light and a dark version
                setStatus(context.getString(R.string.vignette_expired_on, expiry),
                        MaterialColors.getColor(textStatus, androidx.appcompat.R.attr.colorError));
                break;
            default:
                break;
        }
    }

    private void updateHistoryVisibility() {
        buttonHistory.setVisibility(hasVignettes ? View.VISIBLE : View.GONE);
        historyList.setVisibility(hasVignettes && historyExpanded ? View.VISIBLE : View.GONE);
        buttonHistory.setText(historyExpanded ? R.string.action_hide_history : R.string.action_show_history);
    }

    private void setStatus(String text, int color) {
        textStatus.setText(text);
        textStatus.setTextColor(color);
    }

    // Plurals pick "1 day left" or "5 days left" depending on the number (and the language)
    private String daysLeftText(long daysLeft) {
        int days = (int) daysLeft;
        return context.getResources().getQuantityString(R.plurals.vignette_days_left, days, days);
    }

    // Grey-ish text color from the theme, works in light and dark mode
    private int neutralColor() {
        return MaterialColors.getColor(textStatus,
                com.google.android.material.R.attr.colorOnSurfaceVariant);
    }
}
