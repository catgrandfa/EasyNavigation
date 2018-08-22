package com.next.easynavigition.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.next.easynavigition.R;
import com.next.easynavigition.adapter.ViewPagerAdapter;
import com.next.easynavigition.constant.Anim;
import com.next.easynavigition.utils.NavigitionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jue on 2018/6/1.
 */

public class EasyNavigitionBar extends LinearLayout {


    private RelativeLayout AddContainerLayout;

    //Tab数量
    private int tabCount = 0;

    private LinearLayout navigitionLayout;
    private RelativeLayout contentView;
    //分割线
    private View lineView;

    //红点集合
    private List<View> hintPointList = new ArrayList<>();

    //消息数量集合
    private List<TextView> msgPointList = new ArrayList<>();

    //底部Image集合
    private List<ImageView> imageViewList = new ArrayList<>();

    //底部Text集合
    private List<TextView> textViewList = new ArrayList<>();

    //底部TabLayout（除中间加号）
    private List<View> tabList = new ArrayList<>();

    private CustomViewPager mViewPager;
    //private GestureDetector detector;

    private ViewGroup addViewLayout;


    //文字集合
    private String[] titleItems;
    //未选择 图片集合
    private int[] normalIconItems;
    //已选择 图片集合
    private int[] selectIconItems;
    //fragment集合
    private List<Fragment> fragmentList = new ArrayList<>();

    private FragmentManager fragmentManager;

    //Tab点击动画效果
    private Techniques anim = Techniques.ZoomIn;
    //ViewPager切换动画
    private boolean smoothScroll = false;
    //图标大小
    private int iconSize = 20;

    //提示红点大小
    private float hintPointSize = 6;
    //提示红点距Tab图标右侧的距离
    private float hintPointLeft = -3;
    //提示红点距图标顶部的距离
    private float hintPointTop = -3;

    private EasyNavigitionBar.OnTabClickListener onTabClickListener;
    //消息红点字体大小
    private float msgPointTextSize = 9;
    //消息红点大小
    private float msgPointSize = 18;
    //消息红点距Tab图标右侧的距离   默认为Tab图标的一半
    private float msgPointLeft = -10;
    //消息红点距图标顶部的距离  默认为Tab图标的一半
    private float msgPointTop = -10;
    //Tab文字距Tab图标的距离
    private float tabTextTop = 2;
    //Tab文字大小
    private float tabTextSize = 12;
    //未选中Tab字体颜色
    private int normalTextColor = Color.parseColor("#666666");
    //选中字体颜色
    private int selectTextColor = Color.parseColor("#333333");
    //分割线高度
    private float lineHeight = 1;
    //分割线颜色
    private int lineColor = Color.parseColor("#f7f7f7");

    private int navigitionBackground = Color.parseColor("#ffffff");
    private float navigitionHeight = 60;

    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;

    private boolean canScroll;
    private ViewPagerAdapter adapter;


    //Add
    private EasyNavigitionBar.OnAddClickListener onAddClickListener;
    private float addIconSize = 36;
    private float addLayoutHeight = navigitionHeight;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_ADD = 1;
    public static final int MODE_ADD_VIEW = 2;

    private float addLayoutBottom = 10;

    //RULE_CENTER 居中只需调节addLayoutHeight 默认和navigitionHeight相等 此时addLayoutBottom属性无效
    //RULE_BOTTOM addLayoutHeight属性无效、自适应、只需调节addLayoutBottom距底部的距离
    private int addIconRule = RULE_CENTER;

    public static final int RULE_CENTER = 0;
    public static final int RULE_BOTTOM = 1;

    //true  ViewPager在Navigition上面
    //false  ViewPager和Navigition重叠
    private boolean hasPadding = true;


    //1、普通的Tab 2、中间带按钮（如加号）3、
    private int mode;

    //true 点击加号切换fragment
    //false 点击加号不切换fragment进行其他操作（跳转界面等）
    private boolean addAsFragment = false;
    //自定义加号view
    private View customAddView;
    private float addTextSize;
    //加号文字未选中颜色（默认同其他tab）
    private int addNormalTextColor;
    //加号文字选中颜色（默认同其他tab）
    private int addSelectTextColor;
    //加号文字距离顶部加号的距离
    private float addTextTopMargin = 3;
    //是否和其他tab文字底部对齐
    private boolean addAlignBottom = true;
    private ImageView addImage;


    public EasyNavigitionBar(Context context) {
        super(context);

        initViews(context, null);
    }

    public EasyNavigitionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {

        contentView = (RelativeLayout) View.inflate(context, R.layout.container_layout, null);
        addViewLayout = contentView.findViewById(R.id.add_view_ll);
        AddContainerLayout = contentView.findViewById(R.id.add_rl);
        navigitionLayout = contentView.findViewById(R.id.navigition_ll);
        mViewPager = contentView.findViewById(R.id.mViewPager);
        lineView = contentView.findViewById(R.id.common_horizontal_line);
        lineView.setTag(-100);
        navigitionLayout.setTag(-100);

        toDp();


        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.EasyNavigitionBar);
        parseStyle(attributes);

        addView(contentView);
    }

    private void parseStyle(TypedArray attributes) {
        if (attributes != null) {
            navigitionHeight = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_navigitionHeight, navigitionHeight);
            navigitionBackground = attributes.getColor(R.styleable.EasyNavigitionBar_Easy_navigitionBackground, navigitionBackground);


            tabTextSize = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_tabTextSize, tabTextSize);
            tabTextTop = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_tabTextTop, tabTextTop);
            iconSize = (int) attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_tabIconSize, iconSize);
            hintPointSize = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_hintPointSize, hintPointSize);
            msgPointSize = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_msgPointSize, msgPointSize);
            hintPointLeft = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_hintPointLeft, hintPointLeft);
            msgPointTop = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_msgPointTop, -iconSize / 2);
            hintPointTop = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_hintPointTop, hintPointTop);

            msgPointLeft = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_msgPointLeft, -iconSize / 2);
            msgPointTextSize = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_msgPointTextSize, msgPointTextSize);
            addIconSize = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_addIconSize, addIconSize);
            addLayoutBottom = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_addIconBottom, addLayoutBottom);

            //加号属性
            addSelectTextColor = attributes.getColor(R.styleable.EasyNavigitionBar_Easy_addSelectTextColor, addSelectTextColor);
            addNormalTextColor = attributes.getColor(R.styleable.EasyNavigitionBar_Easy_addNormalTextColor, addNormalTextColor);
            addTextSize = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_addTextSize, addTextSize);
            addTextTopMargin = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_addTextTopMargin, addTextTopMargin);
            addAlignBottom = attributes.getBoolean(R.styleable.EasyNavigitionBar_Easy_addAlignBottom, addAlignBottom);


            lineHeight = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_lineHeight, lineHeight);
            lineColor = attributes.getColor(R.styleable.EasyNavigitionBar_Easy_lineColor, lineColor);


            addLayoutHeight = attributes.getDimension(R.styleable.EasyNavigitionBar_Easy_addLayoutHeight, navigitionHeight + lineHeight);

            normalTextColor = attributes.getColor(R.styleable.EasyNavigitionBar_Easy_tabNormalColor, normalTextColor);
            selectTextColor = attributes.getColor(R.styleable.EasyNavigitionBar_Easy_tabSelectColor, selectTextColor);

            int type = attributes.getInt(R.styleable.EasyNavigitionBar_Easy_scaleType, 0);
            if (type == 0) {
                scaleType = ImageView.ScaleType.CENTER_INSIDE;
            } else if (type == 1) {
                scaleType = ImageView.ScaleType.CENTER_CROP;
            } else if (type == 2) {
                scaleType = ImageView.ScaleType.CENTER;
            } else if (type == 3) {
                scaleType = ImageView.ScaleType.FIT_CENTER;
            } else if (type == 4) {
                scaleType = ImageView.ScaleType.FIT_END;
            } else if (type == 5) {
                scaleType = ImageView.ScaleType.FIT_START;
            } else if (type == 6) {
                scaleType = ImageView.ScaleType.FIT_XY;
            } else if (type == 7) {
                scaleType = ImageView.ScaleType.MATRIX;
            }

            addIconRule = attributes.getInt(R.styleable.EasyNavigitionBar_Easy_addIconRule, addIconRule);
            hasPadding = attributes.getBoolean(R.styleable.EasyNavigitionBar_Easy_hasPadding, hasPadding);

            addAsFragment = attributes.getBoolean(R.styleable.EasyNavigitionBar_Easy_addAsFragment, addAsFragment);

            attributes.recycle();
        }
    }

    //将dp、sp转换成px
    private void toDp() {
        navigitionHeight = NavigitionUtil.dip2px(getContext(), navigitionHeight);
        iconSize = NavigitionUtil.dip2px(getContext(), iconSize);
        hintPointSize = NavigitionUtil.dip2px(getContext(), hintPointSize);
        hintPointTop = NavigitionUtil.dip2px(getContext(), hintPointTop);
        hintPointLeft = NavigitionUtil.dip2px(getContext(), hintPointLeft);

        msgPointLeft = NavigitionUtil.dip2px(getContext(), msgPointLeft);
        msgPointTop = NavigitionUtil.dip2px(getContext(), msgPointTop);
        msgPointSize = NavigitionUtil.dip2px(getContext(), msgPointSize);
        msgPointTextSize = NavigitionUtil.sp2px(getContext(), msgPointTextSize);

        tabTextTop = NavigitionUtil.dip2px(getContext(), tabTextTop);
        tabTextSize = NavigitionUtil.sp2px(getContext(), tabTextSize);


        //Add
        addIconSize = NavigitionUtil.dip2px(getContext(), addIconSize);
        addLayoutHeight = NavigitionUtil.dip2px(getContext(), addLayoutHeight);
        addLayoutBottom = NavigitionUtil.dip2px(getContext(), addLayoutBottom);
        addTextSize = NavigitionUtil.sp2px(getContext(), addTextSize);
        addTextTopMargin = NavigitionUtil.dip2px(getContext(), addTextTopMargin);
    }


    public void build() {

        if (addLayoutHeight < navigitionHeight + lineHeight)
            addLayoutHeight = navigitionHeight + lineHeight;

        if (addIconRule == RULE_CENTER) {
            RelativeLayout.LayoutParams addLayoutParams = (RelativeLayout.LayoutParams) AddContainerLayout.getLayoutParams();
            addLayoutParams.height = (int) addLayoutHeight;
            AddContainerLayout.setLayoutParams(addLayoutParams);
        } else if (addIconRule == RULE_BOTTOM) {
           /* RelativeLayout.LayoutParams addLayoutParams = (RelativeLayout.LayoutParams) AddContainerLayout.getLayoutParams();
            if ((addIconSize + addIconBottom) > (navigitionHeight + 1))
                addLayoutParams.height = (int) (addIconSize + addIconBottom);
            else
                addLayoutParams.height = (int) (navigitionHeight + 1);
            AddContainerLayout.setLayoutParams(addLayoutParams);*/
        }


        navigitionLayout.setBackgroundColor(navigitionBackground);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navigitionLayout.getLayoutParams();
        params.height = (int) navigitionHeight;
        navigitionLayout.setLayoutParams(params);


        if (hasPadding) {
            mViewPager.setPadding(0, 0, 0, (int) (navigitionHeight + lineHeight));
        }

        RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams) lineView.getLayoutParams();
        lineParams.height = (int) lineHeight;
        lineView.setBackgroundColor(lineColor);
        lineView.setLayoutParams(lineParams);

//若没有设置中间添加的文字字体大小、颜色、则同其他Tab一样
        if (addTextSize == 0) {
            addTextSize = tabTextSize;
        }
        if (addNormalTextColor == 0) {
            addNormalTextColor = normalTextColor;
        }
        if (addSelectTextColor == 0) {
            addSelectTextColor = selectTextColor;
        }

        if (mode == MODE_NORMAL) {
            buildNavigition();
        } else if (mode == MODE_ADD) {
            buildAddNavigition();
        } else if (mode == MODE_ADD_VIEW) {
            buildAddViewNavigition();
        }
        if (canScroll) {
            getmViewPager().setCanScroll(true);
        } else {
            getmViewPager().setCanScroll(false);
        }
    }

    public void buildNavigition() {
        if ((titleItems.length != normalIconItems.length) || (titleItems.length != selectIconItems.length) || (normalIconItems.length != selectIconItems.length)) {
            Log.e("EasyNavigition", "请传入相同数量的Tab文字集合、未选中图标集合、选中图标集合");
            return;
        }

        tabCount = titleItems.length;

        removeNavigationAllView();

        setViewPagerAdapter();

        for (int i = 0; i < tabCount; i++) {
            View itemView = View.inflate(getContext(), R.layout.navigition_tab_layout, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;

            itemView.setLayoutParams(params);
            itemView.setId(i);

            TextView text = itemView.findViewById(R.id.tab_text_tv);
            ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
            icon.setScaleType(scaleType);
            LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
            iconParams.width = (int) iconSize;
            iconParams.height = (int) iconSize;
            icon.setLayoutParams(iconParams);

            View hintPoint = itemView.findViewById(R.id.red_point);

            //提示红点
            RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
            hintPointParams.bottomMargin = (int) hintPointTop;
            hintPointParams.width = (int) hintPointSize;
            hintPointParams.height = (int) hintPointSize;
            hintPointParams.leftMargin = (int) hintPointLeft;
            hintPoint.setLayoutParams(hintPointParams);

            //消息红点
            TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
            msgPoint.setTextSize(NavigitionUtil.px2sp(getContext(), msgPointTextSize));
            RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
            msgPointParams.bottomMargin = (int) msgPointTop;
            msgPointParams.width = (int) msgPointSize;
            msgPointParams.height = (int) msgPointSize;
            msgPointParams.leftMargin = (int) msgPointLeft;
            msgPoint.setLayoutParams(msgPointParams);


            hintPointList.add(hintPoint);
            msgPointList.add(msgPoint);

            imageViewList.add(icon);
            textViewList.add(text);

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onTabClickListener != null) {
                        if (!onTabClickListener.onTabClickEvent(view, view.getId())) {
                            mViewPager.setCurrentItem(view.getId(), smoothScroll);
                        }
                    } else {
                        mViewPager.setCurrentItem(view.getId(), smoothScroll);
                    }
                }
            });

            LayoutParams textParams = (LayoutParams) text.getLayoutParams();
            textParams.topMargin = (int) tabTextTop;
            text.setLayoutParams(textParams);
            text.setText(titleItems[i]);
            text.setTextSize(NavigitionUtil.px2sp(getContext(), tabTextSize));


            tabList.add(itemView);
            navigitionLayout.addView(itemView);
        }
        select(0, false);
    }

    private void setViewPagerAdapter() {
        adapter = new ViewPagerAdapter(fragmentManager, fragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                select(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //构建中间带按钮的navigition
    public void buildAddNavigition() {

        if ((titleItems.length != normalIconItems.length) || (titleItems.length != selectIconItems.length) || (normalIconItems.length != selectIconItems.length)) {
            Log.e("EasyNavigition", "请传入相同数量的Tab文字集合、未选中图标集合、选中图标集合");
            return;
        }
        tabCount = titleItems.length;
        if (tabCount % 2 == 0) {
            Log.e("EasyNavigition", "MODE_ADD模式下请传入奇奇奇奇奇奇奇奇奇奇奇数数量的Tab文字集合、未选中图标集合、选中图标集合");
            return;
        }

        if (addAsFragment) {
            if (fragmentList.size() < tabCount) {
                Log.e("EasyNavigition", "MODE_ADD模式下/addAsFragment=true时Fragment的数量应和传入tab集合数量相等");
                return;
            }
        } else {
            if (fragmentList.size() < tabCount - 1) {
                Log.e("EasyNavigition", "MODE_ADD模式下/addAsFragment=false时Fragment的数量应比传入tab集合数量少一个");
                return;
            }
        }

        removeNavigationAllView();

        setViewPagerAdapter();

        for (int i = 0; i < tabCount; i++) {

            if (i == tabCount / 2) {
                RelativeLayout addItemView = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                addItemParams.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;
                addItemView.setLayoutParams(addItemParams);
                navigitionLayout.addView(addItemView);

                RelativeLayout addLayout = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams addParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                addParams.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;
                if (addIconRule == RULE_CENTER) {
                    addParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                } else if (addIconRule == RULE_BOTTOM) {
                    addParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    addParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }


                final LinearLayout addLinear = new LinearLayout(getContext());
                addLinear.setOrientation(VERTICAL);
                addLinear.setGravity(Gravity.CENTER);
                final RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                 addImage = new ImageView(getContext());
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imageParams.width = (int) addIconSize;
                imageParams.height = (int) addIconSize;
                addImage.setLayoutParams(imageParams);

                TextView addText = new TextView(getContext());
                addText.setTextSize(NavigitionUtil.px2sp(getContext(), addTextSize));
                LinearLayout.LayoutParams addTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addTextParams.topMargin = (int) addTextTopMargin;
                if (TextUtils.isEmpty(titleItems[i])) {
                    addText.setVisibility(GONE);
                } else {
                    addText.setVisibility(VISIBLE);
                }
                addText.setLayoutParams(addTextParams);
                addText.setText(titleItems[i]);

                if (addIconRule == RULE_CENTER) {
                    linearParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                } else if (addIconRule == RULE_BOTTOM) {
                    linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    linearParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    if (addAlignBottom) {
                        if (textViewList != null && textViewList.size() > 0) {
                            textViewList.get(0).post(new Runnable() {
                                @Override
                                public void run() {
                                    linearParams.bottomMargin = (int) ((navigitionHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2);
                                    addLinear.setLayoutParams(linearParams);
                                }
                            });

                        }
                    }else{
                        linearParams.bottomMargin = (int) addLayoutBottom;
                        addLinear.setLayoutParams(linearParams);
                    }
                }

                addImage.setId(i);
                addImage.setImageResource(normalIconItems[i]);
                addImage.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onTabClickListener != null) {
                            if (!onTabClickListener.onTabClickEvent(view, view.getId())) {
                                if (addAsFragment)
                                    mViewPager.setCurrentItem(view.getId(), smoothScroll);
                            }
                        } else {
                            if (addAsFragment)
                                mViewPager.setCurrentItem(view.getId(), smoothScroll);
                        }
                        if (onAddClickListener != null)
                            onAddClickListener.OnAddClickEvent(view);
                    }
                });

                imageViewList.add(addImage);
                textViewList.add(addText);


                addLinear.addView(addImage);
                addLinear.addView(addText);

                tabList.add(addLinear);

                addLayout.addView(addLinear, linearParams);
                AddContainerLayout.addView(addLayout, addParams);
            } else {
                int index = i;

                View itemView = View.inflate(getContext(), R.layout.navigition_tab_layout, null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;

                itemView.setLayoutParams(params);
                itemView.setId(index);

                TextView text = itemView.findViewById(R.id.tab_text_tv);
                ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
                icon.setScaleType(scaleType);
                LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
                iconParams.width = (int) iconSize;
                iconParams.height = (int) iconSize;
                icon.setLayoutParams(iconParams);

                imageViewList.add(icon);
                textViewList.add(text);
                itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        final int tabPosition = view.getId();
                        if (onTabClickListener != null) {
                            if (!onTabClickListener.onTabClickEvent(view, view.getId())) {

                                if (tabPosition > tabCount / 2 && !addAsFragment) {
                                    mViewPager.setCurrentItem(tabPosition - 1, smoothScroll);
                                } else {
                                    mViewPager.setCurrentItem(view.getId(), smoothScroll);
                                }
                            }
                        } else {
                            if (tabPosition > tabCount / 2 && !addAsFragment) {
                                mViewPager.setCurrentItem(tabPosition - 1, smoothScroll);
                            } else {
                                mViewPager.setCurrentItem(view.getId(), smoothScroll);
                            }
                        }
                    }
                });

                LayoutParams textParams = (LayoutParams) text.getLayoutParams();
                textParams.topMargin = (int) tabTextTop;
                text.setLayoutParams(textParams);
                text.setText(titleItems[index]);
                text.setTextSize(NavigitionUtil.px2sp(getContext(), tabTextSize));


                View hintPoint = itemView.findViewById(R.id.red_point);

                //提示红点
                RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
                hintPointParams.bottomMargin = (int) hintPointTop;
                hintPointParams.width = (int) hintPointSize;
                hintPointParams.height = (int) hintPointSize;
                hintPointParams.leftMargin = (int) hintPointLeft;
                hintPoint.setLayoutParams(hintPointParams);

                //消息红点
                TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
                msgPoint.setTextSize(NavigitionUtil.px2sp(getContext(), msgPointTextSize));
                RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
                msgPointParams.bottomMargin = (int) msgPointTop;
                msgPointParams.width = (int) msgPointSize;
                msgPointParams.height = (int) msgPointSize;
                msgPointParams.leftMargin = (int) msgPointLeft;
                msgPoint.setLayoutParams(msgPointParams);


                hintPointList.add(hintPoint);
                msgPointList.add(msgPoint);


                tabList.add(itemView);
                navigitionLayout.addView(itemView);
            }
        }
        select(0, false);
    }


    private void removeNavigationAllView() {

        for (int i = 0; i < AddContainerLayout.getChildCount(); i++) {
            if (AddContainerLayout.getChildAt(i).getTag() == null) {
                AddContainerLayout.removeViewAt(i);
            }
        }

        msgPointList.clear();
        hintPointList.clear();
        imageViewList.clear();
        textViewList.clear();
        tabList.clear();

        navigitionLayout.removeAllViews();
    }

    private void addTabView(final int index) {
        View itemView = View.inflate(getContext(), R.layout.navigition_tab_layout, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;

        itemView.setLayoutParams(params);
        itemView.setId(index);

        TextView text = itemView.findViewById(R.id.tab_text_tv);
        ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
        icon.setScaleType(scaleType);
        LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
        iconParams.width = (int) iconSize;
        iconParams.height = (int) iconSize;
        icon.setLayoutParams(iconParams);

        imageViewList.add(icon);
        textViewList.add(text);

        if (mode == MODE_ADD) {
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    final int tabPosition = view.getId();
                    if (onTabClickListener != null) {
                        if (!onTabClickListener.onTabClickEvent(view, view.getId())) {

                            if (tabPosition > tabCount / 2 && !addAsFragment) {
                                mViewPager.setCurrentItem(tabPosition - 1, smoothScroll);
                            } else {
                                mViewPager.setCurrentItem(view.getId(), smoothScroll);
                            }
                        }
                    } else {
                        if (tabPosition > tabCount / 2 && !addAsFragment) {
                            mViewPager.setCurrentItem(tabPosition - 1, smoothScroll);
                        } else {
                            mViewPager.setCurrentItem(view.getId(), smoothScroll);
                        }
                    }
                }
            });
        } else if (mode == MODE_ADD_VIEW) {
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    final int tabPosition = view.getId();
                    if (onTabClickListener != null) {
                        if (!onTabClickListener.onTabClickEvent(view, index)) {

                            if (index > tabCount / 2 && !addAsFragment) {
                                mViewPager.setCurrentItem(tabPosition, smoothScroll);
                            } else {
                                mViewPager.setCurrentItem(index, smoothScroll);
                            }
                        }
                    } else {
                        if (index > tabCount / 2 && !addAsFragment) {
                            mViewPager.setCurrentItem(tabPosition, smoothScroll);
                        } else {
                            mViewPager.setCurrentItem(index, smoothScroll);
                        }
                    }
                }
            });
        }

        LayoutParams textParams = (LayoutParams) text.getLayoutParams();
        textParams.topMargin = (int) tabTextTop;
        text.setLayoutParams(textParams);
        text.setText(titleItems[index]);
        text.setTextSize(NavigitionUtil.px2sp(getContext(), tabTextSize));


        View hintPoint = itemView.findViewById(R.id.red_point);

        //提示红点
        RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
        hintPointParams.bottomMargin = (int) hintPointTop;
        hintPointParams.width = (int) hintPointSize;
        hintPointParams.height = (int) hintPointSize;
        hintPointParams.leftMargin = (int) hintPointLeft;
        hintPoint.setLayoutParams(hintPointParams);

        //消息红点
        TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
        msgPoint.setTextSize(NavigitionUtil.px2sp(getContext(), msgPointTextSize));
        RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
        msgPointParams.bottomMargin = (int) msgPointTop;
        msgPointParams.width = (int) msgPointSize;
        msgPointParams.height = (int) msgPointSize;
        msgPointParams.leftMargin = (int) msgPointLeft;
        msgPoint.setLayoutParams(msgPointParams);


        hintPointList.add(hintPoint);
        msgPointList.add(msgPoint);


        tabList.add(itemView);
        navigitionLayout.addView(itemView);
    }


    //自定义中间按钮
    public void buildAddViewNavigition() {

        if ((titleItems.length != normalIconItems.length) || (titleItems.length != selectIconItems.length) || (normalIconItems.length != selectIconItems.length)) {
            Log.e("EasyNavigition", "请传入相同数量的Tab文字集合、未选中图标集合、选中图标集合");
            return;
        }
        tabCount = titleItems.length + 1;
        if (tabCount % 2 == 0) {
            Log.e("EasyNavigition", "MODE_ADD_VIEW模式下请传入偶偶偶偶偶偶偶偶偶偶偶偶数数量的Tab文字集合、未选中图标集合、选中图标集合");
            return;
        }
        if (addAsFragment) {
            if (fragmentList.size() < tabCount) {
                Log.e("EasyNavigition", "MODE_ADD_VIEW模式下/addAsFragment=true时Fragment的数量应比传入tab集合数量多一个");
                return;
            }
        } else {
            if (fragmentList.size() < tabCount - 1) {
                Log.e("EasyNavigition", "MODE_ADD_VIEW模式下/addAsFragment=false时,Fragment的数量应和传入tab集合数量相等");
                return;
            }
        }

        removeNavigationAllView();

        setViewPagerAdapter();

        for (int i = 0; i < tabCount; i++) {

            if (i == tabCount / 2) {
                RelativeLayout addItemView = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                addItemParams.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;
                addItemView.setLayoutParams(addItemParams);
                navigitionLayout.addView(addItemView);

                RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //linearParams.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;

                if (addIconRule == RULE_CENTER) {
                    linearParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                } else if (addIconRule == RULE_BOTTOM) {
                    linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    linearParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    linearParams.bottomMargin = (int) addLayoutBottom;
                }
                customAddView.setLayoutParams(linearParams);

                customAddView.setId(i);
                customAddView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onTabClickListener != null) {
                            if (!onTabClickListener.onTabClickEvent(view, view.getId())) {
                                if (addAsFragment)
                                    mViewPager.setCurrentItem(view.getId(), smoothScroll);
                            }
                        } else {
                            if (addAsFragment)
                                mViewPager.setCurrentItem(view.getId(), smoothScroll);
                        }
                        if (onAddClickListener != null)
                            onAddClickListener.OnAddClickEvent(view);
                    }
                });

                AddContainerLayout.addView(customAddView, linearParams);
            } else {
                int index;

                if (i > tabCount / 2) {
                    index = i - 1;
                } else {
                    index = i;
                }

                View itemView = View.inflate(getContext(), R.layout.navigition_tab_layout, null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.width = NavigitionUtil.getScreenWidth(getContext()) / tabCount;

                itemView.setLayoutParams(params);
                itemView.setId(index);

                TextView text = itemView.findViewById(R.id.tab_text_tv);
                ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
                icon.setScaleType(scaleType);
                LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
                iconParams.width = (int) iconSize;
                iconParams.height = (int) iconSize;
                icon.setLayoutParams(iconParams);

                imageViewList.add(icon);
                textViewList.add(text);
                final int finalI = i;
                itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        final int tabPosition = view.getId();
                        if (onTabClickListener != null) {
                            if (!onTabClickListener.onTabClickEvent(view, finalI)) {

                                if (finalI > tabCount / 2 && !addAsFragment) {
                                    mViewPager.setCurrentItem(tabPosition, smoothScroll);
                                } else {
                                    mViewPager.setCurrentItem(finalI, smoothScroll);
                                }
                            }
                        } else {
                            if (finalI > tabCount / 2 && !addAsFragment) {
                                mViewPager.setCurrentItem(tabPosition, smoothScroll);
                            } else {
                                mViewPager.setCurrentItem(finalI, smoothScroll);
                            }
                        }
                    }
                });

                LayoutParams textParams = (LayoutParams) text.getLayoutParams();
                textParams.topMargin = (int) tabTextTop;
                text.setLayoutParams(textParams);
                text.setText(titleItems[index]);
                text.setTextSize(NavigitionUtil.px2sp(getContext(), tabTextSize));


                View hintPoint = itemView.findViewById(R.id.red_point);

                //提示红点
                RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
                hintPointParams.bottomMargin = (int) hintPointTop;
                hintPointParams.width = (int) hintPointSize;
                hintPointParams.height = (int) hintPointSize;
                hintPointParams.leftMargin = (int) hintPointLeft;
                hintPoint.setLayoutParams(hintPointParams);

                //消息红点
                TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
                msgPoint.setTextSize(NavigitionUtil.px2sp(getContext(), msgPointTextSize));
                RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
                msgPointParams.bottomMargin = (int) msgPointTop;
                msgPointParams.width = (int) msgPointSize;
                msgPointParams.height = (int) msgPointSize;
                msgPointParams.leftMargin = (int) msgPointLeft;
                msgPoint.setLayoutParams(msgPointParams);


                hintPointList.add(hintPoint);
                msgPointList.add(msgPoint);


                tabList.add(itemView);
                navigitionLayout.addView(itemView);

            }
        }

        select(0, false);

    }


    public CustomViewPager getmViewPager() {
        return mViewPager;
    }


    public void setAddViewLayout(View addViewLayout) {
        FrameLayout.LayoutParams addParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addViewLayout.addView(addViewLayout, addParams);
    }

    public ViewGroup getAddViewLayout() {
        return addViewLayout;
    }

    /**
     * tab图标、文字变换
     *
     * @param position
     */
    private void select(int position, boolean showAnim) {
        if (mode == MODE_NORMAL) {
            for (int i = 0; i < tabCount; i++) {
                if (i == position) {
                    if (anim != null && showAnim)
                        YoYo.with(anim).duration(300).playOn(tabList.get(i));
                    imageViewList.get(i).setImageResource(selectIconItems[i]);
                    textViewList.get(i).setTextColor(selectTextColor);
                } else {
                    imageViewList.get(i).setImageResource(normalIconItems[i]);
                    textViewList.get(i).setTextColor(normalTextColor);
                }
            }
        } else if (mode == MODE_ADD) {
            if (addAsFragment) {
                for (int i = 0; i < tabCount; i++) {
                    if (i == position) {
                        if (anim != null && showAnim && (position != tabCount / 2))
                            YoYo.with(anim).duration(300).playOn(tabList.get(i));
                        if (i == tabCount / 2) {
                            textViewList.get(i).setTextColor(addSelectTextColor);
                        } else {
                            textViewList.get(i).setTextColor(selectTextColor);
                        }
                        imageViewList.get(i).setImageResource(selectIconItems[i]);
                    } else {
                        imageViewList.get(i).setImageResource(normalIconItems[i]);
                        if (i == tabCount / 2) {
                            textViewList.get(i).setTextColor(addNormalTextColor);
                        } else {
                            textViewList.get(i).setTextColor(normalTextColor);
                        }
                    }
                }
            } else {
                if ((position > ((tabCount - 2) / 2))) {
                    position = position + 1;
                }
                for (int i = 0; i < tabCount; i++) {
                    if (i == position) {
                        if (anim != null && showAnim && (i != tabCount / 2))
                            YoYo.with(anim).duration(300).playOn(tabList.get(i));
                        imageViewList.get(i).setImageResource(selectIconItems[i]);
                        if (i == tabCount / 2) {
                            textViewList.get(i).setTextColor(addSelectTextColor);
                        } else {
                            textViewList.get(i).setTextColor(selectTextColor);
                        }
                    } else {
                        imageViewList.get(i).setImageResource(normalIconItems[i]);
                        if (i == tabCount / 2) {
                            textViewList.get(i).setTextColor(addNormalTextColor);
                        } else {
                            textViewList.get(i).setTextColor(normalTextColor);
                        }
                    }
                }
            }
        } else if (mode == MODE_ADD_VIEW) {
            int realPosition;

            if (addAsFragment) {
                for (int i = 0; i < tabCount; i++) {
                    if (i == tabCount / 2) {
                        continue;
                    } else if (i > tabCount / 2) {
                        realPosition = i - 1;
                    } else {
                        realPosition = i;
                    }
                    if (i == position) {
                        if (anim != null && showAnim)
                            YoYo.with(anim).duration(300).playOn(tabList.get(realPosition));
                        imageViewList.get(realPosition).setImageResource(selectIconItems[realPosition]);
                        textViewList.get(realPosition).setTextColor(selectTextColor);
                    } else {
                        imageViewList.get(realPosition).setImageResource(normalIconItems[realPosition]);
                        textViewList.get(realPosition).setTextColor(normalTextColor);
                    }
                }
            } else {
                for (int i = 0; i < tabCount - 1; i++) {
                    if (i == position) {
                        if (anim != null && showAnim)
                            YoYo.with(anim).duration(300).playOn(tabList.get(i));
                        imageViewList.get(i).setImageResource(selectIconItems[i]);
                        textViewList.get(i).setTextColor(selectTextColor);
                    } else {
                        imageViewList.get(i).setImageResource(normalIconItems[i]);
                        textViewList.get(i).setTextColor(normalTextColor);
                    }
                }
            }
        }
    }


    public void selectTab(int position) {
        getmViewPager().setCurrentItem(position, smoothScroll);
    }


    /**
     * 设置是否显示小红点
     *
     * @param position 第几个tab
     * @param isShow   是否显示
     */
    public void setHintPoint(int position, boolean isShow) {
        if (hintPointList == null || hintPointList.size() < (position + 1))
            return;
        if (isShow) {
            hintPointList.get(position).setVisibility(VISIBLE);
        } else {
            hintPointList.get(position).setVisibility(GONE);
        }
    }


    /**
     * 设置消息数量
     *
     * @param position 第几个tab
     * @param count    显示的数量  99个以上显示99+  少于1则不显示
     */
    public void setMsgPointCount(int position, int count) {
        if (msgPointList == null || msgPointList.size() < (position + 1))
            return;
        if (count > 99) {
            msgPointList.get(position).setText("99+");
            msgPointList.get(position).setVisibility(VISIBLE);
        } else if (count < 1) {
            msgPointList.get(position).setVisibility(GONE);
        } else {
            msgPointList.get(position).setText(count + "");
            msgPointList.get(position).setVisibility(VISIBLE);
        }
    }

    /**
     * 清除数字消息
     * @param position
     */
    public void clearMsgPoint(int position) {
        if (msgPointList == null || msgPointList.size() < (position + 1))
            return;
        msgPointList.get(position).setVisibility(GONE);
    }

    /**
     * 清除提示红点
     * @param position
     */
    public void clearHintPoint(int position) {
        if (hintPointList == null || hintPointList.size() < (position + 1))
            return;
        hintPointList.get(position).setVisibility(GONE);
    }

    /**
     * 清空所有提示红点
     */
    public void clearAllHintPoint() {
        for (int i = 0; i < hintPointList.size(); i++) {
            hintPointList.get(i).setVisibility(GONE);
        }
    }

    /**
     * 清空所有消息红点
     */
    public void clearAllMsgPoint() {
        for (int i = 0; i < msgPointList.size(); i++) {
            msgPointList.get(i).setVisibility(GONE);
        }
    }

    public interface OnTabClickListener {
        boolean onTabClickEvent(View view, int position);
    }


    public interface OnAddClickListener {
        boolean OnAddClickEvent(View view);
    }


    public EasyNavigitionBar addLayoutHeight(int addLayoutHeight) {
        this.addLayoutHeight = NavigitionUtil.dip2px(getContext(), addLayoutHeight);
        return this;
    }

    public EasyNavigitionBar scaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }


    public EasyNavigitionBar mode(int mode) {
        this.mode = mode;
        return this;
    }

    public EasyNavigitionBar hasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
        return this;
    }

    public EasyNavigitionBar addIconSize(int addIconSize) {
        this.addIconSize = NavigitionUtil.dip2px(getContext(), addIconSize);
        return this;
    }

 /*   public EasyNavigitionBar onAddClickListener(EasyNavigitionBar.OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
        return this;
    }*/


    public EasyNavigitionBar navigitionBackground(int navigitionBackground) {
        this.navigitionBackground = navigitionBackground;
        return this;
    }

    public EasyNavigitionBar navigitionHeight(int navigitionHeight) {
        this.navigitionHeight = NavigitionUtil.dip2px(getContext(), navigitionHeight);
        return this;
    }

    public EasyNavigitionBar normalTextColor(int normalTextColor) {
        this.normalTextColor = normalTextColor;
        return this;
    }

    public EasyNavigitionBar selectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
        return this;
    }

    public EasyNavigitionBar lineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        return this;
    }

    public EasyNavigitionBar lineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public EasyNavigitionBar tabTextSize(int tabTextSize) {
        this.tabTextSize = NavigitionUtil.sp2px(getContext(), tabTextSize);
        return this;
    }

    public EasyNavigitionBar tabTextTop(int tabTextTop) {
        this.tabTextTop = NavigitionUtil.dip2px(getContext(), tabTextTop);
        return this;
    }

    public EasyNavigitionBar msgPointTextSize(int msgPointTextSize) {
        this.msgPointTextSize = NavigitionUtil.sp2px(getContext(), msgPointTextSize);
        return this;
    }

    public EasyNavigitionBar msgPointSize(int msgPointSize) {
        this.msgPointSize = NavigitionUtil.dip2px(getContext(), msgPointSize);
        return this;
    }

    public EasyNavigitionBar msgPointLeft(int msgPointLeft) {
        this.msgPointLeft = NavigitionUtil.dip2px(getContext(), msgPointLeft);
        return this;
    }

    public EasyNavigitionBar msgPointTop(int msgPointTop) {
        this.msgPointTop = NavigitionUtil.dip2px(getContext(), msgPointTop);
        return this;
    }


    public EasyNavigitionBar hintPointSize(int hintPointSize) {
        this.hintPointSize = NavigitionUtil.dip2px(getContext(), hintPointSize);
        return this;
    }

    public EasyNavigitionBar hintPointLeft(int hintPointLeft) {
        this.hintPointLeft = NavigitionUtil.dip2px(getContext(), hintPointLeft);
        return this;
    }

    public EasyNavigitionBar hintPointTop(int hintPointTop) {
        this.hintPointTop = NavigitionUtil.dip2px(getContext(), hintPointTop);
        return this;
    }


    public EasyNavigitionBar titleItems(String[] titleItems) {
        this.titleItems = titleItems;
        return this;
    }

    public EasyNavigitionBar normalIconItems(int[] normalIconItems) {
        this.normalIconItems = normalIconItems;
        return this;
    }

    public EasyNavigitionBar selectIconItems(int[] selectIconItems) {
        this.selectIconItems = selectIconItems;
        return this;
    }

    public EasyNavigitionBar fragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
        return this;
    }

    public EasyNavigitionBar fragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public EasyNavigitionBar anim(Anim anim) {
        this.anim = anim.getYoyo();
        return this;
    }

    public EasyNavigitionBar addIconRule(int addIconRule) {
        this.addIconRule = addIconRule;
        return this;
    }

    public EasyNavigitionBar canScroll(boolean canScroll) {
        this.canScroll = canScroll;
        return this;
    }

    public EasyNavigitionBar smoothScroll(boolean smoothScroll) {
        this.smoothScroll = smoothScroll;
        return this;
    }


    public EasyNavigitionBar onTabClickListener(EasyNavigitionBar.OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }


    public EasyNavigitionBar iconSize(int iconSize) {
        this.iconSize = NavigitionUtil.dip2px(getContext(), iconSize);
        return this;
    }

    public EasyNavigitionBar addLayoutBottom(int addLayoutBottom) {
        this.addLayoutBottom = NavigitionUtil.dip2px(getContext(), addLayoutBottom);
        return this;
    }

    public EasyNavigitionBar addAsFragment(boolean addAsFragment) {
        this.addAsFragment = addAsFragment;
        return this;
    }

    public EasyNavigitionBar addCustomView(View customAddView) {
        this.customAddView = customAddView;
        return this;
    }

    public EasyNavigitionBar addTextSize(int addTextSize) {
        this.addTextSize = NavigitionUtil.sp2px(getContext(), addTextSize);
        return this;
    }

    public EasyNavigitionBar addNormalTextColor(int addNormalTextColor) {
        this.addNormalTextColor = addNormalTextColor;
        return this;
    }

    public EasyNavigitionBar addSelectTextColor(int addSelectTextColor) {
        this.addSelectTextColor = addSelectTextColor;
        return this;
    }

    public EasyNavigitionBar addTextTopMargin(int addTextTopMargin) {
        this.addTextTopMargin = NavigitionUtil.dip2px(getContext(), addTextTopMargin);
        return this;
    }

    public EasyNavigitionBar addAlignBottom(boolean addAlignBottom) {
        this.addAlignBottom = addAlignBottom;
        return this;
    }

    public String[] getTitleItems() {
        return titleItems;
    }

    public int[] getNormalIconItems() {
        return normalIconItems;
    }

    public int[] getSelectIconItems() {
        return selectIconItems;
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public Techniques getAnim() {
        return anim;
    }

    public boolean isSmoothScroll() {
        return smoothScroll;
    }

    public EasyNavigitionBar.OnTabClickListener getOnTabClickListener() {
        return onTabClickListener;
    }

    public int getIconSize() {
        return iconSize;
    }


    public float getHintPointSize() {
        return hintPointSize;
    }

    public float getHintPointLeft() {
        return hintPointLeft;
    }

    public float getHintPointTop() {
        return hintPointTop;
    }


    public float getMsgPointTextSize() {
        return msgPointTextSize;
    }

    public float getMsgPointSize() {
        return msgPointSize;
    }

    public float getMsgPointLeft() {
        return msgPointLeft;
    }

    public float getMsgPointTop() {
        return msgPointTop;
    }

    public float getTabTextTop() {
        return tabTextTop;
    }

    public float getTabTextSize() {
        return tabTextSize;
    }

    public int getNormalTextColor() {
        return normalTextColor;
    }

    public int getSelectTextColor() {
        return selectTextColor;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public int getLineColor() {
        return lineColor;
    }

    public EasyNavigitionBar.OnAddClickListener getOnAddClickListener() {
        return onAddClickListener;
    }

    public float getAddIconSize() {
        return addIconSize;
    }

    public float getAddLayoutHeight() {
        return addLayoutHeight;
    }

    public int getNavigitionBackground() {
        return navigitionBackground;
    }

    public float getNavigitionHeight() {
        return navigitionHeight;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public ViewPagerAdapter getAdapter() {
        return adapter;
    }


    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public int getMode() {
        return mode;
    }

    public LinearLayout getNavigitionLayout() {
        return navigitionLayout;
    }

    public RelativeLayout getContentView() {
        return contentView;
    }

    public View getLineView() {
        return lineView;
    }

    public ViewGroup getAddLayout() {
        return addViewLayout;
    }

    public float getAddLayoutBottom() {
        return addLayoutBottom;
    }

    public int getAddIconRule() {
        return addIconRule;
    }

    public RelativeLayout getAddContainerLayout() {
        return AddContainerLayout;
    }

    public boolean isHasPadding() {
        return hasPadding;
    }

    public boolean isAddAsFragment() {
        return addAsFragment;
    }

    public View getCustomAddView() {
        return customAddView;
    }

    public float getAddTextSize() {
        return addTextSize;
    }

    public int getAddNormalTextColor() {
        return addNormalTextColor;
    }

    public int getAddSelectTextColor() {
        return addSelectTextColor;
    }

    public float getAddTextTopMargin() {
        return addTextTopMargin;
    }

    public boolean isAddAlignBottom() {
        return addAlignBottom;
    }

    public ImageView getAddImage() {
        return addImage;
    }

   // --addIconBottom
}
