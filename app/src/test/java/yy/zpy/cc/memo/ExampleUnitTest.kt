package yy.zpy.cc.memo

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val list = mutableListOf(1, 4, 3, 8)
        var flag = false
        list.forEach {
            println(it.toString())
            flag = it > 5
            if (flag) {
                return@forEach
            }
        }
        println("flag=" + flag.toString())
    }
}
