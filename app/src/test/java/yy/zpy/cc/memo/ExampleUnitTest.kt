package yy.zpy.cc.memo

import org.junit.Test
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val content = "周培源222<img id=\"49579475\"/>333魏娇阳"
        val p = Pattern.compile("")
        val m = p.matcher(content)
//        println(m.find().toString())
//        println(content.replace("<img\\sid=\"\\d*\"/>".toRegex(),"888888"))
        while (m.find()) {
            println(m.group(4))
        }
    }
}
