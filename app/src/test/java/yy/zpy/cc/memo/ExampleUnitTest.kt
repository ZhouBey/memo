package yy.zpy.cc.memo

import android.text.format.DateFormat
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(DateFormat.format("MM月dd日 HH:mm", Calendar.getInstance()).toString())
    }
}
