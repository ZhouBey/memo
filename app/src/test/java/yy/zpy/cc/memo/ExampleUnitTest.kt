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
        var data = (0..10).toList()
        data = data.filter {
            it > 4
        }
        data.forEach {
            println(it)
        }
    }
}
