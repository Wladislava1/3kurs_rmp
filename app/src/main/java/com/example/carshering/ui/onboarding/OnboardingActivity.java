package com.example.carshering.ui.onboarding;

import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.R;
import com.example.carshering.ui.welcome.WelcomeActivity;
import com.example.carshering.utils.PrefManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.databinding.ActivityOnboardingBinding;
import com.example.carshering.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;
    private static final int INDICATOR_ACTIVE_WIDTH_DP = 48;
    private static final int INDICATOR_INACTIVE_WIDTH_DP = 24;
    private static final int INDICATOR_HEIGHT_DP = 8;
    private static final int INDICATOR_MARGIN_DP = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefManager prefManager = new PrefManager(this);

        // Если первый запуск уже был, сразу переходим на WelcomeActivity
        if (!prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.item1,
                getString(R.string.youre_welcome), getString(R.string.we_help_you)));
        onboardingItems.add(new OnboardingItem(R.drawable.item2,
                getString(R.string.comfortable), getString(R.string.auto_here)));
        onboardingItems.add(new OnboardingItem(R.drawable.item3,
                getString(R.string.go), getString(R.string.beggin_now)));

        OnboardingAdapter adapter = new OnboardingAdapter(onboardingItems);
        binding.onboardingViewPager.setAdapter(adapter);

        setupIndicators(adapter.getCount());

        binding.onboardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position,
                    float positionOffset,
                    int positionOffsetPixels
            ) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);

                if (position == adapter.getCount() - 1) {
                    binding.btnNext.setVisibility(View.GONE);
                    binding.btnStart.setVisibility(View.VISIBLE);
                } else {
                    binding.btnNext.setVisibility(View.VISIBLE);
                    binding.btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        binding.btnSkip.setOnClickListener(v -> goToWelcome());

        binding.btnNext.setOnClickListener(v -> {
            int next = binding.onboardingViewPager.getCurrentItem() + 1;
            if (next < adapter.getCount()) {
                binding.onboardingViewPager.setCurrentItem(next, true);
            }
        });

        binding.btnStart.setOnClickListener(v -> goToWelcome());
    }


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupIndicators(int count) {
        binding.indicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            View indicator = new View(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(dpToPx(INDICATOR_ACTIVE_WIDTH_DP),
                            dpToPx(INDICATOR_HEIGHT_DP)
                    );

            params.setMargins(dpToPx(INDICATOR_MARGIN_DP), 0,
                    dpToPx(INDICATOR_MARGIN_DP), 0);

            if (i == 0) {
                params.width = dpToPx(INDICATOR_ACTIVE_WIDTH_DP); // активный
                indicator.setBackgroundResource(R.drawable.indicator_active);
            } else {
                params.width = dpToPx(INDICATOR_INACTIVE_WIDTH_DP); // неактивный
                indicator.setBackgroundResource(R.drawable.indicator_inactive);
            }

            indicator.setLayoutParams(params);
            binding.indicatorLayout.addView(indicator);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = binding.indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = binding.indicatorLayout.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

            if (i == index) {
                params.width = dpToPx(INDICATOR_ACTIVE_WIDTH_DP);
                child.setBackgroundResource(R.drawable.indicator_active);
            } else {
                params.width = dpToPx(INDICATOR_INACTIVE_WIDTH_DP);
                child.setBackgroundResource(R.drawable.indicator_inactive);
            }

            child.setLayoutParams(params);
        }
    }

    private void goToWelcome() {
        PrefManager prefManager = new PrefManager(this);
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }
}
