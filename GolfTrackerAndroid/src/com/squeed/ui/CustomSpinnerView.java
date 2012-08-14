package com.squeed.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.squeed.golftracker.R;

/**
 * 
 * @author Erik Lupander, Squeed
 *
 */
public class CustomSpinnerView extends View {
	
	public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

	
	private Paint mTextPaint;
	private Paint mTextPaint2;
       
    
    
    private float lastY = 0;    
    private final VelocityTracker vt = VelocityTracker.obtain();
	
    private int selectedIndex;
	private boolean myAnimRunning;
   
    private MyListItem[] items;
    
   private static final DecelerateInterpolator intp = new DecelerateInterpolator();
      
   private int myWidth, myHeight;
   private int borderPadding = 5;
    
   /*
    * using sensible defaults.
    */
    private int itemsVisible = 5;
	private int textColor = Color.BLACK;
	private int textSize = 32;
	private float text1CentreOffset, text2CentreOffset;
	private float realTextSize;
	
	private int textColor2 = Color.BLACK;
	private int textSize2 = 32;
	
	private float currentListOffset = 0.0f;
	private float baseOffset;
    private float minOffset, maxOffset; // Max/min offset. Calculated depending on number of items, textSize and rowMargin.
	
	private float rowMargin = 20;
	
	private int textAlign, text2Align, iconAlign;

	private OnCustomSpinnerSelectListener listener;
	
	private float textXPos, text2XPos, iconXPos;
	
	private Bitmap icon;
	private float moveRow2Diff;
	private float ascent, ascent2, descent, descent2;
	
	
    /**
     * Constructor.  This version is only needed if you will be instantiating
     * the object manually (not from a layout XML file).
     * @param context
     */
    public CustomSpinnerView(Context context) {
        super(context);
        init();
    }

    /**
     * Construct object, initializing with any attributes we understand from a
     * layout file. These attributes are defined in
     * SDK/assets/res/any/classes.xml.
     * 
     * @see android.view.View#View(android.content.Context, android.util.AttributeSet)
     */
    public CustomSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomSpinnerView);
        setTextColor(a.getColor(R.styleable.CustomSpinnerView_textColor, 0xFF000000));
        setText2Color(a.getColor(R.styleable.CustomSpinnerView_textColor2, 0xFF000000));

        int textSize = a.getInteger(R.styleable.CustomSpinnerView_textSize, 30);
        int textSize2 = a.getInteger(R.styleable.CustomSpinnerView_textSize2, 30);
        if (textSize > 0) {
            setTextSize(textSize);
        }
        if (textSize2 > 0) {
            setText2Size(textSize2);
        }
        itemsVisible = a.getInteger(R.styleable.CustomSpinnerView_itemsVisible, 5);
        
        textAlign = a.getInt(R.styleable.CustomSpinnerView_textAlign, LEFT);
        textAlign = a.getInt(R.styleable.CustomSpinnerView_text2Align, RIGHT);
        iconAlign = a.getInt(R.styleable.CustomSpinnerView_iconAlign, RIGHT);


        init();
        
        a.recycle();
    }

    private final void init() {        
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.green_icon);
    	mTextPaint = new Paint();
    	mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        
        mTextPaint2 = new Paint();
    	mTextPaint2.setTextSize(textSize2);
        mTextPaint2.setColor(textColor2);
        
        initMockdata();       
    }

	private void initMockdata() {
		Long i = 0L;
		items = new MyListItem[18];
		
		//String[] t = new String[]{"Birdie", "Eagle", "Albatross", "Bogey", "2xBogey", "Par"};
		String[] t = new String[]{"Ä", "q", "T", "q", "d", "f"};
        for(int x = 0; x < 18; x++) {
        	items[x] = new MyListItem(i, Long.toString(i++), null, icon);
        }
	}


    /**
     * Sets the text size for this label
     * @param size Font size
     */
    public void setTextSize(int size) {
        this.textSize = size;        
        requestLayout();
        invalidate();
    }

    /**
     * Sets the text color for this label.
     * @param color ARGB value for the text
     */
    public void setTextColor(int color) {
        this.textColor = color;
        invalidate();
    }
    
    /**
     * Sets the text size for this label
     * @param size Font size
     */
    public void setText2Size(int size) {
        this.textSize2 = size;
        //this.rowMargin = size / 2;
        requestLayout();
        invalidate();
    }

    /**
     * Sets the text color for this label.
     * @param color ARGB value for the text
     */
    public void setText2Color(int color) {
        this.textColor2 = color;
        invalidate();
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
        recalculateDimensions();
    }

	private void recalculateDimensions() {
		myWidth = getWidth();
        myHeight = getHeight();
        maxOffset = getHeight()/2 - items.length*(rowMargin+realTextSize);
        minOffset = getHeight()/2;
        
        // Set text positioning
        textXPos = getTextPosition(textAlign);
        mTextPaint.setTextAlign(getTextAlign(textAlign));
        
        text2XPos = getTextPosition(text2Align);
        mTextPaint2.setTextAlign(getTextAlign(text2Align));
        iconXPos = getIconPosition();
                
        mTextPaint.setAntiAlias(true);        
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        
        mTextPaint2.setAntiAlias(true);        
        mTextPaint2.setTextSize(textSize2);
        mTextPaint2.setColor(textColor2);

        descent = mTextPaint.descent();
        ascent = mTextPaint.ascent();
        descent2 = mTextPaint2.descent();
        ascent2 = mTextPaint2.ascent();
        baseOffset = (myHeight/2); // Why the 4. Fix this somehow...
        
        Log.i("CustomSpinnerView", "D1: " + descent + " A1: " + ascent + " D2: " + descent2 + " A2: " + ascent2);
        
        float text1CentreOffset = (((Math.abs(ascent)+Math.abs(descent))    / 2) - Math.abs(descent));
        float text2CentreOffset = ((Math.abs(ascent2)+Math.abs(descent2)) / 2) - Math.abs(descent2);
        
        
        moveRow2Diff = text2CentreOffset - text1CentreOffset;
        
        realTextSize = Math.abs(ascent) + Math.abs(descent);
        this.rowMargin = realTextSize / 2;
        this.text1CentreOffset = ((Math.abs(ascent)+ descent) / 2)-descent;
        this.text2CentreOffset = ((Math.abs(ascent2)+ descent2) / 2)-descent2;
	}

    

	/**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text widest... todo fix this, for now using 12 char dummy?
            result = (int) mTextPaint.measureText("1") + getPaddingLeft()
                    + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {

        	result = calculateHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    
   private int calculateHeight() {
		return (int) (itemsVisible*(rowMargin+realTextSize));
	}
    
    
    @Override
	public boolean onTouchEvent(MotionEvent e) {		
		final float y = e.getY();
		
		//dumpEvent(e);
		switch (e.getAction()) {
				
		case MotionEvent.ACTION_DOWN:
			handleActionDown();
			return true;
		case MotionEvent.ACTION_MOVE:
			handleActionMove(e, y);
			return true;
		case MotionEvent.ACTION_UP:
			handleActionUp(e);
			return true;
		}
		return true;
    }

	private void handleActionUp(MotionEvent e) {
		invalidate();
		
		vt.addMovement(e);
		vt.computeCurrentVelocity(9);
		float yVelocity = vt.getYVelocity();
		vt.clear();
		startAnimation(new ScrollAnimation(yVelocity));
		myAnimRunning = true;		
	}

	private void handleActionMove(MotionEvent e, final float y) {
		if(lastY != -1) {
			currentListOffset -= (lastY - y);
		}
		vt.addMovement(e);
		lastY = y;
		invalidate();
	}

	private void handleActionDown() {
		myAnimRunning = false;		
		vt.clear();
		lastY = -1;
		Animation animation = getAnimation();
		if(animation != null) {
			//animation.cancel();
			animation.reset();
			if(animation instanceof ScrollAnimation)
				((ScrollAnimation) animation).initialVelocity = 0.0f;
			
		}
		invalidate();
	}
    
    public void setOnCustomSpinnerSelectListener(OnCustomSpinnerSelectListener listener) {
    	this.listener = listener;
    }
    
    @Override
    protected void onAnimationStart() {
    	super.onAnimationStart();
    }
    	
    @Override
    protected void onAnimationEnd() {
    	super.onAnimationEnd();
    	
    	if(myAnimRunning) {
    		// When fling animation ends, animate to nearest "value"
    		selectedIndex = Math.round((baseOffset+(-currentListOffset)) / (realTextSize+rowMargin));
    		if(selectedIndex < 0) {
    			selectedIndex = 0;
    		} else if(selectedIndex > items.length -1) {
    			selectedIndex = items.length - 1;
    		}    		
    		snapToIndex(selectedIndex);    		
    	}
    	myAnimRunning = false;
    }

	private void snapToIndex(int index) {		
		// Find out by how many pixels we need to scroll to align perfectly.
		float targetScroll = baseOffset - (index * (realTextSize+rowMargin));
		float scrollBy = currentListOffset - targetScroll;
		
		startAnimation(new ScrollToAnimation(scrollBy));
		if(listener!=null)
			listener.onSelect(items[index]);
	}
    
	
    /** Show an event in the LogCat view, for debugging */
//	private void dumpEvent(MotionEvent event) {
//	   String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
//	      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
//	   StringBuilder sb = new StringBuilder();
//	   int action = event.getAction();
//	   int actionCode = action & MotionEvent.ACTION_MASK;
//	   sb.append("event ACTION_" ).append(names[actionCode]);
//	   if (actionCode == MotionEvent.ACTION_POINTER_DOWN
//	         || actionCode == MotionEvent.ACTION_POINTER_UP) {
//	      sb.append("(pid " ).append(
//	      action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
//	      sb.append(")" );
//	   }
//	   sb.append("[" );
//	   for (int i = 0; i < event.getPointerCount(); i++) {
//	      sb.append("#" ).append(i);
//	      sb.append("(pid " ).append(event.getPointerId(i));
//	      sb.append(")=" ).append((int) event.getX(i));
//	      sb.append("," ).append((int) event.getY(i));
//	      if (i + 1 < event.getPointerCount())
//	         sb.append(";" );
//	   }
//	   sb.append("]" );
//	   Log.d("CustomSpinnerView", sb.toString() + " currentListOffset: " + currentListOffset);
//	}

    /**
     * Render the text
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
                
        drawGradients(canvas);
        drawContent(canvas);       
        drawMiddleOverlay(canvas);
        drawOuterBorders(canvas);

    }

	private void drawOuterBorders(Canvas canvas) {
		Paint borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));

		borderPaint.setStrokeWidth(10f);
		
		canvas.drawLine(borderPadding, borderPadding, myWidth-borderPadding, borderPadding, borderPaint);
		canvas.drawLine(myWidth-borderPadding, borderPadding, myWidth-borderPadding, myHeight-borderPadding, borderPaint);
		canvas.drawLine(myWidth-borderPadding, myHeight-borderPadding, borderPadding, myHeight-borderPadding, borderPaint);
		canvas.drawLine(borderPadding, myHeight-borderPadding, borderPadding, borderPadding, borderPaint);
	}

	private void drawMiddleOverlay(Canvas canvas) {
		Paint overlayRectPaint = new Paint();
        overlayRectPaint.setARGB(100, 80, 150, 30);
        
        RectF rect = new RectF(borderPadding, (myHeight/2) - ((realTextSize+(descent))/2) - descent, myWidth-borderPadding, (myHeight/2) + ((realTextSize+(descent))/2) + descent);
        canvas.drawRect(rect, overlayRectPaint); 
        
        Paint p3 = new Paint();
		p3.setARGB(200, 80, 150, 30);
		p3.setStrokeWidth(4);
		p3.setAntiAlias(true);

		// Draw lines around middle transparent box.
		canvas.drawLine(borderPadding, (myHeight/2) - ((realTextSize+(descent))/2) - descent, myWidth-borderPadding, (myHeight/2) - ((realTextSize+(descent))/2) - descent, p3);
		canvas.drawLine(myWidth-borderPadding, (myHeight/2) - ((realTextSize+(descent))/2) - descent, myWidth-borderPadding, (myHeight/2) + ((realTextSize+(descent))/2) + descent, p3);
		canvas.drawLine(myWidth-borderPadding, (myHeight/2) + ((realTextSize+(descent))/2) + descent, borderPadding, (myHeight/2) + ((realTextSize+(descent))/2) + descent, p3);
		canvas.drawLine(borderPadding, (myHeight/2) + ((realTextSize+(descent))/2) + descent, borderPadding, (myHeight/2) - ((realTextSize+(descent))/2) - descent, p3);
        
		// Draw line through the middle.
		overlayRectPaint.setStrokeWidth(1.0f);
		canvas.drawLine(borderPadding, (myHeight/2), myWidth - borderPadding, (myHeight / 2), overlayRectPaint);
		
		//canvas.drawLine(borderPadding, (myHeight/2)+ascent, myWidth - borderPadding, (myHeight / 2)+ascent, mTextPaint);		
		//canvas.drawLine(borderPadding, (myHeight/2)+descent, myWidth - borderPadding, (myHeight / 2)+descent, mTextPaint);
	}

	private void drawContent(Canvas canvas) {
		// Clip so text doesn't get overdrawn outside "spinner"
		canvas.save();
        canvas.clipRect(borderPadding+5, borderPadding+5, myWidth - borderPadding-5, myHeight - borderPadding-5);
        
        
        if(items != null) {
	        for(int i = 0; i < items.length; i++) {
	        	if(items[i].text != null) {
	        		canvas.drawText(items[i].text, textXPos, currentListOffset + (i*((realTextSize)+rowMargin)) + text1CentreOffset  , mTextPaint);
	        	}
	        	if(items[i].text2 != null) {
	        		canvas.drawText(items[i].text2, text2XPos, currentListOffset + (i*(realTextSize+rowMargin)) + text2CentreOffset + moveRow2Diff, mTextPaint2);
	        	}
	        	if(items[i].icon != null) {
	        		// We must calculate mid position of icon to position it aligned to the text.
	        		int height = icon.getHeight();	        		
	        		canvas.drawBitmap(items[i].icon, iconXPos, currentListOffset + (i*(realTextSize+rowMargin)) - (height/2), mTextPaint);
	        	}
	        }
        }
        canvas.restore();
	}

	private void drawGradients(Canvas canvas) {
		GradientDrawable gradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{0xFF666666, Color.WHITE});
        gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradient.setDither(true);
        gradient.setBounds(borderPadding, borderPadding, myWidth-borderPadding, myHeight/2-borderPadding);
        gradient.draw(canvas);
        
        GradientDrawable gradient2 = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{0xFF666666, Color.WHITE});        
        gradient2.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradient2.setDither(true);
        gradient2.setBounds(borderPadding, myHeight/2-borderPadding, myWidth-borderPadding, myHeight-borderPadding);
        gradient2.draw(canvas);
	}
    
    
    private Align getTextAlign(int lTextAlign) {
    	switch(lTextAlign) {
		case LEFT:
			return Align.LEFT;
		case CENTER:
			return Align.CENTER;
		case RIGHT:
			return Align.RIGHT;
		default:
			return Align.LEFT;
		}
	}

	private float getTextPosition(int lTextAlign) {
		switch(lTextAlign) {
		case LEFT:
			return borderPadding*2;
		case CENTER:
			return myWidth / 2;
		case RIGHT:
			return myWidth - (borderPadding*2);
		default: 
			return 10.0f;
		}		
	}
	
	private float getIconPosition() {
		switch(iconAlign) {
		case LEFT:
			return borderPadding*4;
		case CENTER:
			return myWidth / 2;
		case RIGHT:
			return myWidth - (borderPadding*4);
		default: 
			return 20.0f;
		}		
	}

	public Long getSelectedItemId() {
    	return items[selectedIndex].id;
    }
    
    public String getSelectedItemValue() {
    	return items[selectedIndex].text;
    }
    
    // TODO make duration dependent on initial velocity. I.e. low velocity should give shorter animation.
    class ScrollAnimation extends Animation {
    	
    	private final static long DURATION = 900;
    	
    	private float initialVelocity;

    	public ScrollAnimation(float initialVelocity){
    		setDuration(DURATION);    		
    		this.initialVelocity = initialVelocity;
    	}

    	public void applyTransformation(float interpolatedTime, Transformation t){
    		currentListOffset += (intp.getInterpolation(1.0f - interpolatedTime)*initialVelocity);
    		Log.i("MyAnimation", "Current offset: " + currentListOffset + " max: " + maxOffset + " min: " + minOffset);
    		if(currentListOffset < maxOffset || currentListOffset > baseOffset) {
    			reset();    			
    		}
    	}
    }
        
    class ScrollToAnimation extends Animation {
    	
    	private final static long DURATION = 100;
		private float scrollBy;
		private long startedAt;
		private float startOffset;
    	
    	public ScrollToAnimation(float scrollBy){
    		this.scrollBy = scrollBy;
    		this.startOffset = currentListOffset;
    		this.startedAt = System.currentTimeMillis();
    		setDuration(DURATION);    		
    	}

    	public void applyTransformation(float interpolatedTime, Transformation t) {    		
    		float percentage = (float) (System.currentTimeMillis() - startedAt) / (float) DURATION;    		
    		currentListOffset = startOffset - percentage*scrollBy;    		
    		if(percentage >= 1.0f) {
    			currentListOffset = startOffset-scrollBy;
    			reset();
    		}
    	}
    }

	public MyListItem[] getItems() {
		return items;
	}

	public void setItems(MyListItem[] items) {
		this.items = items;
		recalculateDimensions();
		invalidate();
	}

	public void setSelectedIndex(int index) {
		this.selectedIndex = index;
		snapToIndex(index);
	}
}