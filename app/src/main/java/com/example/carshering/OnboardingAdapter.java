package com.example.carshering;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

public class OnboardingAdapter extends PagerAdapter {

    private List<OnboardingItem> items;

    public OnboardingAdapter(List<OnboardingItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_onboarding, container, false);

        ImageView image = view.findViewById(R.id.onboardingImage);
        TextView title = view.findViewById(R.id.onboardingTitle);
        TextView description = view.findViewById(R.id.onboardingDescription);

        OnboardingItem item = items.get(position);
        image.setImageResource(item.getImageResId());
        title.setText(item.getTitle());
        description.setText(item.getDescription());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
