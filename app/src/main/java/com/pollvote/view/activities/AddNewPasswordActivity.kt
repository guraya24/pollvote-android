package com.pollvote.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pollvote.R
import kotlinx.android.synthetic.main.activity_add_new_password.*

class AddNewPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_new_password)
        initUI()
    }

    private fun initUI() {
        btn_continue.setOnClickListener {

            if (et_new_password.text.toString() == "") {
                et_new_password.error = "Please Enter new password"
            } else if (et_confirm_password.text.toString() == "") {
                et_confirm_password.error = "Please Enter new confirm-password"
            }else if (et_new_password.text.toString() !=et_confirm_password.text.toString()) {
                Toast.makeText(this, "Both password are not matched", Toast.LENGTH_SHORT).show()
            } else
             {
                startActivity(Intent(this, LoginActivity::class.java))

                Toast.makeText(this, "Your Password has been changed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}