package com.vunke.videochat.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vunke.videochat.R
import kotlinx.android.synthetic.main.activity_proudct_des.*

class ProductDesActivity : AppCompatActivity() {
//    var isFirst = true;
    var nextPage = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proudct_des)
        productdes_but.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                   if (productdes_but.text.equals("下一页")){
                       if(nextPage == 0){
                           nextPage++;
                           productdes_rl.setBackgroundResource(R.mipmap.product_description3)
                       }else if (nextPage == 1){
                           nextPage++;
                           productdes_rl.setBackgroundResource(R.mipmap.product_description4)
                       }else if (nextPage == 2){
                           productdes_rl.setBackgroundResource(R.mipmap.product_description2)
                           productdes_but.setText("返回")
                       }
//                        if (isFirst){
//                            isFirst = false;
//                            productdes_rl.setBackgroundResource(R.mipmap.product_description3)
//                        }else{
//                            productdes_rl.setBackgroundResource(R.mipmap.product_description2)
//                            productdes_but.setText("返回")
//                        }
                   }else if (productdes_but.text.equals("返回")){
                       finish()
                   }
            }
        })
    }
}
