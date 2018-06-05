package com.brijesh.loaderapplication

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLoader.setOnClickListener {
            startLoading()
        }
    }

    private fun startLoading() {
        flLoader.visibility = View.GONE
        val up1 = AnimationUtils.loadAnimation(this, R.anim.translate_up_1)
        val up2 = AnimationUtils.loadAnimation(this, R.anim.translate_up_2)
        up1.fillAfter = true
        up2.fillAfter = true
        redWave1.startAnimation(up1)
        redWave2.startAnimation(up2)
        val up3 = AnimationUtils.loadAnimation(this, R.anim.translate_up_3)
        up3.fillAfter = true
        up3.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                rlWave.gravity = Gravity.CENTER
                redWave1.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate_1))
                redWave2.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate_2))
                redWave3.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate_3))
            }
        })
        redWave3.startAnimation(up3)
        Handler().postDelayed({
            rlWave.gravity = Gravity.START
            redWave1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_down_1))
            redWave2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_down_2))
            redWave3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_down_3))
            Handler().postDelayed({
                flLoader.visibility = View.VISIBLE
            }, 1000)

        }, 10000)
    }
}
