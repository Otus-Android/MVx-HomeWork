package ru.otus.cryptosample.coins.feature

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CustomItemAnimator : DefaultItemAnimator() {

    init {
        addDuration = 300L
        removeDuration = 300L
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null) {
            return false
        }
        holder.itemView.alpha = 0f
        holder.itemView.translationX = -holder.itemView.width.toFloat()
        dispatchAddStarting(holder)
        holder.itemView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(addDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchAddFinished(holder)
                }
                override fun onAnimationCancel(animation: Animator) {
                    holder.itemView.alpha = 1f
                    holder.itemView.translationX = 0f
                }
            })
            .start()
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null) {
            return false
        }
        dispatchRemoveStarting(holder)
        holder.itemView.animate()
            .alpha(0f)
            .translationX(holder.itemView.width.toFloat())
            .setDuration(removeDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchRemoveFinished(holder)
                    holder.itemView.alpha = 1f
                    holder.itemView.translationX = 0f
                }
                override fun onAnimationCancel(animation: Animator) {
                    holder.itemView.alpha = 1f
                    holder.itemView.translationX = 0f
                }
            })
            .start()
        return true
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return super.animateMove(holder, fromX, fromY, toX, toY)
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {
        return super.animateChange(oldHolder, newHolder, fromLeft, fromTop, toLeft, toTop)
    }
}
