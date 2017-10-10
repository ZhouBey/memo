package yy.zpy.cc.memo

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val a = "file:///storage/emulated/0/MemoCropPictures/1507610906725.png"
        val substring = a.substring(a.lastIndexOf("/") + 1, a.length - 4)
        println(substring)
    }
}
