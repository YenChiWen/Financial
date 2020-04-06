package com.example.Financial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.Financial.SwipeController.ButtonStatus.GONE;
import static com.example.Financial.SwipeController.ButtonStatus.LEFT_VISIBLE;
import static com.example.Financial.SwipeController.ButtonStatus.RIGHT_VISIBLE;

public class SwipeController extends ItemTouchHelper.Callback {
    // https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28

    enum ButtonStatus{
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    boolean swipeBack = false;
    private ButtonStatus buttonStatus = GONE;
    private int buttonWidth = 150;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;

    private SwipeControllerActions buttonsActions;
    private String mLeftButtonEdit;
    private String mRightButtonEdit;

    public SwipeController(SwipeControllerActions buttonsActions, String LeftButtonEdit, String RightButtonEdit){
        this.buttonsActions = buttonsActions;
        this.mLeftButtonEdit = LeftButtonEdit;
        this.mRightButtonEdit = RightButtonEdit;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(this.swipeBack){
            this.swipeBack = (this.buttonStatus != GONE);
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if(this.mRightButtonEdit != null && buttonStatus.equals(RIGHT_VISIBLE)){
                dX = Math.min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else if(this.mLeftButtonEdit != null && buttonStatus.equals(LEFT_VISIBLE)){
                dX = Math.max(dX, buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else{
                this.setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
        currentItemViewHolder = viewHolder;
    }

    private void setTouchListener(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeBack = (   motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                                motionEvent.getAction() == MotionEvent.ACTION_UP);

                if(swipeBack){
                    if(dX < -buttonWidth)
                        buttonStatus = ButtonStatus.RIGHT_VISIBLE;
                    else if(dX > buttonWidth)
                        buttonStatus = ButtonStatus.LEFT_VISIBLE;

                    if(buttonStatus != GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItmeClickable(recyclerView, false);
                    }
                }

                return false;
            }
        });
    }

    private void setTouchDownListener(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener(){
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    setItmeClickable(recyclerView, true);
                    swipeBack = false;

                    if(buttonsActions != null && buttonInstance != null && buttonInstance.contains(motionEvent.getX(), motionEvent.getY())){
                        if(buttonStatus == LEFT_VISIBLE){
                            buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                        }
                        else if(buttonStatus == RIGHT_VISIBLE) {
                            buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                        }
                    }
                    buttonStatus = GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItmeClickable(RecyclerView recyclerView, boolean isCurrentlyActive){
        for(int i=0; i <recyclerView.getChildCount(); ++i){
            recyclerView.getChildAt(i).setClickable(isCurrentlyActive);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){

        float corners = 0;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft()+5, itemView.getTop(), itemView.getLeft()+buttonWidth, itemView.getBottom());
        if(this.mLeftButtonEdit != null){
            p.setColor(Color.BLUE);
            c.drawRoundRect(leftButton, corners, corners, p);
            drawText("EDIT", c, leftButton, p);
        }

        RectF rightButton = new RectF(itemView.getRight()-buttonWidth, itemView.getTop(), itemView.getRight()-5, itemView.getBottom());
        if(this.mRightButtonEdit != null){
            p.setColor(Color.RED);
            c.drawRoundRect(rightButton, corners, corners, p);
            drawText("DELETE", c, rightButton, p);
        }

        buttonInstance = null;
        if (buttonStatus == ButtonStatus.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        }
        else if (buttonStatus == ButtonStatus.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 30;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

    public void onDraw(Canvas canvas){
        if(this.currentItemViewHolder != null){
            drawButtons(canvas, this.currentItemViewHolder);
        }
    }
}

