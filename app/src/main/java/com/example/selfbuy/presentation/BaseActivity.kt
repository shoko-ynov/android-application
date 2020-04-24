package com.example.selfbuy.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        this.supportActionBar?.setShowHideAnimationEnabled(false)
    }

    /**
     * Masque le clavier si on clique en dehors d'un champ
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val v: View? = currentFocus

        if (v != null &&
            (ev!!.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + v.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) hideKeyboard(
                this
            )
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     * Permettant de masquer le clavier
     */
    private fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    fun supportActionBar(show: Boolean){
        if(show){
            this.supportActionBar?.show()
        }
        else{
            this.supportActionBar?.hide()
        }
    }
}
