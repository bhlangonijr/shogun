import com.xmppjingle.shogun.Shogun
import com.xmppjingle.shogun.ShogunDictionary
import com.xmppjingle.shogun.ShogunUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShogunTester {

    @BeforeEach
    fun init() {
    }

    @Test
    fun testBasic() {

        val testInput = "JingleNodesJingleNodesJingleTestNodesTestFinalNodesJingle"

        val p = Shogun.crunch(testInput, 4, 30, 6, Charsets.US_ASCII)

        assertEquals(testInput, Shogun.uncrunch(p.crunched, p.dict))

        val jsonDict = ShogunUtils.exportDict(p.dict)

        println(jsonDict)

        val dict = ShogunUtils.importDict(jsonDict)

        assertEquals(p.dict, dict!!.map)

        println(p.crunched.md5())

        println(dict)

        assertEquals(testInput, Shogun.uncrunch(p.crunched, p.dict))

    }

    @Test
    fun testNoDeltaCharset() {

        val testInput = "JingleNodesJingleNodesJingleTestNodesTestFinalNodesJingle"

        val p = Shogun.crunch(testInput, 4, 30, 6, Charsets.UTF_8)

        assertEquals(testInput, Shogun.uncrunch(p.crunched, p.dict))

        val jsonDict = ShogunUtils.exportDict(p.dict)

        println(jsonDict)

        val dict = ShogunUtils.importDict(jsonDict)

        assertEquals(p.dict, dict!!.map)

        println(dict)

    }

    @Test
    fun testDumbReplace() {

        val op = (165).toChar()
        assertEquals("Post@©", "$op@©".replace("¥", "Post"))
    }

    @Test
    fun testDictList() {
        val files = File(Thread.currentThread().contextClassLoader.getResources(".").nextElement().path + "/sdp").walk().filter { it.isFile }
        val s = files.joinToString { ShogunUtils.readFileDirectlyAsText(it) }

        val p = Shogun.crunch(s, 4, 60, 30, Charsets.US_ASCII)

        assertEquals(s, Shogun.uncrunch(p.crunched, p.dict))

        val jsonDict = ShogunUtils.exportDict(p.dict)

        println(jsonDict)

        val dict = ShogunUtils.importDict(jsonDict)

        assertEquals(p.dict, dict!!.map)

        assertEquals(s, Shogun.uncrunch(p.crunched, dict.map))
        assertEquals(Shogun.uncrunch(Shogun.crunch(s, p.dict), dict.map), Shogun.uncrunch(p.crunched, dict.map))

        println(dict)

    }

    @Test
    fun testDictListBalance() {
        val files = File(Thread.currentThread().contextClassLoader.getResources(".").nextElement().path + "../resources/sdp").walk().filter { it.isFile }
        val s = files.joinToString { ShogunUtils.readFileDirectlyAsText(it) }

        println(Thread.currentThread().contextClassLoader.getResources(".").nextElement().path + "../resources/")

        var sm:Double = 1.0
        var sl:Int = 0

        for (l in 30..50) {
            val c = Shogun.crunch(s, 4, 60, l, Charsets.US_ASCII)
            val ss = c.crunched.length.div(s.length.toDouble())
            println("Layers[$l]: $ss")

            if(ss < sm) sl = l

        }

        ShogunUtils.writeDictToFile(ShogunUtils.exportDict(Shogun.crunch(s, 4, 60, sl, Charsets.US_ASCII).dict))

    }

}

private fun String.md5(): String {
    return ShogunUtils.md5(this)
}
