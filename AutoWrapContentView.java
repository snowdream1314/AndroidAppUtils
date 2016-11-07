
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xxq on 2016/8/26.
 * 搜索热词、搜索记录展示，自动换行控件
 */

public class AutoWrapContentView extends ViewGroup {
//    private int PADDING_HOR = 10;//水平方向padding
//    private int PADDING_VERTICAL = 5;//垂直方向padding
    private int TEXT_MARGIN = 10;
    private int SIDE_MARGIN = 10;//左右间距
    private int Padding_Left = 10;
    private int Padding_Top = 10;
    private int Padding_Right = 10;
    private int Padding_Bottom = 10;
    private int Vertical_Margin = 20;
    private int Horizontal_MARGIN = 20;

    /**
     * @param context
     */
    public AutoWrapContentView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoWrapContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public AutoWrapContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChildViewPadding(int left, int top, int right, int bottom) {
        Padding_Left = left;
        Padding_Top = top;
        Padding_Right = right;
        Padding_Bottom = bottom;
    }

    public void setLayoutVerticalMargin(int verticalMargin) {
        Vertical_Margin = verticalMargin;
    }

    public void setLayoutHorizontalMargin(int horizontalMargin) {
        Horizontal_MARGIN = horizontalMargin;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = SIDE_MARGIN;// 横坐标开始
        int y = 0;//纵坐标开始
        int rows = 1;
        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width+Horizontal_MARGIN;
            if(x>autualWidth){
                x = width+SIDE_MARGIN;
                rows++;
            }
            y = rows*(height+Vertical_Margin);
            if(i==0){
                view.layout(x-width-Horizontal_MARGIN, y-height, x, y);
            }else{
                view.layout(x-width, y-height, x, y);
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;//横坐标
        int y = 0;//纵坐标
        int rows = 1;//总行数
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - SIDE_MARGIN * 2;//实际宽度
        int childCount = getChildCount();
        for(int index = 0;index<childCount;index++){
            View child = getChildAt(index);
//            child.setPadding(Padding_Left, Padding_Top, Padding_Right, Padding_Bottom);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            x += width+Horizontal_MARGIN;
            if(x>actualWidth){//换行
                x = width;
                rows++;
            }
            y = rows*(height+Vertical_Margin);
        }
        setMeasuredDimension(actualWidth, y);
    }

}
