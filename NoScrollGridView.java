
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by xxq on 2016/6/10.
 * 重写onMeasure解决和listView嵌套时只能显示一行的问题
 */
public class NoScrollGridView extends GridView {

    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}