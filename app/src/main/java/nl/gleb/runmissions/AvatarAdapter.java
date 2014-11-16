package nl.gleb.runmissions;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Gleb on 16/11/14.
 */
public class AvatarAdapter extends PagerAdapter {

    Context context;

    private int[] AvatarImages = new int[]{
            R.drawable.a_1,
            R.drawable.a_2,
            R.drawable.a_3,
            R.drawable.a_4,
            R.drawable.a_5,
            R.drawable.a_6,
            R.drawable.a_7,
            R.drawable.a_8,
            R.drawable.a_9,
            R.drawable.a_10,
            R.drawable.a_11,
            R.drawable.a_12,
            R.drawable.a_13,
            R.drawable.a_14,
            R.drawable.a_15,
            R.drawable.a_16,
            R.drawable.a_17,
            R.drawable.a_18,
            R.drawable.a_19,
            R.drawable.a_20,
            R.drawable.a_21,
            R.drawable.a_22,
            R.drawable.a_23,
            R.drawable.a_24,
            R.drawable.a_25,
            R.drawable.a_26,
            R.drawable.a_27,
            R.drawable.a_28,
            R.drawable.a_29,
            R.drawable.a_30,
            R.drawable.a_31,
            R.drawable.a_32,
            R.drawable.a_33,
            R.drawable.a_34,
            R.drawable.a_35,
            R.drawable.a_36,
            R.drawable.a_37,
            R.drawable.a_38,
            R.drawable.a_39,
            R.drawable.a_40,
            R.drawable.a_41,
            R.drawable.a_42,
            R.drawable.a_43,
            R.drawable.a_44,
            R.drawable.a_45,
            R.drawable.a_46,
            R.drawable.a_47,
            R.drawable.a_48,
            R.drawable.a_49,
            R.drawable.a_50,
            R.drawable.a_51,
            R.drawable.a_52
    };

    AvatarAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return AvatarImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        imageView.setPadding(padding * 5, padding * 5, padding * 5, padding * 5);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(AvatarImages[position]);
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    public int getAvatar(int i) {
        return AvatarImages[i];
    }
}
