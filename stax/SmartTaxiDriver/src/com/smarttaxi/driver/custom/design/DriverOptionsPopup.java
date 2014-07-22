package com.smarttaxi.driver.custom.design;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smarttaxi.driver.R;

public class DriverOptionsPopup extends PopupWindows implements OnDismissListener {
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private Animation mTrackAnim;
	private LayoutInflater inflater;
	private ViewGroup mTrack;
	private LinearLayout mScroller;
	private RelativeLayout cab_available_container;
	private int rootWidth=0;
	private OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;
	
	private List<ActionItem> mActionItemList = new ArrayList<ActionItem>();
	
	private boolean mDidAction;
	private boolean mAnimateTrack;
	
	private int mChildPos;    
    private int mAnimStyle;
    
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;
	
	/**
	 * Constructor.
	 * 
	 * @param context Context
	 */
	public DriverOptionsPopup(Context context) {
		super(context);
		
		inflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTrackAnim 	= AnimationUtils.loadAnimation(context, R.anim.rail);
		
		mTrackAnim.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
	              // Pushes past the target area, then snaps back into place.
	                // Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = (t * 1.55f) - 1.1f;
				
	            return 1.2f - inner * inner;
	        }
		});
	        
		setRootViewId(R.layout.quickaction);
		
		
		mAnimStyle		= ANIM_GROW_FROM_RIGHT;
		mAnimateTrack	= true;
		mChildPos		= 0;
	}
	
	/**
     * Get action item at an index
     * 
     * @param index  Index of item (position from callback)
     * 
     * @return  Action Item at the position
     */
    public ActionItem getActionItem(int index) {
        return mActionItemList.get(index);
    }
    
	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootView	= (ViewGroup) inflater.inflate(id, null);
		mTrack 		= (ViewGroup) mRootView.findViewById(R.id.tracks);

		mArrowDown 	= (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp 	= (ImageView) mRootView.findViewById(R.id.arrow_up);
		mScroller   = (LinearLayout) mRootView.findViewById(R.id.scroll);
		//cab_available_container = (RelativeLayout)mRootView.findViewById(R.id.cab_available_container);
		//This was previously defined on show() method, moved here to prevent force close that occured
		//when tapping fastly on a view to show quickaction dialog.
		//Thanx to zammbi (github.com/zammbi)
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
		 
		
		 setContentView(mRootView);
	}
	
	/**
	 * Animate track.
	 * 
	 * @param mAnimateTrack flag to animate track
	 */
	public void mAnimateTrack(boolean mAnimateTrack) {
		this.mAnimateTrack = mAnimateTrack;
	}
	
	/**
	 * Set animation style.
	 * 
	 * @param mAnimStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}

	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem(ActionItem action) {
		mActionItemList.add(action);
		
		String title 	= action.getTitle();
		Drawable icon 	= action.getIcon();
		
		View container	= (View) inflater.inflate(R.layout.action_item, null);
		
		ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);
		TextView text 	= (TextView) container.findViewById(R.id.tv_title);
		
		if (icon != null) { 
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}
		
		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}
		
		final int pos 		=  mChildPos;
		final int actionId 	= action.getActionId();
		
		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(DriverOptionsPopup.this, pos, actionId);
                }
				
                if (!getActionItem(pos).isSticky()) {  
                	mDidAction = true;
                	
                	//workaround for transparent background bug
                	//thx to Roman Wozniak <roman.wozniak@gmail.com>
                	v.post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }
			}
		});
		
		container.setFocusable(true);
		container.setClickable(true);
			 
		mTrack.addView(container, mChildPos+1);
		
		mChildPos++;
	}
	
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}
	
	/**
	 * Show popup mWindow
	 */
	
	
	public void show (View anchor) {
	    preShow();

	    int xPos, yPos, arrowPos;

	    mDidAction          = false;

	    int[] location      = new int[2];

	    anchor.getLocationOnScreen(location);

	    Rect anchorRect     = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
	                        + anchor.getHeight());

	    //mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

	    mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    

	    int rootHeight      = mRootView.getMeasuredHeight();
	    	
	    if ( rootWidth == 0) {
	        rootWidth       = mRootView.getMeasuredWidth();
	    }

	    int screenWidth     = mWindowManager.getDefaultDisplay().getWidth();
	    int screenHeight    = mWindowManager.getDefaultDisplay().getHeight();

	    //automatically get X coord of popup (top left)
	    if ((anchorRect.left + rootWidth) > screenWidth) {
	        xPos        = anchorRect.left - (rootWidth-anchor.getWidth());          
	        xPos        = (xPos < 0) ? 0 : xPos;

	        arrowPos    = anchorRect.centerX()-xPos;

	    } else {
	        if (anchor.getWidth() > rootWidth) {
	            xPos = anchorRect.centerX() - (rootWidth/2);
	        } else {
	            xPos = anchorRect.left;
	        }

	        arrowPos = anchorRect.centerX()-xPos;
	    }

	    int dyTop           = anchorRect.top;
	    int dyBottom        = screenHeight - anchorRect.bottom;

	    boolean onTop       = (dyTop > dyBottom) ? true : false;
	    
	    if (onTop) {
	        if (rootHeight > dyTop) {
	            yPos            = 15;
	            LayoutParams l  = mScroller.getLayoutParams();
	            l.height        = dyTop - anchor.getHeight();
	        } else {
	            yPos = anchorRect.top - rootHeight;
	        }
	    } else {
	        yPos = anchorRect.bottom;

	        if (rootHeight > dyBottom) { 
	            LayoutParams l  = mScroller.getLayoutParams();
	            l.height        = dyBottom;
	        }
	    }

	    showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

	    setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

	    mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth Screen width
	 * @param requestedX distance from left screen
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;
					
		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;
					
		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;
					
		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopDownMenu_Right : R.style.Animations_PopDownMenu_Right);
			}
					
			break;
		}
	}
	
	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        
        param.leftMargin = requestedX - arrowWidth / 2;
      
        hideArrow.setVisibility(View.INVISIBLE);
    }
	
	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener(DriverOptionsPopup.OnDismissListener listener) {
		setOnDismissListener(this);
		
		mDismissListener = listener;
	}
	
	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}
	
	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(DriverOptionsPopup source, int pos, int actionId);
	}
	
	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
}

