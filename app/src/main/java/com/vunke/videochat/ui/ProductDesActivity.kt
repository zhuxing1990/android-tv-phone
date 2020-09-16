package com.vunke.videochat.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vunke.videochat.R
import kotlinx.android.synthetic.main.activity_proudct_des.*

class ProductDesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proudct_des)
        productdes_but.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                if(productdes_but.text.equals("返回")){
                    finish()
                }else{
                    productdes_rl.setBackgroundResource(R.mipmap.product_description2)
                    productdes_but.setText("返回")
                }
            }
        })
    }
}
