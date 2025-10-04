package com.example.carshering;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefManager prefManager = new PrefManager(this);

        // Если уже запускалось, сразу переходим к WelcomeActivity
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

        // 1. Создаем список слайдов
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.item1, "Добро пожаловать", "Мы поможем тебе найти поездку"));
        onboardingItems.add(new OnboardingItem(R.drawable.item2, "Удобно", "Выбирай машины поблизости"));
        onboardingItems.add(new OnboardingItem(R.drawable.item3, "Поехали", "Начни своё путешествие прямо сейчас"));

        // 2. Создаем адаптер и устанавливаем его в ViewPager
        OnboardingAdapter adapter = new OnboardingAdapter(onboardingItems);
        binding.onboardingViewPager.setAdapter(adapter);

        // **Добавляем индикаторы**
        setupIndicators(adapter.getCount());

        // **Слушатель перелистывания ViewPager**
        binding.onboardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);

                // Показать кнопку "Поехали" на последнем слайде
                if (position == adapter.getCount() - 1) {
                    binding.btnNext.setVisibility(View.GONE);
                    binding.btnStart.setVisibility(View.VISIBLE);
                } else {
                    binding.btnNext.setVisibility(View.VISIBLE);
                    binding.btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // 3. Кнопка "Пропустить" сразу переходит к WelcomeActivity
        binding.btnSkip.setOnClickListener(v -> goToWelcome());

        // 4. Кнопка "Далее" перелистывает ViewPager
        binding.btnNext.setOnClickListener(v -> {
            int next = binding.onboardingViewPager.getCurrentItem() + 1;
            if (next < adapter.getCount()) {
                binding.onboardingViewPager.setCurrentItem(next, true);
            }
        });

        // 5. Кнопка "Поехали" переходит к WelcomeActivity
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
                    new LinearLayout.LayoutParams(dpToPx(48), dpToPx(8)); // макс ширина

            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);

            if (i == 0) {
                params.width = dpToPx(48); // активный
                indicator.setBackgroundResource(R.drawable.indicator_active);
            } else {
                params.width = dpToPx(24); // неактивный
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
                params.width = dpToPx(48);
                child.setBackgroundResource(R.drawable.indicator_active);
            } else {
                params.width = dpToPx(24);
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
