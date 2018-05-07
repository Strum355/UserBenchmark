package xyz.noahsc.userbenchmark.listener

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class RecyclerItemClickListener(context: Context, val recyclerView: RecyclerView, private val clickListener: ClickListener) : RecyclerView.OnItemTouchListener {

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            recyclerView.findChildViewUnder(e.x, e.y)?.run {
                clickListener.onLongClick(this, recyclerView.getChildAdapterPosition(this))
            }
        }
    })

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e)) {
            view.findChildViewUnder(e.x, e.y)?.run {
                clickListener.onClick(this, view.getChildAdapterPosition(this))
            }
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}

interface ClickListener {
    fun onClick(view: View, position: Int)
    fun onLongClick(view: View, position: Int)
}