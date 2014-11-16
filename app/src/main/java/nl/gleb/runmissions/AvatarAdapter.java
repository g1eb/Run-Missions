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
            R.drawable.a_3
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
}
