package com.example.pontos_turisticos.util

import android.content.Context
import android.widget.Toast

class Util {

    companion object {
        fun setToast(context: Context, mensagem: String) {
            try {
                val toast = Toast.makeText(context, mensagem, Toast.LENGTH_SHORT)
                toast.show()
            } catch (e: Exception){
                e.printStackTrace()
            }

        }
    }
}