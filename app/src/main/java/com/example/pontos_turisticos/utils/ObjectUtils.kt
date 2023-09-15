package com.example.pontos_turisticos.utils
object ObjectUtils {
    fun isEmpty(o: Any?): Boolean {
        return (o == null) || ((o is String) && o.trim { it <= ' ' }
            .isEmpty()) || ((o is List<*>) && o.isEmpty())
    }

    fun isNotEmpty(o: Any?): Boolean {
        return !isEmpty(o)
    }
}
