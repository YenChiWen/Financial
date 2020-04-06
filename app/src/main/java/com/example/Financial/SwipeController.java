package com.example.Financial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeController extends ItemTouchHelper.Callback {
    // https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28

    enum ButtonStatus{
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    boolean swipeBack = false;
    private ButtonStatus buttonStatus = ButtonStatus.GONE;
    private int buttonWidth = 300;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private SwipeControllerActions buttonsActions = null;

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
            this.swipeBack = (this.buttonStatus != ButtonStatus.GONE);
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // TODO
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            this.setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        drawButtons(c, viewHolder);
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

                    if(buttonStatus != ButtonStatus.GONE) {
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

                    // TODO
                    buttonStatus = ButtonStatus.GONE;
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
        float buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
        p.setColor(Color.BLUE);
        c.drawRoundRect(leftButton, corners, corners, p);
        drawText("EDIT", c, leftButton, p);

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("DELETE", c, rightButton, p);

        buttonInstance = null;
        if (buttonStatus == ButtonStatus.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        }
        else if (buttonStatus == ButtonStatus.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 15;
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

class SwipeControllerActions{
    public void onLeftClicked(int position) {

    }

    public void onRightClicked(int position) {

    }
}
